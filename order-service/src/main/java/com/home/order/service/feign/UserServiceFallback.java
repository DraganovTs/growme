package com.home.order.service.feign;

import com.home.growme.common.module.dto.AddressInfo;
import com.home.growme.common.module.dto.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserServiceFallback implements UserServiceClient {


    @Override
    public UserInfo getUserInfo(String userId) {
        log.error("Fallback: Unable to fetch user info for userId {}", userId);

        return UserInfo.builder()
                .username("unknown username for this user")
                .email("unknown@email.com")
                .firstName("unknown first name for this user")
                .lastName("unknown last name for this user")
                .phone("unknown phone for this user")
                .addressInfo(AddressInfo.builder()
                        .city("unknown")
                        .state("unknown")
                        .street("unknown")
                        .zipCode("0000")
                        .build())
                .build();
    }
}
