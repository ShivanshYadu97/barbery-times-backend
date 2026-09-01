package com.barbery_backend.service.impl;

import com.barbery_backend.entity.Shop;
import com.barbery_backend.repository.ShopRepository;
import com.barbery_backend.service.ShopService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShopServiceImpl implements ShopService {

    private final ShopRepository shopRepository;

    public ShopServiceImpl(ShopRepository shopRepository) {
        this.shopRepository = shopRepository;
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

        shopRepository.save(shop);
    }

}