package tokoibuelin.storesystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tokoibuelin.storesystem.entity.Order;
import tokoibuelin.storesystem.entity.Sale;
import tokoibuelin.storesystem.entity.SaleDetails;
import tokoibuelin.storesystem.entity.User;
import tokoibuelin.storesystem.model.Authentication;
import tokoibuelin.storesystem.model.Response;
import tokoibuelin.storesystem.model.request.OnlineSaleReq;
import tokoibuelin.storesystem.model.request.OfflineSaleReq;
import tokoibuelin.storesystem.model.response.ShippingCostResponse;
import tokoibuelin.storesystem.repository.OrderRepository;
import tokoibuelin.storesystem.repository.SaleRepository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SaleService extends AbstractService{
    private final SaleRepository saleRepository;

    private final OrderRepository orderRepository;
    @Autowired
    private RajaOngkirService rajaOngkirService;

    @Autowired AddressService addressService;

    public SaleService(final SaleRepository saleRepository,final OrderRepository orderRepository){
        this.saleRepository = saleRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public Response<Object> createOnlineSale(final Authentication authentication, final OnlineSaleReq req) {
        return precondition(authentication, User.Role.PEMBELI).orElseGet(() -> {
            if (req == null) {
                return Response.badRequest();
            }

            List<SaleDetails> saleDetail = req.saleDetails();
            int totalWeight = 0;

            // Loop untuk menghitung berat total
            for (SaleDetails detail : saleDetail) {
                // Hitung berat total untuk detail penjualan saat ini
                long weightForDetail = detail.unit() * detail.quantity();
                // Tambahkan berat total ke totalWeight
                totalWeight += weightForDetail;
            }
            System.out.println("Cek totalWeight : "+totalWeight);

            String city = addressService.extractCity(req.deliveryAddress());
            System.out.println("Cek City : "+city);

            String destinationId = rajaOngkirService.findCityIdByName(city);
            System.out.println("Cek destionationId : "+destinationId);

            String originCityId = "103";
            ShippingCostResponse shipping;
            try {
                shipping = rajaOngkirService.getShippingCost(originCityId, destinationId, totalWeight, req.courier());
            } catch (Exception e) {
                e.printStackTrace();
                shipping = new ShippingCostResponse("Error calculating shipping cost", 0, "N/A", OffsetDateTime.now());
            }

            if (shipping == null || shipping.cost() <= 0) {
                return Response.create("05", "01", "Failed to get shipping cost", null);
            }

            System.out.println("Estimated Delivery: " + shipping.estimatedDelivery());
            System.out.println("Estimated Delivery Date: " + shipping.estimatedDeliveryDate());
            System.out.println("Shipping Cost : "+shipping.cost() );
            System.out.println("Shipping Service : "+shipping.service() );


            final Order order = new Order(
                    null,
                    null,
                    req.customerId(),
                    req.deliveryAddress(),
                    Order.Status.PENDING,
                    authentication.id(),
                    null,
                    OffsetDateTime.now(),
                    null,
                    shipping.cost(),
                    null,
                    req.courier(),
                    shipping.service(),
                    shipping.estimatedDeliveryDate(),
                    null
            );
            final String savedOrder = orderRepository.saveOrder(order);
            if (null == savedOrder  ) {
                return Response.create("05", "01", "Gagal menambahkan Order", null);
            }

            final Sale sale = new Sale(
                    null,
                    OffsetDateTime.now(),
                    req.totalPrice(),
                    req.customerId(),
                    savedOrder,
                    req.amountPaid(),
                    Sale.PaymentMethod.fromString(req.paymentMethod())
            );

            final String savedSale = saleRepository.saveSale(sale);
            if (null == savedSale ) {
                return Response.create("05", "01", "Gagal menambahkan Penjualan", null);
            }

            List<SaleDetails> saleDetails = req.saleDetails().stream().map(detailReq -> new SaleDetails(
                    null,
                    savedSale,
                    detailReq.productId(),
                    detailReq.productName(),
                    detailReq.quantity(),
                    detailReq.price(),
                    detailReq.unit()
            )).collect(Collectors.toList());
            final Long savedSaleDetails = saleRepository.saveSaleDetails(saleDetails);
            if (0L == savedSaleDetails ) {
                return Response.create("05", "01", "Gagal menambahkan Detail Penjualan", null);
            }
            return Response.create("05", "00", "Sukses", true);
        });
    }

    @Transactional
    public Response<Object> createOfflineSale(final Authentication authentication, final OfflineSaleReq req) {
        return precondition(authentication, User.Role.ADMIN).orElseGet(() -> {
            if (req == null) {
                return Response.badRequest();
            }
            final Sale sale = new Sale(
                    null,
                    OffsetDateTime.now(),
                    req.totalPrice(),
                    null,
                    null,
                    req.amountPaid(),
                    Sale.PaymentMethod.fromString(req.paymentMethod())
            );
            final String savedSale = saleRepository.saveSale(sale);
            if (null == savedSale ) {
                return Response.create("05", "01", "Gagal menambahkan Penjualan", null);
            }
            List<SaleDetails> saleDetails = req.saleDetails().stream().map(detailReq -> new SaleDetails(
                    null,
                    savedSale,
                    detailReq.productId(),
                    detailReq.productName(),
                    detailReq.quantity(),
                    detailReq.price(),
                    detailReq.unit()
            )).collect(Collectors.toList());
            final Long savedSaleDetails = saleRepository.saveSaleDetails(saleDetails);
            if (0L == savedSaleDetails ) {
                return Response.create("05", "01", "Gagal menambahkan Detail Penjualan", null);
            }
            return Response.create("05", "00", "Sukses", true);
        });
    }
}
