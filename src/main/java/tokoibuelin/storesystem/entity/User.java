package tokoibuelin.storesystem.entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.OffsetDateTime;


public record User(String userId,
                   String name,
                   String email,
                   String password,
                   Role role,
                   String address,
                   String phone,
                   String createdBy,
                   String updatedBy,
                   String deletedBy,
                   OffsetDateTime createdAt, //
                   OffsetDateTime updatedAt, //
                   OffsetDateTime deletedAt) { //

    public static final String TABLE_NAME = "users";

    public PreparedStatement insert(final Connection connection) {
        try {
            final String sql = "INSERT INTO " + TABLE_NAME + " (name, email, password, role, address, phone, created_by, created_at) VALUES (?, ?, ?, ?,?,?, ?, CURRENT_TIMESTAMP)";
            final PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, password);
            ps.setString(4, role.name());
            ps.setString(5, address);
            ps.setString(6, phone);
            ps.setString(7, createdBy);
            return ps;
        } catch (Exception e) {
            return null;
        }
    }

    public enum Role {
        ADMIN, PEMASOK,PEMBELI, UNKNOWN;

        public static Role fromString(String str) {
            if (ADMIN.name().equals(str)) {
                return ADMIN;
            } else if (PEMBELI.name().equals(str)) {
                return PEMBELI;
            } else if (PEMASOK.name().equals(str)) {
                return PEMASOK;
            } else {
                return UNKNOWN;
            }
        }
    }
}
