package tokoibuelin.storesystem;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tokoibuelin.storesystem.util.Base64Utils;
import tokoibuelin.storesystem.util.HexUtils;

import java.security.SecureRandom;
import java.util.Base64;

@SpringBootApplication
public class StoreSystemApplication {
<<<<<<< HEAD
//	public static byte[] generateHmacSha256Key() {
//		try {
//			KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
//			SecureRandom secureRandom = new SecureRandom(); // Pengaturan randomizer yang aman
//			keyGen.init(256, secureRandom); // Inisialisasi dengan panjang 256 bits
//			SecretKey secretKey = keyGen.generateKey(); // Membuat secret key
//
//			return secretKey.getEncoded(); // Mendapatkan representasi byte dari secret key
//		} catch (NoSuchAlgorithmException e) {
//			throw new RuntimeException("Failed to generate HMACSHA256 key", e);
//		}
//	}
	public static void main(String[] args) {
		System.out.println("hello world ");
		SpringApplication.run(StoreSystemApplication.class, args);
//		byte[] hmacSha256Key = generateHmacSha256Key();
//		System.out.println("HMACSHA256 Key: " + HexUtils.bytesToHex(hmacSha256Key));
//
//		String originalString = "masyaallah";
//		byte[] originalBytes = originalString.getBytes();
//
//		// Encode string to base64
//		String base64Encoded = Base64Utils.base64Encode(originalBytes);
//		System.out.println("Base64 Encoded: " + base64Encoded);
=======

	public static void main(String[] args) {
		System.out.println("hello world ");
		SpringApplication.run(StoreSystemApplication.class, args);

>>>>>>> d6e081891fe819ab569ec7be3584e93d6c6378a2
	}

}
