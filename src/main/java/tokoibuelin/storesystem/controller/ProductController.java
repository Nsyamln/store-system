package tokoibuelin.storesystem.controller;

import org.springframework.web.bind.annotation.*;
import tokoibuelin.storesystem.model.Authentication;
import tokoibuelin.storesystem.model.Response;
import tokoibuelin.storesystem.model.request.RegisterProductReq;
import tokoibuelin.storesystem.model.request.UpdateProductReq;
import tokoibuelin.storesystem.model.request.UpdateProfileReq;
import tokoibuelin.storesystem.service.ProductService;
import tokoibuelin.storesystem.util.SecurityContextHolder;

@RestController
@RequestMapping("/secured/product")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService){
        this.productService = productService;
    }
//    @GetMapping("/list")
//    public Response<Object> listUser(@RequestParam(value = "page", defaultValue = "1") int page,
//                                     @RequestParam(value = "size", defaultValue = "3") int size) {
//        Authentication authentication = SecurityContextHolder.getAuthentication();
//        return productService.listProducts(authentication, page, size);
//    }
    @PostMapping("/add-product")
    public Response<Object> createProduct(@RequestBody RegisterProductReq req){
        Authentication authentication = SecurityContextHolder.getAuthentication();
        return productService.createProduct(authentication,req);
    }
    @PostMapping("/update-product")
    public Response<Object> updateProduct(@RequestBody UpdateProductReq req) {
        Authentication authentication = SecurityContextHolder.getAuthentication();
        return productService.updateProduct(authentication, req);
    }

    @DeleteMapping("/delete-product/{id}")
    public Response<Object> deleteUser(@PathVariable String userId) {
        //Long userId = requestBody.get("id");
        Authentication authentication = SecurityContextHolder.getAuthentication();
        return productService.deleteProduct(authentication, userId);
    }
}
