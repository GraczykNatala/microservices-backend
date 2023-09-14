package com.example.product.service.implementations;

import com.example.product.entity.Category;
import com.example.product.entity.CategoryDTO;
import com.example.product.exceptions.ObjectExistsInDbException;
import com.example.product.repository.CategoryRepository;
import com.example.product.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public void create(CategoryDTO categoryDTO) throws ObjectExistsInDbException {
        Category category = new Category();
        category.setName(categoryDTO.getName());
        category.setShortId(UUID.randomUUID().toString().replace("-","").substring(0,12));

        categoryRepository.findByName(category.getName()).ifPresent( value -> {
            throw new ObjectExistsInDbException("Category already exists in database");
        });
        categoryRepository.save(category);
    }

    @Override
    public List<Category> getCategory() {
        return categoryRepository.findAll();
    }

}
