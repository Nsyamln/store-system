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
    @PostMapping("/konfirm-order")
    public Response<Object> confirmSale(@PathVariable String id){
        Authentication authentication = SecurityContextHolder.getAuthentication();
        return orderService.approveOrder(authentication,id);
    }
    @PostMapping("/order-label")
    public String getOrderLabel(@RequestBody OnlineSaleReq saleRequest) {
        return orderService.generateOrderLabel(saleRequest);

    }

}
