package tokoibuelin.storesystem.entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

public record SaleDetails(
     String detailId,
     String saleId,
     String productId,
     Long quantity,
     Long price


) {
    public static final String TABLE_NAME = "sale_details";

    public PreparedStatement insert(final Connection connection) {
        try {
            final String sql = "INSERT INTO " + TABLE_NAME + " (saleDate, total_price,customer_id) VALUES (?, ?, ?)";
            final PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, saleId);
            ps.setString(2, productId);
            ps.setLong(3, quantity);
            ps.setLong(3, price);
            return ps;
        } catch (Exception e) {
            return null;
        }
    }
}
