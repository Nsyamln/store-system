package tokoibuelin.storesystem.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import tokoibuelin.storesystem.entity.ConsignmentProduct;
import tokoibuelin.storesystem.entity.Product;
import tokoibuelin.storesystem.model.Page;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class ConsignmentProductRepository {
    private static final Logger log = LoggerFactory.getLogger(ProductRepository.class);
    private final JdbcTemplate jdbcTemplate;

    public ConsignmentProductRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    public Page<ConsignmentProduct> listConsignmentProducts(int page, int size) {
        final int offset = (page - 1) * size;
        final String sql = "SELECT * FROM %s WHERE deleted_at is NULL ORDER BY id LIMIT ? OFFSET ?".formatted(ConsignmentProduct.TABLE_NAME);
        final String count = "SELECT COUNT(id) FROM %s".formatted(ConsignmentProduct.TABLE_NAME);

        final Long totalData = jdbcTemplate.queryForObject(count, Long.class);
        final Long totalPage = (totalData / size) + 1;

        final List<ConsignmentProduct> consignmentProductsproducts = jdbcTemplate.query(sql, new Object[] { size, offset }, new RowMapper<ConsignmentProduct>() {
            @Override
            public ConsignmentProduct mapRow(ResultSet rs, int rowNum) throws SQLException {
                final Timestamp consignmentDate = rs.getTimestamp("consignment_date");
                return new ConsignmentProduct(
                        rs.getString("consignment_id"),
                        rs.getString("supplier_id"),
                        rs.getString("product_id"),
                        consignmentDate == null ? null : consignmentDate.toInstant().atOffset(ZoneOffset.UTC),
                        rs.getLong("consignment_quantity"),
                        rs.getLong("purchase_price")

                );
            }
        });

        return new Page<>(totalPage, totalPage, page, size, consignmentProductsproducts);
    }

    public Optional<ConsignmentProduct> findById(String  id) {
        System.out.println("ID nya : " + id);
        if (id == null || id == "") {
            return Optional.empty();
        }
        return Optional.ofNullable(jdbcTemplate.query(con -> {
            final PreparedStatement ps = con.prepareStatement("SELECT * FROM " + ConsignmentProduct.TABLE_NAME + " WHERE consignment_id=?");
            ps.setString(1, id);
            return ps;
        }, rs -> {
            if (rs.getString("id")== null ) {
                return null;
            }
            final String supplierId = rs.getString("supplier_id");
            final String productId = rs.getString("product_id");
            final OffsetDateTime consignmentDate = rs.getTimestamp("consignment_date") == null ? null : rs.getTimestamp("created_at").toInstant().atOffset(ZoneOffset.UTC);
            final Long consignmentQuantity = rs.getLong("consignment_quantity");
            final Long purchasePrice = rs.getLong("purchase_price");
            return new ConsignmentProduct(id,supplierId, productId, consignmentDate,consignmentQuantity,purchasePrice);
        }));
    }

    public long saveConsigmentProduct(final ConsignmentProduct consignmentProduct) {
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            if (jdbcTemplate.update(con -> Objects.requireNonNull(consignmentProduct.insert(con)), keyHolder) != 1) {
                return 0L;
            } else {
                return Objects.requireNonNull(keyHolder.getKey()).longValue();
            }
        } catch (Exception e) {
            log.error("{}", e.getMessage());
            return 0L;
        }
    }

//    public long deleteConsignmentProduct(ConsignmentProduct consignmentProduct) {
//        try {
//            String sql = "UPDATE " + ConsignmentProduct.TABLE_NAME + " SET deleted_at=CURRENT_TIMESTAMP, deleted_by=? WHERE consignment_id=?";
//            return jdbcTemplate.update(sql, consignmentProduct.deletedBy(), consignmentProduct.productId());
//        } catch (Exception e) {
//            log.error("Gagal untuk menghapus produk: {}", e.getMessage());
//            return 0L;
//        }
//    }

    public boolean updateConsignmentProduct(final ConsignmentProduct consignmentProduct) {
        final String sql = "UPDATE " + Product.TABLE_NAME + " SET purchase_price=? WHERE consignment_id = ?";
        try {
            int rowsAffected = jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setLong(1, consignmentProduct.purchasePrice());
                ps.setString(2,consignmentProduct.consignmentId());
                return ps;
            });
            return rowsAffected > 0;
        } catch (Exception e) {
            log.error("Gagal untuk update produk titipan : {}", e.getMessage());
            return false;
        }
    }

    public boolean updateStockConsignmentProduct(final ConsignmentProduct consignmentProduct,Long newStock) {
        final String sql = "UPDATE " + ConsignmentProduct.TABLE_NAME + " SET consignment_quantity=? WHERE consignment_id = ?";
        try {
            int rowsAffected = jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setLong(1, newStock);
                ps.setString(2,consignmentProduct.consignmentId());
                return ps;
            });
            return rowsAffected > 0;
        } catch (Exception e) {
            log.error("Gagal untuk update stock produk titipan: {}", e.getMessage());
            return false;
        }
    }

}
