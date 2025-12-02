package com.vno.web;

import com.vno.util.SubdomainUtil;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/whoami")
public class WhoAmIResource {

    @Inject
    SubdomainUtil subdomainUtil;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response whoAmI(@Context HttpHeaders headers) {
        String host = headers.getHeaderString("Host");
        String org = subdomainUtil.extractOrgSlug(host);
        String json = String.format("{\"host\":\"%s\",\"orgSlug\":%s}",
                host == null ? "" : host,
                org == null ? "null" : ("\"" + org + "\""));
        return Response.ok(json).build();
    }
}
