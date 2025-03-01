package com.home.growme.produt.service.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/categories/",produces = MediaType.APPLICATION_JSON_VALUE)
public class CategoryController {

    //TODO
    //GET /categories → Fetch all categories
}
