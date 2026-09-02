package com.barbery_backend.service;

import com.barbery_backend.dto.BarberMenuRequest;
import com.barbery_backend.entity.BarberMenu;

import java.util.List;

public interface BarberMenuService {

    List<BarberMenu> getServicesByShop(Long shopId);

    List<BarberMenu> getActiveServicesByShop(Long shopId);

    BarberMenu getServiceById(Long id);

    BarberMenu createService(BarberMenuRequest request);

    BarberMenu updateService(Long id, BarberMenuRequest request);

    void deleteService(Long id);
}