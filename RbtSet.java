package edu.ktu.ds.lab2.utils;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Stack;

public class RbtSet<E extends Comparable<? super E>> {


    // Medžio šaknies mazgas
   protected Node<E> root = null;
    // Medžio dydis
   protected int size = 0;
   // rodykle i komparatoriu
   protected Comparator<? super E> c = null;
   // Braizymui reikalingi kintamieji
   static final int COUNT = 10;
   private static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
   private static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
   private static final String ANSI_RESET = "\u001B[0m";

    /**
     * Konstruktorius su komparatoriumi
     */
   public RbtSet(){
        this.c = Comparator.naturalOrder();
    }

    /**
     * Patikrina ar medis tuscias
     * @return
     */
    public boolean isEmpty() { return root == null; }

    /**
     * @return Grazina medzio dydi
     */
    public int size() {
        return size;
    }

    /**
     * Isvalo medi
     */
    public void clear() {
        root = null;
        size = 0;
    }

    /**
     * Patikrina ar elementas egzistuoja
     * @param element - Ieskomas elementas
     * @return - Ar elementas egzistuoja
     */

    public boolean contains(E element) {
        if (element == null) {
            throw new IllegalArgumentException("Element is null in contains(E element)");
        }
        Node<E> node = new Node<>(element);

        return node == get(element);
    }

    /**
     * Suskaiciuoja raudonu mazgu kieki
     * @param node - Medzio mazgas
     * @return - raudonu mazgu kiekis
     */
    private int countRed(Node<E> node)
    {
        int redNodes = 0;
        if (node == null) {
            return 0;
        }
        redNodes += countRed(node.left);
        redNodes += countRed(node.right);

        if(node.isRed()){
            redNodes++;
        }
        return redNodes;
    }

    public int countRedNodes()
    {
       return countRed(root);
    }

    /**
     * Suskaiciuoja juodu mazgu kieki
     * @param node - medzio mazgas
     * @return - juodu mazgu kiekis
     */

    private int countBlack(Node<E> node)
    {
        int blackNodes = 0;
        if (node == null) {
            return 0;
        }
        blackNodes += countBlack(node.left);
        blackNodes += countBlack(node.right);

        if(node.isBlack()){
            blackNodes++;
        }
        return blackNodes;
    }

    public int countBlackNodes()
    {
        return countBlack(root);
    }

    /**
     * Elementas pridedamas i medi
     * @param e - elementa, kuri reikia prideti
     */
    public void add(E e)
    {
        Node<E> node = new Node<E>(e);
        if(root == null)
        {
            root = node;
            root.black = true;
            size++;
            return;
        }
        AddRecursive(root,node);
    }

    /**
     * Ieskomas elementas
     * @param e - iesskoomas elementas
     * @return - ar elementas buvo rastas
     */
    public E get(E e)
    {
        Node<E> node = root;

        while(node != null)
        {
            if(node.element == e)
            {
                return node.element;
            }
            int cmp = c.compare(e,node.element);
            if(cmp > 0)
            {
                node = node.right;
            }
            else
            {
                node = node.left;
            }
        }
        return null;
    }

    /**
     * Pagalbinis metodas add  metodui
     * @param root
     * @param node
     */

    private void AddRecursive(Node<E> root, Node<E> node)
    {
        int cmp = c.compare(node.element, root.element);
        if(node!= null) {
            if (cmp > 0) {
                if (root.right == null) {
                    root.right = node;
                    node.parent = root;
                    size++;
                }
                AddRecursive(root.right,node);
                Case1(node);
            }
            else if (cmp < 0) {
                if (root.left == null) {
                    root.left = node;
                    node.parent = root;
                    size++;
                }
                AddRecursive(root.left,node);
                Case1(node);
            }
        }

    }

    /**
     * Istrinamas elementas
     * @param value - elementa, kuri reikia istrinti.
     * @return - ar elementas buvo istrintas
     */
    public boolean remove(E value) {

        if(delete(value))
        {
            size--;
        }
        return delete(value);

    }

    /**
     * Pagalbinis remove metodas
     * @param x
     * @return
     */

    private boolean delete(E x) {
        Node<E> node = find(x);
        if (node != null) {
            if (!isLeaf(node.left) && !isLeaf(node.right)) {
                node = maxPredecessor(node);
            }
            Node<E> child = isLeaf(node.right) ? node.left : node.right;
            if (node.isBlack()) {
                if (!isBlack(child)) {
                    node.setRed();
                }
                deleteCase1(node);
            }
            replace(node, child);
        }
        return node != null;
    }

    private void deleteCase1(Node<E> node) {
        if (node.parent != null) {
            deleteCase2(node);
        }
    }

    private void deleteCase2(Node<E> node) {
        Node<E> sibling = Sibling(node);
        if (isRed(sibling)) {
            node.parent.setRed();
            sibling.setBlack();
            if (node == node.parent.left) {
                rotateLeft(node.parent);
            } else {
                rotateRight(node.parent);
            }
        }
        deleteCase3(node);
    }

    private void deleteCase3(Node<E> node) {
        Node<E> sibling = Sibling(node);
        if (node.parent.isBlack() &&
                sibling != null &&
                isBlack(sibling) &&
                isBlack(sibling.left) &&
                isBlack(sibling.right)) {
            sibling.setRed();
            deleteCase1(node.parent);
        } else {
            deleteCase4(node);
        }
    }

    private void deleteCase4(Node<E> node) {
        Node<E> sibling = Sibling(node);
        if (node.parent.isRed() &&
                sibling != null &&
                isBlack(sibling) &&
                isBlack(sibling.left) &&
                isBlack(sibling.right)) {
            sibling.setRed();
            node.parent.setBlack();
        } else {
            deleteCase5(node);
        }
    }

    private void deleteCase5(Node<E> node) {
        Node<E> sibling = Sibling(node);
        if (node == node.parent.left &&
                sibling != null &&
                sibling.isBlack() &&
                isRed(sibling.left) &&
                isBlack(sibling.right)) {
            sibling.setRed();
            if (sibling.left != null) sibling.left.setBlack();
            rotateRight(sibling);
        } else if (node == node.parent.right &&
                sibling != null &&
                isBlack(sibling) &&
                isBlack(sibling.left) &&
                isRed(sibling.right)) {
            sibling.setRed();
            if (sibling.right != null) sibling.right.setBlack();
            rotateLeft(sibling);
        }
        deleteCase6(node);
    }
    private void deleteCase6(Node<E> node) {
        Node<E> sibling = Sibling(node);
        setColorOfOther(sibling, node.parent);
        setBlack(node.parent);
        if (node == node.parent.left) {
            setBlack(sibling.right);
            rotateLeft(node.parent);
        } else {
            setBlack(sibling.left);
            rotateRight(node.parent);
        }
    }

    /**
     * Patikrina ar mazgas yra raudonas
     * @param node - tikrinamas mazgas
     * @return- ar mazgas yra raudonas
     */
    private boolean isRed(Node<E> node) {
        return node != null && node.isRed();
    }
    /**
     * Patikrina ar mazgas yra juoodas
     * @param node - tikrinamas mazgas
     * @return- ar mazgas yra juodas
     */
    private boolean isBlack(Node<E> node) {
        return node == null || node.isBlack();
    }

    /**
     * Nustatomas mazgas juuoda spalva
     * @param node - nustatomas mazgas
     */
    private void setBlack(Node<E> node) {
        if (node != null) {
            node.setBlack();
        }
    }
    /**
     * Nustatomas mazgas raudona spalva
     * @param node - nustatomas mazgas
     */
    private void setRed(Node<E> node) {
        if (node != null) {
            node.setRed();
        }
    }

    /**
     * Patikrinama, ar mazgas yra lapas
     * @param node  - tikrinamas mazgas
     * @return
     */
    private boolean isLeaf(Node<E> node) {
        return node == null;
    }

    /**
     * Nustatomas vieno mazgo spalva kitam mazgui
     * @param node
     * @param other
     */
    private void setColorOfOther(Node<E> node, Node<E> other) {
        if (node != null && other != null) {
            node.black = other.black;
        }
    }

    /**
     * Ieskomas mazgo "senelis"
     * @param node
     * @return
     */
    private Node<E> Grandparent(Node<E> node)
    {
        if(node.parent != null)
        {
            return  node.parent.parent;
        }
        else
            return null;
    }
    /**
     * Ieskomas mazgo "dede"
     * @param node
     * @return
     */
    private Node<E> Uncle(Node<E> node)
    {
        Node<E> g = Grandparent(node);
        if(g  == null)
            return null;
        else if(node.parent == g.left)
            return g.right;
        else
        {
            return g.left;
        }
    }

    /**
     * Ieskomas mazgo "dvynys"
     * @param node
     * @return
     */
    private Node<E> Sibling(Node<E> node)
    {
        if(node == node.parent.left)
        {
            return node.parent.right;
        }
        else
        {
            return node.parent.left;
        }
    }

    /**
     * Surandamas reikalingas keitimui mazgas
     * @param node
     * @return
     */
    private Node<E> maxPredecessor(Node<E> node) {
        node = node.left;
        while (node.right != null) {
            node = node.right;
        }
        return node;
    }
    private Node<E> find(E e)
    {
        Node<E> node = root;

        while(node != null)
        {
            if(node.element == e)
            {
                return node;
            }
            int cmp = c.compare(e,node.element);
            if(cmp > 0)
            {
                node = node.right;
            }
            else
            {
                node = node.left;
            }
        }
        return null;
    }
    private void rotateLeft(Node<E> node)
    {
        if (node != null) {
            Node<E> right = node.right;
            replace(node, right);
            node.right = right == null ? null : right.left;
            if (right != null) {
                if (right.left != null) {
                    right.left.parent = node;
                }
                right.left = node;
            }
            node.parent = right;
        }
    }
    private void rotateRight(Node<E> node)
    {
        if (node != null) {
            Node<E> left = node.left;
            replace(node, left);
            node.left = left == null ? null : left.right;
            if (left != null) {
                if (left.right != null) {
                    left.right.parent = node;
                }
                left.right = node;
            }
            node.parent = left;
        }
    }

    /**
     * Apkeiciami mazgai
     * @param node
     * @param replacement
     */
    private void replace(Node<E> node, Node<E> replacement) {
        if (node == root) {
            root = replacement;
        }
        else {
            if (node == node.parent.left) {
                node.parent.left = replacement;
            } else {
                node.parent.right = replacement;
            }
        }
        if (replacement != null) {
            replacement.parent = node.parent;
        }
    }

    private void Case1(Node<E> node)
    {
        if(node == root)
        {
            node.black = true;
        }
        else
        {
            Case2(node);
        }
    }
    private void Case2(Node<E> node)
    {
        if(!node.parent.black)
        {
            Case3(node);
        }
    }
    private void Case3(Node<E> node)
    {
        Node<E> grandparent;
        Node<E> uncle = Uncle(node);

        if(uncle != null && !uncle.black)
        {
            node.parent.black = true;
            uncle.black = true;
            grandparent = Grandparent(node);
            grandparent.black = false;
            Case1(grandparent);
        }
        else
        {
            Case4(node);
        }
    }
    private void Case4(Node<E> node)
    {
        Node<E> grandparent = Grandparent(node);

        if(node == node.parent.right && node.parent == grandparent.left)
        {
            rotateLeft(node.parent);
            node = node.left;
        }
        else if(node == node.parent.left && node.parent == grandparent.right)
        {
            rotateRight(node.parent);
            node = node.right;
        }
        Case5(node);
    }
    private void Case5(Node<E> node)
    {
    Node<E> grandparent = Grandparent(node);
        node.parent.black = true;
        grandparent.black = false;
        if (node == node.parent.left && node.parent == grandparent.left) {
            rotateRight(grandparent);
        }
        else if (node == node.parent.right && node.parent == grandparent.right) {
            rotateLeft(grandparent);
        }

    }


    void print2DUtil(Node<E> root, int space)
    {
        if (root == null)
            return;

        space += COUNT;

        print2DUtil(root.right, space);

        System.out.print("\n");
        for (int i = COUNT; i < space; i++)
            System.out.print(" ");
        if(root.black)
            System.out.print(ANSI_BLUE_BACKGROUND + root.element + ANSI_RESET + "\n");
        else
            System.out.print(ANSI_PURPLE_BACKGROUND+ root.element + ANSI_RESET + "\n");

        print2DUtil(root.left, space);
    }

    public void print2D()
    {
        print2DUtil(root, 0);
    }

    public Iterator<E> iterator() {
        return new RbtSet.IteratorRbt(true);
    }


    private class IteratorRbt implements Iterator<E>
    {

        private Stack<Node<E>> stack = new Stack<>();
        private boolean ascending;
        private Node<E> parent = root;


        IteratorRbt(boolean ascendingOrder) {
            this.ascending = ascendingOrder;
            this.toStack(root);
        }
        private void toStack(Node<E> n) {
            while (n != null) {
                stack.push(n);
                n = (ascending) ? n.left : n.right;
            }
        }
        @Override
        public boolean hasNext() {
            return !stack.empty();
        }

        @Override
        public E next() {
            if (!stack.empty()) {
                Node<E> n = stack.pop();
                parent = (!stack.empty()) ? stack.peek() : root;
                Node<E> node = (ascending) ? n.right : n.left;
                toStack(node);
                return n.element;
            }
            else {
                return null;
            }
        }
    }


    class Node<N> {

        protected N element;
        protected Node<N> left;
        protected Node<N> right;
        protected Node<N> parent;
        protected boolean black;
        public Node(N element)
        {
            this.element = element;
            this.left = null;
            this.right = null;
            this.parent = null;
            this.black = false;

        }
        void setBlack() {
            black = true;
        }

        void setRed() {
            black = false;
        }

        boolean isBlack() {
            return black;
        }

        boolean isRed() {
            return !black;
        }
    }


}
