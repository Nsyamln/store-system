package tokoibuelin.storesystem.entity;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.OffsetDateTime;
import java.util.Arrays;

public record Sale(
        String saleId,
        OffsetDateTime saleDate,
        BigDecimal totalPrice,
        String customerId,
        String orderId,
        BigDecimal amountPaid,
        PaymentMethod paymentMethod
) {

    public static final String TABLE_NAME = "sales";

    public PreparedStatement insert(final Connection connection) {
        try {
            final String sql = "INSERT INTO " + TABLE_NAME + " (sale_date, total_price,customer_id,order_id,amount_paid,payment_method) VALUES (CURRENT_TIMESTAMP, ?, ?, ?, ?, ?)";
            final PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setBigDecimal(1, totalPrice);
            ps.setString(2, customerId);
            ps.setString(3,orderId);
            ps.setBigDecimal(4,amountPaid);
            ps.setString(5, paymentMethod().name());
            return ps;
        } catch (Exception e) {
            System.out.println("Error during saveOrder: {}"+ e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public enum PaymentMethod {
        BANK_BRI, SHOPEEPAY, DANA, CASH;

        public static PaymentMethod fromString(String str) {
            return Arrays.stream(PaymentMethod.values())
                    .filter(method -> method.name().equalsIgnoreCase(str))
                    .findFirst()
                    .orElse(CASH); // Default to CASH if not found
        }

    }
//    public enum PaymentMethod {
//        BANK_BRI, SHOPEEPAY,DANA, CASH;
//
//        public static PaymentMethod fromString(String str) {
//            if (BANK_BRI.name().equals(str)) {
//                return BANK_BRI;
//            } else if (SHOPEEPAY.name().equals(str)) {
//                return SHOPEEPAY;
//            } else if (DANA.name().equals(str)) {
//            return DANA;
//            }else {
//                return CASH;
//            }
//        }
//    }
}
