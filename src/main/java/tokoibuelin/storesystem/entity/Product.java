package tokoibuelin.storesystem.entity;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.OffsetDateTime;

public record Product (
        String productId,
        String productName,
        String description,
        BigDecimal unit,
        Long price,
        Long stock,
        String supplierId,
        String productImage,
        Long purchasePrice,
        String createdBy,
        String updatedBy,
        String deletedBy,
        OffsetDateTime createdAt, //
        OffsetDateTime updatedAt, //
        OffsetDateTime deletedAt

                      ) {

    public static final String TABLE_NAME = "products";

    public PreparedStatement insert(final Connection connection) {
        try {
            final String sql = "INSERT INTO " + TABLE_NAME + " (product_name, description,unit, price, stock, supplier_id,product_image,purchase_price, created_by,  created_at) VALUES (?, ?, ?, ?, ?, ?, ? ,? ,? CURRENT_TIMESTAMP)";
            final PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, productName);
            ps.setString(2, description);
            ps.setBigDecimal(3,unit);
            ps.setLong(4, price);
            ps.setLong(5, stock);
            ps.setString(6, supplierId);
            ps.setString(7, productImage);
            ps.setLong(8, purchasePrice);
            ps.setString(9, createdBy);
            return ps;
        } catch (Exception e) {
            return null;
        }
    }
}