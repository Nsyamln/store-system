package tokoibuelin.storesystem.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
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
public class ProductRepository {
    private static final Logger log = LoggerFactory.getLogger(ProductRepository.class);
    private final JdbcTemplate jdbcTemplate;

    public ProductRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Page<Product> listProducts(int page, int size) {
        final int offset = (page - 1) * size;
        final String sql = "SELECT * FROM %s WHERE deleted_at is NULL ORDER BY id LIMIT ? OFFSET ?".formatted(Product.TABLE_NAME);
        final String count = "SELECT COUNT(id) FROM %s".formatted(Product.TABLE_NAME);

        final Long totalData = jdbcTemplate.queryForObject(count, Long.class);
        final Long totalPage = (totalData / size) + 1;

        final List<Product> products = jdbcTemplate.query(sql, new Object[] { size, offset }, new RowMapper<Product>() {
            @Override
            public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
                final Timestamp createdAt = rs.getTimestamp("created_at");
                final Timestamp updatedAt = rs.getTimestamp("updated_at");
                final Timestamp deletedAt = rs.getTimestamp("deleted_at");
                return new Product(
                        rs.getString("product_id"),
                        rs.getString("product_name"),
                        rs.getString("description"),
                        rs.getLong("price"),
                        rs.getLong("stock"),
                        rs.getString("supplier_id"),
                        rs.getString("product_image"),
                        rs.getLong("purchase_price"),
                        rs.getString("created_by"),
                        rs.getString("updated_by"),
                        rs.getString("deleted_by"),
                        createdAt == null ? null : createdAt.toInstant().atOffset(ZoneOffset.UTC),
                        updatedAt == null ? null : updatedAt.toInstant().atOffset(ZoneOffset.UTC),
                        deletedAt == null ? null : deletedAt.toInstant().atOffset(ZoneOffset.UTC)
                );
            }
        });

        return new Page<>(totalPage, totalPage, page, size, products);
    }

    public Optional<Product> findById(String  id) {
        System.out.println("ID nya : " + id);
        if (id == null || id == "") {
            return Optional.empty();
        }
        return Optional.ofNullable(jdbcTemplate.query(con -> {
            final PreparedStatement ps = con.prepareStatement("SELECT * FROM " + Product.TABLE_NAME + " WHERE product_id=?");
            ps.setString(1, id);
            return ps;
        }, rs -> {
            if (rs.getString("id")== null ) {
                return null;
            }
            final String productName = rs.getString("product_name");
            final String description = rs.getString("description");
            final Long price = rs.getLong("price");
            final Long stock = rs.getLong("stock");
            final String supplierId = rs.getString("supplier_id");
            final String productImage = rs.getString("product_image");
            final Long purchasePrice = rs.getLong("purchase_price");
            final String createdBy = rs.getString("created_by");
            final String updatedBy = rs.getString("updated_by");
            final String deletedBy = rs.getString("deleted_by");
            final OffsetDateTime createdAt = rs.getTimestamp("created_at") == null ? null : rs.getTimestamp("created_at").toInstant().atOffset(ZoneOffset.UTC);
            final OffsetDateTime updatedAt = rs.getTimestamp("updated_at") == null ? null : rs.getTimestamp("updated_at").toInstant().atOffset(ZoneOffset.UTC);
            final OffsetDateTime deletedAt = rs.getTimestamp("deleted_at") == null ? null : rs.getTimestamp("deleted_at").toInstant().atOffset(ZoneOffset.UTC);
            return new Product(id, productName, description, price, stock, supplierId, productImage,purchasePrice, createdBy, updatedBy, deletedBy, createdAt, updatedAt, deletedAt);
        }));
    }

    public long saveProduct(final Product product) {
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            if (jdbcTemplate.update(con -> Objects.requireNonNull(product.insert(con)), keyHolder) != 1) {
                return 0L;
            } else {
                return Objects.requireNonNull(keyHolder.getKey()).longValue();
            }
        } catch (Exception e) {
            log.error("{}", e.getMessage());
            return 0L;
        }
    }

    public long deleteProduct(Product product) {
        try {
            String sql = "UPDATE " + Product.TABLE_NAME + " SET deleted_at=CURRENT_TIMESTAMP, deleted_by=? WHERE product_id=?";
            return jdbcTemplate.update(sql, product.deletedBy(), product.productId());
        } catch (Exception e) {
            log.error("Gagal untuk menghapus produk: {}", e.getMessage());
            return 0L;
        }
    }

    public boolean updateProduct(final Product product, final String userId) {
        final String sql = "UPDATE " + Product.TABLE_NAME + " SET product_name = ?,description = ?,price=?,product_image=?,updated_by=?, updated_at = CURRENT_TIMESTAMP WHERE product_id = ?";
        try {
            int rowsAffected = jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, product.productName());
                ps.setString(2,product.description());
                ps.setLong(3,product.price());
                ps.setString(4, product.productImage());
                ps.setString(5,userId);
                ps.setString(6,product.productId());
                return ps;
            });
            return rowsAffected > 0;
        } catch (Exception e) {
            log.error("Gagal untuk update produk: {}", e.getMessage());
            return false;
        }
    }

    public boolean updateStockProduct(final Product product,Long newStock) {
        final String sql = "UPDATE " + Product.TABLE_NAME + " SET stock=? WHERE product_id = ?";
        try {
            int rowsAffected = jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setLong(1, newStock);
                ps.setString(2,product.productId());
                return ps;
            });
            return rowsAffected > 0;
        } catch (Exception e) {
            log.error("Gagal untuk update stock produk: {}", e.getMessage());
            return false;
        }
    }
}
