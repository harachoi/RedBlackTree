import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;


/**
 * RedBlackBST class
 */
public class RedBlackBST<Key extends Comparable<Key>, Value> {

    private static final boolean RED = true;
    private static final boolean BLACK = false;
    Node root;

    private LinkedList<Key> keyList = new LinkedList<>();
    private LinkedList<Key> list = new LinkedList<>();

    public RedBlackBST() {
        this.root = null;
    }

    public static void main(String[] args) {

        Scanner readerTest = null;

        try {
            readerTest = new Scanner(new File("test.txt"));
        } catch (IOException e) {
            System.out.println("Reading Oops");
        }

        RedBlackBST<Integer, Integer> test = new RedBlackBST<>();

        while (readerTest.hasNextLine()) {
            String[] input = readerTest.nextLine().split(" ");

            for (String x : input) {
                System.out.print(x + " ");
            }

            System.out.println();
            switch (input[0]) {
                case "insert":
                    Integer key = Integer.parseInt(input[1]);
                    Integer val = Integer.parseInt(input[2]);
                    test.insert(key, val);
                    printTree(test.root);
                    System.out.println();
                    break;

                case "delete":
                    Integer key1 = Integer.parseInt(input[1]);
                    test.delete(key1);
                    printTree(test.root);
                    System.out.println();
                    break;

                case "search":
                    Integer key2 = Integer.parseInt(input[1]);
                    Integer ans2 = test.search(key2);
                    System.out.println(ans2);
                    System.out.println();
                    break;

                case "getval":
                    Integer key3 = Integer.parseInt(input[1]);
                    Integer ans21 = test.getValByRank(key3);
                    System.out.println(ans21);
                    System.out.println();
                    break;

                case "rank":
                    Integer key4 = Integer.parseInt(input[1]);
                    Object ans22 = test.rank(key4);
                    System.out.println(ans22);
                    System.out.println();
                    break;

                case "getelement":
                    Integer low = Integer.parseInt(input[1]);
                    Integer high = Integer.parseInt(input[2]);
                    List<Integer> testList = test.getElements(low, high);

                    for (Integer list : testList) {
                        System.out.println(list);
                    }

                    break;

                default:
                    System.out.println("Error, Invalid test instruction! " + input[0]);
            }
        }

    }

    /*************************************************************************
     *  Prints the tree
     *************************************************************************/
    public static void printTree(RedBlackBST.Node node) {

        if (node == null) {
            return;
        }

        printTree(node.left);
        System.out.println("node key:: " + node.key + " color:: " + node.color + " node.NN:: " + node.N);
        printTree(node.right);

    }

    // is node x red; false if x is null ?
    private boolean isRed(Node x) {
        if (x == null) return false;
        return (x.color == RED);
    }

    // number of node in subtree rooted at x; 0 if x is null
    private int size(Node x) {
        if (x == null) return 0;
        return x.N;
    }

    // return number of key-value pairs in this symbol table
    public int size() {
        return size(root);
    }

    // is this symbol table empty?
    public boolean isEmpty() {
        return root == null;
    }

    /*************************************************************************
     *  Modification Functions
     *************************************************************************/

    // insert the key-value pair; overwrite the old value with the new value
    // if the key is already present
    public void insert(Key key, Value val) {
        root = insert(root, key, val);
        root.color = BLACK;
    }

    private Node insert(Node node, Key key, Value val) {
        if (node == null) {
            node = new Node(key, val, RED, 1);
            return node;
        }
        int compare = key.compareTo(node.key);
        if (compare > 0) {
            node.right = insert(node.right, key, val);
        } else if (compare < 0) {
            node.left = insert(node.left, key, val);
        } else {
            node.val = val;
        }
        if (isRed(node.right)) {
            node.right.color = BLACK;
            node = rotateLeft(node);
            node.left.color = RED;
        }
        if (isRed(node.left) && isRed(node.left.left)) {
            node = splitFourNode(node);
        }
        node.N = 1 + size(node.right) + size(node.left);
        return node;
    }

    public void test(Node node) {
        if (node == null) {
            return;
        }
        test(node.left);
        System.out.println("node.key:: " + node.key + " node.color::  " + node.color + " node.N:: " + node.N);
        test(node.right);
    }

    private Node splitFourNode(Node node) {
        node = rotateRight(node);
        assert (!isRed(node));
        flipColors(node);
        assert isRed(node.left);
        assert isRed(node.left.left);
        return node;
    }

    private Node fixUp(Node node) {
        if (isRed(node.right)) {
            node = rotateLeft(node);
        }
        if (isRed(node.left) && isRed(node.left.left)) {
            node = rotateRight(node);
        }
        if (isRed(node.left) && isRed(node.right)) {
            flipColors(node);
        }
        return node;
    }

    public void delete(Key key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        if (!contains(key)) {
            return;
        }
        if (!isRed(root.left) & !isRed(root.right)) {
            root.color = RED;
        }
        root = delete(root, key);
        if (!isEmpty()) {
            root.color = BLACK;
        }

        test(root);
    }

    private Node delete(Node node, Key key) {
        if (key.compareTo(node.key) < 0) {
            if (!isRed(node.left) && !isRed(node.left.left)) {
                node = moveRedLeft(node);
            }
            node.left = delete(node.left, key);
        } else {
            if (isRed(node.left)) {
                node = rotateRight(node);
            }

            if (isRed(node) && node.left != null && node.right != null) {
                if (!isRed(node.left) && !isRed(node.right)) {
                    flipColors(node);
                }
            }
            if (key.compareTo(node.key) == 0 && (node.right == null)) {
                if (node.left != null) {
                    node.key = node.left.key;
                    node.val = node.left.val;
                    node.left = deleteMin(node.left);
                    node.N = size(node.left) + size(node.right) + 1;
                    return node;
                }
                return null;
            }
            if (key.compareTo(node.key) == 0) {
                Node x = findMin(node.right);
                node.key = x.key;
                node.val = x.val;
                node.right = deleteMin(node.right);
            } else {
                node.right = delete(node.right, key);
            }
        }
        node.N = size(node.left) + size(node.right) + 1;

        return fixUp(node);
    }

    private Node findMin(Node node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    public void deleteMin() {
        root = deleteMin(root);
        if (root != null) {
            root.color = BLACK;
        }
    }

    private Node deleteMin(Node node) {
        if (node.left == null) {
            return null;
        }
        if (!isRed(node.left) && !isRed(node.left.left)) {
            node = moveRedLeft(node);
        }
        node.left = deleteMin(node.left);
        if (isRed(node.right)) {
            node = rotateLeft(node);
        }
        node.N = 1 + size(node.left) + size(node.right);
        return node;
    }

    /*************************************************************************
     *  Search FUnctions
     *************************************************************************/

    // value associated with the given key; null if no such key
    public Value search(Key key) {
        int compare;
        Node head = root;

        if (head == null) {
            return null;
        }
        while (head != null) {
            compare = key.compareTo(head.key);
            if (compare < 0) {
                head = head.left;
            } else if (compare > 0) {
                head = head.right;
            } else if (compare == 0) {
                return head.val;
            }
        }
        return null;
    }

    // is there a key-value pair with the given key?
    public boolean contains(Key key) {
        return (search(key) != null);
    }

    /*************************************************************************
     *  Utility functions
     *************************************************************************/

    // height of tree (1-node tree has height 0)
    public int height() {
        return height(root);
    }

    private int height(Node x) {
        if (x == null) return -1;
        return 1 + Math.max(height(x.left), height(x.right));
    }

    /*************************************************************************
     *  Rank Methods
     *************************************************************************/


    // the key of rank k
    public Key getValByRank(int k) {
        keyList = new LinkedList<>();
        makeKeyList(root, keyList);
        if (keyList.size() <= k) {
            return null;
        }

        return keyList.get(k);
    }

    // number of keys less than key
    public int rank(Key key) {
        LinkedList<Key> rankList = new LinkedList<>();
        inorder(root, rankList);

        for (int i = 0; i < root.N; i++) {
            if (rankList.get(i).compareTo(key) == 0) {
                return i;
            }
        }
        for (int i = 0; i < root.N; i++) {
            if (rankList.get(i).compareTo(key) > 0) {
                return i;
            }
        }
        return root.N;
    }

    private void inorder(Node node, LinkedList<Key> rankList) {
        if (node == null) {
            return;
        }
        inorder(node.left, rankList);
        rankList.add(node.key);
        inorder(node.right, rankList);
    }

    /***********************************************************************
     *  Range count and range search.
     ***********************************************************************/

    public List<Key> getElements(int a, int b) {
        LinkedList<Key> lists = new LinkedList<>();
        list = new LinkedList<>();
        if (!list.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {
                System.out.println(list.get(i));
            }
        }
        if (root != null) {
            if (a < 0 || b < 0 || b >= root.N) {
                return lists;
            } else {
                makeKeyList(root, list);
                for (int i = a; i <= b; i++) {
                    lists.addLast(list.get(i));
                }
            }
        }
        if (!lists.isEmpty()) {
            for (int i = 0; i < lists.size(); i++) {
                System.out.print(" " + lists.get(i));
            }
            System.out.println("");
        }
        return lists;
    }

    public void makeKeyList(Node node, LinkedList<Key> list) {
        if (node == null) {
            return;
        }
        makeKeyList(node.left, list);
        list.addLast(node.key);
        makeKeyList(node.right, list);
    }

    /*************************************************************************
     *  red-black tree helper functions
     *************************************************************************/

    // make a left-leaning link lean to the right
    private Node rotateRight(Node h) {
        Node x = h.left;
        h.left = x.right;
        x.right = h;
        x.color = x.right.color;
        x.right.color = RED;
        x.N = h.N;
        h.N = size(h.left) + size(h.right) + 1;
        return x;
    }

    // make a right-leaning link lean to the left
    private Node rotateLeft(Node h) {
        Node x = h.right;
        h.right = x.left;
        x.left = h;
        x.color = x.left.color;
        x.left.color = RED;
        x.N = h.N;
        h.N = size(h.left) + size(h.right) + 1;
        return x;
    }

    // flip the colors of a node and its two children
    private void flipColors(Node h) {
        h.color = !h.color;
        h.left.color = !h.left.color;
        h.right.color = !h.right.color;
    }

    // Assuming that h is red and both h.left and h.left.left
    // are black, make h.left or one of its children red.
    private Node moveRedLeft(Node h) {
        flipColors(h);
        if (isRed(h.right.left)) {
            h.right = rotateRight(h.right);
            h = rotateLeft(h);
        }
        return h;
    }

    // Assuming that h is red and both h.right and h.right.left
    // are black, make h.right or one of its children red.
    private Node moveRedRight(Node h) {
        flipColors(h);
        if (isRed(h.left.left)) {
            h = rotateRight(h);
        }
        return h;
    }

    public class Node {
        Key key;           // key
        Value val;         // associated data
        Node left, right;  // links to left and right subtrees
        boolean color;     // color of parent link
        int N;             // subtree count

        public Node(Key key, Value val, boolean color, int N) {
            this.key = key;
            this.val = val;
            this.color = color;
            this.N = N;
        }
    }
}