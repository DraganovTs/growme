package com.home.growme.common.module.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private AddressInfo addressInfo;
}
