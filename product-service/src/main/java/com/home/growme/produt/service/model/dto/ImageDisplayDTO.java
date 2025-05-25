package com.home.growme.produt.service.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
/**
 * Represents an image ready for display in the UI.
 * Contains all necessary information to render the image.
 */
@Schema(
        name = "DisplayImage",
        description = """
        Image information formatted for frontend display.
        Includes both technical and presentation properties.
        """
)
@Data
@AllArgsConstructor
public class ImageDisplayDTO {
    @Schema(
            description = "System-generated filename",
            example = "prod_12345.jpg"
    )
    private String filename;
    @Schema(
            description = "User-friendly display name",
            example = "Product Preview",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String displayName;
    @Schema(
            description = "Full URL to access the image",
            example = "https://cdn.growme.com/images/prod_12345.jpg",
            format = "uri"
    )
    private String url;
}
