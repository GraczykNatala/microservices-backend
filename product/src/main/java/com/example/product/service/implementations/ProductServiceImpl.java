package com.example.product.service.implementations;

import com.example.product.entity.ProductDTO;
import com.example.product.entity.ProductEntity;
import com.example.product.repository.CategoryRepository;
import com.example.product.repository.ProductRepository;
import com.example.product.service.ProductService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.example.product.Utils.Constant.*;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    @PersistenceContext
    EntityManager entityManager;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;


    @Override
    public List<ProductEntity> getProduct(
            String name, String category, Float price_min, Float price_max, String data,
            int page, int limit, String sort, String order) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ProductEntity> query = criteriaBuilder.createQuery(ProductEntity.class);
        Root<ProductEntity> root = query.from(ProductEntity.class);

        if (data != null && !data.equals(EMPTY) && name != null && !name.trim().equals(EMPTY)) {
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate date = LocalDate.parse(data, inputFormatter);
            return productRepository.findByNameAndCreateAt(name, date);
        }

        if (page <=0) {
            page = 1;
        }
        List<Predicate> predicates = prepareQuery(criteriaBuilder, root, name, category, price_min, price_max);

        if (!order.isEmpty() && !sort.isEmpty()) {
            String column = null;
            switch (sort) {
                case "name":
                    column = COLUMN_NAME;
                    break;
                case "category":
                    column = COLUMN_CATEGORY;
                    break;
                case "data":
                    column = COLUMN_DATE;
                    break;
                default:
                    column = COLUMN_PRICE;
                    break;
            }
            Order orderQuery;
            if (order.equals(SORT_DESC)) {
                orderQuery = criteriaBuilder.desc(root.get(column));
            } else {
                orderQuery = criteriaBuilder.asc(root.get(column));
            }
            query.orderBy(orderQuery);
        }

        query.where(predicates.toArray(new Predicate[0]));
            int firstResult = (page -1)* limit;
        return entityManager.createQuery(query)
                .setFirstResult(firstResult)
                .setMaxResults(limit)
                .getResultList();
    }

    @Override
    public ProductDTO getProductDTO() {
        return null;
    }

    @Override
    public long countActiveProducts(String name, String category,
                                    Float price_min, Float price_max) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
        Root<ProductEntity> root = query.from(ProductEntity.class);
        List<Predicate> predicates = prepareQuery(criteriaBuilder, root, name, category, price_min, price_max);
        query.select(criteriaBuilder.count(root)).where(predicates.toArray(new Predicate[0]));
    return entityManager.createQuery(query).getSingleResult();
    }

    private List<Predicate> prepareQuery(CriteriaBuilder criteriaBuilder,
                                         Root<ProductEntity> root,
                                         String name, String category,
                                         Float price_min, Float price_max) {
        List<Predicate> predicates = new ArrayList<>();
        if (name != null && !name.trim().equals(EMPTY)) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(COLUMN_NAME)), "%" + name.toLowerCase() + "%"));
        }
        if (category != null && !category.equals(EMPTY)) {
            categoryRepository.findByShortId(category)
                    .ifPresent(value -> predicates.add(criteriaBuilder.equal(root.get(COLUMN_CATEGORY), value)));

        }
        if (price_min != null) {
            predicates.add(criteriaBuilder.greaterThan(root.get(COLUMN_PRICE), price_min-0.01));
        }
        if (price_max != null) {
            predicates.add(criteriaBuilder.lessThan(root.get(COLUMN_PRICE), price_max+0.01));
        }
        predicates.add(criteriaBuilder.isTrue(root.get(COLUMN_ACTIVATE)));
        return predicates;
    }
}
