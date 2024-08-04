package tokoibuelin.storesystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tokoibuelin.storesystem.entity.Address;
import tokoibuelin.storesystem.model.response.ShippingCostResponse;
import tokoibuelin.storesystem.service.AddressService;
import tokoibuelin.storesystem.service.RajaOngkirService;

import java.util.Arrays;
import java.util.List;

@RestController
public class ShippingController {
    @Autowired
    private AddressService addressService;

    @Autowired
    private RajaOngkirService rajaOngkirService;
    private static final String DEFAULT_ORIGIN_ID = "103"; // Ganti dengan ID yang sesuai dari RajaOngkir

    @GetMapping("/shipping-cost")
    public ShippingCostResponse getShippingCost(
            @RequestParam String origin,
            @RequestParam String destination,
            @RequestParam int weight,
            @RequestParam String courier) {
        ShippingCostResponse shippingCost = rajaOngkirService.getShippingCost(origin, destination, weight, courier);

        // Cetak estimasi pengiriman ke terminal
        System.out.println("Estimated Delivery Date: " + shippingCost.estimatedDeliveryDate());

        return shippingCost;
    }


}
