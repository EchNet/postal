//
// The indispensable prime number sieve.
//

package com.shopximity.math;

import java.util.BitSet;

public class Sieve
{
    private int size;
    private BitSet sieve;

    public Sieve(int size)
    {
        this.size = size;
        this.sieve = new BitSet(size + 1);

        // for n > 1, isPrime[n] remains false if n is prime.
        sieve.set(0, true); // isPrime[0] is meaningless.
        sieve.set(1, true); // isPrime[1] is always false.

        for (int n = 0; n < size; ++n)
        {
            // Working from bottom to top, each true sieve entry we find 
            // represents a prime number.
            if (!sieve.get(n))
            {
                // Strike out all multiples of the prime, excluding prime * 1.
                for (int j = n * 2; j <= size; j += n)
                {
                    sieve.set(j, true);
                }
            }
        }
    }

    final public int getSize()
    {
        return size;
    }

    final public boolean quickIsPrime(int n)
    {
        if (n > size) throw new IllegalArgumentException(n + " > " + size);
        if (n < 0) return false;
        return !sieve.get(n);
    }

    public boolean isPrime(long n)
    {
        if (n < 0) return false;
        if (n == 2) return true;
        if (n <= size) return !sieve.get((int)n);
        if (n % 2 == 0) return false;
        for (long i = 3; i <= size; i += 2)
        {
            if (!sieve.get((int)i))
            {
                if (i * i > n)
                    return true;
                if (n % i == 0)
                    return false;
            }
        }

        throw new IllegalArgumentException(n + ": too big to tell");
    }

    public int[] toArray()
    {
        int pCount = 0;
        for (int i = 2; i <= size; ++i)
        {
            if (isPrime(i))
            {
                ++pCount;
            }
        }

        int[] primes = new int[pCount];
        int p = 0;
        for (int i = 2; i <= size; ++i)
        {
            if (isPrime(i))
            {
                primes[p++] = i;
            }
        }

        return primes;
    }
}
