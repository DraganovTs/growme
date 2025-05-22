package com.home.growme.common.module.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddressInfo {
    private String street;
    private String city;
    private String state;
    private String zipCode;
}
