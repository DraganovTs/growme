package com.home.preorder.service.specification;

import com.home.preorder.service.model.entity.Bid;
import com.home.preorder.service.model.enums.BidStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class BidSpecification {
    public Specification<Bid> getBids(BidSpecParams specParams) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filter by user (bidder)
            if (specParams.getUserId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("userId"), specParams.getUserId()));
            }

            // Filter by task
            if (specParams.getTaskId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("task").get("taskId"),
                        UUID.fromString(specParams.getTaskId())));
            }

            // Filter by status
            if (specParams.getStatus() != null) {
                try {
                    BidStatus status = BidStatus.valueOf(specParams.getStatus().toUpperCase());
                    predicates.add(criteriaBuilder.equal(root.get("status"), status));
                } catch (IllegalArgumentException ignored) {

                }
            }

            // Sorting
            if (specParams.getSort() != null) {
                switch (specParams.getSort().toLowerCase()) {
                    case "priceasc":
                        query.orderBy(criteriaBuilder.asc(root.get("price")));
                        break;
                    case "pricedesc":
                        query.orderBy(criteriaBuilder.desc(root.get("price")));
                        break;
                    case "harvestdateasc":
                        query.orderBy(criteriaBuilder.asc(root.get("proposedHarvestDate")));
                        break;
                    case "harvestdatedesc":
                        query.orderBy(criteriaBuilder.desc(root.get("proposedHarvestDate")));
                        break;
                    default:
                        query.orderBy(criteriaBuilder.desc(root.get("createdAt")));
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
