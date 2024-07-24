package tokoibuelin.storesystem.service;

import org.springframework.stereotype.Service;
import tokoibuelin.storesystem.entity.SaleDetails;
import tokoibuelin.storesystem.entity.User;
import tokoibuelin.storesystem.entity.Order;
import tokoibuelin.storesystem.model.request.OnlineSaleReq;
import tokoibuelin.storesystem.repository.OrderRepository;
import tokoibuelin.storesystem.model.Authentication;
import tokoibuelin.storesystem.model.Response;

import java.time.format.DateTimeFormatter;
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


    public String generateOrderLabel(OnlineSaleReq saleRequest) {
        StringBuilder labelBuilder = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        labelBuilder.append("-----------------------------------------------\n")
                .append("           Toko Ibu Elin\n")
                .append("       Informasi Pengiriman Paket\n")
                .append("-----------------------------------------------\n\n")
                .append("Order ID: ").append(saleRequest.customerId()).append("\n")
                .append("Tanggal: ").append(saleRequest.orderDate().format(formatter)).append("\n\n")
                .append("Alamat Pengiriman:\n")
                .append(saleRequest.deliveryAddress()).append("\n\n")
                .append("Metode Pembayaran:\n")
                .append(saleRequest.paymentMethod()).append("\n\n")
                .append("Pengirim:\n")
                .append("Nama      : Toko Ibu Elin\n\n")
                .append("-----------------------------------------------\n")
                .append("Produk yang Dibeli:\n")
                .append("-----------------------------------------------\n");

        for (SaleDetails detail : saleRequest.saleDetails()) {
            labelBuilder.append(detail.productName()).append("\n")
                    .append("Jumlah  : ").append(detail.quantity()).append("\n\n");
        }

        labelBuilder.append("-----------------------------------------------");

        return labelBuilder.toString();
    }
}
