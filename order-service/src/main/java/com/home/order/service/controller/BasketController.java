package com.home.order.service.controller;

import com.home.growme.common.module.exceptions.ErrorResponseDTO;
import com.home.order.service.model.dto.BasketData;
import com.home.order.service.service.BasketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
/**
 * REST controller for managing shopping baskets.
 * Provides endpoints for creating/updating, retrieving, and deleting baskets.
 */
@Tag(
        name = "Basket Management API",
        description = """
        Set of REST APIs for managing shopping baskets in the GrowMe platform.
        Supports creation, update, retrieval, and deletion of baskets.
        """
)
@Slf4j
@RestController
@RequestMapping(value = "/api/basket" , produces = MediaType.APPLICATION_JSON_VALUE)
public class BasketController {

    private final BasketService basketService;

    public BasketController(BasketService basketService) {
        this.basketService = basketService;
    }

    @Operation(
            summary = "Create or update a basket",
            description = "Creates a new basket or updates an existing one for a user.",
            operationId = "createOrUpdateBasket"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Basket created or updated successfully",
                    content = @Content(schema = @Schema(implementation = BasketData.class))),
            @ApiResponse(responseCode = "400", description = "Invalid basket data",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping
    public ResponseEntity<BasketData> createOrUpdateBasket(@RequestBody @Valid BasketData basketData){
        BasketData savedBasked = basketService.createOrUpdateBasket(basketData);
        return ResponseEntity.ok(savedBasked);
    }

    @Operation(
            summary = "Retrieve basket by ID",
            description = "Fetches a user's basket using their unique ID.",
            operationId = "getBasketById"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Basket retrieved successfully",
                    content = @Content(schema = @Schema(implementation = BasketData.class))),
            @ApiResponse(responseCode = "404", description = "Basket not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<BasketData> getBasketById(@PathVariable String id){
        BasketData basket = basketService.getBasketById(id);

            return ResponseEntity.ok(basket);

    }
    @Operation(
            summary = "Delete basket by ID",
            description = "Deletes the user's basket using their unique ID.",
            operationId = "deleteBasket"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Basket deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Basket not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBasket(@PathVariable String id){
        basketService.deleteBasket(id);
        return ResponseEntity.noContent().build();
    }

}
