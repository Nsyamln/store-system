package tokoibuelin.storesystem.entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.OffsetDateTime;

public record Sale(
        String saleId,
         OffsetDateTime saleDate,
         Long totalPrice,
         String customerId) {

    public static final String TABLE_NAME = "sales";

    public PreparedStatement insert(final Connection connection) {
        try {
            final String sql = "INSERT INTO " + TABLE_NAME + " (saleDate, total_price,customer_id) VALUES (CURRENT_TIMESTAMP, ?, ?)";
            final PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
//            ps.setString(1, saleDate);
            ps.setLong(1, totalPrice);
            ps.setString(2, customerId);
            return ps;
        } catch (Exception e) {
            return null;
        }
    }
}
