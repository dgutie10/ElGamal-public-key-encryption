import java.math.BigInteger;
import java.util.Random;

public class Utilities {

    final private int ROUNDS_OF_TEST = 20;
    final private Random random = new Random();

    public BigInteger generateOddRandomPrime(int bitSize){
        boolean primeIsOdd = false;
        BigInteger min = BigInteger.valueOf(2).pow(bitSize-1).add(BigInteger.ONE);
        BigInteger max = BigInteger.valueOf(2).pow(bitSize).subtract(BigInteger.ONE);
        BigInteger primeCandidate = null;
        while (!primeIsOdd) {
            primeCandidate = BigInteger.probablePrime(bitSize, random);
            if (primeCandidate.mod(BigInteger.valueOf(2)).compareTo(BigInteger.ZERO) != 0) {
                primeIsOdd = true;
            }
        }
        return  primeCandidate;
    }

    public BigInteger generateRandom(BigInteger max, BigInteger min){
        int len = max.bitLength();
        BigInteger randomNumber =  new BigInteger(len, random);
        BigInteger diff = max.subtract(min);
        if (randomNumber.compareTo(min) < 0) {
            randomNumber = randomNumber.add(min);
        }
        if (randomNumber.compareTo(diff) >= 0){
            randomNumber = randomNumber.mod(diff).add(min);
        }
        return  randomNumber;
    }

    private BigInteger squareAndMultiply(BigInteger a, BigInteger r, BigInteger n){
        BigInteger result = BigInteger.ONE;

        a = a.mod(n);
        while (r.compareTo(BigInteger.ZERO) >  0 ){
            if (r.mod(BigInteger.valueOf(2)).compareTo(BigInteger.ONE) ==  0) {
                result = result.multiply(a).mod(n);
            }

            r = r.divide(BigInteger.valueOf(2));
            a = a.multiply(a).mod(n);
        }
        return result;
    }

    public  boolean isPrime(BigInteger primeCandidate) throws Exception {
        boolean isPrime = false;
        BigInteger evenComponent  = primeCandidate.subtract(BigInteger.ONE);
        int maxDiv = 0;
        while (evenComponent.mod(BigInteger.valueOf(2)).compareTo(BigInteger.ZERO) == 0){
            evenComponent = evenComponent.divide(BigInteger.valueOf(2));
            maxDiv++;
        }
        if (BigInteger.valueOf(2).pow(maxDiv).multiply(evenComponent).compareTo(primeCandidate.subtract(BigInteger.ONE)) != 0){
            throw new Exception("Prime candidate could not be reduced to its even component: " + primeCandidate.toString());
        }

        for (int round = 0; round < ROUNDS_OF_TEST; round++ ){
            if(!testMillerRabin(primeCandidate, BigInteger.valueOf(maxDiv), evenComponent)) { return false; }
        }
        return  true;

    }


    private boolean testMillerRabin(BigInteger primeCandidate, BigInteger s, BigInteger r){
        BigInteger a = generateRandom(primeCandidate.subtract(BigInteger.valueOf(2)), BigInteger.valueOf(2));
        BigInteger y = squareAndMultiply(a, r, primeCandidate);
        BigInteger j = BigInteger.ONE;

        while (j.compareTo(s.subtract(BigInteger.ONE)) <= 0 && y.compareTo(primeCandidate.subtract(BigInteger.ONE))!= 0 ){
            y = y.pow(2).mod(primeCandidate);
            if (y.compareTo(BigInteger.ONE) == 0){ return  false;}
            if (y.compareTo(primeCandidate.subtract(BigInteger.ONE)) == 0 ){ return false; }
            j = j.add(BigInteger.ONE);
        }

        return true;
    }

    public BigInteger power (BigInteger base, BigInteger exponent){
        if (exponent.equals(BigInteger.ONE)){ return  base; }
        if (exponent.equals(BigInteger.ZERO)){ return  BigInteger.ONE; }

        BigInteger result = base;
        while(exponent.compareTo(BigInteger.ONE) > 0){
            result  = result.multiply(base);
            exponent = exponent.subtract(BigInteger.ONE);
        }
        return result;
    }

    public static void main(String[] args) {
        Utilities utilities = new Utilities();
        utilities.power(BigInteger.valueOf(2),BigInteger.valueOf(10));
    }
}
