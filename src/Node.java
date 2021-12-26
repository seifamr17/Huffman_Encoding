import java.util.Comparator;

public class Node {

	public Node left = null;
	public Node right = null;
	public char character;
	public int frequency;

	public Node(Node left, Node right, int frequency) {
		this.left = left;
		this.right = right;
		this.frequency = frequency;
	}
	
	public Node(Node left, Node right, int frequency, char character) {
		this.left = left;
		this.right = right;
		this.frequency = frequency;
		this.character = character;
	}
}

class NodeComparator implements Comparator<Node>{ 
     
    public int compare(Node n1, Node n2) { 
        if (n1.frequency > n2.frequency) 
            return 1; 
        else if (n1.frequency < n2.frequency) 
            return -1; 
        return 0; 
    } 
}
