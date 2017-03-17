package Election;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.Map.Entry;

class CTF {
	RSA rsa = new RSA();

	String myKey = rsa.e + "," + rsa.N;

	// files

	// username,password,hasVoted
	static String CTF_username_password = "CTF_username_password.txt";
	// contains candiate names and their current no. of votes
	// canidateName,candidateId,noVotes
	static String CTF_candidate_list = "CTF_candidate_list.txt";

	// candidateNames
	HashSet<Candidate> candidatesList = new HashSet<Candidate>();

	// candidateName - candidate ID
	HashMap<String, Integer> candidate_Id = new HashMap<String, Integer>();
	// username - password
	HashMap<String, String> username_password = new HashMap<String, String>();
	// username - hasVoted
	HashMap<String, Boolean> username_vote = new HashMap<String, Boolean>();
	// username - validationNo
	HashMap<String, String> username_validationNumber = new HashMap<String, String>();

	static int Voter_port = 6101;
	static int CTF_port = 8101;

	static BigInteger voter_e;
	static BigInteger voter_N;
	static BigInteger CLA_e;
	static BigInteger CLA_N;



	private ServerSocket Voter_ServerSocket;
	private Socket voter_socket;
	private ServerSocket CLA_ServerSocket;

	public void getKey(Socket client, int number) {
		// if number is 1 - CLA
		// if number is 2 - Voter

		try {
			ObjectInputStream in = new ObjectInputStream(client.getInputStream());
			String key = (String) in.readObject();

			String[] keys = key.split(",");
			if (number == 1) {
				System.out.println("getting the keys from cla");
				CLA_e = new BigInteger(keys[0]);
				CLA_N = new BigInteger(keys[1]);
			} else {
				System.out.println("getting key from voter");
				voter_e = new BigInteger(keys[0]);
				voter_N = new BigInteger(keys[1]);
			}
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		

	}

	public void sendResults(Socket voter) {
		try {
			ObjectOutputStream out = new ObjectOutputStream(voter.getOutputStream());

			HashMap<String, Integer> temp = new HashMap<String, Integer>();
			for (Candidate t : candidatesList) {
				temp.put(t.name, t.votes);
			}

			/////// software connection errror
			System.out.println("Sending the Result");
			out.writeObject(temp);
			out.flush();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void castVote(String choice, Socket voter) {
		// TODO Auto-generated method stub
		// CV,username,validationNo,candidateid;
		String[] list = choice.split(",");
		String username = list[1];
		username = rsa.decrypt(username, voter_e, voter_N);

		BigInteger validationNo = new BigInteger(list[2]);
		validationNo = rsa.decrypt(validationNo, voter_e, voter_N);

		BigInteger id_val = new BigInteger(list[3]);
		id_val = rsa.decrypt(id_val, voter_e, voter_N);

		String res = "";
		try {
			ObjectOutputStream out = new ObjectOutputStream(voter.getOutputStream());
			if (username_vote.get(username)) {
				res = "You have already voted !!";
			} else if (!username_validationNumber.get(username).equals(validationNo.toString())) {
				res = "Invalid Validation Number";
			} else {
				Boolean flag = true;
				for (Candidate t : candidatesList) {
					if (t.id == id_val.intValue()) {
						System.out.println("updating vote for " + t.name);
						t.updateVote();
						flag = false;

						res = "Succesfulley Voted !!";
						username_vote.put(username, true);
						updateCTF_username_password();
						updateCTF_candidate_list();
						break;
					}
				}
				if (flag) {
					res = "Invalid Candidate ID";
				}
			}

			/////// software connection errror

			out.writeObject(res);
			out.flush();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	// open a server for voter at 8001

	public void validateUser(String choice, Socket voter) {

		sendKey(voter);
		getKey(voter, 2);
		sendCandidateList(voter);

		String[] list = choice.split(",");

		String username = rsa.decrypt(list[1], voter_e, voter_N);
		String password = rsa.decrypt(list[2], voter_e, voter_N);

		Boolean res = false;

		try {
			ObjectOutputStream out = new ObjectOutputStream(voter.getOutputStream());
			for (Entry<String, String> t : username_password.entrySet()) {
				if (t.getKey().equals(username) && t.getValue().equals(password)) {
					res = true;
					break;
				}
			}

			out.writeObject(res);
			out.flush();
			// out.close();

		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

	public void sendCandidateList(Socket voter) {

		System.out.println("sending the candidates");
		try {
			ObjectOutputStream out = new ObjectOutputStream(voter.getOutputStream());

			out.writeObject(candidate_Id);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void sendKey(Socket s) {
		
		try {
			ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
			out.writeObject(myKey);
			out.flush();

		} catch (IOException ex) {
			System.err.println("Error: " + ex);

		}

	}

	public void closeCLASocket() {
		try {
			CLA_ServerSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	Boolean stop = false;

	public void startServer() {

		// server for CLA
		(new Thread() {
			@SuppressWarnings("unchecked")
			@Override
			public void run() {
				try {

					CLA_ServerSocket = new ServerSocket(CTF_port);
					System.out.println("Waiting for CLA ...");
					Socket cla = null;
					while (true && !stop) {
						cla = CLA_ServerSocket.accept();
						System.out.println("CLA is connected ");

						sendKey(cla);
						getKey(cla, 1);

						ObjectInputStream input = new ObjectInputStream(cla.getInputStream());
						username_validationNumber = (HashMap<String, String>) input.readObject();
						System.out.println("Got available validation numbers from CLA");

						cla.close();

					}
					System.out.println("shutting down cla server");
					System.exit(0);
				} catch (Exception e) {
					e.printStackTrace();
				}

				closeCLASocket();
			}
		}).start();

		// server for Voter
		(new Thread() {
			@Override
			public void run() {

				try {
					Voter_ServerSocket = new ServerSocket(Voter_port);
					System.out.println("Waiting for voter ...");
					// Socket voter = null;
					while (true && !stop) {
						Socket voter = Voter_ServerSocket.accept();
						System.out.println("Voter is connected ...");
						String choice;

						ObjectInputStream input = new ObjectInputStream(voter.getInputStream());

						choice = (String) input.readObject();
						// input.close();

						switch (choice.substring(0, 2)) {
						case "UL":
							System.out.println("Voter wants to validate himself");
							validateUser(choice, voter);
							break;
						case "CV":
							System.out.println("Voter wants  to vote");
							castVote(choice, voter);
							break;
						case "QT":
							System.out.println("Shuting down the voter server");
							stop = true;
							return;
						case "GR":
							System.out.println("Voter wants to get the candiate results");
							sendResults(voter);

						}

						voter.close();
						System.out.println("The voter disconnected");

					}
					// voter_socket.close();

				} catch (Exception e) {
					e.printStackTrace();
				}
				System.out.println("Voter server Shutdown");
				// closeVoterSocket();
				// closeCLASocket();
				// System.out.println("closed cla server stop is :" + stop);
			}

		}).start();

	}

	public void readFromFiles() {
		getUsernamePassword();
		getCandidateList();
	}

	private void getCandidateList() {
		// TODO Auto-generated method stub
		BufferedReader br = null;
		String path = "./" + CTF_candidate_list;
		try {

			FileInputStream f = new FileInputStream(path);
			String sCurrentLine;

			br = new BufferedReader(new InputStreamReader(f));

			while ((sCurrentLine = br.readLine()) != null) {
				String[] line = sCurrentLine.split(",");
				Candidate cand = new Candidate(line[0], line[1], line[2]);
				candidatesList.add(cand);
				candidate_Id.put(cand.getName(), cand.getId());
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

	}

	private void getUsernamePassword() {
		// TODO Auto-generated method stub
		BufferedReader br = null;
		String path = "./" + CTF_username_password;
		try {

			FileInputStream f = new FileInputStream(path);
			String sCurrentLine;

			br = new BufferedReader(new InputStreamReader(f));

			while ((sCurrentLine = br.readLine()) != null) {
				String[] line = sCurrentLine.split(",");
				username_password.put(line[0], line[1]);
				username_vote.put(line[0], new Boolean(line[2]));

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

	}

	public void updateCTF_candidate_list() {
		// System.out.println("writing to file " + filename + " : " + content);
		try {
			File file = new File(CTF_candidate_list);

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
			for (Candidate t : candidatesList) {
				String line = t.name + "," + t.id + "," + t.votes;
				bw.write(line);
				bw.newLine();
				bw.flush();
			}
			bw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// this function will write the content to the file
	public void updateCTF_username_password() {
		// System.out.println("writing to file " + filename + " : " + content);
		try {
			File file = new File(CTF_username_password);

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
			for (String user : username_password.keySet()) {
				String line = user + "," + username_password.get(user) + "," + username_vote.get(user);
				bw.write(line);
				bw.newLine();
				bw.flush();
			}
			bw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CTF ctf = new CTF();
		ctf.readFromFiles();
		ctf.startServer();
		// ctf.startSender();
	}

}
