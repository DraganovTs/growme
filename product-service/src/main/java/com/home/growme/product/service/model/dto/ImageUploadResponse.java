package com.home.growme.product.service.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
/**
 * Response payload after successful image upload.
 * Contains all necessary information to reference the uploaded image.
 */
@Schema(
        name = "ImageUploadResult",
        description = """
        Confirmation of successful image upload.
        Provides identifiers and access information.
        """
)
@Data
@AllArgsConstructor
public class ImageUploadResponse {
    @Schema(
            description = "System-generated unique filename",
            example = "prod_67890.png"
    )
    private String filename;
    @Schema(
            description = "Original filename from upload",
            example = "product_image.png"
    )
    private String originalName;
    @Schema(
            description = "Access path for the uploaded image",
            example = "/images/prod_67890.png",
            format = "uri-reference"
    )
    private String url;
}
