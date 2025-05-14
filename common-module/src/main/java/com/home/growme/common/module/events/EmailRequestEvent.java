package com.home.growme.common.module.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.home.growme.common.module.enums.EmailType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class EmailRequestEvent extends Event{

    private final String email;
    private final EmailType type;



    @JsonCreator
    public EmailRequestEvent(
            @JsonProperty("email") String email,
            @JsonProperty("type") EmailType type)
           {
        this.email = email;
        this.type = type;

    }
}
