package tokoibuelin.storesystem.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import tokoibuelin.storesystem.entity.Address;
import tokoibuelin.storesystem.entity.Order;
import tokoibuelin.storesystem.entity.Product;
import tokoibuelin.storesystem.entity.User;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Repository
public class OrderRepository {
    private static final Logger log = LoggerFactory.getLogger(AddressRepository.class);

    private final JdbcTemplate jdbcTemplate;

    public OrderRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String saveOrder(final Order order) {
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            int updateCount = jdbcTemplate.update(con -> {
                PreparedStatement ps = order.insert(con);
                System.out.println("Cek Save Order -> "+ps.toString());
                return ps;
            }, keyHolder);

            if (updateCount != 1) {
                return null; // atau return 0L sesuai kebutuhan
            }

            // Ambil hasil dari KeyHolder sebagai String
            Map<String, Object> keys = keyHolder.getKeys();
            if (keys != null && keys.containsKey("order_id")) {
                return (String) keys.get("order_id");
            }

            return null;
        } catch (Exception e) {
            log.error("Error during saveOrder: {}", e.getMessage());
            return null; // atau return 0L sesuai kebutuhan
        }
    }

    public Optional<Order> findById(String  id) {
        System.out.println("ID nya : " + id);
        if (id == null || id == "") {
            return Optional.empty();
        }
        return Optional.ofNullable(jdbcTemplate.query(con -> {
            final PreparedStatement ps = con.prepareStatement("SELECT * FROM " + Order.TABLE_NAME + " WHERE order_id=?");
            ps.setString(1, id);
            return ps;
        }, rs -> {
            if(rs.next()) {
                final OffsetDateTime orderDate = rs.getTimestamp("updated_at") == null ? null : rs.getTimestamp("order_date").toInstant().atOffset(ZoneOffset.UTC);
                final String customerId = rs.getString("customer_id");
                final String deliveryAddress = rs.getString("delivery_address");
                final Order.Status status = Order.Status.valueOf(rs.getString("status"));
                final String createdBy = rs.getString("created_by");
                final String updatedBy = rs.getString("updated_by");
                final OffsetDateTime createdAt = rs.getTimestamp("created_at") == null ? null : rs.getTimestamp("created_at").toInstant().atOffset(ZoneOffset.UTC);
                final OffsetDateTime updatedAt = rs.getTimestamp("updated_at") == null ? null : rs.getTimestamp("updated_at").toInstant().atOffset(ZoneOffset.UTC);
                final Integer snippingChost = rs.getInt("snipping_post");
                final String trackingNumber = rs.getString("tracking_number");
                final String courier = rs.getString("courier");
                final String shippingMethod  =rs.getString("shipping_method");
                final OffsetDateTime estimatedDeliveryDate = rs.getTimestamp("estimated_delivery_date") == null ? null : rs.getTimestamp("estimated_delivery_date").toInstant().atOffset(ZoneOffset.UTC);
                final OffsetDateTime actualDeliveryDate = rs.getTimestamp("actual_delivery_date") == null ? null : rs.getTimestamp("actual_delivery_date").toInstant().atOffset(ZoneOffset.UTC);
                return new Order(id, orderDate, customerId, deliveryAddress, status, createdBy, updatedBy, createdAt, updatedAt,snippingChost,trackingNumber,courier,shippingMethod,estimatedDeliveryDate,actualDeliveryDate);
            }
            return null;
        }));
    }


    public Long updateOrderStatus(String authId, String id, Order.Status status) {
        try {
            return (long) jdbcTemplate.update(con -> {
                final PreparedStatement ps = con.prepareStatement(
                        "UPDATE " + Order.TABLE_NAME + " SET status = ?,updated_by=?, updated_at = CURRENT_TIMESTAMP WHERE order_id = ?");
                ps.setString(1, status.toString());
                ps.setObject(2, authId);
                ps.setString(3, id);
                return ps;
            });
        } catch (Exception e) {
            log.error("Gagal update status Auction: {}", e.getMessage());
            return 0L;
        }
    }

    public boolean addResi(final Order order, final String userId) {
        final String sql = "UPDATE " + Order.TABLE_NAME + " SET tracking_number=?,updated_by=?, updated_at = CURRENT_TIMESTAMP WHERE order_id = ?";
        try {
            int rowsAffected = jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, order.trackingNumber());
                ps.setString(2, userId);
                ps.setString(3, order.orderId());
                return ps;
            });
            return rowsAffected > 0;
        } catch (Exception e) {
            log.error("Gagal untuk update produk: {}", e.getMessage());
            return false;
        }
    }

    public boolean delivered(final Order order, final String userId) {
        final String sql = "UPDATE " + Order.TABLE_NAME + " SET actual_delivery_date = CURRENT_TIMESTAMP ,updated_by=?, updated_at = CURRENT_TIMESTAMP WHERE order_id = ?";
        try {
            int rowsAffected = jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, userId);
                ps.setString(2, order.orderId());
                return ps;
            });
            return rowsAffected > 0;
        } catch (Exception e) {
            log.error("Gagal untuk update produk: {}", e.getMessage());
            return false;
        }
    }

    public String formatAddress(String addressId) {
        String sql = "SELECT street, rt, rw, village, district, city, postal_code FROM addresses WHERE address_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{addressId}, (rs, rowNum) -> {
            String street = rs.getString("street");
            String rt = rs.getString("rt");
            String rw = rs.getString("rw");
            String village = rs.getString("village");
            String district = rs.getString("district");
            String city = rs.getString("city");
            String postalCode = rs.getString("postal_code");
            return String.format("%s, RT %s/RW %s, %s, %s, %s, %s", street, rt, rw, village, district, city, postalCode);
        });
    }
}

