package koh.cypher;

public abstract class CipherAdapter implements CipherInterface {

	/* (non-Javadoc)
	 * @see net.ankafriend.mambo.common.ciphers.CipherInterface#cipher(java.lang.String)
	 */
	public String cipher(String string) {
		return new String(cipher(string.getBytes()));
	}

	/* (non-Javadoc)
	 * @see net.ankafriend.mambo.common.ciphers.CipherInterface#cipherString(byte[])
	 */
	public String cipherString(byte[] bytes) {
		return new String(cipher(bytes));
	}
	
	

}
