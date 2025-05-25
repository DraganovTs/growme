package com.home.order.service.controller;

import com.home.order.service.model.dto.DeliveryMethodDTO;
import com.home.order.service.service.DeliveryMethodService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
/**
 * REST controller for managing available delivery methods in the Order Service.
 */
@Tag(name = "Delivery Methods", description = "Operations related to delivery methods")
@RestController
@RequestMapping(value = "/api/deliverymethods", produces = MediaType.APPLICATION_JSON_VALUE)
public class DeliveryMethodController {

    private final DeliveryMethodService deliveryMethodService;

    public DeliveryMethodController(DeliveryMethodService deliveryMethodService) {
        this.deliveryMethodService = deliveryMethodService;
    }
    @Operation(summary = "Get all delivery methods", description = "Retrieve a list of all available delivery methods.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved delivery methods",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = DeliveryMethodDTO.class)))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content())
    })
    @GetMapping
    public ResponseEntity<List<DeliveryMethodDTO>> getAllDeliveryMethods() {
        List<DeliveryMethodDTO> deliveryMethods = deliveryMethodService.getAllDeliveryMethods();
        return ResponseEntity.ok(deliveryMethods);
    }
}
