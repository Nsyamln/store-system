package tokoibuelin.storesystem.service;

import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tokoibuelin.storesystem.entity.User;
import tokoibuelin.storesystem.model.Authentication;
import tokoibuelin.storesystem.model.request.*;
import tokoibuelin.storesystem.model.Response;
import tokoibuelin.storesystem.model.response.UserDto;
import tokoibuelin.storesystem.repository.UserRepository;
import tokoibuelin.storesystem.util.HexUtils;
import tokoibuelin.storesystem.util.JwtUtils;

import java.time.OffsetDateTime;
import java.util.Optional;
@Service
public class UserService extends AbstractService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final byte[] jwtKey;

    public UserService(final Environment env, final UserRepository userRepository, final PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        final String skJwtKey = env.getProperty("jwt.secret-key");
        this.jwtKey = HexUtils.hexToBytes(skJwtKey);
    }

    public Response<Object> login(final LoginReq req) {
        if (req == null) {
            return Response.badRequest();
        }
        final Optional<User> userOpt = userRepository.findByEmail(req.email());
        if (userOpt.isEmpty()) {
            return Response.create("08", "01", "Email  salah", null);
        }
        final User user = userOpt.get();
        if (!passwordEncoder.matches(req.password(), user.password())) {
            return Response.create("08", "02", "password salah", null);
        }
        final Authentication authentication = new Authentication(user.userId(), user.role(), true);
        System.out.println("auth : "+authentication);
        final long iat = System.currentTimeMillis();
        final long exp = 1000 * 60 * 60 * 24; // 24 hour
        final JwtUtils.Header header = new JwtUtils.Header() //
                .add("typ", "JWT") //
                .add("alg", "HS256"); //
        final JwtUtils.Payload payload = new JwtUtils.Payload() //
                .add("sub", authentication.id()) //
                .add("role", user.role().name()) //
                .add("iat", iat) //
                .add("exp", exp); //
        final String token = JwtUtils.hs256Tokenize(header, payload, jwtKey);
        return Response.create("08", "00", "Sukses", token);
    }

    public Response<Object> registerSupplier(final Authentication authentication, final RegisterSupplierReq req) {
        return precondition(authentication, User.Role.ADMIN).orElseGet(() -> {
            if (req == null) {
                return Response.badRequest();
            }
            final String encoded = passwordEncoder.encode(req.password());
            final User user = new User( //
                    null, //
                    req.name(),//
                    req.email(), //
                    encoded, //
                    User.Role.PEMASOK,//
                    req.address(),
                    req.phone(),
                    authentication.id(),
                    null,
                    null,
                    OffsetDateTime.now(),
                    null,
                    null

            );
            final Long saved = userRepository.saveSupplier(user);
            if (0L == saved) {
                return Response.create("05", "01", "Gagal mendaftarkan seller", null);
            }
            return Response.create("05", "00", "Sukses", saved);
        });
    }

    public Response<Object> registerBuyer(final Authentication authentication, final RegisterBuyerReq req) {
        return precondition(authentication, User.Role.PEMBELI).orElseGet(() -> {
            if (req == null) {
                return Response.badRequest();
            }
            final String encoded = passwordEncoder.encode(req.password());
            final User user = new User(
                    null,
                    req.name(),
                    req.email(),
                    encoded,
                    User.Role.PEMBELI, // Updated role to PEMBELI
                    req.address(),
                    req.phone(),
                    authentication.id(),
                    null,
                    null,
                    OffsetDateTime.now(),
                    null,
                    null
            );
            final Long saved = userRepository.saveSupplier(user); // This might also need to change to saveBuyer if there's a separate method
            if (0L == saved) {
                return Response.create("05", "01", "Gagal mendaftarkan buyer", null); // Updated message
            }
            return Response.create("05", "00", "Sukses", saved);
        });
    }

    public Response<Object> resetPassword(final Authentication authentication, final ResetPasswordReq req) {
        if (req.newPassword() == null || req.newPassword().isEmpty()) {
            return Response.create("07", "03", "Password baru tidak boleh kosong", null);
        }
        if (req.newPassword().length() < 8) {
            return Response.create("07", "03", "Password baru harus memiliki minimal 8 karakter", null);
        }

        try {
            Optional<User> userOpt = userRepository.findById(authentication.id());
            if (userOpt.isEmpty()) {
                return Response.create("07", "01", "Pengguna tidak ditemukan", null);
            }

            User user = userOpt.get();
            if (passwordEncoder.matches(req.newPassword(), user.password())) {
                return Response.create("07", "03", "Password lama dan password baru sama. Silakan gunakan password yang berbeda",
                        null);
            }

            if ((user.deletedBy() != null && user.deletedBy() != null) || user.deletedAt() != null) {
                return Response.create("07", "04", "Akun pengguna telah dihapus", null);
            }

            String newEncodedPassword = passwordEncoder.encode(req.newPassword());
            String updatedUserId = userRepository.resetPassword(user.userId(), newEncodedPassword);
            if (updatedUserId == null) {
                return Response.create("07", "02", "Gagal memperbarui password", null);
            }

            UserDto userDto = new UserDto(user.userId(), user.name());
            return Response.create("07", "00", "Password berhasil diperbarui", userDto);
        } catch (Exception e) {
            return Response.create("07", "02", "Token tidak valid", null);
        }
    }

    public Response<Object> deletedUser(Authentication authentication, String userId) {
        return precondition(authentication, User.Role.ADMIN).orElseGet(() -> {
            Optional<User> userOpt = userRepository.findById(userId);

            if (!userOpt.isPresent()) {
                return Response.create("10", "02", "ID tidak ditemukan", null);
            }

            User dataUser = userOpt.get();

            if (dataUser.deletedAt() != null) {
                return Response.create("10", "03", "Data sudah dihapus", null);
            }

            User updatedUser = new User(
                    dataUser.userId(),
                    dataUser.name(),
                    dataUser.email(),
                    dataUser.password(),
                    dataUser.role(),
                    dataUser.address(),
                    dataUser.phone(),
                    dataUser.createdBy(),
                    dataUser.updatedBy(),
                    authentication.id(),
                    dataUser.createdAt(),
                    dataUser.updatedAt(),
                    OffsetDateTime.now());

            Long updatedRows = userRepository.deletedUser(updatedUser);
            if (updatedRows > 0) {
                return Response.create("10", "00", "Berhasil hapus data", null);
            } else {
                return Response.create("10", "01", "Gagal hapus data", null);
            }
        });
    }

    public Response<Object> updateProfile(final Authentication authentication, final UpdateProfileReq req) {
        return precondition(authentication, User.Role.ADMIN, User.Role.PEMBELI, User.Role.PEMASOK).orElseGet(() -> {
            Optional<User> userOpt = userRepository.findById(authentication.id());
            if (userOpt.isEmpty()) {
                return Response.create("07", "01", "User tidak ditemukan", null);
            }
            User user = userOpt.get();
            User updatedUser = new User(
                    user.userId(),
                    req.name(),
                    user.email(),
                    user.password(),
                    user.role(),
                    user.address(),
                    user.phone(),
                    user.createdBy(),
                    user.updatedBy(),
                    user.deletedBy(),
                    user.createdAt(),
                    OffsetDateTime.now(),
                    user.deletedAt());

            if (userRepository.updateUser(updatedUser)) {
                return Response.create("07", "00", "Profil berhasil diupdate", null);
            } else {
                return Response.create("07", "02", "Gagal mengupdate profil", null);
            }
        });
    }

}
