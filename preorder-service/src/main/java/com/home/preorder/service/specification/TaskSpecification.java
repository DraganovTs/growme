package com.home.preorder.service.specification;

import com.home.preorder.service.model.entity.Task;
import com.home.preorder.service.model.enums.TaskStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TaskSpecification {
    public Specification<Task> getTasks(TaskSpecParams specParams) {
        return ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (specParams.getUserId() != null && !specParams.getUserId().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("userId"), specParams.getUserId()));
            }

            if (specParams.getStatus() != null && !specParams.getStatus().isEmpty()) {
                try {
                    TaskStatus status = TaskStatus.valueOf(specParams.getStatus().toUpperCase());
                    predicates.add(criteriaBuilder.equal(root.get("status"), status));
                } catch (IllegalArgumentException e) {

                }
            }

            if (specParams.getSearch() != null && !specParams.getSearch().isEmpty()) {
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("title")),
                                "%" + specParams.getSearch().toLowerCase() + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("description")),
                                "%" + specParams.getSearch().toLowerCase() + "%")
                ));
            }

            if (specParams.getSort() != null && !specParams.getSort().isEmpty()) {
                switch (specParams.getSort()) {
                    case "createdAtAsc":
                        query.orderBy(criteriaBuilder.asc(root.get("createdAt")));
                        break;
                    case "createdAtDesc":
                        query.orderBy(criteriaBuilder.desc(root.get("createdAt")));
                        break;
                    case "titleAsc":
                        query.orderBy(criteriaBuilder.asc(root.get("title")));
                        break;
                    case "titleDesc":
                        query.orderBy(criteriaBuilder.desc(root.get("title")));
                        break;
                    default:
                        query.orderBy(criteriaBuilder.desc(root.get("createdAt")));
                        break;
                }
            }

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        });
    }
}
