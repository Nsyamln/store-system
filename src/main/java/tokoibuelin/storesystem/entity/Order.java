
package tokoibuelin.storesystem.entity;

        import java.math.BigDecimal;
        import java.sql.Connection;
        import java.sql.PreparedStatement;
        import java.sql.Statement;
        import java.sql.Timestamp;
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
        OffsetDateTime updatedAt,
        Integer shippingCost,
        String trackingNumber,
        String courier,
        String shippingMethod,
        OffsetDateTime estimatedDeliveryDate,
        OffsetDateTime actualDeliveryDate
) {
        public static final String TABLE_NAME = "orders";


        public PreparedStatement insert(final Connection connection) {
                try {
                        final String sql = "INSERT INTO orders (order_date, customer_id, delivery_address, status, created_by, created_at, shipping_cost,courier,shipping_method,estimated_delivery_date) " +
                                "VALUES (CURRENT_TIMESTAMP, ?, ?, ?, ?, CURRENT_TIMESTAMP,?,?,?,?)";
                        final PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                        ps.setString(1, customerId);          // customer_id
                        ps.setString(2, deliveryAddress);     // delivery_address
                        ps.setString(3, status.toString());       // status
                        ps.setString(4, createdBy);           // created_by
                        ps.setInt(5,shippingCost);
                        ps.setString(6,courier);
                        ps.setString(7,shippingMethod);
                        ps.setObject(8,estimatedDeliveryDate);
                        return ps;
                } catch (Exception e) {
                        System.out.println("Error preparing statement: {}"+ e.getMessage());
                        return null;
                }
        }

//        public Order(OffsetDateTime orderDate, String customerId, String deliveryAddress,String createdBy,OffsetDateTime createdAt,BigDecimal shippingCost,String courier, String shippingMethod, OffsetDateTime estimatedDeliveryDate) {
//                this(null, orderDate, customerId, deliveryAddress,Status.PENDING, createdBy ,null,createdAt, null,shippingCost,null, courier, shippingMethod, estimatedDeliveryDate, null);
//        }




        public enum Status {
                PENDING, PROCESS, DELIVERY, DELIVERED;

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