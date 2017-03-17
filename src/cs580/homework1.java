package cs580;

import java.util.Scanner;

public class homework1 {

	public static void main(String[] args) 
	{
		Scanner in = new Scanner(System.in);
		System.out.println("Z38* = ");
		int n=38;
		Calculations calculate = new Calculations();
		Integer[] ele = new Integer[n];
		for(int i=0;i<n;i++)
		{
			ele[i]=i;
		}
		
		
		Integer[] star= calculate.zstar(n);
		for(int i=0;i<star.length;i++){
			System.out.print(star[i]+"\t");
		}
		
		System.out.println("Additive Inverses");
		System.out.println("Z");
		calculate.additiveInverse(ele, n);
		System.out.println("Star");
		calculate.additiveInverse(star, n);
		
		System.out.println("Multiplicative Inverses");
		System.out.println("Z");
		calculate.multiplicativeInverse(ele, n);
		System.out.println("Star");
		calculate.multiplicativeInverse(star, n);
	}
	
	
}
