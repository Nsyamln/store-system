package tokoibuelin.storesystem.model.request;

public record UpdateProfileReq(
        String name,
        String phone,
        String email) {
}
