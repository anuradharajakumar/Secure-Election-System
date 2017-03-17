package Election;

import java.net.*;
import java.io.*;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map.Entry;

import Project2.voter;

import java.util.Scanner;

//VN - validation number
//CV - cast vote
//KY - key
//QT - quit
//UL - user validation
//GR - get result

public class Voter {
	static RSA rsa = new RSA();
	static String myKey = rsa.e + "," + rsa.N;

	String username, password;
	BigInteger validationNumber;
	


	HashMap<String, Integer> candidateList = new HashMap<String, Integer>();

	static BigInteger CTF_e;
	static BigInteger CTF_N;
	static BigInteger CLA_e;
	static BigInteger CLA_N;

	static int CLA_port = 6780;
	static int CTF_port = 7780;

	private Socket CTF_socket;
	private Socket CLA_socket;

	// private Socket socket_CTF;
	String serverName = "localhost";
	static String cla_serverName = "172.20.10.7";
	static String ctf_severName = "172.20.10.4";
	
	public void sendKey(Socket s) {

		System.out.println("sending key");
		try {
			ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
			out.writeObject(myKey);
			out.flush();

		} catch (IOException ex) {
			System.err.println("Error: " + ex);

		}
	}

	public BigInteger getValNumber(Socket s) {
		BigInteger val = null;
		try {

			String user = rsa.encrypt(this.username);

			ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
			System.out.println("Requesting for a validation number");
			out.writeObject("VN," + user);
			out.flush();

			ObjectInputStream CLA_ois = new ObjectInputStream(s.getInputStream());

			String cla_key = (String) CLA_ois.readObject();

			getKey(cla_key, 1);

			sendKey(s);

			ObjectInputStream in = new ObjectInputStream(s.getInputStream());
			val = (BigInteger) in.readObject();
			val = rsa.decrypt(val, CLA_e, CLA_N);
			

			out.close();
			in.close();

		} catch (IOException | ClassNotFoundException ex) {
			System.err.println("Error: " + ex);

		}

		return (val);
	}

	public void closeCTFSocket() {
		try {
			CTF_socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void closeCLASocket() {
		try {
			CLA_socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void getKey(String key, int number) {

		String[] keys = key.split(",");

		// if number is 1 - CLA
		// if number is 2 - CTF
		if (number == 1) {
			
			CLA_e = new BigInteger(keys[0]);
			CLA_N = new BigInteger(keys[1]);

		} else {
			
			CTF_e = new BigInteger(keys[0]);
			CTF_N = new BigInteger(keys[1]);

		}

	}

	public void getValidationNumber() {

		try {
			System.out.println("Trying to connect to CLA at port " + CLA_port);
			CLA_socket = new Socket(cla_serverName, CLA_port);

			System.out.println("Connected to CLA");

			this.validationNumber = getValNumber(CLA_socket);

		} catch (IOException e) {
			e.printStackTrace();
		}

		closeCLASocket();

	}

	public void castVote(int candidateNumber) {

		// CV,username,validationNo,candidateid;
		BigInteger val = rsa.encrypt(validationNumber);

		BigInteger id = rsa.encrypt(candidateNumber);

		String name = rsa.encrypt(username);

		String vote = "CV," + name + "," + val.toString() + "," + id;

		try {
			System.out.println("Trying to connect to CTF at port " + CTF_port);
			CTF_socket = new Socket(ctf_severName, CTF_port);

			System.out.println("Just connected to CTF");

			ObjectOutputStream out = new ObjectOutputStream(CTF_socket.getOutputStream());
			out.writeObject(vote);
			out.flush();

			ObjectInputStream in = new ObjectInputStream(CTF_socket.getInputStream());
			String res = (String) in.readObject();
			System.out.println(res);
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}

		closeCTFSocket();

	}

	public Boolean validateUser(String username, String password) {

		Boolean res = false;
		try {
			System.out.println("Trying to connect to CTF at port " + CTF_port);
			CTF_socket = new Socket(ctf_severName, CTF_port);

			System.out.println("Just connected to CTF");

			username = rsa.encrypt(username);
			password = rsa.encrypt(password);

			ObjectOutputStream out = new ObjectOutputStream(CTF_socket.getOutputStream());

			out.writeObject("UL," + username + "," + password);
			out.flush();
			ObjectInputStream in = new ObjectInputStream(CTF_socket.getInputStream());

			String ctf_key = (String) in.readObject();

			getKey(ctf_key, 2);

			sendKey(CTF_socket);

			getCandidateList(CTF_socket);

			res = checkUser_withCTF(username, password, CTF_socket);

		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}

		closeCTFSocket();
		return res;

	}

	public void getResults() {

		try {
			System.out.println("Trying to connect to CTF at port " + CTF_port);
			CTF_socket = new Socket(ctf_severName, CTF_port);

			System.out.println("Just connected to CTF");

			ObjectOutputStream out = new ObjectOutputStream(CTF_socket.getOutputStream());

			out.writeObject("GR");
			out.flush();
			ObjectInputStream in = new ObjectInputStream(CTF_socket.getInputStream());

			HashMap<String, Integer> candidateResult = (HashMap<String, Integer>) in.readObject();

			for (String candidate : candidateResult.keySet()) {
				System.out.println(candidate + " - " + candidateResult.get(candidate));
			}

		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}

		closeCTFSocket();

	}

	public void logout() {

	}

	public Boolean checkUser_withCTF(String username, String password, Socket s) {

		Boolean val = null;
		try {

			ObjectInputStream in = new ObjectInputStream(CTF_socket.getInputStream());

			val = (Boolean) in.readObject();

		} catch (IOException | ClassNotFoundException ex) {
			System.err.println("Error: " + ex);
		}

		return (val);

	}

	@SuppressWarnings("unchecked")
	public void getCandidateList(Socket socket) {
		ObjectInputStream in;
		try {
			in = new ObjectInputStream(socket.getInputStream());
			candidateList = (HashMap<String, Integer>) in.readObject();
		} catch (IOException | ClassNotFoundException e) {

			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void displayCandidateList() {

		System.out.println("Candidates are: \n");
		for (Entry<String, Integer> t : candidateList.entrySet()) {
			System.out.println(t.getValue() + ". " + t.getKey() + "\t");
		}

	}

	public static void exitSockets() {
		String serverName = "localhost";
		Boolean res = false;
		try {

			Socket cla = new Socket(ctf_severName, CLA_port);

			ObjectOutputStream cla_out = new ObjectOutputStream(cla.getOutputStream());

			cla_out.writeObject("QT");
			cla_out.flush();
			cla.close();

			Socket ctf = new Socket(ctf_severName, CTF_port);

			ObjectOutputStream out = new ObjectOutputStream(ctf.getOutputStream());

			out.writeObject("QT");
			out.flush();
			ctf.close();

			System.out.println("All the servers are shutdown !! ");

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) throws NumberFormatException, IOException {
		// TODO Auto-generated method stub
		Scanner in = new Scanner(System.in);
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String option;
		do {
			
			System.out.println("1. Login\t2. Close\t3. Shut down");

			option = br.readLine();

			switch (option) {

			case "3":
				exitSockets();
				return;

			case "2":
				return;

			case "1":
				System.out.print("Username: ");
				String username = br.readLine();
				System.out.print("Password: ");
				String password = br.readLine();

				Voter voter = new Voter();
				if (!voter.validateUser(username, password)) {
					System.out.println("Invalid username or password");
					continue;
				}
				voter.username = username;
				voter.password = password;

				String choice;
				do {

					System.out.println("1. Get Validation Number\t2. Cast Vote\t3. Get Results\t4. Logout");
					choice = br.readLine();

					switch (choice) {
					case "1": {
						if (voter.validationNumber == null) {
							voter.getValidationNumber();
						}
						System.out.println("Validation number is : " + voter.validationNumber);
						break;
					}
					case "2": {
						if (voter.validationNumber == null) {
							System.out.println(
									"You dont have a validation number... Get a validation number and then vote");
							break;
						}
						voter.displayCandidateList();
						System.out.println("Enter the candiate number you want to vote");
						int vote = Integer.parseInt(br.readLine());
						voter.castVote(vote);

						break;
					}

					case "3": {
						voter.getResults();
						break;
					}
					case "4": {
						System.out.println("Logging out...");
						voter.logout();
						break;
					}

					default: {
						System.out.println("Invalid input.. To Logout press 4");
						break;
					}
					}
				} while (!choice.equals("4"));

			}

		} while (!option.equals("2"));

	}

}
