package com.example.product.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SimpleProductDto {
    private String name;
    private String mainDesc;
    private float price;
    private String imageUrl;
    private LocalDate createAt;
}
