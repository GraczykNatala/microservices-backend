package com.example.product.controller;

import com.example.product.entity.BasicResponse;
import com.example.product.entity.ProductFormDTO;
import com.example.product.mediator.ProductMediator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.product.Utils.Constant.COLUMN_PRICE;
import static com.example.product.Utils.Constant.SORT_ASC;

@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductMediator productMediator;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> get(HttpServletRequest request,
                                 @RequestParam(required = false) String name_like,
                                 @RequestParam(required = false) String date,
                                 @RequestParam(required = false) String _category,
                                 @RequestParam(required = false) Float price_min,
                                 @RequestParam(required = false) Float price_max,
                                 @RequestParam(required = false, defaultValue = "0") int _page,
                                 @RequestParam(required = false, defaultValue = "10") int _limit,
                                 @RequestParam(required = false, defaultValue = COLUMN_PRICE) String _sort,
                                 @RequestParam(required = false, defaultValue = SORT_ASC) String _order){

            return productMediator.getProduct(_page, _limit,
                                              name_like, _category,
                                              price_min, price_max, date, _sort, _order);
    }
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<BasicResponse> save(@RequestBody ProductFormDTO form){
        return productMediator.saveProduct(form);
    }
    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity<BasicResponse> delete(@RequestParam String uuid){
        return productMediator.deleteProduct(uuid);
    }

}
