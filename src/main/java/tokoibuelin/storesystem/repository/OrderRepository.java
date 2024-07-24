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
import java.util.Objects;
import java.util.Optional;

@Repository
public class OrderRepository {
    private static final Logger log = LoggerFactory.getLogger(AddressRepository.class);

    private final JdbcTemplate jdbcTemplate;

    public OrderRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long saveOrder(final Order order) {
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            if (jdbcTemplate.update(con -> Objects.requireNonNull(order.insert(con)), keyHolder) != 1) {
                return 0L;
            } else {
                return Objects.requireNonNull(keyHolder.getKey()).longValue();
            }
        } catch (Exception e) {
            log.error("{}", e.getMessage());
            return 0L;
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
            if (rs.getString("order_id")== null ) {
                return null;
            }
            final OffsetDateTime orderDate = rs.getTimestamp("updated_at") == null ? null : rs.getTimestamp("order_date").toInstant().atOffset(ZoneOffset.UTC);
            final String customerId = rs.getString("customer_id");
            final String deliveryAddress = rs.getString("delivery_address");
            final Order.Status status = Order.Status.valueOf(rs.getString("status"));
            final String createdBy = rs.getString("created_by");
            final String updatedBy = rs.getString("updated_by");
            final OffsetDateTime createdAt = rs.getTimestamp("created_at") == null ? null : rs.getTimestamp("created_at").toInstant().atOffset(ZoneOffset.UTC);
            final OffsetDateTime updatedAt = rs.getTimestamp("updated_at") == null ? null : rs.getTimestamp("updated_at").toInstant().atOffset(ZoneOffset.UTC);
            return new Order(id, orderDate, customerId, deliveryAddress, status,createdBy, updatedBy,  createdAt, updatedAt);
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

