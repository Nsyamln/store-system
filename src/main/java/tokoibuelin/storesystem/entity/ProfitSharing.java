package tokoibuelin.storesystem.entity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record ProfitSharing(
        String profitSharingId,
        String saleId,
        String productId,
        String supplierId,
        int productQuantity,
        BigDecimal totalPurchasePrice,
        BigDecimal totalSalePrice,
        String status,
        OffsetDateTime paymentDate
) {}

