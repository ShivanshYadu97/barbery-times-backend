package com.barbery_backend.service.impl;

import com.barbery_backend.entity.Barber;
import com.barbery_backend.entity.Shop;
import com.barbery_backend.repository.BarberRepository;
import com.barbery_backend.repository.ShopRepository;
import com.barbery_backend.service.BarberService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BarberServiceImpl implements BarberService {

    private final BarberRepository barberRepository;
    private final ShopRepository shopRepository;

    public BarberServiceImpl(
            BarberRepository barberRepository,
            ShopRepository shopRepository
    ) {
        this.barberRepository = barberRepository;
        this.shopRepository = shopRepository;
    }


    @Override
    public Barber createBarber(Long shopId, Barber barber) {

        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() ->
                        new RuntimeException("Shop not found")
                );

        barber.setShop(shop);

        // New barber always starts with shift OFF
        barber.setShiftActive(false);

        return barberRepository.save(barber);
    }


    @Override
    public List<Barber> getBarbersByShop(Long shopId) {

        return barberRepository.findByShopId(shopId);
    }


    @Override
    public Barber getBarberById(Long barberId) {

        return barberRepository.findById(barberId)
                .orElseThrow(() ->
                        new RuntimeException("Barber not found")
                );
    }


    @Override
    public Barber updateBarber(
            Long barberId,
            Barber barberDetails
    ) {

        Barber existingBarber = barberRepository.findById(barberId)
                .orElseThrow(() ->
                        new RuntimeException("Barber not found")
                );

        existingBarber.setName(barberDetails.getName());
        existingBarber.setEmail(barberDetails.getEmail());
        existingBarber.setPhone(barberDetails.getPhone());
        existingBarber.setActive(barberDetails.isActive());

        // If barber becomes inactive,
        // shift must also be OFF.
        if (!barberDetails.isActive()) {
            existingBarber.setShiftActive(false);
        }

        return barberRepository.save(existingBarber);
    }


    @Override
    public void deleteBarber(Long barberId) {

        Barber barber = barberRepository.findById(barberId)
                .orElseThrow(() ->
                        new RuntimeException("Barber not found")
                );

        // Soft delete / deactivate
        barber.setActive(false);

        // Inactive barber cannot remain on shift
        barber.setShiftActive(false);

        barberRepository.save(barber);
    }


    @Override
    public Barber updateShiftStatus(
            Long barberId,
            boolean shiftActive
    ) {

        Barber barber = barberRepository.findById(barberId)
                .orElseThrow(() ->
                        new RuntimeException("Barber not found")
                );

        // Inactive barber cannot start shift
        if (!barber.isActive() && shiftActive) {
            throw new RuntimeException(
                    "Inactive barber cannot start shift"
            );
        }

        // Shop must be open to start shift
        if (!barber.getShop().isOpen() && shiftActive) {
            throw new RuntimeException(
                    "Shop is closed"
            );
        }

        barber.setShiftActive(shiftActive);

        return barberRepository.save(barber);
    }


    @Override
    public List<Barber> getBarbersForCustomer(Long shopId) {

        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() ->
                        new RuntimeException("Shop not found")
                );

        // Shop closed → customer gets no barber list
        if (!shop.isOpen()) {
            return List.of();
        }

        // Shop open → return ALL barbers
        // Active + Inactive
        return barberRepository.findByShopId(shopId);
    }

}