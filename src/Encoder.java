import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.PriorityQueue;

public class Encoder {
	static HashMap<Character, Integer> frequencyTable = new HashMap<Character, Integer>();
	static HashMap<Character, String> codeTable = new HashMap<Character, String>();
	static int numBits;
	static byte padding;

	public static void encode(String fileName) throws IOException {
		Long start = System.currentTimeMillis();
		
		// Scanning the file to calculate frequencies
		File file = new File(fileName);
		FileInputStream fileInput = new FileInputStream(file);
		int r;
		StringBuilder builder = new StringBuilder();
		while ((r = fileInput.read()) != -1) {
		   char c = (char) r;
		   builder.append(c);
		   
		   Integer temp;
		   if ((temp = frequencyTable.get(c)) != null) {
			   frequencyTable.replace(c, temp + 1);
		   }else {
			   frequencyTable.put(c, 1);
		   }
		}
		fileInput.close();
		String input = builder.toString();
		
		Node treeRoot = generateTree();
		generateCodes(treeRoot, "");
		
		builder.setLength(0);	// Resetting the String Builder
		builder.append("000");	// Place holder for the padding (to be determined later)
		builder = encodeTree(treeRoot, builder); // Storing the Huffman tree to be used for decompression
		
		// Scanning the input again to build the correct sequence of bits using the codes table
		int len = input.length();
		for (int i = 0; i < len; i++) {
			builder.append(codeTable.get(input.charAt(i)));
		}
		
		// Converting String of 0s and 1s to a bitSet
		String str = builder.toString();
		numBits = str.length();
		padding = (byte)((8 - (numBits % 8)) % 8);
		BitSet paddingBitSet = BitSet.valueOf(new byte[] { padding });
		BitSet encodedBits = stringToBitSet(str);
		
		// Storing the padding in first 3 bits of the stream ( possible values 0 --> 7 )
		for (int i = 0; i < 3; i++) {
			if (paddingBitSet.get(i)) {
				encodedBits.set(i);
			}
		}
		
		// Converting BitSet to Byte array
		byte[] encodedBytes = encodedBits.toByteArray();
		if (encodedBytes.length < ((numBits + padding) / 8))
			encodedBytes = Arrays.copyOf(encodedBytes, (numBits + padding) / 8);

		// Writing encoded data to a file
		double compressionRatio = ((double)file.length() / encodedBytes.length);
		Files.write(Paths.get(fileName), encodedBytes);
		
		// Printing required outputs
		System.out.println("Compression ratio = "+compressionRatio);
		System.out.println("Execution time =  " + (((double)System.currentTimeMillis() - start) / 1000) + "s\n");
		
		System.out.println("Byte\tCode\t\tNew Code");
		for (char c : codeTable.keySet()) {
			builder.setLength(0);
			BitSet oldCode = BitSet.valueOf(new byte[] { (byte)c });
			for (int i = 7; i >= 0; i--) {
	        	if (oldCode.get(i)) {
	        		builder.append('1');
	        	}else {
	        		builder.append('0');
	        	}
	        }
			System.out.println((int)c+"\t"+builder.toString()+"\t"+codeTable.get(c));
		}
	}
	
	public static Node generateTree() {
		
		int size = frequencyTable.size();
		PriorityQueue<Node> queue = new PriorityQueue<Node>(size, new NodeComparator());
		for (char c : frequencyTable.keySet()) {
			Node newNode = new Node(null, null, frequencyTable.get(c), c);
			queue.add(newNode);
		}
		
		while (size > 1) {
			Node left = queue.poll();
			Node right = queue.poll();
			int totalFrequency = left.frequency + right.frequency;
			Node newNode = new Node(left, right, totalFrequency);
			queue.add(newNode);
			size--;
		}
		
		Node root = queue.poll();		
		return root;
	}
	
	public static void generateCodes(Node node, String path) {
		if (node.left == null && node.right == null) {
			codeTable.put(node.character, path);
			return;
		}
		
		generateCodes(node.left, path+"0");
		generateCodes(node.right, path+"1");
	}
	
	public static BitSet stringToBitSet(String str) {
		int len = str.length();
		BitSet bitset = new BitSet(len);
	    for (int i = 0; i < len; i++) {
	        if (str.charAt(i) == '1') {
	            bitset.set(i);
	        }
	    }
	    return bitset;
	}

	public static StringBuilder encodeTree(Node node, StringBuilder builder) {
		
		if (node.left == null && node.right == null)
	    {
	        builder.append('1');
	        int ascii = (int) node.character;
	        BitSet temp = BitSet.valueOf(new byte[] { (byte) ascii });
	        for (int i = 0; i < 8; i++) {
	        	if (temp.get(i)) {
	        		builder.append('1');
	        	}else {
	        		builder.append('0');
	        	}
	        }
	    }
	    else
	    {
	    	builder.append('0');
	        builder = encodeTree(node.left, builder);
	        builder = encodeTree(node.right, builder);
	    }
		
		return builder;
	}
}

















