package com.home.preorder.service.controller;

import com.home.preorder.service.model.dto.BidResponseDTO;
import com.home.preorder.service.model.dto.CounterOfferRequestDTO;
import com.home.preorder.service.model.dto.CreateBidRequestDTO;
import com.home.preorder.service.model.dto.UpdateBidStatusRequestDTO;
import com.home.preorder.service.service.PreorderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
    public ResponseEntity<Page<BidResponseDTO>> getBidsForTask(@PathVariable UUID taskId,
                                                               @PageableDefault(size = 20)Pageable pageable) {
        System.out.println();
        return ResponseEntity.status(HttpStatus.OK).body(preorderService.requestBidsForTask(taskId, pageable));
    }

    @GetMapping("/my-bids")
    public ResponseEntity<Page<BidResponseDTO>> getUserBids(@RequestAttribute UUID userId,
                                                            @PageableDefault(size = 20) Pageable pageable) {
        System.out.println();
        return ResponseEntity.status(HttpStatus.OK)
                .body(preorderService.requestUserBids(userId,pageable));
    }


    @PatchMapping("/{bidId}/status")
    public ResponseEntity<BidResponseDTO> updateBidStatus(@PathVariable UUID bidId,
                                                          @RequestBody @Valid UpdateBidStatusRequestDTO dto) {
        System.out.println();
        return ResponseEntity.status(HttpStatus.OK)
                .body(preorderService.requestUpdateBidStatus(bidId, dto));
    }

    @GetMapping("/requires-action")
    public ResponseEntity<Page<BidResponseDTO>> getBidsRequiringAction(@RequestAttribute UUID userId,
                                                                       @PageableDefault(size = 20)Pageable pageable) {
        System.out.println();
        return ResponseEntity.status(HttpStatus.OK)
                .body(preorderService.requestBidsRequiringAction(userId, pageable));
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
