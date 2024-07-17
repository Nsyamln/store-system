package tokoibuelin.storesystem.model.request;

public record RegisterProductReq(String productName, String description, Long price, Long stock, String supplierId, String productImage) {
}
