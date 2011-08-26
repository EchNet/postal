package com.shopximity.math;

import java.util.*;

public class IntegerFunFacts
{
	int n;

	static Sieve sieve = new Sieve(10000000);
	static Factorizer facto = new Factorizer(sieve.toArray());

	public IntegerFunFacts(int n)
	{
		this.n = n;
	}

	public String getTitle()
	{
		return "The Number " + n;
	}

	public int getValue()
	{
		return n;
	}

	public String getDecimal()
	{
		return Integer.toString(n);
	}

	public String getHexadecimal()
	{
		return "0x" + Integer.toString(n, 16);
	}

	public String getOctal()
	{
		return "0" + Integer.toString(n, 8);
	}

	public boolean isPrime()
	{
		return sieve.isPrime(n);
	}

	public long getSumOfProperDivisors()
	{
		return facto.getSumOfProperDivisors(n);
	}

	public long getRad()
	{
		return facto.rad(n);
	}

	public Map<String,Integer> getBaseExpo()
	{
		if (facto.getNumberOfPrimeFactors(n, n) == 1)
		{
			Map<String,Integer> baseExpo = new HashMap<String,Integer>();
			Factorizer.PrimeFactor[] pf = facto.factorize(n);
			baseExpo.put("base", new Integer((int) pf[0].getFactor()));
			baseExpo.put("exponent", new Integer((int) pf[0].getPower()));
			return baseExpo;
		}
		return null;
	}

	public List<IntegerFunFacts> getRelated()
	{
		return new AbstractList<IntegerFunFacts>()
		{
			public int size()
			{
				return (n % 5) + 1;
			}

			public IntegerFunFacts get(int index)
			{
				int nn;
				switch (index)
				{
				case 0:
					nn = n + 1;
					break;
				case 1:
					nn = n * 2 + 1;
					break;
				case 2:
					nn = n * 3;
					break;
				case 3:
					nn = n * 10;
					break;
				default:
					nn = n * 100 - 1;
					break;
				}
				return new IntegerFunFacts(nn);
			}
		};
	}

	public String toString()
	{
		return getDecimal();
	}
}
