package tokoibuelin.storesystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tokoibuelin.storesystem.entity.Address;
import tokoibuelin.storesystem.model.Authentication;
import tokoibuelin.storesystem.model.Response;
import tokoibuelin.storesystem.model.request.UpdateAddressReq;
import tokoibuelin.storesystem.service.AddressService;
import tokoibuelin.storesystem.util.SecurityContextHolder;

@RestController
@RequestMapping("/secured/address")
public class AddressController {

    private final AddressService addressService;
    public AddressController(final AddressService addressService){
        this.addressService = addressService;
    }

    @PostMapping("/edit-address")
    public Response<Object> editAddress (@RequestBody UpdateAddressReq req){
        Authentication authentication = SecurityContextHolder.getAuthentication();
        return addressService.updateAddress(authentication,req);
    }


    @PostMapping("/validate")
    public ResponseEntity<Response<Object>> validateAddress(@RequestBody Address address) {
        Response<Object> response = addressService.validateAddress(address);
        if (response.code().endsWith("200")) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }
}
