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

	public static void main(String[] args) {
		System.out.println("hello world ");
		SpringApplication.run(StoreSystemApplication.class, args);

	}

}
