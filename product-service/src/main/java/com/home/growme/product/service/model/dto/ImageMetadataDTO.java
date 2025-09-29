package com.home.growme.product.service.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
/**
 * Contains technical metadata about an uploaded image.
 * Used for image management and processing.
 */
@Schema(
        name = "ImageMetadata",
        description = """
        Technical details about stored images.
        Used by administrators and for system processing.
        """
)
@Data
@AllArgsConstructor
public class ImageMetadataDTO {
    @Schema(
            description = "Access URL for the image",
            example = "https://storage.growme.com/images/prod_12345.jpg",
            format = "uri"
    )
    private String url;
    @Schema(
            description = "System-generated filename",
            example = "prod_12345.jpg"
    )
    private String filename;
    @Schema(
            description = "File size in bytes",
            example = "245678"
    )
    private long size;
    @Schema(
            description = "MIME type of the image",
            example = "image/jpeg"
    )
    private String contentType;
}
