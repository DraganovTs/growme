package com.home.growme.produt.service.service.impl;

import com.home.growme.produt.service.exception.CategoryNotFoundException;
import com.home.growme.produt.service.exception.OwnerNotFoundException;
import com.home.growme.produt.service.exception.ProductNotFoundException;
import com.home.growme.produt.service.kafka.publisher.ProductEventPublisher;
import com.home.growme.produt.service.mapper.ProductMapper;
import com.home.growme.produt.service.model.dto.ProductRequestDTO;
import com.home.growme.produt.service.model.dto.ProductResponseDTO;
import com.home.growme.produt.service.model.dto.ProductResponseListDTO;
import com.home.growme.produt.service.model.entity.Category;
import com.home.growme.produt.service.model.entity.Owner;
import com.home.growme.produt.service.model.entity.Product;
import com.home.growme.produt.service.repository.CategoryRepository;
import com.home.growme.produt.service.repository.OwnerRepository;
import com.home.growme.produt.service.repository.ProductRepository;
import com.home.growme.produt.service.service.ProductService;
import com.home.growme.produt.service.specification.ProductSpecParams;
import com.home.growme.produt.service.specification.ProductSpecificationNameOwnerCategory;
import com.home.growme.produt.service.util.ProductValidator;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

    @Value("${pagination.page.size.default}")
    private int defaultPageSize;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ProductSpecificationNameOwnerCategory productSpecification;
    private final CategoryRepository categoryRepository;
    private final OwnerRepository ownerRepository;
    private final ProductEventPublisher productEventPublisher;
    private final ProductValidator productValidator;

    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper,
                              ProductSpecificationNameOwnerCategory productSpecification,
                              CategoryRepository categoryRepository, OwnerRepository ownerRepository,
                              ProductEventPublisher productEventPublisher, ProductValidator productValidator) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.productSpecification = productSpecification;
        this.categoryRepository = categoryRepository;
        this.ownerRepository = ownerRepository;
        this.productEventPublisher = productEventPublisher;
        this.productValidator = productValidator;
    }


    @Override
    public ProductResponseDTO getProductById(String productId) {
        return productRepository.findById(UUID.fromString(productId))
                .map(productMapper::mapProductToProductResponseDTO)
                .orElseThrow(() -> new ProductNotFoundException("Product with id " + productId + " not found"));
    }

    @Override
    public ProductResponseListDTO getAllProducts(ProductSpecParams specParams) {
        int pageIndex = Optional.ofNullable(specParams.getPageIndex()).orElse(1);
        int pageSize = Optional.ofNullable(specParams.getPageSize()).orElse(defaultPageSize);

        if (pageSize < 1) {
            pageSize = defaultPageSize;
        }

        Pageable paging = PageRequest.of(pageIndex - 1, pageSize);
        Page<Product> productPage = productRepository.findAll(productSpecification.getProducts(specParams), paging);


        return buildProductResponseListDTO(productPage, pageIndex, pageSize);
    }


    @Transactional
    @Override
    public ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO) {
        log.info("Creating new product: {}", productRequestDTO.getName());
        productValidator.validateProductRequest(productRequestDTO);


        Category category = categoryRepository.findByCategoryName(productRequestDTO.getCategoryName())
                .orElseThrow(() -> new CategoryNotFoundException("Category not found whit name: "
                        + productRequestDTO.getCategoryName()));

        Owner owner = ownerRepository.findById(productRequestDTO.getOwnerId())
                .orElseThrow(() -> new OwnerNotFoundException("Owner not found whit ID: "
                        + productRequestDTO.getOwnerId()));


        Product product = productMapper.mapProductRequestDTOToProduct(productRequestDTO, category, owner);

        if (productRequestDTO.getImageUrl() != null) {
            product.setImageUrl(productRequestDTO.getImageUrl());
        }


        productRepository.save(product);

        productEventPublisher.publishProductAssignment(
                productRequestDTO.getOwnerId().toString(),
                product.getProductId().toString());

        log.info("Product saved successfully: {}", product.getProductId());
        return productMapper.mapProductToProductResponseDTO(product);
    }

    @Transactional
    @Override
    public ProductResponseDTO updateProduct(String productId, ProductRequestDTO productRequestDTO) {
        log.info("Updating product with ID: {}", productId);

        Product product = productRepository.findById(UUID.fromString(productId))
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        product.setName(productRequestDTO.getName());
        product.setBrand(productRequestDTO.getBrand());
        product.setDescription(productRequestDTO.getDescription());
        product.setPrice(productRequestDTO.getPrice());
        product.setUnitsInStock(productRequestDTO.getUnitsInStock());
        product.setImageUrl(productRequestDTO.getImageUrl());

        productRepository.save(product);

        log.info("Product updated successfully: {}", productId);

        return productMapper.mapProductToProductResponseDTO(product);
    }

    @Override
    public void deleteProduct(String productId) {

    }

    @Override
    public List<ProductResponseDTO> getProductsByCategory(UUID categoryId) {
        return List.of();
    }

    @Override
    public ProductResponseListDTO getProductsByOwner(ProductSpecParams specParams) {
        int pageIndex = Optional.ofNullable(specParams.getPageIndex()).orElse(1);
        int pageSize = Optional.ofNullable(specParams.getPageSize()).orElse(defaultPageSize);

        if (pageSize < 1) {
            pageSize = defaultPageSize;
        }

        Pageable paging = PageRequest.of(pageIndex - 1, pageSize);

        // Create specification that filters by ownerId
        Specification<Product> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (specParams.getOwnerId() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("owner").get("ownerId"),
                        UUID.fromString(specParams.getOwnerId())
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Page<Product> productPage = productRepository.findAll(spec, paging);
        return buildProductResponseListDTO(productPage, pageIndex, pageSize);
    }

    private ProductResponseListDTO buildProductResponseListDTO(Page<Product> productPage, int pageIndex, int pageSize) {
        if (productPage.isEmpty()) {
            return null;
        }

        List<ProductResponseDTO> productResponseDTOList = productPage.getContent().stream()
                .map(productMapper::mapProductToProductResponseDTO)
                .collect(Collectors.toList());

        return ProductResponseListDTO.builder()
                .totalPages(productPage.getTotalPages())
                .totalCount(productPage.getTotalElements())
                .pageIndex(pageIndex)
                .pageSize(pageSize)
                .dataList(productResponseDTOList)
                .build();
    }


    private String sanitizeFilename(String name) {
        // 1. Replace problematic characters with underscore
        String sanitized = name.replaceAll("[^a-zA-Z0-9._-]", "_");

        // 2. Remove consecutive underscores
        sanitized = sanitized.replaceAll("_{2,}", "_");

        // 3. Remove leading/trailing underscores
        sanitized = sanitized.replaceAll("^_+|_+$", "");

        // 4. Ensure not empty
        if (sanitized.isEmpty()) {
            return "file_" + System.currentTimeMillis();
        }

        return sanitized;
    }
}
