package com.anubhav.airbnb.strategy;


import com.anubhav.airbnb.models.Inventory;
import lombok.RequiredArgsConstructor;
import java.math.BigDecimal;

@RequiredArgsConstructor
public class HolidayPricingStrategy implements PricingStrategy
{

    private final PricingStrategy wrapped;

    @Override
    public BigDecimal calculatePrice(Inventory inventory)
    {
        BigDecimal price = wrapped.calculatePrice(inventory);
        boolean isTodayHoliday = true;
        if (isTodayHoliday)
        {
            price = price.multiply(BigDecimal.valueOf(1.25));
        }
        return price;
    }
}
