package tokoibuelin.storesystem.service;

import org.springframework.stereotype.Service;
import tokoibuelin.storesystem.entity.Order;
import tokoibuelin.storesystem.entity.Sale;
import tokoibuelin.storesystem.entity.SaleDetails;
import tokoibuelin.storesystem.entity.User;
import tokoibuelin.storesystem.model.Authentication;
import tokoibuelin.storesystem.model.Response;
import tokoibuelin.storesystem.model.request.OnlineSaleReq;
import tokoibuelin.storesystem.model.request.ReportReq;
import tokoibuelin.storesystem.model.request.OfflineSaleReq;
import tokoibuelin.storesystem.repository.OrderRepository;
import tokoibuelin.storesystem.repository.SaleRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SaleService extends AbstractService{
    private final SaleRepository saleRepository;

    private final OrderRepository orderRepository;
    public SaleService(final SaleRepository saleRepository,final OrderRepository orderRepository){
        this.saleRepository = saleRepository;
        this.orderRepository = orderRepository;
    }

    public Response<Object> createOnlineSale(final Authentication authentication, final OnlineSaleReq req) {
        return precondition(authentication, User.Role.PEMBELI).orElseGet(() -> {
            if (req == null) {
                return Response.badRequest();
            }
            final Order order = new Order(
                    null,
                    req.orderDate(),
                    req.customerId(),
                    req.deliveryAddress(),
                    Order.Status.PENDING,
                    authentication.id(),
                    null,
                    OffsetDateTime.now(),
                    null
            );
            final Long savedOrder = orderRepository.saveOrder(order);
            if (0L == savedOrder  ) {
                return Response.create("05", "01", "Gagal menambahkan Order", null);
            }

            final Sale sale = new Sale( //
                    null, //
                    OffsetDateTime.now(),//
                    req.totalPrice(), //
                    req.customerId(),
                    order.orderId(),
                    Sale.PaymentMethod.fromString(req.paymentMethod()),
                    req.amountPaid()
            );

            final Long savedSale = saleRepository.saveSale(sale);
            if (0L == savedSale ) {
                return Response.create("05", "01", "Gagal menambahkan Penjualan", null);
            }

            List<SaleDetails> saleDetails = req.saleDetails().stream().map(detailReq -> new SaleDetails(
                    null,
                    detailReq.saleId(),
                    detailReq.productId(),
                    detailReq.productName(),
                    detailReq.quantity(),
                    detailReq.price()
            )).collect(Collectors.toList());
            final Long savedSaleDetails = saleRepository.saveSaleDetails(saleDetails);
            if (0L == savedSaleDetails ) {
                return Response.create("05", "01", "Gagal menambahkan Penjualan", null);
            }
            return Response.create("05", "00", "Sukses", true);
        });
    }

    public Response<Object> createOfflineSale(final Authentication authentication, final OfflineSaleReq req) {
        return precondition(authentication, User.Role.ADMIN).orElseGet(() -> {
            if (req == null) {
                return Response.badRequest();
            }

            final Sale sale = new Sale( //
                    null, //
                    OffsetDateTime.now(),//
                    req.totalPrice(), //
                    req.customerId(),
                    null,
                    Sale.PaymentMethod.fromString(req.paymentMethod()),
                    req.amountPaid()
            );

            final Long savedSale = saleRepository.saveSale(sale);

            if (0L == savedSale   ) {
                return Response.create("05", "01", "Gagal menambahkan Product", null);
            }
            List<SaleDetails> saleDetails = req.saleDetails().stream().map(detailReq -> new SaleDetails(
                    null,
                    detailReq.saleId(),
                    detailReq.productId(),
                    detailReq.productName(),
                    detailReq.quantity(),
                    detailReq.price()
            )).collect(Collectors.toList());
            final Long savedSaleDetails = saleRepository.saveSaleDetails(saleDetails);
            return Response.create("05", "00", "Sukses", true);
        });
    }


    public Response<Object> generateReport(ReportReq request) {
        OffsetDateTime startedAt = request.startedAt();
        OffsetDateTime endedAt = request.endedAt();

        List<Sale> sales = saleRepository.findSalesByDateRange(startedAt, endedAt);
        if (sales.isEmpty()) {
            return Response.create("02","02","No sales found", null);
        }

        List<String> saleIds = sales.stream().map(Sale::saleId).toList();
        List<SaleDetails> saleDetails = saleRepository.findSaleDetailsBySaleIds(saleIds);

        return Response.create("02","00", "Report generated successfully", new ReportData(sales, saleDetails));
    }

    // Helper class to wrap the report data
    public static class ReportData {
        private final List<Sale> sales;
        private final List<SaleDetails> saleDetails;

        public ReportData(List<Sale> sales, List<SaleDetails> saleDetails) {
            this.sales = sales;
            this.saleDetails = saleDetails;
        }

        public List<Sale> getSales() {
            return sales;
        }

        public List<SaleDetails> getSaleDetails() {
            return saleDetails;
        }
    }
}
