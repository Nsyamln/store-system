package tokoibuelin.storesystem.model.request;

import java.math.BigDecimal;

public record UpdateProductReq(String productName, String description, BigDecimal unit, Long price, String productImage) {
}
