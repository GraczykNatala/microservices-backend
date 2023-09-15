package com.example.product.service;

import com.example.product.entity.Category;
import com.example.product.entity.CategoryDTO;


import java.util.List;
import java.util.Optional;


public interface CategoryService {

    void create(CategoryDTO categoryDTO);

    Optional<Category> findCategoryByShortId(String shortId);

    List<Category> getCategory();




}
