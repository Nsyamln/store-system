package tokoibuelin.storesystem.entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.OffsetDateTime;

public record Product (
        String productName,
        String description,
        Long price,
        Long stock,
        String supplierId,
        String productImage,
        String createdBy,
        String updatedBy,
        String deletedBy,
        OffsetDateTime createdAt, //
        OffsetDateTime updatedAt, //
        OffsetDateTime deletedAt

                      ){

    public static final String TABLE_NAME = "products";

    public PreparedStatement insert(final Connection connection) {
        try {
            final String sql = "INSERT INTO " + TABLE_NAME + " (product_name, description, price, stock, supplier_id,product_image,created_by,  created_at) VALUES (?, ?, ?, ?, ? ,? ,? CURRENT_TIMESTAMP)";
            final PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, productName);
            ps.setString(2, description);
            ps.setLong(3, price);
            ps.setLong(4, stock);
            ps.setString(5, supplierId);
            ps.setString(6, productImage);
            ps.setString(7, createdBy);
            return ps;
        } catch (Exception e) {
            return null;
        }
    }


}
