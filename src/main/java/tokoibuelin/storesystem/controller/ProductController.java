package tokoibuelin.storesystem.controller;

import org.springframework.web.bind.annotation.*;
import tokoibuelin.storesystem.model.Authentication;
import tokoibuelin.storesystem.model.Response;
import tokoibuelin.storesystem.model.request.RegistProductReq;
import tokoibuelin.storesystem.model.request.UpdateProductReq;
import tokoibuelin.storesystem.service.ProductService;
import tokoibuelin.storesystem.util.SecurityContextHolder;

@RestController
@RequestMapping("/secured/product")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService){
        this.productService = productService;
    }
    @GetMapping("/list")
    public Response<Object> listProducts(@RequestParam(value = "page", defaultValue = "1") int page,
                                     @RequestParam(value = "size", defaultValue = "3") int size) {
        Authentication authentication = SecurityContextHolder.getAuthentication();
        return productService.listProducts(authentication, page, size);
    }
    @PostMapping("/add-product")
    public Response<Object> createProduct(@RequestBody RegistProductReq req){
        Authentication authentication = SecurityContextHolder.getAuthentication();
        return productService.createProduct(authentication,req);
    }
    @PostMapping("/update-product")
    public Response<Object> updateProduct(@RequestBody UpdateProductReq req) {
        Authentication authentication = SecurityContextHolder.getAuthentication();
        return productService.updateProduct(authentication, req);
    }

    @PostMapping("/add-stock/{addStock}/{productId}")
    public Response<Object> addStockProduct( @PathVariable Long addStock, @PathVariable String productId ) {
        Authentication authentication = SecurityContextHolder.getAuthentication();
        return productService.addStockProduct(authentication, addStock,productId);
    }

    @DeleteMapping("/delete-product/{productId}/{}")
    public Response<Object> deleteProduct(@PathVariable String productId) {
        Authentication authentication = SecurityContextHolder.getAuthentication();
        return productService.deleteProduct(authentication, productId);
    }
}
