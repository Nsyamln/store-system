package tokoibuelin.storesystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tokoibuelin.storesystem.entity.ProfitSharing;
import tokoibuelin.storesystem.model.response.ProfitSharingSummary;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProfitSharingService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Map<String, Object> getPendingPaymentsSummaryBySupplier(String supplierName) {
        String query = "SELECT * FROM profit_sharing WHERE status = 'PENDING' AND supplier_id = (SELECT user_id FROM users WHERE name = ?)";

        List<ProfitSharing> pendingPayments = jdbcTemplate.query(query, new Object[]{supplierName},
                (rs, rowNum) -> new ProfitSharing(
                        rs.getString("profit_sharing_id"),
                        rs.getString("sale_id"),
                        rs.getString("product_id"),
                        rs.getString("supplier_id"),
                        rs.getInt("product_quantity"),
                        rs.getBigDecimal("total_purchase_price"),
                        rs.getBigDecimal("total_sale_price"),
                        rs.getString("status"),
                        rs.getObject("payment_date", OffsetDateTime.class)
                ));

        double totalAmountToPay = pendingPayments.stream()
                .mapToDouble(payment -> payment.totalPurchasePrice().doubleValue())
                .sum();

        List<ProfitSharingSummary> summaries = pendingPayments.stream().map(payment -> new ProfitSharingSummary(
                payment.profitSharingId(),
                payment.saleId(),
                payment.productId(),
                payment.supplierId(),
                payment.productQuantity(),
                payment.totalPurchasePrice()
        )).collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("totalAmountToPay", totalAmountToPay);
        result.put("details", summaries);

        return result;
    }

    @Transactional
    public void updatePaymentStatus(String supplierName) {
        String selectQuery = "SELECT * FROM profit_sharing WHERE status = 'PENDING' AND supplier_id = (SELECT user_id FROM users WHERE name = ?)";
        String updateQuery = "UPDATE profit_sharing SET status = 'PAID', payment_date = ? WHERE profit_sharing_id = ?";

        List<ProfitSharing> paymentsToUpdate = jdbcTemplate.query(selectQuery, new Object[]{supplierName},
                (rs, rowNum) -> new ProfitSharing(
                        rs.getString("profit_sharing_id"),
                        rs.getString("sale_id"),
                        rs.getString("product_id"),
                        rs.getString("supplier_id"),
                        rs.getInt("product_quantity"),
                        rs.getBigDecimal("total_purchase_price"),
                        rs.getBigDecimal("total_sale_price"),
                        rs.getString("status"),
                        rs.getObject("payment_date", OffsetDateTime.class)

                ));

        for (ProfitSharing payment : paymentsToUpdate) {
            jdbcTemplate.update(updateQuery, LocalDateTime.now(), payment.profitSharingId());
        }
    }
}
