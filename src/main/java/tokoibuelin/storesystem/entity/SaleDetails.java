package tokoibuelin.storesystem.entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

public record SaleDetails(
     String detailId,
     String saleId,
     String productId,
     String productName,
     Integer quantity,
     Integer price,
     Integer unit


) {
    public static final String TABLE_NAME = "sale_details";


}
