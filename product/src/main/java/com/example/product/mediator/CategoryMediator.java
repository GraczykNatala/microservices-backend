package com.example.product.mediator;

import com.example.product.entity.CategoryDTO;
import com.example.product.exceptions.ObjectExistsInDbException;
import com.example.product.mapper.CategoryToCategoryDTOMapper;
import com.example.product.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CategoryMediator {

    private final CategoryService categoryService;
    private final CategoryToCategoryDTOMapper mapper;

    public void createCategory(CategoryDTO categoryDTO) throws ObjectExistsInDbException {
        categoryService.create(categoryDTO);
    }

    public ResponseEntity<List<CategoryDTO>> getCategory() {
        List<CategoryDTO> categories = new ArrayList<>();
        categoryService.getCategory().forEach(value -> {
            categories.add(mapper.tocategoryDTO(value));
        });
        return ResponseEntity.ok(categories);
    }
}
