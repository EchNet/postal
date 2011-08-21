package com.shopximity.math;

import java.util.ArrayList;
import java.util.List;

/**
 * Provide analyses of numbers based on prime factorization.
 */
public class Factorizer
{
    private int[] primes;

    public Factorizer()
    {
        this(10000);
    }

    public Factorizer(int sieveSize)
    {
        this(new Sieve(sieveSize).toArray());
    }

    public Factorizer(int[] primes)
    {
        this.primes = primes;
    }

    public static class PrimeFactor
    {
        private long factor;
        private int power;

        PrimeFactor(long factor, int power)
        {
            this.factor = factor;
            this.power = power;
        }

        public long getFactor()
        {
            return factor;
        }

        public int getPower()
        {
            return power;
        }

        public String toString()
        {
            return factor + "^" + power;
        }
    }

    public PrimeFactor[] factorize(long n)
    {
        FactorKeeper keeper = new FactorKeeper(n);
        factorize(n, keeper);
        return keeper.getArray();
    }

    public int getNumberOfPrimeFactors(long n, int limit)
    {
        FactorCounter counter = new FactorCounter(n, limit);
        try
        {
            factorize(n, counter);
        }
        catch (Abort a)
        {
        }
        return counter.getCount();
    }

    public long getSumOfProperDivisors(long n)
    {
        FactorKeeper keeper = new FactorKeeper(n);
        factorize(n, keeper);
        return keeper.getSumOfProperDivisors();
    }

    public int getNumberOfProperDivisors(long n)
    {
        FactorKeeper keeper = new FactorKeeper(n);
        factorize(n, keeper);
        return keeper.getNumberOfProperDivisors();
    }

    public long[] getProperDivisors(long n)
    {
        FactorKeeper keeper = new FactorKeeper(n);
        factorize(n, keeper);
        return keeper.getProperDivisors();
    }

    public long rad(long n)
    {
        FactorKeeper keeper = new FactorKeeper(n);
        factorize(n, keeper);
        return keeper.getRad();
    }

    private void factorize(long n, FactorConsumer consumer)
    {
        long x = n;

        for (int p = 0; x > 1; ++p)
        {
            if (p >= primes.length)
            {
                throw new RuntimeException("need larger factorizer to factor " + n);
            }

            int prime = primes[p];
            if ((long)prime * prime > x)
            {
                consumer.consumeFactor(x, 1);
                return;
            }

            if (x % prime == 0)
            {
                int power = 0;
                do
                {
                    x /= prime;
                    ++power;
                }
                while (x % prime == 0);
                consumer.consumeFactor(prime, power);
            }
        }
    }

    private static class Abort extends RuntimeException{}

    private static abstract class FactorConsumer
    {
        public long n;
        FactorConsumer(long n) { this.n = n; }
        public abstract void consumeFactor(long factor, int power);
    }

    private static class FactorKeeper
        extends FactorConsumer
    {
        List<PrimeFactor> list = new ArrayList<PrimeFactor>();

        FactorKeeper(long n) { super(n); }

        public void consumeFactor(long factor, int power)
        {
            list.add(new PrimeFactor(factor, power));
        }

        public PrimeFactor[] getArray()
        {
            return (PrimeFactor[]) list.toArray(new PrimeFactor[list.size()]);
        }

        public long[] getProperDivisors()
        {
            return new DivisorSummer(n, list).getValues();
        }

        public long getSumOfProperDivisors()
        {
            return new DivisorSummer(n, list).getSum();
        }

        public int getNumberOfProperDivisors()
        {
            int num = 1;
            for (PrimeFactor pf : list)
            {
                num *= pf.getPower() + 1;
            }
            return num - 1;  // we said "proper"
        }

        public long getRad()
        {
            long rad = 1;
            for (PrimeFactor pf : list)
            {
                rad *= pf.getFactor();
            }
            return rad;
        }
    }

    private static class FactorCounter
        extends FactorConsumer
    {
        int limit;
        int count;

        FactorCounter(long n, int limit)
        {
            super(n);
            this.limit = limit;
        }

        public void consumeFactor(long factor, int power)
        {
            ++count;
            if (count == limit)
                throw new Abort();
        }

        public int getCount()
        {
            return count;
        }
    }

    private static class DivisorSummer
    {
        long n;
        List<PrimeFactor> factors;
        long sum = 0;
        List<Long> divisors;

        public DivisorSummer(long n, List<PrimeFactor> factors)
        {
            this.n = n;
            this.factors = factors;
        }

        public long getSum()
        {
            recombine(0, 1, false);
            return sum;
        }

        public long[] getValues()
        {
            divisors = new ArrayList<Long>();
            recombine(0, 1, true);
            long[] array = new long[divisors.size()];
            for (int i = 0; i < array.length; ++i)
                array[i] = divisors.get(i);
            return array;
        }

        private void recombine(int pos, long inValue, boolean keep)
        {
            if (pos >= factors.size())
            {
                if (inValue != n)  // proper divisors only
                {
                    sum += inValue;
                    if (keep)
                        divisors.add(inValue);
                }
            }
            else
            {
                PrimeFactor pf = factors.get(pos);
                long factor = pf.getFactor();
                int power = pf.getPower();
                for (int i = 0; i <= power; ++i)
                {
                    recombine(pos + 1, inValue, keep);
                    inValue *= factor;
                }
            }
        }
    }

    public static void main(String[] args)
    {
        Factorizer factorizer = new Factorizer();
        for (String arg : args)
        {
            for (PrimeFactor factor : factorizer.factorize(Integer.parseInt(arg)))
            {
                System.out.print(factor);
                System.out.print(" ");
            }
            System.out.println();
        }
    }
}
