package com.home.growme.product.service.controller;

import com.home.growme.product.service.model.dto.OwnerDTO;
import com.home.growme.product.service.service.OwnerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for managing product owners in the GrowMe platform.
 * Provides endpoints for retrieving owner details and statistics.
 */
@Tag(
        name = "Owner Management API",
        description = """
        REST APIs to retrieve owner information in the GrowMe platform.
        Includes:
        - Basic listing of product owners
        - Listing owners ranked by number of products
        """
)
@Slf4j
@RestController
@RequestMapping(value = "/api/owners",produces = MediaType.APPLICATION_JSON_VALUE)
public class OwnerController {

    private final OwnerService ownerService;

    public OwnerController(OwnerService ownerService) {
        this.ownerService = ownerService;
    }

    @Operation(
            summary = "Get all product owners",
            description = "Retrieves a list of all product owners, limited to 8 for UI display purposes.",
            operationId = "getAllOwners"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of owners retrieved", content = @Content(schema = @Schema(implementation = OwnerDTO.class)))
    })
    @GetMapping
    public ResponseEntity<List<OwnerDTO>> getAllOwners() {
        log.debug("Fetching all product owners (limit 8)");
        List<OwnerDTO>ownerDTOList = ownerService.getAllOwners();
        return ResponseEntity.ok(ownerDTOList);
    }

    @Operation(
            summary = "Get owners sorted by product count",
            description = "Returns a list of top 8 product owners ranked by the number of products they own.",
            operationId = "getOwnersSortedByProductCount"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Owners sorted by product count retrieved", content = @Content(schema = @Schema(implementation = OwnerDTO.class)))
    })
    @GetMapping("/sorted-by-product-count")
    public ResponseEntity<List<OwnerDTO>> getOwnersSortedByProductCount() {
        log.debug("Fetching owners sorted by product count (top 8)");
        List<OwnerDTO> ownerDTOList = ownerService.getAllOwnersSortedByProductCount();
        return ResponseEntity.ok(ownerDTOList);
    }
}
