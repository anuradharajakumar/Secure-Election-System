package cs580;

import java.math.BigInteger;
import java.util.Random;

import javax.management.modelmbean.ModelMBeanInfoSupport;

public class ModularArithmetic {

	// BigInteger c,b,N;

	static BigInteger one = BigInteger.ONE;
	static BigInteger zero = BigInteger.ZERO;
	static BigInteger two = BigInteger.valueOf(2);

	// Question 1

	// c = modadd(a, b, N) : returns c = a + b (mod N)
	public static BigInteger modadd(BigInteger a, BigInteger b, BigInteger N) {
		BigInteger c;
		a = a.mod(N);
		b = b.mod(N);
		c = (a.add(b)).mod(N);
		return c;
	}

	// c = modmult(a, b, N): returns c = a * b (mod N)
	public static BigInteger modmult(BigInteger a, BigInteger b, BigInteger N) {
		BigInteger c;
		a = a.mod(N);
		b = b.mod(N);
		c = (a.multiply(b)).mod(N);
		return c;
	}

	// c = modexp(a, b, N): returns c = a^b (mod N)
	public static BigInteger modexp(BigInteger a, BigInteger b, BigInteger N) {
		BigInteger c;

		c = a.modPow(b, N);
		return c;
	}

	// c = moddiv(a, b, N): returns c = a/b (mod N)
	public static BigInteger moddiv(BigInteger a, BigInteger b, BigInteger N) {
		BigInteger c;
		a = a.mod(N);
		b = b.mod(N);

		c = (a.divide(b)).mod(N);

		return c;
	}

	// b = isPrime(N, k): returns true if N is prime with probability 1/2^k, or
	// false if N is not prime
	public static Boolean isPrime(BigInteger N, int k) {

		// returns if the number is prime or not with a probability 1/2^k
		return N.isProbablePrime(k);
	}

	// N = genPrime(n): returns a n-bit prime number N
	public static BigInteger genPrime(int n) {

		// probablePrime returns a prime number
		BigInteger N = BigInteger.probablePrime(n, new Random());

		return N;
	}

	// this function calculates the phi value for a given number n
	public static BigInteger phiN(BigInteger n) {
		BigInteger phi = one;

		// if n is 1, return 0
		if (n.equals(one))
			return zero;

		BigInteger i = two;

		// calculate until n becomes 1
		while (!n.equals(one)) {

			int c = 0;

			// calculate no. of times i divides by n
			// Eg: n=40 is divided by i=2 for 3 times
			// so, c = 3

			while (n.mod(i).equals(zero)) {
				n = n.divide(i);
				c++;
				// System.out.println(n + " - " + c);
			}

			// c contains the no of times the i divides n
			// Eg: n=40, i=2, c = 3
			// that is, 2^3

			if (c != 0) {
				// System.out.println("for " + i +" count is " + c);

				// using the formula,
				// (p^e - p^(e-1))
				BigInteger t = i.pow(c).subtract(i.pow(c - 1));

				// phi(40) = phi(2^3 * 5)
				// phi(2^3) = 2^3 - 2^2

				// this will work even if there is no power
				// Eg: phi(30) = phi(2*3*5)
				// for 2, c=1, phi(2) = 2^1 - 2^0 = 2-1 = 1

				// System.out.println("t = " + t);

				// multiply it to the phi value
				phi = phi.multiply(t);
			}

			i = i.add(one);
		}

		return phi;
	}

}
