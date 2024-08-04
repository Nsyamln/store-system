package tokoibuelin.storesystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tokoibuelin.storesystem.model.Authentication;
import tokoibuelin.storesystem.model.Response;
import tokoibuelin.storesystem.model.request.OnlineSaleReq;
import tokoibuelin.storesystem.service.OrderService;
import tokoibuelin.storesystem.util.SecurityContextHolder;

@RestController
@RequestMapping("/secured/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping("/konfirm-order/{id}")
    public Response<Object> confirmSale(@PathVariable String id){
        Authentication authentication = SecurityContextHolder.getAuthentication();
        return orderService.approveOrder(authentication,id);
    }

    @PostMapping("/add-trackingnumber")
    public Response<Object> addResi(@PathVariable String orderId, String resi){
        Authentication authentication = SecurityContextHolder.getAuthentication();
        return orderService.addResi(authentication,orderId,resi);
    }

    @PostMapping("/konfirm-delivered")
    public Response<Object> delivered(@PathVariable String orderId){
        Authentication authentication = SecurityContextHolder.getAuthentication();
        return orderService.delivered(authentication,orderId);
    }

//    @GetMapping("/order-label")
//    public String getOrderLabel(@Path Variable String orderId) {
//        return orderService.generateOrderLabel(orderId);
//
//    }

}
