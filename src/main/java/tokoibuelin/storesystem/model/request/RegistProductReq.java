package tokoibuelin.storesystem.model.request;

import java.math.BigDecimal;

public record RegistProductReq(String productName,
                               String description,
                               BigDecimal unit,
                               Long price,
                               Long stock,
                               String supplierId,
                               String productImage,
                               Long purchasePrice

                                 ) {
}
