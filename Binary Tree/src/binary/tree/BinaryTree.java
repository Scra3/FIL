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

        // initialisation de l'arbre
        setHead(new BinaryTree());
        headTemp = getHead();

        for (int i = 0; i < mots.length; i++) {
            char[] mot = mots[i].toCharArray();
                System.out.println(mot[1]);
            
            headTemp = BinaryTree.getHead();
            for (int j = 0; j < mot.length; j++) {
                System.out.print(headTemp.getElement() );
                // charger la tete de l'arbre
                if (i == 0 && j < mot.length) {
                    headTemp.setElement(mot[j]);
                    headTemp.setDroite(new BinaryTree());
                    headTemp = headTemp.getDroite();
                } else {
                    if (headTemp.getElement() == mot[j]) {
                        if (headTemp.getDroite() == null) {

                            headTemp.setDroite(new BinaryTree());
                            BinaryTree right = headTemp.getDroite();
                            right.setElement(mot[j]);
                            headTemp = right;

                            // si l'élément de droite a été ajouté
                        } else {
                            BinaryTree right = headTemp.getDroite();
                            headTemp = right.getDroite();
                        }
                    } else {
                        if (headTemp.getGauche() == null) {

                            headTemp.setGauche(new BinaryTree());
                            BinaryTree left = headTemp.getGauche();
                            // on met l'élément de droite sur le pointeur
                            left.setElement(mot[j]);
                            headTemp = left;
                            
                        } else {
                            
                            BinaryTree left = headTemp.getGauche();
                            headTemp = left.getGauche();
                        }
                    }
                }
            }
        }
    }

    public static void main(String[] args) {

        BinaryTree arbre = new BinaryTree();
        String[] mots = {"Abdallah", "Abdel", "Abdelkader", "Abdellatif", "Abdou", "Abdul"};
        arbre.getBinaryTree(mots);
    }

}
