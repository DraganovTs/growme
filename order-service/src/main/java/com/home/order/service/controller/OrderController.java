package com.home.order.service.controller;

import com.home.growme.common.module.exceptions.ErrorResponseDTO;
import com.home.order.service.model.dto.IOrderDto;
import com.home.order.service.model.dto.OrderDTO;
import com.home.order.service.model.entity.Basket;
import com.home.order.service.model.entity.Order;
import com.home.order.service.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Orders", description = "Operations related to orders and payment intents")
@RestController
@RequestMapping(value = "/api/orders", produces = MediaType.APPLICATION_JSON_VALUE)
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(summary = "Create or update payment intent", description = "Creates or updates a payment intent based on the basket ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment intent created or updated successfully",
                    content = @Content(schema = @Schema(implementation = Basket.class))),
            @ApiResponse(responseCode = "404", description = "Basket not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping("/{basketId}")
    public ResponseEntity<Basket> createOrUpdatePaymentIntent(@PathVariable String basketId){

        Basket basket = orderService.createOrUpdatePaymentIntent(basketId);
        if (basket == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(basket);

    }
    @Operation(summary = "Create or update an order", description = "Creates or updates an order based on provided order details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order created or updated successfully",
                    content = @Content(schema = @Schema(implementation = Order.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping
    public ResponseEntity<Order> createOrUpdateOrder(@Valid @RequestBody OrderDTO orderDTO){
        System.out.println("********");
        Order order = orderService.createOrUpdateOrder(orderDTO);
        return ResponseEntity.ok(order);
    }
    @Operation(summary = "Get all orders for a user", description = "Retrieves a list of orders for the specified user email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orders retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = IOrderDto.class)))),
            @ApiResponse(responseCode = "404", description = "Orders not found for the given user",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/user/{userEmail}")
    public  ResponseEntity<List<IOrderDto>> getAllOrderForUser(@PathVariable String userEmail ){
        List<IOrderDto> orderDTOList = orderService.getAllOrdersForUser(userEmail);
        if (orderDTOList == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(orderDTOList);
    }
    @Operation(summary = "Get order details", description = "Retrieves detailed information about a specific order by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order details retrieved successfully",
                    content = @Content(schema = @Schema(implementation = IOrderDto.class))),
            @ApiResponse(responseCode = "404", description = "Order not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/{orderId}")
    public ResponseEntity<IOrderDto> getOrderDetails(@PathVariable String orderId){
        IOrderDto iOrderDto = orderService.getOrderById(UUID.fromString(orderId));
        if (iOrderDto == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(iOrderDto);
    }

}
