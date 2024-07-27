package tokoibuelin.storesystem.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import tokoibuelin.storesystem.entity.Order;
import tokoibuelin.storesystem.entity.Sale;
import tokoibuelin.storesystem.entity.SaleDetails;
import tokoibuelin.storesystem.entity.User;
import tokoibuelin.storesystem.model.Authentication;
import tokoibuelin.storesystem.model.Response;
import tokoibuelin.storesystem.model.request.OnlineSaleReq;
import tokoibuelin.storesystem.model.request.ReportReq;
import tokoibuelin.storesystem.model.request.OfflineSaleReq;
import tokoibuelin.storesystem.repository.OrderRepository;
import tokoibuelin.storesystem.repository.SaleRepository;

import java.io.IOException;
import java.io.OutputStream;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SaleService extends AbstractService{
    private final SaleRepository saleRepository;

    private final OrderRepository orderRepository;
    public SaleService(final SaleRepository saleRepository,final OrderRepository orderRepository){
        this.saleRepository = saleRepository;
        this.orderRepository = orderRepository;
    }

    public Response<Object> createOnlineSale(final Authentication authentication, final OnlineSaleReq req) {
        return precondition(authentication, User.Role.PEMBELI).orElseGet(() -> {
            if (req == null) {
                return Response.badRequest();
            }
            final Order order = new Order(
                    null,
                    req.orderDate(),
                    req.customerId(),
                    req.deliveryAddress(),
                    Order.Status.PENDING,
                    authentication.id(),
                    null,
                    OffsetDateTime.now(),
                    null
            );
            final Long savedOrder = orderRepository.saveOrder(order);
            if (0L == savedOrder  ) {
                return Response.create("05", "01", "Gagal menambahkan Order", null);
            }

            final Sale sale = new Sale( //
                    null, //
                    OffsetDateTime.now(),//
                    req.totalPrice(), //
                    req.customerId(),
                    order.orderId(),
                    Sale.PaymentMethod.fromString(req.paymentMethod()),
                    req.amountPaid()
            );

            final Long savedSale = saleRepository.saveSale(sale);
            if (0L == savedSale ) {
                return Response.create("05", "01", "Gagal menambahkan Penjualan", null);
            }

            List<SaleDetails> saleDetails = req.saleDetails().stream().map(detailReq -> new SaleDetails(
                    null,
                    detailReq.saleId(),
                    detailReq.productId(),
                    detailReq.productName(),
                    detailReq.quantity(),
                    detailReq.price()
            )).collect(Collectors.toList());
            final Long savedSaleDetails = saleRepository.saveSaleDetails(saleDetails);
            if (0L == savedSaleDetails ) {
                return Response.create("05", "01", "Gagal menambahkan Penjualan", null);
            }
            return Response.create("05", "00", "Sukses", true);
        });
    }

    public Response<Object> createOfflineSale(final Authentication authentication, final OfflineSaleReq req) {
        return precondition(authentication, User.Role.ADMIN).orElseGet(() -> {
            if (req == null) {
                return Response.badRequest();
            }

            final Sale sale = new Sale( //
                    null, //
                    OffsetDateTime.now(),//
                    req.totalPrice(), //
                    req.customerId(),
                    null,
                    Sale.PaymentMethod.fromString(req.paymentMethod()),
                    req.amountPaid()
            );

            final Long savedSale = saleRepository.saveSale(sale);

            if (0L == savedSale   ) {
                return Response.create("05", "01", "Gagal menambahkan Product", null);
            }
            List<SaleDetails> saleDetails = req.saleDetails().stream().map(detailReq -> new SaleDetails(
                    null,
                    detailReq.saleId(),
                    detailReq.productId(),
                    detailReq.productName(),
                    detailReq.quantity(),
                    detailReq.price()
            )).collect(Collectors.toList());
            final Long savedSaleDetails = saleRepository.saveSaleDetails(saleDetails);
            return Response.create("05", "00", "Sukses", true);
        });
    }

    //    public void generateSalesReportPDF(ReportReq reportRequest, OutputStream out) throws DocumentException {
//        List<Sale> sales = saleRepository.findSalesByDateRange(reportRequest.startedAt(), reportRequest.endedAt());
//        List<SaleDetails> saleDetails = saleRepository.findSaleDetailsBySaleIds(
//                sales.stream().map(Sale::saleId).toList()
//        );
//
//        Document document = new Document();
//        PdfWriter.getInstance(document, out);
//        document.open();
//
//        Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
//
//        document.add(new Paragraph("Sales Report", boldFont));
//        document.add(new Paragraph("Generated on: " + reportRequest.startedAt().format(DateTimeFormatter.ISO_DATE_TIME)));
//        document.add(new Paragraph("Report Period: " + reportRequest.startedAt().format(DateTimeFormatter.ISO_DATE) + " to " + reportRequest.endedAt().format(DateTimeFormatter.ISO_DATE)));
//        document.add(new Paragraph(" "));
//
//        PdfPTable table = new PdfPTable(12);
//        table.setWidthPercentage(100);
//        String[] headers = {"Sale ID", "Sale Date", "Total Price", "Customer ID", "Order ID", "Payment Method", "Amount Paid", "Detail ID", "Product ID", "Product Name", "Quantity", "Price"};
//        for (String header : headers) {
//            PdfPCell cell = new PdfPCell(new Paragraph(header, boldFont));
//            table.addCell(cell);
//        }
//
//        // Add data rows
//        document.add(table);
//        document.close();
//    }

    public void generateSalesReport(ReportReq reportRequest, OutputStream out) throws IOException {
        // Ambil parameter dari reportRequest
        OffsetDateTime startedAt = reportRequest.startedAt();
        OffsetDateTime endedAt = reportRequest.endedAt();

        // Ambil data dari repository
        List<Sale> sales = saleRepository.findSalesByDateRange(startedAt, endedAt);
        List<SaleDetails> saleDetails = saleRepository.findSaleDetailsBySaleIds(
                sales.stream().map(Sale::saleId).toList()
        );

        // Buat workbook dan sheet
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sales Report");

            // Buat baris header
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Sale ID", "Sale Date", "Total Price", "Customer ID", "Order ID", "Payment Method", "Amount Paid", "Detail ID", "Product ID", "Product Name", "Quantity", "Price"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(createHeaderCellStyle(workbook));
            }

            // Isi data
            int rowNum = 1;
            for (Sale sale : sales) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(sale.saleId());
                row.createCell(1).setCellValue(sale.saleDate().toString());
                row.createCell(2).setCellValue(sale.totalPrice());
                row.createCell(3).setCellValue(sale.customerId());
                row.createCell(4).setCellValue(sale.orderId());
                row.createCell(5).setCellValue(sale.paymentMethod().name());
                row.createCell(6).setCellValue(sale.amountPaid());

                // Ambil detail penjualan yang terkait
                for (SaleDetails detail : saleDetails) {
                    if (detail.saleId().equals(sale.saleId())) {
                        Row detailRow = sheet.createRow(rowNum++);
                        detailRow.createCell(7).setCellValue(detail.detailId());
                        detailRow.createCell(8).setCellValue(detail.productId());
                        detailRow.createCell(9).setCellValue(detail.productName());
                        detailRow.createCell(10).setCellValue(detail.quantity());
                        detailRow.createCell(11).setCellValue(detail.price());
                    }
                }
            }

            workbook.write(out);
        }
    }

    private CellStyle createHeaderCellStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    public Response<Object> generateReport(ReportReq request) {
        OffsetDateTime startedAt = request.startedAt();
        OffsetDateTime endedAt = request.endedAt();

        List<Sale> sales = saleRepository.findSalesByDateRange(startedAt, endedAt);
        if (sales.isEmpty()) {
            return Response.create("02","02","No sales found", null);
        }

        List<String> saleIds = sales.stream().map(Sale::saleId).toList();
        List<SaleDetails> saleDetails = saleRepository.findSaleDetailsBySaleIds(saleIds);

        return Response.create("02","00", "Report generated successfully", new ReportData(sales, saleDetails));
    }

    // Helper class to wrap the report data
    public static class ReportData {
        private final List<Sale> sales;
        private final List<SaleDetails> saleDetails;

        public ReportData(List<Sale> sales, List<SaleDetails> saleDetails) {
            this.sales = sales;
            this.saleDetails = saleDetails;
        }

        public List<Sale> getSales() {
            return sales;
        }

        public List<SaleDetails> getSaleDetails() {
            return saleDetails;
        }
    }
}
