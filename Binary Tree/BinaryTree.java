/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package binary.tree;

/**
 *
 * @author scra
 */
public class BinaryTree {

    /**
     * @param args the command line arguments
     */
    public BinaryTree gauche;
    public BinaryTree droite;
    public char element;
    static BinaryTree head;

    public BinaryTree() {

    }

    public BinaryTree getGauche() {
        return gauche;
    }

    public void setGauche(BinaryTree gauche) {
        this.gauche = gauche;
    }

    public BinaryTree getDroite() {
        return droite;
    }

    public void setDroite(BinaryTree droite) {
        this.droite = droite;
    }

    public char getElement() {
        return element;
    }

    public void setElement(char element) {
        this.element = element;
    }

    public static BinaryTree getHead() {
        return head;
    }

    public static void setHead(BinaryTree head) {
        BinaryTree.head = head;
    }

    public void getBinaryTree(String[] mots) {
        BinaryTree headTemp = null;
        for (int i = 0; i < mots.length; i++) {
            char[] mot = mots[i].toCharArray();
            for (int j = 0; j < mot.length; j++) {
                if (i == 0 && j == 0) {
                    setHead(new BinaryTree());
                    BinaryTree.getHead().setElement(mot[j]);
                } else {
                    BinaryTree temp = BinaryTree.getHead();
                    if (temp.getElement() == mot[j]) {
                        BinaryTree right = temp;
                        if (right == null) {
                            right.setDroite(new BinaryTree());
                            right.getDroite().setElement(mot[j]);

                        } else if (right.getElement() == mot[j]) {
                            // tant que l'élément de droite soit le meme que la lettre du mot
                            headTemp = right.getDroite();

                            if (headTemp.getElement() == mot[j]) {

                                // faire le swap
                                headTemp = headTemp.getDroite();
                                // si c'est null écrire Element
                                if (headTemp == null) {
                                    headTemp = new BinaryTree();
                                    headTemp.setElement(mot[j]);
                                } else if (headTemp.getElement() == ' ') {
                                    headTemp.setElement(mot[j]);
                                }
                            }
                        }
                    } else if (temp.getElement() != mot[j]) {
                        BinaryTree left = temp;
                        left.setGauche(new BinaryTree());
                        left.getGauche().setElement(mot[j]);
                    }
                }
            }
        }

    }

    public static void main(String[] args) {

    }

}
