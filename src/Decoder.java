import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;

public class Decoder {
	
	public static int numBits;
	public static int bitsRead = 3;
	public static BitSet encodedBits;
	static HashMap<String, Character> codeTable = new HashMap<String, Character>();

	public static void decode(String fileName) throws IOException {
		Long start = System.currentTimeMillis();
		
		// Reading encoded data from the compressed file
		byte[] encodedBytes = Files.readAllBytes(Paths.get(fileName));
		encodedBits = BitSet.valueOf(encodedBytes);
		
		// Extracting the padding (First 3 bits in the file)
		int padding = 0;
		for (int i = 0; i < 3; i++) {
			padding += encodedBits.get(i) ? (1 << i) : 0;
		}
		numBits = (encodedBytes.length * 8) - padding;
		
		// Extracting the Huffman tree and using it to generate the codes table
		Node treeRoot = decodeTree();
		generateCodes(treeRoot, "");
		
		// Decoding the compressed file using the codes table
		StringBuilder codeBuilder = new StringBuilder();
		StringBuilder outBuilder = new StringBuilder();
		while (bitsRead < numBits) {
			if (encodedBits.get(bitsRead++))
				codeBuilder.append('1');
			else
				codeBuilder.append('0');
			
			Character c = codeTable.get(codeBuilder.toString());
			if (c != null) {
				codeBuilder.setLength(0);
				outBuilder.append(c);
			}
		}
		
		// Writing the decoded data to a file
		byte[] output = outBuilder.toString().getBytes();
		Files.write(Paths.get(fileName), output);
		System.out.println("Execution time =  " + (((double)System.currentTimeMillis() - start) / 1000) + "s\n");
		
	}
	
	public static Node decodeTree()
	{		
	    if (encodedBits.get(bitsRead++))
	    {  	
	    	char character = 0;
	    	for (int i = bitsRead; i < bitsRead + 8; i++) {
				character += encodedBits.get(i) ? (1 << (i - bitsRead)) : 0;
			}
	    	
	    	bitsRead += 8;
	    	
	        return new Node(null, null, 0, character);
	    }
	    else
	    {
	        Node leftChild = decodeTree();
	        Node rightChild = decodeTree();
	        return new Node(leftChild, rightChild, 0);
	    }
	}
	
	public static void generateCodes(Node node, String path) {
		if (node.left == null && node.right == null) {
			codeTable.put(path, node.character);
			return;
		}
		
		generateCodes(node.left, path+"0");
		generateCodes(node.right, path+"1");
	}
}
