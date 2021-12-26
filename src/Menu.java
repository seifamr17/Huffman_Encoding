import java.io.IOException;
import java.util.Scanner;

public class Menu {

	public static void main(String[] args) throws IOException {
		
		Scanner scanner = new Scanner(System.in);
        System.out.print("Enter file name: ");
        String fileName = scanner.nextLine();
        System.out.println("What would you like to do? ");
        System.out.println("1. Compress the file ");
        System.out.println("2. Decompress the file ");
        int selection = scanner.nextInt();
        scanner.close();
        
        switch(selection) {
        	case 1: Encoder.encode(fileName); break;
        	case 2: Decoder.decode(fileName); break;
        	default: System.out.println("Invalid selection!!");
        }
        
	}

}
