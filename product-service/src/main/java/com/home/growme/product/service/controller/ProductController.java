package com.home.growme.product.service.controller;

import com.home.growme.common.module.dto.BasketItemDTO;
import com.home.growme.common.module.dto.ProductInfo;
import com.home.growme.common.module.dto.ProductValidationResult;
import com.home.growme.common.module.exceptions.ErrorResponseDTO;
import com.home.growme.product.service.model.dto.*;
import com.home.growme.product.service.service.ImageService;
import com.home.growme.product.service.service.ProductService;
import com.home.growme.product.service.specification.ProductSpecParams;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * REST controller for managing products in the GrowMe platform.
 * Provides endpoints for product creation, update, retrieval, and deletion.
 */
@Tag(
        name = "Product Management API",
        description = """
                    Complete set of REST APIs for product management in the GrowMe platform.
                    Includes operations for:
                    - Creating new products
                    - Updating existing products
                    - Deleting products (soft delete)
                    - Retrieving product details and lists
                
                    Requires ADMIN or appropriate permissions for write operations.
                """
)
@Slf4j
@RestController
@RequestMapping(value = "/api/products", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductController {

    @Value("${app.upload.dir}")
    private String uploadDir;

    private final ProductService productService;
    private final ImageService imageService;

    public ProductController(ProductService productService, ImageService imageService) {
        this.productService = productService;
        this.imageService = imageService;
    }

    @Operation(
            summary = "Get product details by ID",
            description = "Retrieves detailed information about a product by its UUID.",
            operationId = "getProductById"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product details retrieved", content = @Content(schema = @Schema(implementation = ProductResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable String productId) {
        log.info("Fetching product with ID: {}", productId);
        ProductResponseDTO productResponseDTO = productService.getProductById(productId);
        return ResponseEntity.ok(productResponseDTO);
    }

    @Operation(
            summary = "Get list of all products",
            description = "Retrieves a list of all products available in the system.",
            operationId = "listProducts"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of products retrieved", content = @Content(schema = @Schema(implementation = ProductResponseListDTO.class, type = "array")))
    })
    @GetMapping
    public ResponseEntity<ProductResponseListDTO> getProducts(ProductSpecParams request) {
        log.info("Fetching product list with params: {}", request);
        ProductResponseListDTO productResponseDTOList = productService.getAllProducts(request);
        return ResponseEntity.ok(productResponseDTOList);
    }


    @Operation(
            summary = "Get products by owner",
            description = "Retrieve all products for the currently authenticated owner.",
            operationId = "getProductsByOwner"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Owner's products retrieved", content = @Content(schema = @Schema(implementation = ProductResponseListDTO.class)))
    })
    @GetMapping("/seller")
    public ResponseEntity<ProductResponseListDTO> getProductsByOwner(@ModelAttribute ProductSpecParams specParams) {

        ProductResponseListDTO productResponseDTOList = productService.getProductsByOwner(specParams);
        return ResponseEntity.ok(productResponseDTOList);
    }


    @GetMapping("/category/{categoryId}")
    public ResponseEntity<CategoryWithProductsDTO> getProductsByCategory(@ModelAttribute ProductSpecParams specParams) {

        CategoryWithProductsDTO categoryWithProductsDTO = productService.getProductsByCategory(specParams);
        return ResponseEntity.ok(categoryWithProductsDTO);
    }

    @Operation(
            summary = "Get all categories with products",
            description = "Returns a list of categories along with the products assigned to them.",
            operationId = "getCategoriesWithProducts"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of categories with products retrieved successfully",
                    content = @Content(schema = @Schema(implementation = CategoryWithProductsDTO.class))),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/categories-with-products")
    public ResponseEntity<List<CategoryWithProductsDTO>> findAllWithProducts() {
        List<CategoryWithProductsDTO> categoryWithProductsDTOList = productService.getCategoriesWithProducts();
        return ResponseEntity.ok(categoryWithProductsDTOList);
    }

    @Operation(
            summary = "Create a new product",
            description = """
                        Creates a new product in the system with the provided details.
                        Accessible only to authorized users with product creation permissions.
                    """,
            operationId = "createProduct"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Product created successfully", content = @Content(schema = @Schema(implementation = ProductResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid product data", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Product with the same name already exists", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(@Valid @RequestBody ProductRequestDTO productRequestDTO) {
        log.info("Creating new product: {}", productRequestDTO);
        ProductResponseDTO responseDTO = productService.createProduct(productRequestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @Operation(
            summary = "Update an existing product",
            description = """
                        Updates product information by product UUID.
                        Only authorized users can perform this operation.
                    """,
            operationId = "updateProduct"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Product updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid update data", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Product data conflict", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable String id,
            @Valid @RequestBody ProductRequestDTO productRequestDTO
    ) {
        log.info("Updating product with ID: {} ", id);
        ProductResponseDTO productResponseDTO = productService.updateProduct(id, productRequestDTO);
        return ResponseEntity.ok(productResponseDTO);
    }

    @Operation(
            summary = "Soft delete a product",
            description = "Marks the product as deleted without physically removing it from the database.",
            operationId = "deleteProduct"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Product deleted"),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @DeleteMapping("/{productId}/{userId}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable String productId,
            @PathVariable String userId
    ) {
        productService.deleteProduct(productId, userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Validate products in basket",
            description = "Validate a list of basket items before checkout.",
            operationId = "validateProducts"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Validation results returned", content = @Content(schema = @Schema(implementation = ProductValidationResult.class)))
    })
    @PostMapping("/validate")
    public ResponseEntity<List<ProductValidationResult>> validateProducts(@RequestHeader("grow-me-correlation-id")
                                                                          String correlationId,
                                                                          @Valid @RequestBody List<BasketItemDTO> basketItems) {
        log.debug("grow-me-correlation-id found: {}", correlationId);
        List<ProductValidationResult> results = productService.validateProducts(basketItems);
        return ResponseEntity.ok(results);
    }

    @Operation(
            summary = "Get product info",
            description = "Retrieve summarized product information by product ID.",
            operationId = "getProductInfo"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product info retrieved", content = @Content(schema = @Schema(implementation = ProductInfo.class)))
    })
    @GetMapping("productinfo/{productId}")
    public ResponseEntity<ProductInfo> getProductInfo(@RequestHeader("grow-me-correlation-id")
                                                      String correlationId,
                                                      @PathVariable String productId) {
        log.debug("grow-me-correlation-id found: {}", correlationId);
        ProductInfo productInfo = productService.getProductInfo(productId);
        return ResponseEntity.ok(productInfo);
    }

    @Operation(
            summary = "Upload product image",
            description = "Upload an image file associated with a product.",
            operationId = "uploadProductImage"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Image uploaded successfully")
    })
    @PostMapping("/upload-image")
    public ResponseEntity<ImageUploadResponse> uploadImage(
            @RequestParam("file") MultipartFile file) {
        String filename = imageService.uploadImage(file);
        return ResponseEntity.ok(new ImageUploadResponse(
                filename,          // ← Must include this
                file.getOriginalFilename(),
                "/images/" + filename
        ));
    }

    @Operation(
            summary = "Get all uploaded image filenames",
            description = "Retrieve a list of all image filenames available.",
            operationId = "getAllImages"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of image filenames returned")
    })
    @GetMapping("/images")
    public ResponseEntity<List<String>> getAllImages() {
        return ResponseEntity.ok(imageService.getAllImages());
    }

    @Operation(
            summary = "Get image by filename",
            description = "Retrieve an image file by filename.",
            operationId = "getImageByFilename"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Image file returned"),
            @ApiResponse(responseCode = "404", description = "Image not found", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/images/{filename:.+}")
    public ResponseEntity<byte[]> getImage(@PathVariable String filename) {
        try {
            Path file = Paths.get(uploadDir).resolve(filename);
            byte[] imageBytes = Files.readAllBytes(file);

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(imageBytes);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Get recent images",
            description = "Returns a list of up to 5 most recently uploaded images for display purposes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of recent images retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ImageDisplayDTO.class)))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/images/recent")
    public ResponseEntity<List<ImageDisplayDTO>> getRecentImages() {
        return ResponseEntity.ok(imageService.getRecentImagesForDisplay());
    }

    @GetMapping("/test")
    public String test() {
        return "Product service works fine";
    }

}
