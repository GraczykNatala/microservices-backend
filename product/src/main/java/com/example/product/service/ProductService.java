package com.example.product.service;

import com.example.product.entity.ProductDTO;
import com.example.product.entity.ProductEntity;

import java.util.List;


public interface ProductService {

    List<ProductEntity> getProduct(
            String name, String category, Float priceMin, Float priceMax, String data,
            int page, int limit, String sort, String order);

    ProductDTO getProductDTO();
    long countActiveProducts(String name, String category, Float price_min, Float price_max);
}
