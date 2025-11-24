package com.codingShuttle.projects.AirbnbApp.service;

import com.codingShuttle.projects.AirbnbApp.entity.Hotel;
import com.codingShuttle.projects.AirbnbApp.entity.HotelMinPrice;
import com.codingShuttle.projects.AirbnbApp.entity.Inventory;
import com.codingShuttle.projects.AirbnbApp.repository.HotelMinPriceRepository;
import com.codingShuttle.projects.AirbnbApp.repository.HotelRepository;
import com.codingShuttle.projects.AirbnbApp.repository.InventoryRepository;
import com.codingShuttle.projects.AirbnbApp.startegy.PricingService;
import com.codingShuttle.projects.AirbnbApp.startegy.PricingStrategy;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.PropertySource;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

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
public class PricingUpdateService {



    private final HotelRepository hotelRepository;
    private final InventoryRepository inventoryRepository;
    private final HotelMinPriceRepository hotelMinPriceRepository;
    private  final PricingService  pricingService;

    //scheduler  to update the inventory and Hotelmin price tables every hour
//    @Scheduled(cron =  "*/5 * * * * *")
    @Scheduled(cron  = "0 0 * * * *")
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
            hotelPage.getContent().forEach(this::updateHotelPrice);

            page++;

        }

    }

    private void updateHotelPrice(Hotel hotel)
    {
        log.info("Updating hotel prices for hotel id : {}" , hotel.getId());
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusYears(1);

        List<Inventory> inventoryList = inventoryRepository.findAllByHotelAndDateBetween(hotel , startDate , endDate);

        updateInventory(inventoryList);

        updateHotelMinPrice(inventoryList, hotel ,  startDate , endDate);

    }

    private void updateHotelMinPrice(List<Inventory> inventoryList, Hotel hotel, LocalDate startDate, LocalDate endDate) {
        //compute the min price per day for the hotel
        Map<LocalDate , BigDecimal> dailyMinPrices = inventoryList.stream()
                .collect(Collectors.groupingBy(
                        Inventory::getDate,
                        Collectors.mapping(Inventory::getPrice , Collectors.minBy(Comparator.naturalOrder()))
                ))
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey , e -> e.getValue().orElse(BigDecimal.ZERO)));

        //prepare HotelPrice entities in bulk
        List<HotelMinPrice> hotelPrices = new ArrayList<>();
        dailyMinPrices.forEach((date , price) ->{
            HotelMinPrice hotelPrice = hotelMinPriceRepository.findByHotelAndDate(hotel , date).
                    orElse(new HotelMinPrice(hotel, date));

            hotelPrice.setPrice(price);
            hotelPrices.add(hotelPrice);

        });

        hotelMinPriceRepository.saveAll(hotelPrices);





    }


    private void updateInventory(List<Inventory>  inventoryList)
    {

        inventoryList.forEach(inventory -> {
            BigDecimal dynamicPrice =  pricingService.calculateDynamicPricing(inventory);
            inventory.setPrice(dynamicPrice);

        });

        //saving all the inventory list
        inventoryRepository.saveAll(inventoryList);

    }


}
