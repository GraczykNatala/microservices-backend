package com.example.product.service;

import com.example.product.entity.Category;
import com.example.product.entity.CategoryDTO;


import java.util.List;


public interface CategoryService {

    void create(CategoryDTO categoryDTO);

    List<Category> getCategory();




}
