package com.home.growme.produt.service.repository;

import com.home.growme.produt.service.model.dto.ProductResponseDTO;
import com.home.growme.produt.service.model.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {

    Page<Product> findAll(Specification<Product> spec, Pageable pageable);
    List<Product> findAll(Specification<Product> spec);

    List<Product> findByCategoryCategoryId(UUID categoryId);

    Page<Product> findAllByCategory_CategoryId(UUID categoryCategoryId, Pageable pageable);
}
