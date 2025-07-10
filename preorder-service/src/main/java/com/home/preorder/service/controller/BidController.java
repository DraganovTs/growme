package com.home.preorder.service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/api/bids",produces = MediaType.APPLICATION_JSON_VALUE)
public class BidController {


    @PostMapping
    public ResponseEntity<?> createBid(){
        return null;
    }


    @GetMapping("/{bidId}")
    public ResponseEntity<?> getBidById(){
        return null;
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<?>>getBidsForTask(){
        return  ResponseEntity.status(HttpStatus.OK).body(new ArrayList<>());
    }

    @GetMapping("/my-bids")
    public ResponseEntity<List<?>> getUserBids(){
        return null;
    }


    @PatchMapping("/{bidId}/status")
    public ResponseEntity<?> updateBidStatus(){
        return null;
    }

    @GetMapping("/requires-action")
    public ResponseEntity<List<?>> getBidsRequiringAction(){
        return null;
    }


    @DeleteMapping("/{bidId}")
    public ResponseEntity<Void> withdrawBid(){
        return null;
    }

    @PostMapping("/{bidId}/counter-offer")
    public ResponseEntity<?> createCounterOffer(){
        return null;
    }


    //TODO
}
