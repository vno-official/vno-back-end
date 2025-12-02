package com.vno.util;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class SubdomainUtil {

    @ConfigProperty(name = "app.domain", defaultValue = "vno.com")
    String appDomain;

    public String extractOrgSlug(String host) {
        if (host == null || host.isBlank()) return null;
        String normalizedHost = host.toLowerCase();
        String domain = appDomain.toLowerCase();
        if (normalizedHost.equals(domain)) return null;
        if (normalizedHost.endsWith("." + domain)) {
            String withoutDomain = normalizedHost.substring(0, normalizedHost.length() - (domain.length() + 1));
            // handle multi-level like hello.acme.vno.com -> we take leftmost segment as orgslug policy for Phase 0
            int dot = withoutDomain.indexOf('.');
            return dot == -1 ? withoutDomain : withoutDomain.substring(0, dot);
        }
        return null;
    }
}
