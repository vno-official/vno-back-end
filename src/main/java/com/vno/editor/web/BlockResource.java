package com.vno.editor.web;

import com.vno.core.entity.Block;
import com.vno.core.entity.Page;
import com.vno.core.entity.User;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.security.Authenticated;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.UUID;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/blocks")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Block", description = "Block (editor content) management endpoints")
public class BlockResource {

    @Inject
    JsonWebToken jwt;

    /**
     * Get all blocks for a page (Reactive)
     */
    @GET
    public Uni<Response> getBlocksByPage(@QueryParam("pageId") UUID pageId) {
        if (pageId == null) {
            throw new BadRequestException("pageId is required");
        }

        return Page.<Page>findById(pageId)
            .onItem().ifNull().failWith(new NotFoundException("Page not found"))
            .flatMap(page -> Block.findByPage(pageId))
            .map(blocks -> Response.ok(blocks).build());
    }

    /**
     * Create a new block (Reactive)
     */
    @POST
    @WithTransaction
    public Uni<Response> createBlock(JsonObject payload) {
        Long pageId = payload.getLong("pageId");
        String type = payload.getString("type");
        JsonObject content = payload.getJsonObject("content");
        Integer orderIndex = payload.getInteger("orderIndex", 0);
        Long userId = Long.parseLong(jwt.getSubject());

        return Page.<Page>findById(pageId)
            .onItem().ifNull().failWith(new NotFoundException("Page not found"))
            .flatMap(page -> User.<User>findById(userId)
                .map(user -> {
                    Block block = new Block();
                    block.page = page;
                    block.type = Block.BlockType.valueOf(type.toUpperCase());
                    block.content = content.toString();
                    block.orderIndex = orderIndex;
                    block.createdBy = user;
                    return block;
                })
            )
            .flatMap(block -> block.persistAndFlush())
            .map(block -> Response.status(Response.Status.CREATED).entity(block).build());
    }

    /**
     * Update a block (Reactive)
     */
    @PUT
    @Path("/{id}")
    @WithTransaction
    public Uni<Response> updateBlock(@PathParam("id") Long id, JsonObject payload) {
        return Block.<Block>findById(id)
            .onItem().ifNull().failWith(new NotFoundException("Block not found"))
            .flatMap(block -> {
                if (payload.containsKey("content")) {
                    block.content = payload.getString("content");
                }
                if (payload.containsKey("orderIndex")) {
                    block.orderIndex = payload.getInteger("orderIndex");
                }
                if (payload.containsKey("type")) {
                    block.type = Block.BlockType.valueOf(payload.getString("type").toUpperCase());
                }
                return block.persistAndFlush();
            })
            .map(block -> Response.ok(block).build());
    }

    /**
     * Batch save/update blocks (Reactive)
     */
    @POST
    @Path("/batch")
    @WithTransaction
    public Uni<Response> batchSaveBlocks(JsonObject payload) {
        UUID pageId = UUID.fromString(payload.getString("pageId"));
        JsonArray blocksArray = payload.getJsonArray("blocks");
        Long userId = Long.parseLong(jwt.getSubject());

        return Page.<Page>findById(pageId)
            .onItem().ifNull().failWith(new NotFoundException("Page not found"))
            .flatMap(page -> User.<User>findById(userId)
                .flatMap(user -> Block.deleteByPage(pageId)
                    .flatMap(v -> {
                        // Create blocks sequentially
                        Uni<Void> chain = Uni.createFrom().voidItem();
                        for (int i = 0; i < blocksArray.size(); i++) {
                            JsonObject blockData = blocksArray.getJsonObject(i);
                            final int index = i;
                            
                            chain = chain.flatMap(ignore -> {
                                Block block = new Block();
                                block.page = page;
                                block.type = Block.BlockType.valueOf(blockData.getString("type").toUpperCase());
                                block.content = blockData.getString("content", "{}");
                                block.orderIndex = index;
                                block.createdBy = user;
                                return block.persistAndFlush().replaceWithVoid();
                            });
                        }
                        return chain;
                    })
                )
            )
            .map(v -> Response.ok()
                .entity(new JsonObject().put("message", "Blocks saved successfully"))
                .build());
    }

    /**
     * Delete a block (Reactive)
     */
    @DELETE
    @Path("/{id}")
    @WithTransaction
    public Uni<Response> deleteBlock(@PathParam("id") Long id) {
        return Block.<Block>findById(id)
            .onItem().ifNull().failWith(new NotFoundException("Block not found"))
            .flatMap(block -> block.delete().replaceWith(block))
            .map(block -> Response.noContent().build());
    }
}
