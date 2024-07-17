package tokoibuelin.storesystem.model.response;

public record ConsignmentProductDto(String supplierId, String productId, Long consignmentQuantity, Long purchasePrice) {
}
