package com.barbery_backend.service.impl;

import com.barbery_backend.dto.BarberMenuRequest;
import com.barbery_backend.entity.BarberMenu;
import com.barbery_backend.entity.Shop;
import com.barbery_backend.repository.BarberMenuRepository;
import com.barbery_backend.repository.ShopRepository;
import com.barbery_backend.service.BarberMenuService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BarberMenuServiceImpl implements BarberMenuService {

    private final BarberMenuRepository barberMenuRepository;
    private final ShopRepository shopRepository;

    public BarberMenuServiceImpl(
            BarberMenuRepository barberMenuRepository,
            ShopRepository shopRepository
    ) {
        this.barberMenuRepository = barberMenuRepository;
        this.shopRepository = shopRepository;
    }

    @Override
    public List<BarberMenu> getServicesByShop(Long shopId) {

        if (!shopRepository.existsById(shopId)) {
            throw new RuntimeException("Shop not found with id: " + shopId);
        }

        return barberMenuRepository.findByShopId(shopId);
    }

    @Override
    public List<BarberMenu> getActiveServicesByShop(Long shopId) {

        if (!shopRepository.existsById(shopId)) {
            throw new RuntimeException("Shop not found with id: " + shopId);
        }

        return barberMenuRepository.findByShopIdAndIsActiveTrue(shopId);
    }

    @Override
    public BarberMenu getServiceById(Long id) {

        return barberMenuRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Service not found with id: " + id)
                );
    }

    @Override
    public BarberMenu createService(BarberMenuRequest request) {

        Shop shop = shopRepository
                .findById(request.getShopId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Shop not found with id: " + request.getShopId()
                        )
                );

        BarberMenu barberMenu = BarberMenu.builder()
                .name(request.getName())
                .description(request.getDescription())
                .durationMinutes(request.getDurationMinutes())
                .price(request.getPrice())
                .isActive(
                        request.getActive() != null
                                ? request.getActive()
                                : true
                )
                .shop(shop)
                .build();

        return barberMenuRepository.save(barberMenu);
    }

    @Override
    public BarberMenu updateService(Long id, BarberMenuRequest request) {

        BarberMenu existingService = getServiceById(id);

        existingService.setName(request.getName());
        existingService.setDescription(request.getDescription());
        existingService.setDurationMinutes(request.getDurationMinutes());
        existingService.setPrice(request.getPrice());

        if (request.getActive() != null) {
            existingService.setActive(request.getActive());
        }

        if (request.getShopId() != null) {

            Shop shop = shopRepository
                    .findById(request.getShopId())
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Shop not found with id: " + request.getShopId()
                            )
                    );

            existingService.setShop(shop);
        }

        return barberMenuRepository.save(existingService);
    }

    @Override
    public void deleteService(Long id) {

        BarberMenu existingService = getServiceById(id);

        // Soft delete: database se record delete nahi hoga
        existingService.setActive(false);

        barberMenuRepository.save(existingService);
    }
}