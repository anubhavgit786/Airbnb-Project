package com.anubhav.airbnb.services;


import com.anubhav.airbnb.models.Hotel;
import com.anubhav.airbnb.models.HotelMinPrice;
import com.anubhav.airbnb.models.Inventory;
import com.anubhav.airbnb.repositories.HotelMinPriceRepository;
import com.anubhav.airbnb.repositories.HotelRepository;
import com.anubhav.airbnb.repositories.InventoryRepository;
import com.anubhav.airbnb.strategy.PricingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PricingUpdateService
{

    // Scheduler to update the inventory and HotelMinPrice tables every hour

    private final HotelRepository hotelRepository;
    private final InventoryRepository inventoryRepository;
    private final HotelMinPriceRepository hotelMinPriceRepository;
    private final PricingService pricingService;

    @Scheduled(cron = "*/5 * * * * *")
    //@Scheduled(cron = "0 0 * * * *")
    public void updatePrices()
    {
        int page = 0;
        int batchSize = 100;

        while(true)
        {
            Page<Hotel> hotelPage = hotelRepository.findAll(PageRequest.of(page, batchSize));
            if(hotelPage.isEmpty())
            {
                break;
            }

            hotelPage.getContent().parallelStream().forEach(this::updateHotelPrices);

            page++;
        }
    }

    private void updateHotelPrices(Hotel hotel)
    {
        log.info("Updating hotel prices for hotel ID: {}", hotel.getId());
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusYears(1);

        List<Inventory> inventoryList = inventoryRepository.findByHotelAndDateBetween(hotel, startDate, endDate);

        updateInventoryPrices(inventoryList);

        updateHotelMinPrice(hotel, inventoryList);
    }

    private void updateHotelMinPrice(Hotel hotel, List<Inventory> inventoryList)
    {
        // Group inventory by date and find the minimum price for each date
        Map<LocalDate, BigDecimal> minPricesByDate = inventoryList
                .stream()
                .collect(Collectors.groupingBy(
                        Inventory::getDate,
                        Collectors.reducing(BigDecimal.ZERO, Inventory::getPrice, BigDecimal::min)
                ));

        // Create or update HotelMinPrice entities
        List<HotelMinPrice> hotelMinPrices = minPricesByDate.entrySet().stream()
                .map(entry ->
                {
                    LocalDate date = entry.getKey();
                    BigDecimal minPrice = entry.getValue();
                    HotelMinPrice hotelPrice = hotelMinPriceRepository.findByHotelAndDate(hotel, date)
                            .orElse(new HotelMinPrice(hotel, date));
                    hotelPrice.setPrice(minPrice);
                    return hotelPrice;
                })
                .collect(Collectors.toList());

        // Save all HotelMinPrice entities in bulk
        hotelMinPriceRepository.saveAll(hotelMinPrices);
    }

    private void updateInventoryPrices(List<Inventory> inventoryList)
    {
        inventoryList.forEach(inventory ->
        {
            BigDecimal dynamicPrice = pricingService.calculateDynamicPricing(inventory);
            inventory.setPrice(dynamicPrice);
        });
        inventoryRepository.saveAll(inventoryList);
    }
}
