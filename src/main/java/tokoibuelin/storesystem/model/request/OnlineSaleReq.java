package tokoibuelin.storesystem.model.request;

import tokoibuelin.storesystem.entity.SaleDetails;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record OnlineSaleReq (

                             String customerId,
                             String deliveryAddress,
                             BigDecimal totalPrice,
                             String paymentMethod,
                             BigDecimal amountPaid,
                             String courier,
                             String shippingMethod,
                             List<SaleDetails> saleDetails
){
}
