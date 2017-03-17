package cs580;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.Random;
import java.util.spi.CurrencyNameProvider;

public class Rsa {

	// public key is public
	public KeyPair publicKey;

	// private key is private
	private KeyPair privateKey;

	// assigning some commonly used int values as Biginteger
	BigInteger one = BigInteger.ONE;
	BigInteger zero = BigInteger.ZERO;
	BigInteger two = BigInteger.valueOf(2);
	BigInteger negOne = BigInteger.valueOf(-1);

	public Rsa() {
		publicKey = new KeyPair(BigInteger.valueOf(17), BigInteger.valueOf(377));
		privateKey = new KeyPair(BigInteger.valueOf(257), BigInteger.valueOf(377));
	}

	// RSA(n) (constructor): generates a public (N; e) and private (N; d) RSA
	// key pair, where N; e; d are numbers of approximately n bits in length.
	public Rsa(int n) {
		keyGeneration(n);
		displayKeys();
	}

	// same as RSA(n) but in addition it stores the public and private key pair
	// in the filename.
	public Rsa(int n, String filename) {

		String key_value = keyGeneration(n);

		// writing the key value with a space between them
		writeToFile(filename, key_value);

		// displaying only the public key
		displayKeys();

	}

	// reads in the private key stored in the file filename.

	public Rsa(String filename) {

		String content = readFromFile(filename);
		String[] keys = content.split(" ");

		// d = new BigInteger(keys[0]);
		// N = new BigInteger(keys[1]);
		privateKey = new KeyPair(new BigInteger(keys[0]), new BigInteger(keys[1]));

	}

	public String keyGeneration(int k) {

		BigInteger e;
		BigInteger N;
		BigInteger d;
		BigInteger phi;

		BigInteger p = ModularArithmetic.genPrime(k);
		BigInteger q = ModularArithmetic.genPrime(k);

		String key = "";
		// Incase, if p and q are of same value
		while (p.equals(q)) {
			q = ModularArithmetic.genPrime(k);
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

		key += e + " ";
		// System.out.println("e: " + e);

		// calculate d = e-1 mod phi
		d = ModularArithmetic.modexp(e, negOne, phi);
		key += d + " " + N;

		// System.out.println("d: " + d);

		publicKey = new KeyPair(e, N);
		privateKey = new KeyPair(d, N);

		return key;
	}

	public void displayKeys() {
		// Display only the public keys
		System.out
				.println("e,d,N : " + publicKey.getValue() + " , " + privateKey.getValue() + " , " + publicKey.getN());
		// System.out.println("Public key : " + e + " , " + N);
		// System.out.println("Private key : " + d + " , " + N);

	}

	// for a given integer m < N and public key (N; e) return the encrypted
	// message c = m^e(mod N)
	public BigInteger encrypt(BigInteger m, BigInteger N, BigInteger e) {
		BigInteger c = ModularArithmetic.modexp(m, e, N);
		return c;
	}

	// for an integer c < N, use the private key to return the decrypted message
	// m = c^d(mod N)
	public BigInteger decrypt(BigInteger c) {
		BigInteger m = ModularArithmetic.modexp(c, privateKey.getValue(), privateKey.getN());
		// BigInteger m = ModularArithmetic.modexp(c, d,N);
		return m;
	}

	public BigInteger decrypt(BigInteger c, BigInteger N, BigInteger d) {
		BigInteger m = ModularArithmetic.modexp(c, d, N);
		// BigInteger m = ModularArithmetic.modexp(c, d,N);
		return m;
	}

	// for a given file (with extension txt) and public key (N; e), it creates a
	// file of the same name (with extension enc) containing the encrypted data
	// to be transmitted over an insecure communication line.

	public void encryptFile(String filename, BigInteger N, BigInteger e) {

		// getting just the name of the file excluding the extension
		// Eg: getting test from test.txt
		String newfilename = filename.substring(0, filename.lastIndexOf('.'));

		// encrypted file has the same name with .enc as extension
		// Eg: test.enc
		newfilename += ".enc";

		// Reading the message to be encrypted
		String m = readFromFile(filename).trim();

		// splitting it based on spaces
		// String[] c = m.split(" ");

		// this will contain the encrypted text
		String result = "";

		for (char t : m.toCharArray()) {

			BigInteger i;

			// the ascii value of the char is used to encrypt the message
			i = BigInteger.valueOf(t);

			// Encrypting each character
			i = encrypt(i, N, e);

			// adding the encrypted value to the result string
			result += i.toString();

			// adding an additional space at the end of each value
			result += " ";

		}

		// writing the encrypted text to the .enc file
		writeToFile(newfilename, result);
	}

	// for a given file (with extension enc) it uses the private key to decrypt
	// the content of the file and print it to standard output.
	public void decryptFile(String filename) {

		// reading the content from the file
		String m = readFromFile(filename).trim();

		// splitting the string based on spaces
		String[] c = m.split(" ");

		String result = "";

		for (String t : c) {

			BigInteger i;

			// System.out.println("decrypting :" + t);

			i = new BigInteger(t);

			// System.out.println(i);

			// decrypting each value of the encrypted text
			BigInteger i1 = decrypt(i);

			// System.out.println(t + " -- " + i + " -- " + i1);

			// getting the char from the decrypted ascii value
			String t1 = getvalueForNumber(i1);

			// adding it to the encrypted text
			result += t1;

		}

		System.out.println("Decrypted result : " + result);

	}

	// this function will read the content from the given filename
	public String readFromFile(String filename) {

		BufferedReader br = null;
		String content = "";
		String path = "./" + filename;

		try {

			FileInputStream f = new FileInputStream(path);
			String sCurrentLine;

			br = new BufferedReader(new InputStreamReader(f));

			while ((sCurrentLine = br.readLine()) != null) {
				content += sCurrentLine;
				// System.out.println(sCurrentLine);
			}

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		return content;
	}

	// this function will write the content to the given filename
	public void writeToFile(String filename, String content) {
		// System.out.println("writing to file " + filename + " : " + content);
		try {
			File file = new File(filename);

			// create new if file doesnt exist
			if (!file.exists()) {
				file.createNewFile();
			}

			// delete the file if it already exists
			else {
				file.delete();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content + " ");
			bw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// this function returns the char value of a number if it is less than 128.
	// less than 128 - since ascii values are from 0 to 127 only
	// if the value is greater than 127, then return the same number
	// Eg: input : 65, output : A
	// Eg: input 520, output : 520 it is greater than 520 and its not an ascii
	// value
	private String getvalueForNumber(BigInteger i) {

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
	
	public static void main(String args[])
	{
		System.out.println(new Boolean("false"));
	}
}
