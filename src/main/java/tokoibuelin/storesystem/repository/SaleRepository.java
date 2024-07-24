package tokoibuelin.storesystem.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import tokoibuelin.storesystem.entity.Sale;
import tokoibuelin.storesystem.entity.SaleDetails;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class SaleRepository {
    private static final Logger log = LoggerFactory.getLogger(SaleRepository.class);
    private final JdbcTemplate jdbcTemplate;

    public SaleRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public long saveSale(final Sale sale) {
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            if (jdbcTemplate.update(con -> Objects.requireNonNull(sale.insert(con)), keyHolder) != 1) {
                return 0L;
            } else {
                return Objects.requireNonNull(keyHolder.getKey()).longValue();
            }
        } catch (Exception e) {
            log.error("{}", e.getMessage());
            return 0L;
        }
    }
    public long saveSaleDetails(List<SaleDetails> saleDetails) {
        String sqlDetail = "INSERT INTO sale_details (detail_id, sale_id, product_id,product_name, quantity, price) VALUES (?, ?, ?, ?, ?)";

        try {
            for (SaleDetails detail : saleDetails) {
                jdbcTemplate.update(sqlDetail, detail.detailId(), detail.saleId(), detail.productId(),detail.productName(), detail.quantity(), detail.price());
            }
            return 1L;
        } catch (Exception e) {
            log.error("{}", e.getMessage());
            return 0L;
        }
    }

    public List<Sale> findSalesByDateRange(OffsetDateTime startedAt, OffsetDateTime endedAt) {
        String sql = "SELECT s.sale_id, s.sale_date, s.total_price, s.customer_id" +
                "FROM sales s WHERE s.sale_date BETWEEN ? AND ?";
        return jdbcTemplate.query(sql, new Object[]{startedAt, endedAt}, new SaleRowMapper());
    }

    public List<SaleDetails> findSaleDetailsBySaleIds(List<String> saleIds) {
        String sql = "SELECT sd.detail_id, sd.sale_id, sd.product_id, sd.product_name ,sd.quantity, sd.price " +
                "FROM sale_details sd WHERE sd.sale_id IN (" +
                saleIds.stream().map(id -> "?").collect(Collectors.joining(",")) + ")";
        return jdbcTemplate.query(sql, saleIds.toArray(), new SaleDetailsRowMapper());
    }

    private static class SaleRowMapper implements RowMapper<Sale> {
        @Override
        public Sale mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Sale(
                    rs.getString("sale_id"),
                    ((java.sql.Timestamp) rs.getObject("sale_date")).toInstant().atOffset(OffsetDateTime.now().getOffset()),
                    rs.getLong("total_price"),
                    rs.getString("customer_id"),
                    rs.getString("order_id"),
                    Sale.PaymentMethod.valueOf(rs.getString("payment_method")),
                    rs.getLong("amount_paid")
            );
        }
    }

    private static class SaleDetailsRowMapper implements RowMapper<SaleDetails> {
        @Override
        public SaleDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new SaleDetails(
                    rs.getString("detail_id"),
                    rs.getString("sale_id"),
                    rs.getString("product_id"),
                    rs.getString("product_name"),
                    rs.getLong("quantity"),
                    rs.getLong("price")
            );
        }
    }


}
