package com.home.order.service.service;

import com.home.order.service.model.entity.Owner;

public interface OwnerService {

    Owner findOwnerByEmail(String email);
}
