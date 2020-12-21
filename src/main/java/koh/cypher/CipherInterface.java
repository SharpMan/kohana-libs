package koh.cypher;

public interface CipherInterface {
	byte[] cipher(byte[] bytes);
	String cipher(String string);

	String cipherString(byte[] bytes);
}
