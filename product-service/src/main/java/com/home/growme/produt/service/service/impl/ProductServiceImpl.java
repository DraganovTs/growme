package com.home.growme.produt.service.service.impl;

import com.home.growme.common.module.dto.BasketItemDTO;
import com.home.growme.common.module.dto.OrderItemDTO;
import com.home.growme.common.module.dto.ProductInfo;
import com.home.growme.common.module.dto.ProductValidationResult;
import com.home.growme.common.module.events.OrderCompletedEvent;
import com.home.growme.common.module.exceptions.CategoryNotFoundException;
import com.home.growme.produt.service.exception.OwnerNotFoundException;
import com.home.growme.produt.service.exception.ProductNotFoundException;
import com.home.growme.produt.service.exception.StockInsufficientException;
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
import com.home.growme.produt.service.service.EventPublisherService;
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
    private final EventPublisherService eventPublisherService;
    private final ProductValidator productValidator;

    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper,
                              ProductSpecificationNameOwnerCategory productSpecification,
                              CategoryRepository categoryRepository, OwnerRepository ownerRepository,
                              EventPublisherService eventPublisherService, ProductValidator productValidator) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.productSpecification = productSpecification;
        this.categoryRepository = categoryRepository;
        this.ownerRepository = ownerRepository;
        this.eventPublisherService = eventPublisherService;
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

        eventPublisherService.publishProductAssignment(
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
    public void deleteProduct(String productId, String ownerId) {
        productRepository.deleteById(UUID.fromString(productId));
        log.info("Product deleted successfully: {}", productId);
        eventPublisherService.publishProductDeletion(productId, ownerId);
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

    @Override
    public List<ProductValidationResult> validateProducts(List<BasketItemDTO> basketItems) {
        return basketItems.stream()
                .map(this::validateItem)
                .collect(Collectors.toList());
    }

    @Override
    public ProductInfo getProductInfo(String productId) {
        Product product = productRepository.findById(UUID.fromString(productId))
                .orElseThrow(() -> new ProductNotFoundException("Product whit ID " + productId + " is not found"));

        return ProductInfo.builder()
                .id(product.getProductId().toString())
                .name(product.getName())
                .imageUrl(product.getImageUrl())
                .price(product.getPrice())
                .build();
    }

    @Override
    @Transactional
    public void completeOrder(OrderCompletedEvent event) {
        List<OrderItemDTO> items = event.getItems();
        List<OrderItemDTO> itemsToRemoveFromUser = new ArrayList<>();

        items.forEach(item -> {
            Product product = productRepository.findById(UUID.fromString(item.getProductId()))
                    .orElseThrow(() -> new ProductNotFoundException("Product not found whit Id: " + item.getProductId()));

            try {
                product.reduceStock(item.getQuantity());
                if (product.getUnitsInStock() == 0) {
                    eventPublisherService.publishProductDeletion(product.getOwner().getOwnerId().toString()
                            , product.getProductId().toString());
                }
                productRepository.save(product);
                log.debug("Reduced stock for product {} by {}", item.getProductId(), item.getQuantity());

            } catch (IllegalStateException e) {
                throw new StockInsufficientException(String.format("Insufficient stock for product %s. Requested: %d, Available: %d",
                        item.getProductId(), item.getQuantity(), product.getUnitsInStock()));
            }
        });
        log.info("Completed order processing for order ID: {}", event.getOrderId());
    }

    private ProductValidationResult validateItem(BasketItemDTO item) {
        Optional<Product> productOptional = productRepository.findById(item.getProductId());

        if (productOptional.isEmpty()) {
            return new ProductValidationResult(item.getProductId(), false, "Product not found");
        }

        Product product = productOptional.get();

        if (product.getPrice().compareTo(item.getPrice()) != 0) {
            return new ProductValidationResult(item.getProductId(), false, "Price mismatch");
        }

        if (product.getUnitsInStock() < item.getQuantity()) {
            return new ProductValidationResult(item.getProductId(), false, "Insufficient stock");
        }
        return new ProductValidationResult(item.getProductId(), true, null);
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


}
