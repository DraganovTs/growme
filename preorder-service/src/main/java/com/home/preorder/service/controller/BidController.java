package com.home.preorder.service.controller;

import com.home.preorder.service.model.dto.*;
import com.home.preorder.service.service.PreorderService;
import com.home.preorder.service.specification.BidSpecParams;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.UUID;

@RestController
@RequestMapping(value = "/api/bids", produces = MediaType.APPLICATION_JSON_VALUE)
public class BidController {


    private final PreorderService preorderService;

    public BidController(PreorderService preorderService) {
        this.preorderService = preorderService;
    }

    @PostMapping
    public ResponseEntity<BidResponseDTO> createBid(@RequestBody @Valid CreateBidRequestDTO dto) {
        System.out.println();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(preorderService.requestCreateBid(dto));
    }


    @GetMapping("/{bidId}")
    public ResponseEntity<BidResponseDTO> getBidById(@PathVariable UUID bidId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(preorderService.requestBidById(bidId));
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<BidResponseListDTO> getBidsForTask(@PathVariable UUID taskId,
                                                             BidSpecParams request) {
        System.out.println();
        return ResponseEntity.status(HttpStatus.OK).body(preorderService.requestBidsForTask(taskId, request));
    }

    @GetMapping("/my-bids")
    public ResponseEntity<BidResponseListDTO> getUserBids(@RequestAttribute UUID userId,
                                                          BidSpecParams request) {
        System.out.println();
        return ResponseEntity.status(HttpStatus.OK)
                .body(preorderService.requestUserBids(userId,request));
    }


    @PatchMapping("/{bidId}/status")
    public ResponseEntity<BidResponseDTO> updateBidStatus(@PathVariable UUID bidId,
                                                          @RequestBody @Valid UpdateBidStatusRequestDTO dto) {
        System.out.println();
        return ResponseEntity.status(HttpStatus.OK)
                .body(preorderService.requestUpdateBidStatus(bidId, dto));
    }

    @GetMapping("/requires-action")
    public ResponseEntity<BidResponseListDTO> getBidsRequiringAction(@RequestAttribute UUID userId,
                                                                     BidSpecParams request) {
        System.out.println();
        return ResponseEntity.status(HttpStatus.OK)
                .body(preorderService.requestBidsRequiringAction(userId, request));
    }


    @DeleteMapping("/{bidId}")
    public ResponseEntity<Void> withdrawBid(@PathVariable UUID bidId, @RequestAttribute UUID userId) {
        preorderService.requestWithdrawBid(bidId, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{bidId}/counter-offer")
    public ResponseEntity<BidResponseDTO> createCounterOffer(
            @PathVariable UUID bidId,
            @RequestBody @Valid CounterOfferRequestDTO dto,
            @RequestAttribute UUID userId
    ) {
        System.out.println();
        return ResponseEntity.status(HttpStatus.OK)
                .body(preorderService.requestCounterOffer(bidId,dto,userId));
    }

}
