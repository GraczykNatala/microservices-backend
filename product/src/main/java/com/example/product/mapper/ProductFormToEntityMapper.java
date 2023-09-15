package com.example.product.mapper;

import com.example.product.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper
public abstract class ProductFormToEntityMapper {
    public ProductEntity toProductEntity(ProductFormDTO form){
        return toEntity(form);
    }

    @Mappings({
            @Mapping(target = "category",
                     expression = "java(toCategory(form.getCategory()))"),
            @Mapping(target = "imageUrls",
                     source = "imagesUuid")

    })
    protected abstract ProductEntity toEntity(ProductFormDTO form);


    protected Category toCategory(String uuid){
        Category category = new Category();
        category.setShortId(uuid);
        return category;
}
}
