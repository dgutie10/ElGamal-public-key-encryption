import java.io.*;
import java.math.BigInteger;
import java.util.Properties;
import java.util.Scanner;

public class CLI {
    final Scanner in = new Scanner(System.in);
    public int KEY_SIZE = 0;


    public void setKeyPrivateSize(){
        boolean validInput = false;
        while (!validInput) {
            System.out.print(
                    "Select public key size: \n" +
                    "1. 64 Bits \n" +
                    "2. 128 Bits \n" +
                    "3. 254 Bits \n" +
                    "4. 512 Bits \n" +
                    "5. 1028 Bits \n" +
                    "6. Exit \n" +
                    "\nEnter number for mode selection: \n");

            String selection = in.nextLine().trim();

            switch (selection){
                case "1":
                    KEY_SIZE = 64;
                    validInput = true;
                    break;
                case "2":
                    KEY_SIZE = 128;
                    validInput = true;
                    break;
                case "3":
                    KEY_SIZE = 254;
                    validInput = true;
                    break;
                case "4":
                    KEY_SIZE = 512;
                    validInput = true;
                    break;
                case "5":
                    KEY_SIZE = 1028;
                    validInput = true;
                    break;
                case "6":
                    KEY_SIZE = -1;
                    validInput = true;
                    break;
                default:
                    System.out.println("Invalid selection. Try again...\n");
            }
        }
    }

    public int mainMenu(){
        String selection = "0";
        boolean validInput = false;
        while (!validInput) {
            System.out.print(
                    "Select function: \n" +
                            "1. Regenerate Keys \n" +
                            "2. Get Public Key \n" +
                            "3. Encrypt Message \n" +
                            "4. Decrypt Message \n" +
                            "5. Exit \n" +
                            "\nEnter number for mode selection: \n");

            selection = in.nextLine().trim();

            switch (selection){
                case "1":
                case "2":
                case "3":
                case "4":
                case "5":
                    validInput = true;
                    break;
                default:
                    System.out.println("Invalid selection. Try again...\n");
            }
        }
        return Integer.parseInt(selection);
    }
}
