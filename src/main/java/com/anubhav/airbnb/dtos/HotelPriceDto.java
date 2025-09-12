package com.anubhav.airbnb.dtos;

import com.anubhav.airbnb.models.Hotel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelPriceDto
{
    private Hotel hotel;
    private Double price;
}