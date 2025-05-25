package com.home.user.service.model.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "AccountStatus",
        description = """
        Represents the lifecycle state of a user account.
        
        Possible values:
        - `ACTIVE`: Account is fully operational
        - `INACTIVE`: Account is temporarily disabled
        - `SUSPENDED`: Account suspended due to policy violation
        - `PENDING`: Awaiting email verification or admin approval
        - `DELETED`: Account marked for deletion (soft delete)
        """,
        example = "ACTIVE"
)
public enum AccountStatus {
    @Schema(description = "Account is fully operational")
    ACTIVE,

    @Schema(description = "Account is temporarily disabled")
    INACTIVE,

    @Schema(description = "Account suspended due to policy violation")
    SUSPENDED,

    @Schema(description = "Awaiting email verification or admin approval")
    PENDING,

    @Schema(description = "Account marked for deletion (soft delete)")
    DELETED
}
