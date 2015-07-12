package koh.cypher;

import java.util.Arrays;

public class Dofus2CipherDecorator extends CipherAdapter {
	
	private final CipherInterface decorated;
	private final String salt;

	public Dofus2CipherDecorator(CipherInterface decorated, String salt) {
		this.decorated = decorated;
		this.salt = salt;
	}

	@Override
	public byte[] cipher(byte[] bytes) {
		byte[] result = decorated.cipher(bytes);
		String salt = new String(Arrays.copyOfRange(result, 0, this.salt.length()));
		if (!this.salt.equals(salt)) {
			return null;
		}
		return Arrays.copyOfRange(result, this.salt.length(), result.length);
	}

	@Override
	public String cipher(String string) {
		String result = decorated.cipher(string);
		if (!salt.equals(result.substring(0, salt.length()))) {
			return null;
		}
		return result.substring(salt.length());
	}

	@Override
	public String cipherString(byte[] bytes) {
		return new String(cipher(bytes));
	}
	
	

}
