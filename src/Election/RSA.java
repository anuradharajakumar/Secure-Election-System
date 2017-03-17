package Election;

import java.math.BigInteger;
import java.util.Random;

import cs580.ModularArithmetic;

 class RSA {

	public BigInteger e;
	public BigInteger d;
	public BigInteger N;

	BigInteger one = BigInteger.ONE;
	BigInteger zero = BigInteger.ZERO;
	BigInteger two = BigInteger.valueOf(2);
	BigInteger negOne = BigInteger.valueOf(-1);

	public RSA() {
		// TODO Auto-generated constructor stub
		keyGeneration(25);
	}

	// N = genPrime(n): returns a n-bit prime number N
	public static BigInteger genPrime(int n) {

		// probablePrime returns a prime number
		BigInteger N = BigInteger.probablePrime(n, new Random());

		return N;
	}
	
	public BigInteger encrypt(int m){
		return encrypt(BigInteger.valueOf(m));
	}

	public String encrypt(String m) {

		String res = "";
		for (char c : m.toCharArray()) {
			BigInteger enc = encrypt(getValueForChar(c));
			// String t = getCharForNumber(enc);
			res += enc + " ";
		}
		return res.trim();
	}

	public String decrypt(String m, BigInteger d_d, BigInteger d_N) {
		String[] list = m.split(" ");
		String res = "";
		for (String s : list) {
			BigInteger t = new BigInteger(s);
			t = decrypt(t, d_d, d_N);
			res += getCharForNumber(t);
		}
		return res;
	}

	public BigInteger encrypt(BigInteger m) {
		BigInteger c= m.modPow(d, N);
		//BigInteger c = ModularArithmetic.modexp(m, d, N);
		return c;
	}

	// for an integer c < N, use the private key to return the decrypted message
	// m = c^d(mod N)
	public BigInteger decrypt(BigInteger c, BigInteger d_d, BigInteger d_N) {
		BigInteger m = c.modPow(d_d, d_N);
		//BigInteger m = ModularArithmetic.modexp(c, d_d, d_N);
		// BigInteger m = ModularArithmetic.modexp(c, d,N);
		return m;
	}

	public void keyGeneration(int k) {

		BigInteger phi;

		BigInteger p = genPrime(k);
		BigInteger q = genPrime(k);

		String key = "";
		// Incase, if p and q are of same value
		while (p.equals(q)) {
			q = genPrime(k);
		}

		// System.out.println(p + " \n" + q);

		// N = p x q
		N = p.multiply(q);

		// System.out.println("N: " + N);
		// phi(N) = (p-1) x (q-1)
		phi = p.subtract(one).multiply(q.subtract(one));

		// System.out.println("phi: " + phi);

		// Select e such that gcd(e,phi) == 1
		do {
			Random rand = new Random();
			e = new BigInteger(k, rand);

			if (e.gcd(phi).equals(one) && e.compareTo(phi) < 0 && !e.equals(one))
				break;
		} while (true);

		// System.out.println("e: " + e);

		// calculate d = e-1 mod phi
		d = modexp(e, negOne, phi);

		// System.out.println("d: " + d);

	}

	// c = modexp(a, b, N): returns c = a^b (mod N)
	public static BigInteger modexp(BigInteger a, BigInteger b, BigInteger N) {
		BigInteger c;

		c = a.modPow(b, N);
		return c;
	}

	private BigInteger getValueForChar(char c) {
		return BigInteger.valueOf((int) c);
	}

	private String getCharForNumber(BigInteger i) {

		String r = "";
		// if less than 127, return the corresponding char value from the ascii
		// table
		if (i.compareTo(BigInteger.valueOf(127)) <= 0) {
			char t = (char) (i.intValue());
			return Character.toString(t);
		}

		// else return the number as such
		return i.toString();
	}
}
