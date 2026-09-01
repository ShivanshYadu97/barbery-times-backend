package com.barbery_backend.service;

import com.barbery_backend.entity.Shop;

import java.util.List;

public interface ShopService {

    List<Shop> getAllShops();

    Shop getShopById(Long id);

    Shop createShop(Shop shop);

    Shop updateShop(Long id, Shop shop);

    void deleteShop(Long id);

    void updateShopStatus(Long id, boolean isOpen);
}