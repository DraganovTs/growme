package com.home.order.service.service;

import com.home.growme.common.module.events.UserCreatedEvent;
import com.home.order.service.model.entity.Owner;

public interface OwnerService {

    Owner findOwnerByEmail(String email);

    void createOwner(UserCreatedEvent event);


}
