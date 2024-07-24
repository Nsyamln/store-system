package tokoibuelin.storesystem.controller;

import org.springframework.web.bind.annotation.*;
import tokoibuelin.storesystem.model.Authentication;
import tokoibuelin.storesystem.model.Response;
import tokoibuelin.storesystem.model.request.OnlineSaleReq;
import tokoibuelin.storesystem.model.request.ReportReq;
import tokoibuelin.storesystem.model.request.OfflineSaleReq;
import tokoibuelin.storesystem.service.OrderService;
import tokoibuelin.storesystem.service.SaleService;
import tokoibuelin.storesystem.util.SecurityContextHolder;

@RestController
@RequestMapping("/secured/sales")
public class SaleController {
    private final SaleService saleService;
    private final OrderService orderService;

    public SaleController(final SaleService saleService, final OrderService orderService){
        this.saleService = saleService;
        this.orderService = orderService;
    }
    @PostMapping("/create-saleOff")
    public Response<Object> createSale(@RequestBody OfflineSaleReq req){
        Authentication authentication = SecurityContextHolder.getAuthentication();
        return saleService.createOfflineSale(authentication,req);
    }
    @PostMapping("/create-saleOn")
    public Response<Object> createOnlineSale(@RequestBody OnlineSaleReq req){
        Authentication authentication = SecurityContextHolder.getAuthentication();
        return saleService.createOnlineSale(authentication,req);
    }


    @PostMapping("/report")
    public Response<Object> generatedSalesReport(@RequestBody ReportReq req){
        return saleService.generateReport(req);
    }


}
