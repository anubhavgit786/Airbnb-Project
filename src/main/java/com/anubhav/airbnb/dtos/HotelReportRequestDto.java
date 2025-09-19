package com.anubhav.airbnb.dtos;

import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelReportRequestDto
{
    private LocalDate startDate;
    private LocalDate endDate;

    @JsonSetter("startDate")
    public void setStartDate(LocalDate startDate)
    {
        this.startDate = (startDate == null) ? LocalDate.now().minusMonths(1) : startDate;
    }

    @JsonSetter("endDate")
    public void setEndDate(LocalDate endDate)
    {
        this.endDate = (endDate == null) ? LocalDate.now() : endDate;
    }
}
