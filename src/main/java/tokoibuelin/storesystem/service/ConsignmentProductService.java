package tokoibuelin.storesystem.service;

import org.springframework.stereotype.Service;
import tokoibuelin.storesystem.entity.ConsignmentProduct;
import tokoibuelin.storesystem.entity.Product;
import tokoibuelin.storesystem.entity.User;
import tokoibuelin.storesystem.model.Authentication;
import tokoibuelin.storesystem.model.Page;
import tokoibuelin.storesystem.model.Response;
import tokoibuelin.storesystem.model.request.CreateConsProductReq;
import tokoibuelin.storesystem.model.request.RegisterProductReq;
import tokoibuelin.storesystem.model.request.UpdateProductReq;
import tokoibuelin.storesystem.model.response.ConsignmentProductDto;
import tokoibuelin.storesystem.model.response.ProductDto;
import tokoibuelin.storesystem.repository.ConsignmentProductRepository;
import tokoibuelin.storesystem.repository.ProductRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ConsignmentProductService extends AbstractService {
    private final ConsignmentProductRepository consignmentProductRepository;

    public ConsignmentProductService(final ConsignmentProductRepository consignmentProductRepository){
        this.consignmentProductRepository =  consignmentProductRepository;
    }
    public Response<Object> listConsignmentProducts(final Authentication authentication, final int page, final int size) {
        return precondition(authentication, User.Role.ADMIN).orElseGet(() -> {
            if (page <= 0 || size <= 0) {
                return Response.badRequest();
            }
            Page<ConsignmentProduct> consProductPage = consignmentProductRepository.listConsignmentProducts(page, size);
            List<ConsignmentProductDto> consProducts = consProductPage.data().stream().map(consProduct -> new ConsignmentProductDto(consProduct.supplierId(), consProduct.productId(),consProduct.consignmentQuantity(),consProduct.purchasePrice())).toList();
            Page<ConsignmentProductDto> p = new Page<>(consProductPage.totalData(), consProductPage.totalPage(), consProductPage.page(), consProductPage.size(), consProducts);
            return Response.create("09", "00", "Sukses", p);
        });
    }

    public Response<Object> createConsProduct(final Authentication authentication, final CreateConsProductReq req) {
        return precondition(authentication, User.Role.ADMIN).orElseGet(() -> {
            if (req == null) {
                return Response.badRequest();
            }

            final ConsignmentProduct consProduct = new ConsignmentProduct( //
                    null, //
                    req.supplierId(),//
                    req.productId(), //
                    OffsetDateTime.now(),
                    req.consignmentQuantity(),
                    req.purchasePrice()

            );
            final Long saved = consignmentProductRepository.saveConsigmentProduct(consProduct);
            if (0L == saved) {
                return Response.create("05", "01", "Gagal menambahkan Product", null);
            }
            return Response.create("05", "00", "Sukses", saved);
        });
    }
//        public Response<Object> updateConsProduct(final Authentication authentication, final UpdateProductReq req) {
//            return precondition(authentication, User.Role.ADMIN, User.Role.PEMBELI, User.Role.PEMASOK).orElseGet(() -> {
//                Optional<Product> productOpt = productRepository.findById(authentication.id());
//                if (productOpt.isEmpty()) {
//                    return Response.create("07", "01", "Produk  tidak ditemukan", null);
//                }
//                Product product = productOpt.get();
//                Product updatedProduct = new Product(
//                        product.productId(),
//                        authentication.id(),
//                        product.deletedBy(),
//                        OffsetDateTime.now(),
//                        product.createdAt(),
//
//                        product.deletedAt());
//
//                if (productRepository.updateProduct(updatedProduct,authentication.id())) {
//                    return Response.create("07", "00", "Data produk berhasil diperbarui", null);
//                } else {
//                    return Response.create("07", "02", "Gagal mengupdate data produk", null);
//                }
//            });
//        }


    //}


}
