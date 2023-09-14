package com.example.product.mediator;

import com.example.product.entity.ProductDTO;
import com.example.product.entity.ProductEntity;
import com.example.product.entity.SimpleProductDto;
import com.example.product.mapper.ProductEntityToDtoMapper;
import com.example.product.mapper.ProductEntityToSimpleProductMapper;
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

@Component
@RequiredArgsConstructor
public class ProductMediator {

    private final ProductService productService;
    private final ProductEntityToSimpleProductMapper productToSimpleMapper;
    private final ProductEntityToDtoMapper productEntityToDtoMapper;
    public ResponseEntity<?> getProduct(int page,
                                        int limit,
                                        String name,
                                        String category,
                                        Float price_min,
                                        Float price_max,
                                        String date,
                                        String sort,
                                        String order) {
        List<ProductEntity> product = productService.getProduct(name, category, price_min, price_max, date, page, limit, sort, order);
        if(name != null && !name.isEmpty()) {
            try {
                name = URLDecoder.decode(name, CHAR_ENC);
            }
            catch(UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }


        if (name == null || name.isEmpty() || date == null || date.isEmpty()){
            List<SimpleProductDto> simpleProductDTOS = new ArrayList<>();
            long totalCount  = productService.countActiveProducts( name, category, price_min, price_max);
            product.forEach(value->{
                simpleProductDTOS.add(productToSimpleMapper.toSimpleProduct(value));
            });
            return ResponseEntity.ok()
                    .header(COUNT_HEADER, String.valueOf(totalCount))
                    .body(simpleProductDTOS);
        }
        ProductDTO productDTO = productEntityToDtoMapper.toProductDTO(product.get(0));
        return ResponseEntity.ok()
                .body(productDTO);
    }
}


