package tokoibuelin.storesystem.entity;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.OffsetDateTime;

public record Sale(
        String saleId,
        OffsetDateTime saleDate,
        Long totalPrice,
        String customerId,
        String orderId,
        PaymentMethod paymentMethod,
        Long amountPaid
) {

    public static final String TABLE_NAME = "sales";

    public PreparedStatement insert(final Connection connection) {
        try {
            final String sql = "INSERT INTO " + TABLE_NAME + " (saleDate, total_price,customer_id,order_id,payment_method,amount_paid) VALUES (CURRENT_TIMESTAMP, ?, ?, ?, ?, ?)";
            final PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
//            ps.setString(1, saleDate);
            ps.setLong(1, totalPrice);
            ps.setString(2, customerId);
            ps.setString(3,orderId);
            ps.setString(4, paymentMethod().name());
            ps.setLong(5,amountPaid);
            return ps;
        } catch (Exception e) {
            return null;
        }
    }

    public enum PaymentMethod {
        BANK_BRI, SHOPEEPAY,DANA, CASH;

        public static PaymentMethod fromString(String str) {
            if (BANK_BRI.name().equals(str)) {
                return BANK_BRI;
            } else if (SHOPEEPAY.name().equals(str)) {
                return SHOPEEPAY;
            } else if (DANA.name().equals(str)) {
            return DANA;
            }else {
                return CASH;
            }
        }
    }
}
