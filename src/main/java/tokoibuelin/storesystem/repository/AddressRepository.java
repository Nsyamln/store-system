package tokoibuelin.storesystem.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import tokoibuelin.storesystem.entity.Address;
import tokoibuelin.storesystem.entity.User;

import java.sql.PreparedStatement;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.Optional;

@Repository
public class AddressRepository {
    private static final Logger log = LoggerFactory.getLogger(AddressRepository.class);

    private final JdbcTemplate jdbcTemplate;

    public AddressRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long saveAddress(final Address address) {
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            if (jdbcTemplate.update(con -> Objects.requireNonNull(address.insert(con)), keyHolder) != 1) {
                return 0L;
            } else {
                return Objects.requireNonNull(keyHolder.getKey()).longValue();
            }
        } catch (Exception e) {
            log.error("{}", e.getMessage());
            return 0L;
        }
    }
    public Optional<Address> findById(String userid) {
        System.out.println("ID nya : " + userid);
        if (userid == null || userid == "") {
            return Optional.empty();
        }
        return Optional.ofNullable(jdbcTemplate.query(con -> {
            final PreparedStatement ps = con.prepareStatement("SELECT * FROM " + Address.TABLE_NAME + " WHERE user_id=?");
            ps.setString(1, userid);
            return ps;
        }, rs -> {
            if (rs.getString("address_id")== null ) {
                return null;
            }
            final String userId = rs.getString("user_id");
            final String street = rs.getString("street");
            final String rt = rs.getString("rt");
            final String rw = rs.getString("rw");
            final String village = rs.getString("village");
            final String district = rs.getString("district");
            final String city = rs.getString("city");
            final String postalCode = rs.getString("postal_code");
            return new Address(userid,userId, street, rt, rw, village, district, city, postalCode);
        }));
    }

    public String formatAddress(String addressId) {
        String sql = "SELECT street, rt, rw, village, district, city, postal_code FROM addresses WHERE address_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{addressId}, (rs, rowNum) -> {
            String street = rs.getString("street");
            String rt = rs.getString("rt");
            String rw = rs.getString("rw");
            String village = rs.getString("village");
            String district = rs.getString("district");
            String city = rs.getString("city");
            String postalCode = rs.getString("postal_code");
            return String.format("%s, RT %s/RW %s, %s, %s, %s, %s", street, rt, rw, village, district, city, postalCode);
        });
    }

    public boolean updateAddress(final Address address) {
        final String sql = "UPDATE " + Address.TABLE_NAME + " SET street = ?, rt = ?, rw=?, village = ?, district=?, city=?, postal_code=? WHERE address_id = ?";
        try {
            int rowsAffected = jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, address.street());
                ps.setString(2,address.rt());
                ps.setString(3,address.rw());
                ps.setString(4, address.village());
                ps.setString(5, address.district());
                ps.setString(5, address.city());
                ps.setString(5, address.postalCode());
                return ps;
            });
            return rowsAffected > 0;
        } catch (Exception e) {
            log.error("Failed to update Address: {}", e.getMessage());
            return false;
        }
    }
}
