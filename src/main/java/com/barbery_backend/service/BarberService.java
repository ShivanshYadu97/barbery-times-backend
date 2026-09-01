package com.barbery_backend.service;

import com.barbery_backend.entity.Barber;

import java.util.List;

public interface BarberService {

    Barber createBarber(Long shopId, Barber barber);

    List<Barber> getBarbersByShop(Long shopId);

    Barber getBarberById(Long barberId);

    Barber updateBarber(Long barberId, Barber barber);

    void deleteBarber(Long barberId);

    Barber updateShiftStatus(Long barberId, boolean shiftActive);

    List<Barber> getBarbersForCustomer(Long shopId);
}