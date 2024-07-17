package tokoibuelin.storesystem.controller;

import tokoibuelin.storesystem.model.Authentication;
import tokoibuelin.storesystem.model.request.RegisterBuyerReq;
import tokoibuelin.storesystem.model.Response;
import tokoibuelin.storesystem.model.request.RegisterSupplierReq;
import tokoibuelin.storesystem.model.request.ResetPasswordReq;
import tokoibuelin.storesystem.model.request.UpdateProfileReq;
import tokoibuelin.storesystem.util.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import tokoibuelin.storesystem.service.UserService;

import java.util.Map;

@RestController
@RequestMapping("/secured/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/list")
    public Response<Object> listUser(@RequestParam(value = "page", defaultValue = "1") int page,
                                     @RequestParam(value = "size", defaultValue = "3") int size) {
        Authentication authentication = SecurityContextHolder.getAuthentication();
        return userService.listUsers(authentication, page, size);
    }
    @PostMapping("/register-supplier")
    public Response<Object> registerSeller(@RequestBody RegisterSupplierReq req) {
        Authentication authentication = SecurityContextHolder.getAuthentication();
        return userService.registerSupplier(authentication, req);
    }

    @PostMapping("/register-buyer")
    public Response<Object> registerBuyer(@RequestBody RegisterBuyerReq req) {
        Authentication authentication = SecurityContextHolder.getAuthentication();
        return userService.registerBuyer(authentication, req);
    }

    @PostMapping("/reset-password")
    public Response<Object> resetPassword(@RequestBody ResetPasswordReq req) {
        Authentication authentication = SecurityContextHolder.getAuthentication();
        return userService.resetPassword(authentication, req);

    }

    @PostMapping("/update-profile")
    public Response<Object> updateProfile(@RequestBody UpdateProfileReq req) {
        Authentication authentication = SecurityContextHolder.getAuthentication();
        return userService.updateProfile(authentication, req);
    }

    @DeleteMapping("/delete-user/{id}")
    public Response<Object> deleteUser(@PathVariable String userId) {
        //Long userId = requestBody.get("id");
        Authentication authentication = SecurityContextHolder.getAuthentication();
        return userService.deletedUser(authentication, userId);
    }
}
