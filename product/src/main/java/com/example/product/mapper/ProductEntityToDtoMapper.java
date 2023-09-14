package com.example.product.mapper;

import com.example.product.entity.Category;
import com.example.product.entity.CategoryDTO;
import com.example.product.entity.ProductDTO;
import com.example.product.entity.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper
public abstract class ProductEntityToDtoMapper {
    public ProductDTO toProductDTO(ProductEntity productEntity){
        return toDTO(productEntity);
    }

    @Mappings({
            @Mapping(expression = "java(toCategoryDTO(productEntity.getCategory()))",target = "categoryDTO")
    })
    protected abstract ProductDTO toDTO(ProductEntity productEntity);

    @Mappings({})
    protected abstract CategoryDTO toCategoryDTO(Category category);
}

