
package tokoibuelin.storesystem.entity;

        import java.sql.Connection;
        import java.sql.PreparedStatement;
        import java.sql.Statement;
        import java.time.OffsetDateTime;

public record Order(
        String orderId,
        OffsetDateTime orderDate,
        String customerId,
        String deliveryAddress,
        Status status,
        String createdBy,
        String updatedBy,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
        public static final String TABLE_NAME = "orders";

        public PreparedStatement insert(final Connection connection) {
                try {
                        final String sql = "INSERT INTO " + TABLE_NAME + " (order_date, costumer_id, delivery_address, status, createdBy, created_at) VALUES (CURRENT_TIMESTAMP, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
                        final PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                        ps.setString(1, deliveryAddress);
                        ps.setString(2, customerId);
                        ps.setString(3, status.name());
                        ps.setString(4, customerId);

                        return ps;
                } catch (Exception e) {
                        return null;
                }
        }

        public enum Status {
                PENDING, PROCESS,DELIVERY, DELIVERED;

                public static Status fromString(String str) {
                        if (PENDING.name().equals(str)) {
                                return PENDING;
                        } else if (PROCESS.name().equals(str)) {
                                return PROCESS;
                        } else if (DELIVERY.name().equals(str)) {
                                return DELIVERY;
                        } else {
                                return DELIVERED;
                        }
                }
        }
}