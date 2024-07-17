package tokoibuelin.storesystem.model.request;

public record CreateConsProductReq(
        String supplierId,
        String productId,
        Long consignmentQuantity,
        Long purchasePrice
) {
}
