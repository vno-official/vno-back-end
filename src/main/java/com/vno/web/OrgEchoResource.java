package com.vno.web;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Path("/api/org/whoami")
public class OrgEchoResource {

    @Inject
    @ConfigProperty(name = "app.domain", defaultValue = "vno.com")
    String appDomain;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response whoami(@HeaderParam("Host") String host) {
        String orgSlug = parseOrgSlug(host);
        String body = String.format("{\"host\":\"%s\",\"orgSlug\":\"%s\"}", host == null ? "" : host, orgSlug == null ? "" : orgSlug);
        return Response.ok(body).build();
    }

    private String parseOrgSlug(String host) {
        if (host == null || host.isEmpty()) return null;
        String normalizedHost = host.toLowerCase();
        String domain = appDomain.toLowerCase();
        if (!normalizedHost.endsWith(domain)) return null;
        String prefix = normalizedHost.substring(0, normalizedHost.length() - domain.length());
        if (prefix.endsWith(".")) prefix = prefix.substring(0, prefix.length() - 1);
        if (prefix.isEmpty()) return null;
        String[] parts = prefix.split("\\.");
        return parts.length == 0 ? null : parts[parts.length - 1];
    }
}
