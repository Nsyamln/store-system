package tokoibuelin.storesystem.service;

import org.springframework.stereotype.Service;
import tokoibuelin.storesystem.entity.User;
import tokoibuelin.storesystem.entity.Order;
import tokoibuelin.storesystem.repository.OrderRepository;
import tokoibuelin.storesystem.model.Authentication;
import tokoibuelin.storesystem.model.Response;

import java.time.OffsetDateTime;
import java.util.Optional;
@Service
public class OrderService extends AbstractService{
    private final OrderRepository orderRepository;

    public OrderService(final OrderRepository orderRepository){
        this.orderRepository = orderRepository;
    }

    public Response<Object> approveOrder(Authentication authentication, String id) {
        return precondition(authentication, User.Role.ADMIN).orElseGet(() -> {
            Optional<Order> auctionOpt = orderRepository.findById(id);
            if (auctionOpt.isEmpty()) {
                return Response.create("07", "02", "Auction tidak ditemukan", null);
            }

            Long updated = orderRepository.updateOrderStatus(authentication.id(), id, Order.Status.PROCESS);

            if (updated == 1L) {
                return Response.create("07", "00", "Sukses", updated);
            } else {
                return Response.create("07", "01", "Gagal memproses Order", null);
            }
        });
    }
    public Response<Object> addResi(final Authentication authentication, final String orderId, final String resi) {
        return precondition(authentication, User.Role.ADMIN).orElseGet(() -> {
            Optional<Order> orderOpt = orderRepository.findById(orderId);
            if (orderOpt.isEmpty()) {
                return Response.create("07", "01", "order  tidak ditemukan", null);
            }
            Order order = orderOpt.get();
            Order updatedOrder = new Order(
                    order.orderId(),
                    order.orderDate(),
                    order.customerId(),
                    order.deliveryAddress(),
                    order.status(),
                    order.createdBy(),
                    authentication.id(),
                    order.createdAt(),
                    OffsetDateTime.now(),
                    order.shippingCost(),
                    resi,
                    order.courier(),
                    order.shippingMethod(),
                    order.estimatedDeliveryDate(),
                    order.actualDeliveryDate());

            if (orderRepository.addResi(updatedOrder,authentication.id())) {
                return Response.create("07", "00", "Sukses", null);
            } else {
                return Response.create("07", "02", "Gagal menambahkan resi", null);
            }
        });
    }

    public Response<Object> delivered(final Authentication authentication, final String orderId) {
        return precondition(authentication, User.Role.PEMBELI).orElseGet(() -> {
            Optional<Order> orderOpt = orderRepository.findById(orderId);
            if (orderOpt.isEmpty()) {
                return Response.create("07", "01", "order  tidak ditemukan", null);
            }
            Order order = orderOpt.get();
            Order updatedOrder = new Order(
                    order.orderId(),
                    order.orderDate(),
                    order.customerId(),
                    order.deliveryAddress(),
                    order.status(),
                    order.createdBy(),
                    authentication.id(),
                    order.createdAt(),
                    OffsetDateTime.now(),
                    order.shippingCost(),
                    order.trackingNumber(),
                    order.courier(),
                    order.shippingMethod(),
                    order.estimatedDeliveryDate(),
                    OffsetDateTime.now());

            if (orderRepository.addResi(updatedOrder,authentication.id())) {
                return Response.create("07", "00", "Paket telah diterima", null);
            } else {
                return Response.create("07", "02", "Gagal mengupdate tanggal diterima", null);
            }
        });
    }

//    public String generateOrderLabel(OnlineSaleReq saleRequest) {
//        StringBuilder labelBuilder = new StringBuilder();
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//
//        labelBuilder.append("-----------------------------------------------\n")
//                .append("           Toko Ibu Elin\n")
//                .append("       Informasi Pengiriman Paket\n")
//                .append("-----------------------------------------------\n\n")
//                .append("Order ID: ").append(saleRequest.customerId()).append("\n")
//                .append("Tanggal: ").append(saleRequest.orderDate().format(formatter)).append("\n\n")
//                .append("Alamat Pengiriman:\n")
//                .append(saleRequest.deliveryAddress()).append("\n\n")
//                .append("Metode Pembayaran:\n")
//                .append(saleRequest.paymentMethod()).append("\n\n")
//                .append("Pengirim:\n")
//                .append("Nama      : Toko Ibu Elin\n\n")
//                .append("-----------------------------------------------\n")
//                .append("Produk yang Dibeli:\n")
//                .append("-----------------------------------------------\n");
//
//        for (SaleDetails detail : saleRequest.saleDetails()) {
//            labelBuilder.append(detail.productName()).append("\n")
//                    .append("Jumlah  : ").append(detail.quantity()).append("\n\n");
//        }
//
//        labelBuilder.append("-----------------------------------------------");
//
//        return labelBuilder.toString();
//    }
}
