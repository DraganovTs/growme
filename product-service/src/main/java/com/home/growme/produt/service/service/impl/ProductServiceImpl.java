package com.home.growme.produt.service.service.impl;

import com.home.growme.produt.service.exception.ProductNotFoundException;
import com.home.growme.produt.service.mapper.ProductMapper;
import com.home.growme.produt.service.model.dto.ProductResponseDTO;
import com.home.growme.produt.service.model.dto.ProductResponseListDTO;
import com.home.growme.produt.service.model.entity.Product;
import com.home.growme.produt.service.repository.ProductRepository;
import com.home.growme.produt.service.service.ProductService;
import com.home.growme.produt.service.specification.ProductSpecParams;
import com.home.growme.produt.service.specification.ProductSpecificationTitleOwnerCategory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Value("${pagination.page.size.default}")
    private int defaultPageSize;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ProductSpecificationTitleOwnerCategory productSpecification;


    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper, ProductSpecificationTitleOwnerCategory productSpecification) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.productSpecification = productSpecification;
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


    @Override
    public Product createProduct(ProductResponseDTO productResponseDto) {
        return null;
    }

    @Override
    public Product updateProduct(UUID productId, ProductResponseDTO productResponseDto) {
        return null;
    }

    @Override
    public void deleteProduct(UUID productId) {

    }

    @Override
    public List<ProductResponseDTO> getProductsByCategory(UUID categoryId) {
        return List.of();
    }

    @Override
    public List<ProductResponseDTO> getProductsByOwner(UUID ownerId) {
        return List.of();
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
