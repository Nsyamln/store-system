package tokoibuelin.storesystem.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tokoibuelin.storesystem.service.ProfitSharingService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/profit-sharing")
public class ProfitSharingController {

    @Autowired
    private ProfitSharingService profitSharingService;

    @GetMapping("/pending-payments-summary/{supplierName}")
    public ResponseEntity<Map<String, Object>> getPendingPaymentsSummaryBySupplier(
            @PathVariable String supplierName) {
        Map<String, Object> pendingPaymentsSummary = profitSharingService.getPendingPaymentsSummaryBySupplier(supplierName);
        return ResponseEntity.ok(pendingPaymentsSummary);
    }

    @PostMapping("/update-payment-status/{supplierName}")
    public ResponseEntity<String> updatePaymentStatus(@PathVariable String supplierName) {
        profitSharingService.updatePaymentStatus(supplierName);
        return ResponseEntity.ok("Payment status updated successfully.");
    }
}
