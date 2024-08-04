package tokoibuelin.storesystem.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import tokoibuelin.storesystem.entity.Order;
import tokoibuelin.storesystem.entity.Sale;
import tokoibuelin.storesystem.entity.SaleDetails;

import java.sql.PreparedStatement;
import java.util.*;

@Repository
public class SaleRepository {
    private static final Logger log = LoggerFactory.getLogger(SaleRepository.class);
    private final JdbcTemplate jdbcTemplate;

    public SaleRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public String saveSale(final Sale sale) {
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            int updateCount = jdbcTemplate.update(con -> {
                PreparedStatement ps = sale.insert(con);
                System.out.println("Cek save Sale -> "+ps.toString());
                return ps;
            }, keyHolder);

            if (updateCount != 1) {
                log.warn("Update count was not 1, it was: {}", updateCount);
                return null;
            }

            Map<String, Object> keys = keyHolder.getKeys();
            if (keys != null && keys.containsKey("sale_id")) {
                return (String) keys.get("sale_id");
            }

            return null;
        } catch (Exception e) {
            log.error("Error during saveSale: {}", e.getMessage());
            return null;
        }
    }


    public long saveSaleDetails(List<SaleDetails> saleDetails) {
        String sqlDetail = "INSERT INTO sale_details (sale_id, product_id, product_name, quantity, price, unit) VALUES (?, ?, ?, ?, ?, ?)";

        try {
            for (SaleDetails detail : saleDetails) {
                jdbcTemplate.update(sqlDetail, detail.saleId(), detail.productId(), detail.productName(), detail.quantity(), detail.price(), detail.unit());
            }
            return 1L;
        } catch (Exception e) {
            log.error("{}", e.getMessage());
            e.printStackTrace(); // Tambahkan ini untuk debugging
            return 0L;
        }
    }






}
