package com.example.product.mediator;

import com.example.product.entity.ProductEntity;
import com.example.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import static com.example.product.Utils.Constant.CHAR_ENC;

@Component
@RequiredArgsConstructor
public class ProductMediator {

    public static final String COUNT_HEADER = "X-Total-Count";

    private final ProductService productService;
    public ResponseEntity<?> getProduct(int page,
                                        int limit,
                                        String name,
                                        String category,
                                        Float price_min,
                                        Float price_max,
                                        String data,
                                        String sort,
                                        String order) {
        long totalCount = productService.countActiveProducts(name,category, price_min, price_max);
        List<ProductEntity> product = productService
                .getProduct(name, category, price_min, price_max, data, page, limit, sort, order);

        if(name != null && !name.isEmpty()) {
            try {
                name = URLDecoder.decode(name, CHAR_ENC);
            } catch(UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        return ResponseEntity.ok()
                .header(COUNT_HEADER, String.valueOf(totalCount))
                .body(product);
    }

}
