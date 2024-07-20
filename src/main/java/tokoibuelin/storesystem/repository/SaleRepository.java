package tokoibuelin.storesystem.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import tokoibuelin.storesystem.entity.Sale;
import tokoibuelin.storesystem.entity.SaleDetails;

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
        String sqlDetail = "INSERT INTO sale_details (detail_id, sale_id, product_id, quantity, price) VALUES (?, ?, ?, ?, ?)";

        try {
            for (SaleDetails detail : saleDetails) {
                jdbcTemplate.update(sqlDetail, detail.detailId(), detail.saleId(), detail.productId(), detail.quantity(), detail.price());
            }
            return 1L;
        } catch (Exception e) {
            log.error("{}", e.getMessage());
            return 0L;
        }
    }

    public Optional<List<SaleDetails>> findSaleDetailsByDateRange(OffsetDateTime startedAt, OffsetDateTime endedAt) {
        String sql = "SELECT " +
                "s.sale_id, " +
                "s.sale_date, " +
                "s.total_price, " +
                "s.customer_id, " +
                "sd.detail_id, " +
                "sd.product_id, " +
                "sd.quantity, " +
                "sd.price " +
                "FROM sales s " +
                "JOIN sale_details sd ON s.sale_id = sd.sale_id " +
                "WHERE s.sale_date BETWEEN :startedAt AND :endedAt";

        Map<String, Object> params = new HashMap<>();
        params.put("startedAt", startedAt);
        params.put("endedAt", endedAt);

        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, params);

        if (result.isEmpty()) {
            return Optional.empty();
        } else {
            List<SaleDetails> saleDetails = result.stream().map(row -> new SaleDetails(
                    (String) row.get("sale_id"),
                    ((java.sql.Timestamp) row.get("sale_date")).toLocalDateTime(),
                    (java.math.BigDecimal) row.get("total_price"),
                    (String) row.get("customer_id"),
                    (String) row.get("detail_id"),
                    (String) row.get("product_id"),
                    (Integer) row.get("quantity"),
                    (java.math.BigDecimal) row.get("price")
            )).collect(Collectors.toList());

            return Optional.of(saleDetails);
        }
    }


}
