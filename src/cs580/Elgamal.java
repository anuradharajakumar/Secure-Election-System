package cs580;

import java.math.BigInteger;

public class Elgamal {

	public static void main(String args[])
	{

		String s = "anuradhaz";
		Boolean[] c = new Boolean[26];
		for(int i=0;i<s.length();i++)
		{
			char t = Character.toUpperCase(s.charAt(i));
			int t1 = t - 65;
			System.out.println(t1);
		}
		
	}
	
	
//	public static void main(String args[])
//	{
//		
//		
//		BigInteger two = BigInteger.valueOf(2);
//		BigInteger one = BigInteger.valueOf(1);
//		BigInteger zero = BigInteger.valueOf(0);
//		BigInteger p = BigInteger.valueOf(881);
//		BigInteger e1 = BigInteger.valueOf(3);
//		BigInteger d = BigInteger.valueOf(61);
//		BigInteger r = BigInteger.valueOf(7);
//		BigInteger m = BigInteger.valueOf(400);
//	 BigInteger e2,s1,s2,v1,v2;
//	 e2 = e1.pow(d.intValue()).mod(p);
//	 System.out.println("e2 = " + e2);
//	 s1 = e1.pow(r.intValue()).mod(p);
//	 System.out.println("s1 = " + s1);
//	 ModularArithmetic arth = new ModularArithmetic();
//	 BigInteger r1 = arth.multiplicativeInverse(r, p.subtract(one));
//	 System.out.println("r1 = " + r1);
//	 
//	 s2 = d.multiply(s1);
//	 System.out.println(s2);
//	 s2 = m.subtract(s2);
//	 System.out.println(s2);
//	 s2 =s2.multiply(r1).mod(p.subtract(one));
//	 
//	 System.out.println(s2);
//	
//	 if(s2.compareTo(zero)<0)
//	 {
//		 System.out.println("less");
//		 s2=s2.add(p.subtract(one));
//	 }
//	 System.out.println("s2 = " + s2);
//	 
//	 
//	 v1 = e1.pow(m.intValue()).mod(p);
//	 System.out.println("v1 = " +v1);
//	 
//	 v2 = e2.pow(s1.intValue()).mod(p);
//	 System.out.println(v2);
//	 BigInteger t = s1.pow(s2.intValue()).mod(p);
//	 System.out.println(t);
//	 v2 = v2.multiply(t).mod(p);
//	 System.out.println("v2 = " +v2);
//	 
//	}
//	
//	
	
}
