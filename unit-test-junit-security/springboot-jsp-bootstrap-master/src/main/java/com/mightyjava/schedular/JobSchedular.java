package com.mightyjava.schedular;

import com.mightyjava.model.Address;
import com.mightyjava.model.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.mightyjava.service.AddressService;
import com.mightyjava.service.UserService;

import java.util.List;


@Component
public class JobSchedular {

    private final AddressService addressService;
    private final UserService userService;

    @Autowired
    public JobSchedular(AddressService addressService, UserService userService) {
        this.addressService = addressService;
        this.userService = userService;
    }

    @Scheduled(fixedDelay = 60000) // Executa a cada 1 minuto após a conclusão da última execução
    public void scheduleUsingFixedDelay() {
        List<Address> addresses = addressService.addressList();
        System.out.println("Address List: " + addresses);
    }

    @Scheduled(initialDelay = 5000, fixedDelay = 60000)
    // Inicia após 5 segundos e executa a cada 1 minuto após a conclusão
    public void scheduleUsingFixedDelayWithInitialDelay() {
        Users user = userService.findOne(1L);
        System.out.println("User: " + user);
    }

    @Scheduled(fixedRate = 60000) // Executa a cada 1 minuto, independente da conclusão
    public void scheduleUsingRate() {
        Address address = addressService.findOne(1L);
        System.out.println("Address: " + address);
    }
}