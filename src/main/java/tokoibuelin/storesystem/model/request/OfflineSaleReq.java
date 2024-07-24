package tokoibuelin.storesystem.model.request;

import tokoibuelin.storesystem.entity.SaleDetails;

import java.util.List;

public record OfflineSaleReq(

     Long totalPrice,
     String customerId,
     String orderId,
     String paymentMethod,
     Long amountPaid,
     List<SaleDetails> saleDetails) {
}
