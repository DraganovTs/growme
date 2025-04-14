package com.home.growme.produt.service.controller;

import com.home.growme.produt.service.model.dto.OwnerDTO;
import com.home.growme.produt.service.service.OwnerService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/owners",produces = MediaType.APPLICATION_JSON_VALUE)
public class OwnerController {

    private final OwnerService ownerService;

    public OwnerController(OwnerService ownerService) {
        this.ownerService = ownerService;
    }

    @GetMapping
    public ResponseEntity<List<OwnerDTO>> findAll() {
        List<OwnerDTO>ownerDTOList = ownerService.getAllOwners();
        return ResponseEntity.ok(ownerDTOList);
    }
}
