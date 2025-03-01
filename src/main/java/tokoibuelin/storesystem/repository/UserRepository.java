package tokoibuelin.storesystem.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import tokoibuelin.storesystem.entity.User;
import tokoibuelin.storesystem.model.Page;

import java.sql.*;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
@Repository
public class UserRepository {
    private static final Logger log = LoggerFactory.getLogger(UserRepository.class);

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Page<User> listUsers(int page, int size) {
        final int offset = (page - 1) * size;
        final String sql = "SELECT * FROM %s WHERE deleted_at is NULL ORDER BY user_id LIMIT ? OFFSET ?".formatted(User.TABLE_NAME);
        final String count = "SELECT COUNT(user_id) FROM %s".formatted(User.TABLE_NAME);

        final Long totalData = jdbcTemplate.queryForObject(count, Long.class);
        final Long totalPage = (totalData / size) + 1;

        final List<User> users = jdbcTemplate.query(sql, new Object[] { size, offset }, new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                final User.Role role = User.Role.fromString(rs.getString("role"));
                final Timestamp createdAt = rs.getTimestamp("created_at");
                final Timestamp updatedAt = rs.getTimestamp("updated_at");
                final Timestamp deletedAt = rs.getTimestamp("deleted_at");
                return new User(rs.getString("user_id"), //
                        rs.getString("name"), //
                        rs.getString("email"), //
                        rs.getString("password"), //
                        role, //
                        rs.getString("phone"),//
                        rs.getString("created_by"), //
                        rs.getString("updated_by"), //
                        rs.getString("deleted_by"), //
                        createdAt == null ? null : createdAt.toInstant().atOffset(ZoneOffset.UTC), //
                        updatedAt == null ? null : updatedAt.toInstant().atOffset(ZoneOffset.UTC), //
                        deletedAt == null ? null : deletedAt.toInstant().atOffset(ZoneOffset.UTC)); //
            }
        });
        return new Page<>(totalPage, totalPage, page, size, users);
    }
    public Optional<User> findByEmail(final String email) {
        if (email == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(jdbcTemplate.query(con -> {
            final PreparedStatement ps = con.prepareStatement("SELECT * FROM " + User.TABLE_NAME + " WHERE email=?");
            ps.setString(1, email);
            return ps;

        }, rs -> {
            if (!rs.next() ) {
                return null;
            }
            final String userId = rs.getString("user_id");
            final String name = rs.getString("name");
            final String password = rs.getString("password");
            final User.Role role = User.Role.valueOf(rs.getString("role"));
            final String phone = rs.getString("phone");
            final String createdBy = rs.getString("created_by");
            final String updatedBy = rs.getString("updated_by");
            final String deletedBy = rs.getString("deleted_by");
            final OffsetDateTime createdAt = rs.getTimestamp("created_at") == null ? null
                    : rs.getTimestamp("created_at").toInstant().atOffset(ZoneOffset.UTC);
            final OffsetDateTime updatedAt = rs.getTimestamp("updated_at") == null ? null
                    : rs.getTimestamp("updated_at").toInstant().atOffset(ZoneOffset.UTC);
            final OffsetDateTime deletedAt = rs.getTimestamp("deleted_at") == null ? null
                    : rs.getTimestamp("deleted_at").toInstant().atOffset(ZoneOffset.UTC);
            return new User(userId, name, email, password, role, phone, createdBy, updatedBy, deletedBy, createdAt, updatedAt,
                    deletedAt);
        }));
    }

    public Optional<User> findById(String id) {
        System.out.println("ID nya : " + id);
        if (id == null || id.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(jdbcTemplate.query(con -> {
            final PreparedStatement ps = con.prepareStatement("SELECT * FROM " + User.TABLE_NAME + " WHERE user_id=?");
            ps.setString(1, id);
            return ps;
        }, rs -> {
            if (rs.next()) { // Pindah ke baris pertama hasil
                final String userId = rs.getString("user_id");
                final String name = rs.getString("name");
                final String email = rs.getString("email");
                final String password = rs.getString("password");
                final User.Role role = User.Role.valueOf(rs.getString("role"));
                final String phone = rs.getString("phone");
                final String createdBy = rs.getString("created_by");
                final String updatedBy = rs.getString("updated_by");
                final String deletedBy = rs.getString("deleted_by");
                final OffsetDateTime createdAt = rs.getTimestamp("created_at") == null ? null : rs.getTimestamp("created_at").toInstant().atOffset(ZoneOffset.UTC);
                final OffsetDateTime updatedAt = rs.getTimestamp("updated_at") == null ? null : rs.getTimestamp("updated_at").toInstant().atOffset(ZoneOffset.UTC);
                final OffsetDateTime deletedAt = rs.getTimestamp("deleted_at") == null ? null : rs.getTimestamp("deleted_at").toInstant().atOffset(ZoneOffset.UTC);
                return new User(userId, name, email, password, role, phone, createdBy, updatedBy, deletedBy, createdAt, updatedAt, deletedAt);
            }
            return null;
        }));
    }


    public String saveUser(final User user) {
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            int updateCount = jdbcTemplate.update(con -> {
                PreparedStatement ps = user.insert(con);
                return ps;
            }, keyHolder);

            if (updateCount != 1) {
                return null; // atau return 0L sesuai kebutuhan
            }

            // Ambil hasil dari KeyHolder sebagai String
            Map<String, Object> keys = keyHolder.getKeys();
            if (keys != null && keys.containsKey("user_id")) {
                return (String) keys.get("user_id");
            }

            return null;
        } catch (Exception e) {
            log.error("Error during saveSupplier: {}", e.getMessage());
            return null; // atau return 0L sesuai kebutuhan
        }
    }





    public String resetPassword(String userId, String newPassword) {
        try {
            int rowsUpdated = jdbcTemplate.update(con -> {
                final PreparedStatement ps = con.prepareStatement("UPDATE " + User.TABLE_NAME
                        + " SET password=?, updated_by=?, updated_at=CURRENT_TIMESTAMP WHERE user_id=?");
                ps.setString(1, newPassword);
                ps.setString(2, userId);
                ps.setString(3, userId);
                return ps;
            });

            if (rowsUpdated > 0) {
                return userId;
            } else {
                return null;
            }
        } catch (DataAccessException e) {
            System.err.println("Error updating password for user id " + userId + ": " + e.getMessage());
            return null;
        }
    }

    public long deletedUser(User user) {
        try {
            String sql = "UPDATE " + User.TABLE_NAME + " SET deleted_at=CURRENT_TIMESTAMP, deleted_by=? WHERE user_id=?";
            return jdbcTemplate.update(sql, user.deletedBy(), user.userId());
        } catch (Exception e) {
            log.error("Failed to soft delete user: {}", e.getMessage());
            return 0L;
        }
    }

    public boolean updateUser(final User user) {
        final String sql = "UPDATE " + User.TABLE_NAME + " SET name = ?, email = ?, phone=?, updated_by = ?, updated_at = CURRENT_TIMESTAMP WHERE user_id = ?";
        try {
            int rowsAffected = jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, user.name());
                ps.setString(2,user.email());
                ps.setString(3,user.phone());
                ps.setString(4, user.userId());
                ps.setString(5, user.userId());
                return ps;
            });
            return rowsAffected > 0;
        } catch (Exception e) {
            log.error("Failed to update user: {}", e.getMessage());
            return false;
        }
    }

}
