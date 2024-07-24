package tokoibuelin.storesystem.model.request;

import tokoibuelin.storesystem.entity.SaleDetails;

import java.time.OffsetDateTime;
import java.util.List;

public record OnlineSaleReq (

                             String customerId,
                             OffsetDateTime orderDate,
                             String deliveryAddress,
                             Long totalPrice,
                             String paymentMethod,
                             Long amountPaid,
                             List<SaleDetails> saleDetails
){
}
