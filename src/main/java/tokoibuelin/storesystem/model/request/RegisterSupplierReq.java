package tokoibuelin.storesystem.model.request;

public record RegisterSupplierReq(
                                  String name, String email, String password,String phone, String address) {
}
