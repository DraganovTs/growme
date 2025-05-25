package com.home.growme.common.module.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressInfo {
    private String street;
    private String city;
    private String state;
    private String zipCode;
}
