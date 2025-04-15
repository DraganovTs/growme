package com.home.growme.produt.service.specification;

import com.home.growme.produt.service.model.entity.Product;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class ProductSpecificationNameOwnerCategory {

    public Specification<Product> getProducts(ProductSpecParams specParams) {
        return ((root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (specParams.getName() != null && !specParams.getName().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),
                        "%" + specParams.getName().toLowerCase() + "%"));
            }
            if (specParams.getOwnerId() != null && !specParams.getOwnerId().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("owner").get("ownerId"),
                        UUID.fromString(specParams.getOwnerId())));
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
                        query.orderBy(criteriaBuilder.desc(root.get("name")));
                        break;
                }
            }

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        });
    }
}
