package koh.cypher;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

import javax.crypto.NoSuchPaddingException;

public class RSACiphers {	
	private KeyPair keys;
	
	private CipherInterface decrypter;
	private CipherInterface encrypter;
	
	public RSACiphers(int keysize) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException {
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		kpg.initialize(keysize);
		
		keys = kpg.generateKeyPair();
		decrypter = new RSADecrypterCipher(keys.getPrivate());
		encrypter = new RSAEncrypterCipher(keys.getPublic());
	}
	
	public PublicKey getPublicKey() {
		return keys.getPublic();
	}
	
	public CipherInterface getDecrypter() {
		return decrypter;
	}
	
	public CipherInterface getEncrypter() {
		return encrypter;
	}
}
