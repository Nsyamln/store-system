package tokoibuelin.storesystem.model.request;

import java.math.BigDecimal;

public record RegistProductReq(String productName,
                               String description,
                               Long unit,
                               Long price,
                               Long stock,
                               String supplierId,
                               String productImage,
                               Long purchasePrice

                                 ) {
}
