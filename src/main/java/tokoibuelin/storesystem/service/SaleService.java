package tokoibuelin.storesystem.service;

import org.springframework.stereotype.Service;
import tokoibuelin.storesystem.entity.Product;
import tokoibuelin.storesystem.entity.Sale;
import tokoibuelin.storesystem.entity.SaleDetails;
import tokoibuelin.storesystem.entity.User;
import tokoibuelin.storesystem.model.Authentication;
import tokoibuelin.storesystem.model.Response;
import tokoibuelin.storesystem.model.request.RegisterProductReq;
import tokoibuelin.storesystem.model.request.SaleReq;
import tokoibuelin.storesystem.repository.SaleRepository;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SaleService extends AbstractService{
    private final SaleRepository saleRepository;
    public SaleService(final SaleRepository saleRepository){
        this.saleRepository = saleRepository;
    }

    public Response<Object> createSale(final Authentication authentication, final SaleReq req) {
        return precondition(authentication, User.Role.ADMIN).orElseGet(() -> {
            if (req == null) {
                return Response.badRequest();
            }
//            Sale.saleId=(req.saleId());
//            sale.setSaleDate(saleRequest.getSaleDate());
//            sale.setTotalPrice(saleRequest.getTotalPrice());
//            sale.setCustomerId(saleRequest.getCustomerId());

           // List<SaleDetails> saleDetails = req.saleDetails();

            final Sale sale = new Sale( //
                    null, //
                    OffsetDateTime.now(),//
                    req.totalPrice(), //
                    req.customerId()

            );

            final Long savedSale = saleRepository.saveSale(sale);

            if (0L == savedSale   ) {
                return Response.create("05", "01", "Gagal menambahkan Product", null);
            }
            List<SaleDetails> saleDetails = req.saleDetails().stream().map(detailReq -> new SaleDetails(
                    null,
                    detailReq.saleId(),
                    detailReq.productId(),
                    detailReq.quantity(),
                    detailReq.price()
            )).collect(Collectors.toList());
            final Long savedSaleDetails = saleRepository.saveSaleDetails(saleDetails);
            return Response.create("05", "00", "Sukses", true);
        });
    }
    public Response<Optional<List<SaleDetails>>> getSalesByDateRange(LocalDateTime startedAt, LocalDateTime endedAt) {
        Optional<List<SaleDetails>> saleDetailsOpt = saleRepository.findSaleDetailsByDateRange(startedAt, endedAt);


    }
}
