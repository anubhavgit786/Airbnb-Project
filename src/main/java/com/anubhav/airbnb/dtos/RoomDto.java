package com.anubhav.airbnb.dtos;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;


@Data
public class RoomDto
{
    private Long id;

    @NotBlank(message = "Room type is required")
    private String type;

    @NotNull(message = "Base price is required")
    @DecimalMin(value = "0.01", message = "Base price must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Base price must be a valid amount with up to 2 decimal places")
    private BigDecimal basePrice;

    @NotNull(message = "Total count is required")
    @Min(value = 1, message = "Total count must be at least 1")
    private Integer totalCount;

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;

    @NotEmpty(message = "At least one photo is required")
    private List<
            @NotBlank(message = "Photo URL cannot be blank")
            @Pattern(regexp = "^(http|https)://.*$", message = "Photo must be a valid URL")
                    String> photos;

    @NotEmpty(message = "At least one amenity is required")
    private List<
            @NotBlank(message = "Amenity cannot be blank")
                    String> amenities;
}