package com.home.growme.produt.service.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class OwnerDTO {

    private UUID ownerId;
    private String ownerName;
}
