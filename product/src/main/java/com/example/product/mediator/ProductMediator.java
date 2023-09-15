package com.example.product.mediator;

import com.example.product.entity.*;
import com.example.product.exceptions.CategoryDoNotExistException;
import com.example.product.mapper.ProductEntityToDtoMapper;
import com.example.product.mapper.ProductEntityToSimpleProductMapper;
import com.example.product.mapper.ProductFormToEntityMapper;
import com.example.product.service.CategoryService;
import com.example.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import static com.example.product.Utils.Constant.CHAR_ENC;
import static com.example.product.Utils.Constant.COUNT_HEADER;
import static com.example.product.service.implementations.ProductServiceImpl.FILE_SERVICE;

@Component
@RequiredArgsConstructor
public class ProductMediator {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final ProductEntityToSimpleProductMapper productToSimpleMapper;
    private final ProductEntityToDtoMapper productEntityToDtoMapper;
    private final ProductFormToEntityMapper formToEntityMapper;
    public ResponseEntity<?> getProduct(int page,
                                        int limit,
                                        String name,
                                        String category,
                                        Float price_min,
                                        Float price_max,
                                        String date,
                                        String sort,
                                        String order) {
        if(name != null && !name.isEmpty()) {
            try {
                name = URLDecoder.decode(name, CHAR_ENC);
            }
            catch(UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        List<ProductEntity> product = productService.getProduct(name, category, price_min, price_max, date, page, limit, sort, order);

        product.forEach(value->{
        for (int i =0; i < value.getImageUrls().length; i++) {
             value.getImageUrls()[i] = FILE_SERVICE + "?uuid=" + value.getImageUrls()[i];
        }
        });
        if (name == null || name.isEmpty() || date == null || date.isEmpty()){
            List<SimpleProductDto> simpleProductDTOS = new ArrayList<>();
            long totalCount  = productService.countActiveProducts( name, category, price_min, price_max);
            product.forEach(value-> simpleProductDTOS.add(productToSimpleMapper.toSimpleProduct(value)));
            return ResponseEntity.ok()
                    .header(COUNT_HEADER, String.valueOf(totalCount))
                    .body(simpleProductDTOS);
        }
        ProductDTO productDTO = productEntityToDtoMapper.toProductDTO(product.get(0));
        return ResponseEntity.ok()
                .body(productDTO);
    }

    public ResponseEntity<BasicResponse> saveProduct(ProductFormDTO form) {
        try{
            ProductEntity product = formToEntityMapper.toProductEntity(form);
            categoryService.findCategoryByShortId(product.getCategory().getShortId())
                    .ifPresentOrElse(product::setCategory, () -> {
                        throw new CategoryDoNotExistException();
                    });
            productService.createProduct(product);
            return ResponseEntity.ok(new BasicResponse("Succesfully created"));
        } catch(RuntimeException e){
            return ResponseEntity.status(400).body(new BasicResponse("Cannot create product, Category do not exist"));
        }
    }

    public ResponseEntity<BasicResponse> deleteProduct(String uuid) {
        try {
           productService.delete(uuid);
           return ResponseEntity.ok(new BasicResponse("Succesfully deleted"));
        } catch(RuntimeException e) {
            return ResponseEntity
                    .status(500)
                    .body(new BasicResponse("Product do not exist"));
        }

    }
}


