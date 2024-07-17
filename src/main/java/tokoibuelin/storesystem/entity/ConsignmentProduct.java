package tokoibuelin.storesystem.entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.OffsetDateTime;

public record ConsignmentProduct(
                                    String consignmentId,
                                 String supplierId,
                                 String productId,
                                 OffsetDateTime consignmentDate,
                                 Long consignmentQuantity,
                                 Long purchasePrice

                                 ) {
    public static final String TABLE_NAME = "consigntment_products";
    public PreparedStatement insert(final Connection connection) {
        try {
            final String sql = "INSERT INTO " + TABLE_NAME + " (supplier_id, product_id, consignment_date,consignment_quantity, purchase_price) VALUES (?, ?,  CURRENT_TIMESTAMP,? ,?)";
            final PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, supplierId);
            ps.setString(2, productId);
            ps.setLong(3, consignmentQuantity);
            ps.setLong(4, purchasePrice);
            return ps;
        } catch (Exception e) {
            return null;
        }
    }
}
