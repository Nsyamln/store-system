package tokoibuelin.storesystem.model.request;

import java.math.BigDecimal;

public record UpdateProductReq(String productId,String productName, String description, Long unit, Long price, String productImage) {
}
