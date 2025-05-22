package com.home.growme.common.module.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserInfo {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private AddressInfo addressInfo;
}
