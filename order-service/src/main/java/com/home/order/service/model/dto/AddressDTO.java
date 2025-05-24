package com.home.order.service.model.dto;

import jakarta.annotation.Nonnull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddressDTO {
    public @Nonnull String firstName;
    public @Nonnull String lastName;
    public @Nonnull String street;
    public @Nonnull String city;
    public @Nonnull String state;
    public @Nonnull String zipCode;


}
