package com.home.growme.produt.service.specification;

import com.home.growme.produt.service.model.entity.Category;
import com.home.growme.produt.service.model.entity.Product;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProductSpecificationTitleOwnerCategory {

    public Specification<Product> getProducts(ProductSpecParams specParams) {
        return ((root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (specParams.getTitle() != null && !specParams.getTitle().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("title")),
                        "%" + specParams.getTitle().toLowerCase() + "%"));
            }
            if (specParams.getOwner() != null && !specParams.getOwner().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("owner")),
                        "%" + specParams.getOwner().toLowerCase() + "%"));
            }
            if (specParams.getCategoryId() != 0) {
                predicates.add(criteriaBuilder.equal(root.get("category"),
                        new Category(String.valueOf(specParams.getCategoryId())))
                );
            }
            if (specParams.getSort() != null && !specParams.getSort().isEmpty()) {
                switch (specParams.getSort()) {
                    case "priceAsc":
                        query.orderBy(criteriaBuilder.asc(root.get("price")));
                        break;
                    case "priceDesc":
                        query.orderBy(criteriaBuilder.desc(root.get("price")));
                        break;
                    default:
                        query.orderBy(criteriaBuilder.desc(root.get("title")));
                        break;
                }
            }

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        });
    }
}
