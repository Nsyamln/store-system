package tokoibuelin.storesystem.model.request;

import tokoibuelin.storesystem.entity.SaleDetails;

import java.util.List;

public record SaleReq(
     String saleId,
     Long totalPrice,
     String customerId,
     List<SaleDetails> saleDetails) {
}
