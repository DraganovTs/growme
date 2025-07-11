package com.home.preorder.service.controller;

import com.home.preorder.service.model.dto.BidResponseDTO;
import com.home.preorder.service.model.dto.CreateBidRequestDTO;
import com.home.preorder.service.service.PreorderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
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
    public ResponseEntity<List<BidResponseDTO>> getBidsForTask(@PathVariable UUID taskId) {
        System.out.println();
        return ResponseEntity.status(HttpStatus.OK).body(preorderService.requestBidsForTask(taskId));
    }

    @GetMapping("/my-bids")
    public ResponseEntity<List<?>> getUserBids() {
        return null;
    }


    @PatchMapping("/{bidId}/status")
    public ResponseEntity<?> updateBidStatus() {
        return null;
    }

    @GetMapping("/requires-action")
    public ResponseEntity<List<?>> getBidsRequiringAction() {
        return null;
    }


    @DeleteMapping("/{bidId}")
    public ResponseEntity<Void> withdrawBid() {
        return null;
    }

    @PostMapping("/{bidId}/counter-offer")
    public ResponseEntity<?> createCounterOffer() {
        return null;
    }


    //TODO
}
