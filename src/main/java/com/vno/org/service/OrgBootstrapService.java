package com.vno.org.service;

import com.vno.core.entity.*;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Random;

@ApplicationScoped
public class OrgBootstrapService {

    private static final Random RANDOM = new Random();

    @WithTransaction
    public Uni<Organization> bootstrapOrganization(User user) {
        String slug = generateSlugFromEmail(user.email);
        
        return ensureUniqueSlug(slug)
            .flatMap(uniqueSlug -> {
                Organization org = new Organization();
                org.name = uniqueSlug.substring(0, 1).toUpperCase() + uniqueSlug.substring(1);
                org.slug = uniqueSlug;
                
                return org.persistAndFlush()
                    .onItem().castTo(Organization.class)
                    .flatMap(savedOrg -> {
                        UserOrganization userOrg = new UserOrganization();
                        userOrg.user = user;
                        userOrg.organization = savedOrg;
                        userOrg.role = Role.OWNER;
                        
                        return userOrg.persistAndFlush()
                            .flatMap(uo -> createPrivateWorkspace(savedOrg, user))
                            .replaceWith(savedOrg);
                    });
            });
    }

    private Uni<Workspace> createPrivateWorkspace(Organization org, User user) {
        Workspace privateWorkspace = new Workspace();
        privateWorkspace.organizationId = org.id;
        privateWorkspace.name = "Private";
        privateWorkspace.iconEmoji = "🔒";
        privateWorkspace.defaultPermission = "private";
        privateWorkspace.isSystem = true;
        privateWorkspace.createdBy = user;
        
        return privateWorkspace.persistAndFlush()
            .onItem().castTo(Workspace.class)
            .flatMap(ws -> createWelcomePage(org, ws, user))
            .replaceWith(privateWorkspace);
    }

    private Uni<Page> createWelcomePage(Organization org, Workspace workspace, User user) {
        Page welcomePage = new Page();
        welcomePage.organizationId = org.id;
        welcomePage.workspace = workspace;
        welcomePage.title = "Welcome to VNO";
        welcomePage.iconEmoji = "👋";
        welcomePage.createdBy = user;
        
        // Persist first, then update path with generated ID
        return welcomePage.persistAndFlush()
            .chain(savedEntity -> {
                Page savedPage = (Page) savedEntity;
                savedPage.path = "." + savedPage.id + ".";
                return savedPage.persistAndFlush();
            })
            .onItem().castTo(Page.class);
    }

    private String generateSlugFromEmail(String email) {
        int atIndex = email.indexOf('@');
        if (atIndex > 0) {
            String domain = email.substring(atIndex + 1);
            int dotIndex = domain.indexOf('.');
            if (dotIndex > 0) {
                return domain.substring(0, dotIndex).toLowerCase();
            }
            return domain.toLowerCase();
        }
        return email.substring(0, atIndex > 0 ? atIndex : email.length()).toLowerCase();
    }

    private Uni<String> ensureUniqueSlug(String baseSlug) {
        return Organization.slugExists(baseSlug)
            .map(exists -> {
                if (exists) {
                    return baseSlug + RANDOM.nextInt(1000);
                }
                return baseSlug;
            });
    }
}
