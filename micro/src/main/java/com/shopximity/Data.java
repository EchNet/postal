package com.shopximity;

import java.util.HashMap;
import java.util.Map;

public class Data
{
	Sieve sieve = new Sieve(10000000);
	Factorizer facto = new Factorizer(sieve.toArray());

	public Map<String,Object> get(int index)
	{
		Map<String,Object> mapp = new HashMap<String,Object>();
		mapp.put("title", "The Number " + index);
		mapp.put("value", new Integer(index));
		mapp.put("decimal", Integer.toString(index));
		mapp.put("hexadecimal", "0x" + Integer.toString(index, 16));
		mapp.put("octal", "0" + Integer.toString(index, 8));
		if (sieve.isPrime(index))
		{
			mapp.put("prime", true);
		}
		else
		{
			mapp.put("sumOfProperDivisors", facto.getSumOfProperDivisors(index));
			mapp.put("rad", facto.rad(index));
			if (facto.getNumberOfPrimeFactors(index, index) == 1)
			{
				Factorizer.PrimeFactor[] pf = facto.factorize(index);
				mapp.put("base", pf[0].getFactor());
				mapp.put("exponent", pf[0].getPower());
			}
		}
		return mapp;
	}
}
