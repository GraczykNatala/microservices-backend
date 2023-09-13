package com.example.product.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Table(name = "products")
@Entity
@NoArgsConstructor
public class ProductEntity extends Product{
    @ManyToOne
    @JoinColumn(name = "category_parameters")
    private Category category;

    public ProductEntity(
            long id, String uid, boolean activate, String name, String mainDesc, String descHtml, float price,
            String[] imageUrls, String parameters, LocalDate createdAt, Category category) {
        super(id, uid, activate, name, mainDesc, descHtml, price, imageUrls, parameters, createdAt);
        this.category = category;
    }

    public ProductEntity(Category category) {
        this.category = category;
    }
}
