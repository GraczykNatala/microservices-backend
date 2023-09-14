package com.example.product.mapper;

import com.example.product.entity.ProductEntity;
import com.example.product.entity.SimpleProductDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper
public abstract class ProductEntityToSimpleProductMapper {

    public SimpleProductDto toSimpleProduct(ProductEntity productEntity){
        return toSimpleProductDTO(productEntity);
    }

    @Mappings({
            @Mapping(target = "imageUrl",
                    expression = "java(getImageUrl(productEntity.getImageUrls()))")
    })
    protected abstract SimpleProductDto toSimpleProductDTO(ProductEntity productEntity);


    String getImageUrl(String[] images) {
        return images != null && images.length >= 1 ? images[0] : null;
    }

}
