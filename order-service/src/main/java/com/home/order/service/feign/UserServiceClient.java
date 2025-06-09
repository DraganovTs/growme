package com.home.order.service.feign;

import com.home.growme.common.module.dto.UserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "http://localhost:8081", fallback = UserServiceFallback.class)
public interface UserServiceClient {

    @GetMapping("/api/users/userinfo/{userId}")
    UserInfo getUserInfo(@PathVariable String userId);
}
