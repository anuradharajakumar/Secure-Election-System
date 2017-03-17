package cs580;

import java.util.ArrayList;
import java.util.List;

public class Calculations {
	
	
	public int euclideanGCD(int a,int b)
	{
		int r1=a,r2=b,r,q;
		
		while(r2>0)
		{
			q=r1/r2;
			r=r1-(q*r2);
			r1=r2;
			r2=r;
		}
		
		return r1;
	}
	
	public Integer[] zstar(int n)
	{
		List<Integer> elements = new ArrayList<Integer>();
		
		int j=0;
		for(int i=0;i<n;i++)
		{
			if(euclideanGCD(i,n) == 1)
			{
				elements.add(i);
				j++;
			}
		}
		Integer[] a = new Integer[j];
		elements.toArray(a);
		return a;
	}
	
	public void additiveInverse(Integer[] elements,int n)
	{
		
		
		for(int i=0;i<elements.length;i++)
		{
			for(int j=0;j<elements.length;j++)
			{
			//	System.out.println((elements[i]+elements[j])%n);
				if((elements[i]+elements[j])%n == 0)
				{
					System.out.print("( " + elements[i] + " , " + elements[j]+" ) , ");
				}
			}
			
		}
		System.out.println();
	}
	
	public void multiplicativeInverse(Integer[] elements,int n)
	{
		for(int i=0;i<elements.length;i++)
		{
			for(int j=0;j<elements.length;j++)
			{
			//	System.out.println((elements[i]+elements[j])%n);
				if((elements[i]*elements[j])%n == 1)
				{
					System.out.print("( " + elements[i] + " , " + elements[j]+" ) , ");
				}
			}
		}
		System.out.println();
	}
	

}
