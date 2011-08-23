package com.shopximity.math;

import java.util.HashMap;
import java.util.Map;

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

	public Integer getBase()
	{
		if (facto.getNumberOfPrimeFactors(n, n) == 1)
		{
			Factorizer.PrimeFactor[] pf = facto.factorize(n);
			return new Integer((int) pf[0].getFactor());
		}
		return null;
	}

	public Integer getExponent()
	{
		if (facto.getNumberOfPrimeFactors(n, n) == 1)
		{
			Factorizer.PrimeFactor[] pf = facto.factorize(n);
			return new Integer((int) pf[0].getPower());
		}
		return null;
	}
}
