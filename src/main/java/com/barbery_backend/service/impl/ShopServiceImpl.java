package com.barbery_backend.service.impl;

import com.barbery_backend.entity.Barber;
import com.barbery_backend.entity.Shop;
import com.barbery_backend.repository.BarberRepository;
import com.barbery_backend.repository.ShopRepository;
import com.barbery_backend.service.ShopService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShopServiceImpl implements ShopService {

    private final ShopRepository shopRepository;
    private final BarberRepository barberRepository;

    public ShopServiceImpl(
            ShopRepository shopRepository,
            BarberRepository barberRepository
    ) {
        this.shopRepository = shopRepository;
        this.barberRepository = barberRepository;
    }

    @Override
    public List<Shop> getAllShops() {
        return shopRepository.findAll();
    }

    @Override
    public Shop getShopById(Long id) {
        return shopRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Shop not found with id: " + id)
                );
    }

    @Override
    public Shop createShop(Shop shop) {
        return shopRepository.save(shop);
    }

    @Override
    public Shop updateShop(Long id, Shop updatedShop) {

        Shop existingShop = getShopById(id);

        existingShop.setName(updatedShop.getName());
        existingShop.setAddress(updatedShop.getAddress());
        existingShop.setOpen(updatedShop.isOpen());

        // If shop is closed,
        // all barbers' shifts must be OFF.
        if (!updatedShop.isOpen()) {

            List<Barber> barbers =
                    barberRepository.findByShopId(id);

            for (Barber barber : barbers) {
                barber.setShiftActive(false);
            }

            barberRepository.saveAll(barbers);
        }

        return shopRepository.save(existingShop);
    }

    @Override
    public void deleteShop(Long id) {

        Shop existingShop = getShopById(id);

        shopRepository.delete(existingShop);
    }


    @Override
    public void updateShopStatus(Long id, boolean isOpen) {

        Shop shop = getShopById(id);

        shop.setOpen(isOpen);

        // Shop CLOSED → all barber shifts OFF
        if (!isOpen) {

            List<Barber> barbers =
                    barberRepository.findByShopId(id);

            for (Barber barber : barbers) {
                barber.setShiftActive(false);
            }

            barberRepository.saveAll(barbers);
        }

        shopRepository.save(shop);
    }

}