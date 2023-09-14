package com.example.product.controller;

import com.example.product.entity.CategoryDTO;
import com.example.product.entity.BasicResponse;
import com.example.product.exceptions.ObjectExistsInDbException;
import com.example.product.mediator.CategoryMediator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/category")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class CategoryController {
    private final CategoryMediator categoryMediator;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<CategoryDTO>> getCategory(){
        return categoryMediator.getCategory();
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> createCategory(@RequestBody CategoryDTO categoryDTO){
        try {
            categoryMediator.createCategory(categoryDTO);
        } catch (ObjectExistsInDbException e) {
            return ResponseEntity.status(400)
                    .body(new BasicResponse("Category already exists in database"));
        }
        return ResponseEntity.ok("Operation successful");
    }
}
