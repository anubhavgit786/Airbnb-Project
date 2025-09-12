package com.anubhav.airbnb.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;


import java.util.List;

@Data
public class HotelDto
{
    private Long id;

    @NotBlank(message = "Hotel name is required")
    private String name;

    @NotBlank(message = "City is required")
    private String city;

    @NotEmpty(message = "At least one photo is required")
    private List<
            @NotBlank(message = "Photo URL cannot be blank")
            @Pattern(regexp = "^(http|https)://.*$", message = "Photo must be a valid URL")
            String> photos;

    @NotEmpty(message = "At least one amenity is required")
    private List<
            @NotBlank(message = "Amenity cannot be blank")
            String> amenities;

    @Valid
    @NotNull(message = "Contact info is required")
    private HotelContactInfoDto contactInfo;
}