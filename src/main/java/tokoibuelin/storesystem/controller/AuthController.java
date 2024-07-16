package tokoibuelin.storesystem.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tokoibuelin.storesystem.model.request.LoginReq;
import tokoibuelin.storesystem.model.Response;
import tokoibuelin.storesystem.service.UserService;

@RestController
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public Response<Object> login(@RequestBody LoginReq req) {
        return userService.login(req);
    }
    @GetMapping("/hello")
    public String helloWorld() {
        return "Hello, World!";
    }
}
