package com.asdin.test_rest.dto.auction;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.Instant;

/** Mutable listing fields before bidding begins. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemUpdateRequest {
    @NotBlank
    @Size(max = 180)
    private String title;
    @NotBlank
    @Size(max = 3000)
    private String description;
    @NotNull
    private Long categoryId;
    @NotNull
    private Instant startTime;
    @NotNull
    private Instant endTime;
    @Size(max = 500)
    private String imageUrl;
}
