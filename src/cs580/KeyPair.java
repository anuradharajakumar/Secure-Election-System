package cs580;

import java.math.BigInteger;

/* This class is to store the public key pair and the private key pair 
 * {e,N} and {d,N}
 */

public class KeyPair {

	private BigInteger value;
	private BigInteger N;

	public KeyPair(BigInteger value, BigInteger n) {
		super();
		this.value = value;
		N = n;
	}

	public void setValue(BigInteger value) {
		this.value = value;
	}
	
	public BigInteger getValue() {
		return value;
	}

	public BigInteger getN() {
		return N;
	}

	public void setN(BigInteger n) {
		N = n;
	}

}
