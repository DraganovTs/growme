package com.home.growme.produt.service.specification;

import com.home.growme.produt.service.model.entity.Category;
import com.home.growme.produt.service.model.entity.Product;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
                predicates.add(criteriaBuilder.equal(root.get("owner").get("ownerId"),
                        UUID.fromString(specParams.getOwner())));
            }
            if (specParams.getCategoryId() != null && !specParams.getCategoryId().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("category").get("categoryId"),
                        UUID.fromString(specParams.getCategoryId())));
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
