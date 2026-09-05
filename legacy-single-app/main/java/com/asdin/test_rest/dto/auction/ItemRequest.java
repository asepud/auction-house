package com.asdin.test_rest.dto.auction;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

/** Input to create an auction listing. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequest {
    @NotBlank
    @Size(max = 180)
    private String title;
    @NotBlank
    @Size(max = 3000)
    private String description;
    @NotNull
    private Long categoryId;
    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal startingPrice;
    @NotNull
    private Instant startTime;
    @NotNull
    private Instant endTime;
    @Size(max = 500)
    private String imageUrl;
}
