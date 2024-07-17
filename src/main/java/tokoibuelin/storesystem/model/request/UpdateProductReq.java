package tokoibuelin.storesystem.model.request;

public record UpdateProductReq(String productName, String description, Long price, String productImage) {
}
