package tokoibuelin.storesystem.service;

import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tokoibuelin.storesystem.entity.Address;
import tokoibuelin.storesystem.entity.User;
import tokoibuelin.storesystem.model.Authentication;
import tokoibuelin.storesystem.model.Page;
import tokoibuelin.storesystem.model.request.*;
import tokoibuelin.storesystem.model.Response;
import tokoibuelin.storesystem.model.response.UserDto;
import tokoibuelin.storesystem.repository.AddressRepository;
import tokoibuelin.storesystem.repository.UserRepository;
import tokoibuelin.storesystem.util.HexUtils;
import tokoibuelin.storesystem.util.JwtUtils;
import tokoibuelin.storesystem.util.SecurityContextHolder;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;



@Service
public class UserService extends AbstractService {
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final PasswordEncoder passwordEncoder;
    private final byte[] jwtKey;

    public UserService(final Environment env, final UserRepository userRepository, final PasswordEncoder passwordEncoder, final AddressRepository addressRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        final String skJwtKey = env.getProperty("jwt.secret-key");
        this.jwtKey = HexUtils.hexToBytes(skJwtKey);
        this.addressRepository = addressRepository;
    }
    public Response<Object> listUsers(final Authentication authentication, final int page, final int size) {
        return precondition(authentication, User.Role.ADMIN).orElseGet(() -> {
            if (page <= 0 || size <= 0) {
                return Response.badRequest();
            }
            Page<User> userPage = userRepository.listUsers(page, size);
            List<UserDto> users = userPage.data().stream().map(user -> new UserDto(user.userId(), user.name())).toList();
            Page<UserDto> p = new Page<>(userPage.totalData(), userPage.totalPage(), userPage.page(), userPage.size(), users);
            return Response.create("09", "00", "Sukses", p);
        });
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
        //SecurityContextHolder.setAuthentication(authentication);
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

    @Transactional
    public Response<Object> registerSupplier(final Authentication authentication, final RegistUserReq req) {
        System.out.println("Authentication: " + authentication);
        System.out.println("Request: " + req);

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
                    req.phone(),
                    authentication.id(), // pastikan ini juga String
                    null,
                    null,
                    OffsetDateTime.now(),
                    null,
                    null
            );

            final String savedId = userRepository.saveUser(user); // mengubah dari Long menjadi String
            System.out.println("Cek save suplier : " + savedId);
            //  8uyaddressRepository.testInsertAddress();
            if (savedId != null) {
                Address address = new Address(
                        null,
                        savedId, // gunakan savedId yang benar
                        req.street(),
                        req.rt(),
                        req.rw(),
                        req.village(),
                        req.district(),
                        req.city(),
                        req.postalCode()
                );
                String  save = addressRepository.saveAddress(address);
                System.out.println("Cek save Address : " + save);
                if (null == save) {
                    return Response.create("05", "01", "Gagal mendaftarkan supplier", null);
                }
                return Response.create("05", "00", "Sukses", savedId);
            }

            return Response.create("05", "01", "Gagal mendaftarkan supplier", null);
        });
    }

    @Transactional
    public Response<Object> registerBuyer(final Authentication authentication, final RegistUserReq req) {
        System.out.println("Authentication: " + authentication);
        System.out.println("Request: " + req);

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
                    User.Role.PEMBELI,//
                    req.phone(),
                    authentication.id(), // pastikan ini juga String
                    null,
                    null,
                    OffsetDateTime.now(),
                    null,
                    null
            );

            final String savedId = userRepository.saveUser(user); // mengubah dari Long menjadi String
            System.out.println("Cek save suplier : " + savedId);
            //  8uyaddressRepository.testInsertAddress();
            if (savedId != null) {
                Address address = new Address(
                        null,
                        savedId, // gunakan savedId yang benar
                        req.street(),
                        req.rt(),
                        req.rw(),
                        req.village(),
                        req.district(),
                        req.city(),
                        req.postalCode()
                );
                String  save = addressRepository.saveAddress(address);
                System.out.println("Cek save Address : " + save);
                if (null == save) {
                    return Response.create("05", "01", "Gagal mendaftarkan sebagai User", null);
                }
                return Response.create("05", "00", "Sukses", savedId);
            }

            return Response.create("05", "01", "Gagal mendaftarkan sebagai User", null);
        });
    }

    public Response<Object> resetPassword(final Authentication authentication, final ResetPasswordReq req) {

        System.out.println("Authentication ID (in reset): " + authentication.id());

        try {
            if (req.newPassword() == null || req.newPassword().isEmpty()) {
                return Response.create("07", "03", "Password baru tidak boleh kosong", null);
            }
            if (req.newPassword().length() < 8) {
                return Response.create("07", "03", "Password baru harus memiliki minimal 8 karakter", null);
            }

            Optional<User> userOpt = userRepository.findById(authentication.id());
            if (userOpt.isEmpty()) {
                return Response.create("07", "01", "Pengguna tidak ditemukan", null);
            }

            User user = userOpt.get();
            if (passwordEncoder.matches(req.newPassword(), user.password())) {
                return Response.create("07", "03", "Password lama dan password baru sama. Silakan gunakan password yang berbeda",
                        null);
            }

            if (user.deletedBy() != null  || user.deletedAt() != null) {
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
            e.printStackTrace();
            return Response.create("07", "02", "Token tidak valid", e.getMessage());
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

    @Transactional
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
                    req.email(),
                    user.password(),
                    user.role(),
                    req.phone(),
                    user.createdBy(),
                    authentication.id(),
                    user.deletedBy(),
                    user.createdAt(),
                    OffsetDateTime.now(),
                    user.deletedAt()
            );

            if (userRepository.updateUser(updatedUser)) {
                return Response.create("07", "00", "Profil berhasil diupdate", null);
            } else {
                return Response.create("07", "02", "Gagal mengupdate profil", null);
            }
        });
    }

}
