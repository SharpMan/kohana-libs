package koh.cypher;

public class Ciphers {	
	public static CipherInterface combine(final CipherInterface c1, final CipherInterface c2) {
		return new CipherAdapter() {
			public byte[] cipher(byte[] bytes) {
				return c2.cipher(c1.cipher(bytes));
			}
			public String cipher(String string) {
				return cipherString(string.getBytes());
			}
			public String cipherString(byte[] bytes) {
				return c2.cipherString(c1.cipher(bytes));
			}
			
			
		};
	}
	
	private Ciphers(){}
 }
