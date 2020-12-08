import org.apache.commons.lang3.StringUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class ElGamalEncryption {
    final private Random random = new Random();
    final private  Utilities utilities = new Utilities();
    final private Properties properties = new Properties();
    final private CLI cli = new CLI();

    private BigInteger P;
    private BigInteger G;
    private BigInteger X;
    private BigInteger PRIVATE_KEY;



    public ElGamalEncryption() throws Exception {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("config.properties");
        try{
            properties.load(inputStream);
        } catch (Exception e ){
            cli.setKeyPrivateSize();
            generateKeys(cli.KEY_SIZE);
        }


        if (properties.getProperty("privateKey") ==  null){
            System.out.println("Both keys have been not been generated.");
            cli.setKeyPrivateSize();
            generateKeys(cli.KEY_SIZE);
        } else {
            System.out.println("Private key has been already been generated. Reading from resource file....");
        }

        P = new BigInteger(properties.getProperty("p"));
        G = new BigInteger(properties.getProperty("g"));
        X = new BigInteger(properties.getProperty("x"));
        PRIVATE_KEY = new BigInteger(properties.getProperty("privateKey"));
        displayPublicKey(true);
    }

    public void displayPublicKey(boolean fullDisplay){
        if (fullDisplay) {
            System.out.println(
                    "\nThis is the existing public key elements: \n" +
                            "P: " + P.toString() + "\n" +
                            "Q: " + G.toString() + "\n" +
                            "X: " + X.toString() + "\n"
            );
        }

        System.out.println("Public key: (" +  P.toString() +
                ", " + G.toString() +
                ", " + X.toString() + ") \n");
    }



    public void generateKeys (int keySize) throws Exception {
        FileOutputStream fileOutputStream = new FileOutputStream("src/main/resources/config.properties");
        BigInteger p = utilities.generateOddRandomPrime(keySize);
        int retry = 0;
        while (!utilities.isPrime(p) && retry < 20){
            retry++;
            System.out.println("Retry " + retry +": The number selected failed primality test.");
            p = utilities.generateOddRandomPrime(keySize);
        }
        if (utilities.isPrime(p)) {

            BigInteger g = utilities.generateRandom(p.subtract(BigInteger.ONE), BigInteger.ONE);
            BigInteger a = new BigInteger(p.bitLength() + 1, random);
            a = a.mod(p);
            if (a.compareTo(BigInteger.ZERO) == 0 || a.compareTo(BigInteger.ONE) == 0) {
                a = a.add(BigInteger.valueOf(2));
            }

            X = g.modPow(a, p);
            PRIVATE_KEY = a;
            P = p;
            G = g;

            properties.setProperty("p", p.toString());
            properties.setProperty("g", g.toString());
            properties.setProperty("x", X.toString());
            properties.setProperty("privateKey", a.toString());
            properties.store(fileOutputStream, null);
            fileOutputStream.close();
        }
    }


    public ArrayList<String> encrypt (String message){
        ArrayList<String> result = new ArrayList<>();
        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
        BigInteger k = utilities.generateRandom(P.subtract(BigInteger.valueOf(2)), BigInteger.valueOf(2));
        BigInteger gamma = G.modPow(k, P);
        BigInteger deltaSuffix = X.modPow(k,P);
        ArrayList<String> delta = new ArrayList<String>();

        for (byte messageByte : messageBytes) {
            BigInteger temp = deltaSuffix.multiply(BigInteger.valueOf(messageByte)).mod(P);
            System.out.println(temp.toString());
            delta.add(temp.toString());
        }
        String deltaString = StringUtils.join(delta, "-");
        result.add(gamma.toString());
        result.add(deltaString);
        return result;
    }

    public String decrypt (List<String> encryptedMessage){
        List<String> deltaList = Arrays.asList(encryptedMessage.get(1).split("-"));
        byte[] result = new byte[deltaList.size()];
        BigInteger gamma = new BigInteger (encryptedMessage.get(0));
        gamma = gamma.modPow(PRIVATE_KEY, P);
        gamma = gamma.modInverse(P);
        for (int i = 0; i < deltaList.size(); i++){
            BigInteger temp = new BigInteger(deltaList.get(i));
            temp = temp.multiply(gamma).mod(P);
            result[i] = (byte) Integer.parseInt(temp.toString());
        }
        return new String(result);
    }



    public static void main(String[] args) throws Exception {
        String outputFormat = "%-30s%s";
        boolean exit = false;
        int functionSelector = 0;
        ElGamalEncryption elGamalEncryption =  new ElGamalEncryption();
        while(!exit){
            switch (functionSelector){
                case 0:
                    functionSelector = elGamalEncryption.cli.mainMenu();
                    break;

                case 1:
                    System.out.println("WARNING: Back up the exiting content of the config.properties as this file will be re-written.");
                    elGamalEncryption.cli.setKeyPrivateSize();
                    elGamalEncryption.generateKeys(elGamalEncryption.cli.KEY_SIZE);
                    elGamalEncryption.displayPublicKey(true);
                    functionSelector = 0;
                    break;
                case 2:
                    elGamalEncryption.displayPublicKey(true);
                    functionSelector = 0;
                    break;
                case 3:
                    System.out.println("Enter clear text message: ");
                    String message = elGamalEncryption.cli.in.nextLine().trim();
                    List<String> encryptedMessage = elGamalEncryption.encrypt(message);
                    System.out.println(String.format(outputFormat, "Plain Text", message));
                    System.out.println(String.format(outputFormat, "Encrypted Text", StringUtils.join(encryptedMessage,",")));
                    elGamalEncryption.displayPublicKey(false);
                    functionSelector = 0;
                    break;
                case 4:
                    System.out.println("Enter encrypted text message: ");
                    String encryptedMsg = elGamalEncryption.cli.in.nextLine().trim();
                    List<String> msgList = Arrays.asList(encryptedMsg.split(","));
                    String decryptedMessage =  elGamalEncryption.decrypt(msgList);
                    System.out.println(String.format(outputFormat, "Plain Text", decryptedMessage));
                    System.out.println(String.format(outputFormat, "Encrypted Text", StringUtils.join(encryptedMsg,",")));
                    elGamalEncryption.displayPublicKey(false);
                    functionSelector = 0;
                    break;
                case 5:
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid selection. Try again...\n");

            }
        }
    }
}
