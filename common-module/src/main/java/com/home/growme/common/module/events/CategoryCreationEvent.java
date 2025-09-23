package com.home.growme.common.module.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CategoryCreationEvent extends Event {
    private final String categoryId;
    private final String categoryName;

    @JsonCreator
    public CategoryCreationEvent(
            @JsonProperty("categoryId") String categoryId,
            @JsonProperty("categoryName") String categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }
}
