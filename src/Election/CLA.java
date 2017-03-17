package Election;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

import Project2.voter;

import java.net.*;
import java.io.*;
import java.math.BigInteger;

//VN - validation number
//VT - vote
//KY - key
//DC - disconnect

class CLA extends Thread {

	static RSA rsa = new RSA();
	static String myKey = rsa.e + "," + rsa.N;

	static BigInteger voter_e;
	static BigInteger voter_N;
	static BigInteger CTF_e;
	static BigInteger CTF_N;

	static String cla_serverName = "10.85.174.11";
	static String ctf_severName = "10.85.144.117";
	// contains the validation number
	String CLA_validationumbers = "CLA_validationumbers.txt";

	static int CTF_port = 8101;
	static int VOTER_port = 7101;

	private ServerSocket voter_Serversocket;
	HashMap<String, String> username_validationNumber = new HashMap<String, String>();

	String serverName = "localhost";

	public void startServers() {

		// server for voter
		new Thread() {
			public void run() {
				try {
					Boolean stop = false;
					voter_Serversocket = new ServerSocket(VOTER_port);
					Socket voter = null;
					while (!stop) {
						System.out.println("waiting for voter");
						voter = voter_Serversocket.accept();
						System.out.println("Voter is connected ...");
						String choice;

						ObjectInputStream input = new ObjectInputStream(voter.getInputStream());
						choice = (String) input.readObject();
						switch (choice.substring(0, 2)) {
						case "VN":
							System.out.println("Voter is asking for a validation number");
							sendValidationNumber(choice, voter);
							break;
						case "QT":
							stop = true;
							break;

						}
						// } while (!choice.substring(0, 2).equals("QT"));
						voter.close();

					}
					System.out.println("shutting down voter server");
					voter_Serversocket.close();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}.start();
	}

	public void getKey(String key, int number) {

		String[] keys = key.split(",");

		// if number is 1 - CLA
		// if number is 2 - Voter
		if (number == 1) {
			System.out.println("getting key from CTF");
			CTF_e = new BigInteger(keys[0]);
			CTF_N = new BigInteger(keys[1]);
		} else {
			System.out.println("getting key from voter");
			voter_e = new BigInteger(keys[0]);
			voter_N = new BigInteger(keys[1]);
		}

	}

	private void sendValidationNumber(String choice, Socket voter) throws IOException {
		// TODO Auto-generated method stub

		try {
			sendKey(voter);
			ObjectInputStream in = new ObjectInputStream(voter.getInputStream());
			String key = (String) in.readObject();
			getKey(key, 2);

		} catch (Exception e) {
			e.printStackTrace();
		}

		String[] str = choice.split(",");

		String username = rsa.decrypt(str[1], voter_e, voter_N);

		ObjectOutputStream out_voter = new ObjectOutputStream(voter.getOutputStream());

		BigInteger val = null;

		if (username_validationNumber.containsKey(username)) {
			val = new BigInteger(username_validationNumber.get(username));
			System.out.println("Validation Number already available ");
		} else {
			val = generateRandom();
			username_validationNumber.put(username, val.toString());
			System.out.println("New generated validation number is: " + val);

			File file = new File(CLA_validationumbers);
			FileWriter fw = new FileWriter(file, true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("\n" + username + "," + val);
			bw.newLine();
			bw.close();
			startSender();

		}

		val = rsa.encrypt(val);
		out_voter.writeObject(val);
		out_voter.flush();

	}

	BigInteger generateRandom() {
		Random rand = new Random();
		return new BigInteger(25, rand);
	}

	public void send_Username_ValidationNumber_CTF(Socket ctf) {

		try {
			ObjectOutputStream out_ctf = new ObjectOutputStream(ctf.getOutputStream());
			System.out.println("sending validaiton numbers");
			out_ctf.writeObject(username_validationNumber);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void sendKey(Socket s) {
		System.out.println("sending key ");
		try {
			ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
			out.writeObject(myKey);
			out.flush();

		} catch (IOException ex) {
			System.err.println("Error: " + ex);

		}

	}

	private void readFromFiles() {
		// TODO Auto-generated method stub
		get_Username_ValidationNumber();

	}

	public void get_Username_ValidationNumber() {
		BufferedReader br = null;

		String path = "./" + CLA_validationumbers;

		try {

			FileInputStream f = new FileInputStream(path);
			String sCurrentLine;

			br = new BufferedReader(new InputStreamReader(f));

			while ((sCurrentLine = br.readLine()) != null) {

				String[] list = sCurrentLine.split(",");
				// System.out.println(sCurrentLine + "," + list.length);

				if (list.length == 2) {

					username_validationNumber.put(list[0], list[1]);

				}
			}

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {

			if (br != null)
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

		}

	}

	public void startSender() {

		// connect to CTF server

		try {
			System.out.println("Connecting to CTF on port " + CTF_port);
			Socket ctf = new Socket(serverName, CTF_port);
			System.out.println("Just connected to CTF ...");

			ObjectInputStream in = new ObjectInputStream(ctf.getInputStream());
			String message = (String) in.readObject();
			getKey(message, 1);

			sendKey(ctf);
			send_Username_ValidationNumber_CTF(ctf);

			ctf.close();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		CLA cla = new CLA();
		cla.readFromFiles();

		cla.startServers();
		cla.startSender();
	}
}
