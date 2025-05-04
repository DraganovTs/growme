package com.home.order.service.mapper;

import com.home.order.service.model.dto.DeliveryMethodDTO;
import com.home.order.service.model.entity.DeliveryMethod;
import org.springframework.stereotype.Component;

@Component
public class DeliveryMethodMapper {

   public DeliveryMethod mapDeliveryMethodDTOToDeliveryMethod(DeliveryMethodDTO input){
        return DeliveryMethod.builder()
                .deliveryMethodId(input.getDeliveryMethodId())
                .price(input.getPrice())
                .description(input.getDescription())
                .shortName(input.getShortName())
                .deliveryTime(input.getDeliveryTime())
                .build();
    }


    public   DeliveryMethodDTO mapDeliveryMethodToDeliveryMethodDTO(DeliveryMethod input){
        return DeliveryMethodDTO.builder()
                .deliveryMethodId(input.getDeliveryMethodId())
                .price(input.getPrice())
                .description(input.getDescription())
                .shortName(input.getShortName())
                .deliveryTime(input.getDeliveryTime())
                .build();
    }


}
