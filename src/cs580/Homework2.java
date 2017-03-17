package cs580;

import java.math.BigInteger;
import java.util.ArrayList;

public class Homework2 {

	static BigInteger PI = BigInteger.valueOf(78), OI = BigInteger.valueOf(95);
	static BigInteger zero = BigInteger.ZERO;
	static BigInteger ten = BigInteger.TEN;

	static BigInteger h = BigInteger.valueOf(13);
	static BigInteger m = BigInteger.valueOf(291);
	static BigInteger N = BigInteger.valueOf(377);
	static BigInteger e = BigInteger.valueOf(17);
	static BigInteger d = BigInteger.valueOf(257);

	public static BigInteger hashFunc(BigInteger n) {
		return n.add(h).mod(m);

	}

	public static BigInteger concatenate(BigInteger a, BigInteger b) {
		BigInteger t = b;
		while (t.compareTo(zero) != 0) {
			t = t.divide(ten);
			a = a.multiply(ten);

		}
		a = a.add(b);
		return a;
	}

	// DS=E(PRc, [H(H(PI)||H(OI))])

	public static BigInteger PIMD;
	public static BigInteger OIMD;
	public static BigInteger ds;

	public static BigInteger dualSignature() {
		Rsa rsa = new Rsa();

		PIMD = hashFunc(PI);
		OIMD = hashFunc(OI);
		BigInteger t = concatenate(PIMD, OIMD);
		System.out.println("t=" + t);
		t = hashFunc(t);
		ds = rsa.encrypt(t, N, d);
		System.out.println("ds= " + ds);
		return PIMD;
	}

	// H[ PIMD || H(OI) ] ; D(PU, DS)
	public static Boolean verifyMerchant() {
		Rsa rsa = new Rsa();

		BigInteger t_OIMD = hashFunc(OI);
		BigInteger t = concatenate(PIMD, t_OIMD);
		System.out.println("t=" + t);
		t = hashFunc(t);
		BigInteger merchant = rsa.decrypt(ds, N, e);

		System.out.println("t= " + t + " Merchant = " + merchant);

		if (t.equals(merchant))
			return true;

		return false;
	}

	// H[H(PI) || OIMD]; D(PUc, DS)

	public static Boolean verifyBank() {
		Rsa rsa = new Rsa();

		BigInteger t_PIMD = hashFunc(PI);
		BigInteger t = concatenate(t_PIMD, OIMD);
		t = hashFunc(t);

		BigInteger bank = rsa.decrypt(ds, N, e);

		System.out.println("t= " + t + " Bank = " + bank);

		if (t.equals(bank))
			return true;

		return false;
	}

	public static void secretSharing() {

		BigInteger bob_E = BigInteger.valueOf(5145);
		BigInteger bob_N = BigInteger.valueOf(7387);
		BigInteger carol_E = BigInteger.valueOf(1421);
		BigInteger carol_N = BigInteger.valueOf(2747);

		int[] b = { 914, 1952, 1824, 1132 };
		int[] c = { 1100, 1230, 1224, 1259 };
		System.out.println("For BOB: ");
		ArrayList<Integer> BOB_fbi = calculate(c[0], bob_N, bob_E);
		System.out.println("FOR CAROL: ");
		ArrayList<Integer> CAROL_fbi = calculate(b[1], carol_N, carol_E);

	}

	public static int[] compliment(int[] a, int x) {

		return a;
	}

	public static ArrayList<Integer> calculate(int c, BigInteger N, BigInteger e) {
		Rsa rsa = new Rsa();
		BigInteger bob = rsa.encrypt(BigInteger.valueOf(c), N, e);
		System.out.println("Encrypted text : " + bob);
		int xored = bob.intValue() ^ c;

		System.out.println("XORING " + bob + " and " + c);
		System.out.println(Integer.toBinaryString(bob.intValue()));
		System.out.println(Integer.toBinaryString(c));

		String bob_fbi = (Integer.toBinaryString(xored));

		System.out.println("answer " + bob_fbi);

		while (bob_fbi.length() % 4 != 0) {
			bob_fbi = "0" + bob_fbi;
		}

		ArrayList<Integer> fbi = new ArrayList<Integer>();

		System.out.println("after adding 0's : " + bob_fbi);
		int n = bob_fbi.length();
		for (int i = 0; i < n; i++) {
			if (bob_fbi.charAt((n - i - 1)) == '1') {
				// System.out.println("remove " + i + " "
				// +bob_fbi.charAt((n-i-1)));

				if (!fbi.contains(i)) {
					fbi.add((i));
					System.out.print(i + " ");
				}
			}

		}
		System.out.println();
		return fbi;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		// System.out.println("Dual Signature : " + dualSignature());
		// System.out.println("Verify Merchant : " + verifyMerchant());
		// System.out.println("Verify Bank : " + verifyBank());
		secretSharing();
	}

}
