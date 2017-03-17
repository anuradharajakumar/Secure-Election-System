package cs580;

import java.io.IOException;
import java.math.BigInteger;
import java.net.Socket;
import java.net.UnknownHostException;

public class CLA {
	// Central Legitimization Agency

	// public key is public
	public KeyPair publicKey;

	// private key is private
	private KeyPair privateKey;

	BigInteger one = BigInteger.ONE;
	BigInteger zero = BigInteger.ZERO;
	BigInteger two = BigInteger.valueOf(2);
	BigInteger negOne = BigInteger.valueOf(-1);
	
	static Socket socket;
	static int port = 8000;
	static String address = "locahost";
	
	
	public CLA()
	{
		try {
			socket = new Socket(address,port);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
