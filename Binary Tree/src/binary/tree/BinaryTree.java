package binary.tree;

import binary.tree.Mot;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class BinaryTree {

    private char element;
    private BinaryTree left;
    private BinaryTree right;
    private int weight;
    private static BinaryTree racine;
    private static Mot[] lexiqueTable;

    public BinaryTree() {
        this.element = ' ';
        this.weight = -1;
    }

    public BinaryTree(BinaryTree left, BinaryTree right) {
        this.element = ' ';
        this.left = left;
        this.right = right;
        this.weight = -1;
    }

    // GETTERS AND SETTERS
    public char getElement() {
        return element;
    }

    public void setElement(char element) {
        this.element = element;
    }

    public BinaryTree getLeft() {
        return left;
    }

    public void setLeft(BinaryTree left) {
        this.left = left;
    }

    public BinaryTree getRight() {
        return right;
    }

    public void setRight(BinaryTree right) {
        this.right = right;
    }

    public static BinaryTree getRacine() {
        return racine;
    }

    public static void setRacine(BinaryTree racine) {
        BinaryTree.racine = racine;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void incrementerWeight(int weight) {
        this.setWeight(weight + 1);
    }

    public void decrementerWeight(int weight) {
        this.setWeight(weight - 1);
    }

    public static Mot[] getLexique() {
        return lexiqueTable;
    }

    public static void setLexique(Mot[] lexique) {
        BinaryTree.lexiqueTable = lexique;
    }

    // FONCTIONS
    public void creerfils() {
        this.setLeft(new BinaryTree());
        this.setRight(new BinaryTree());
    }

    public BinaryTree getNode(char element) {
        BinaryTree noeud = new BinaryTree();

        if (this.getElement() == element) {
            noeud = this.getRight();

        } else if (this.getElement() == ' ') {

            this.creerfils();
            this.setElement(element);
            noeud = this.getRight();

        } else if (this.getElement() != element) {
            noeud = this.getLeft();
            noeud = noeud.getNode(element);
        }

        // on cherche le token du mot
        return noeud;
    }

    public int getToken(String mot) {
        int token = -1;
        Mot[] mots = BinaryTree.getLexique();
        int i = 0;
        boolean find = false;

        while (i < mots.length || find == false) {
            if (mot.equals(mots[i].getMot())) {
                token = mots[i].getToken();
                find = true;
            }
            i++;
        }
        return token;
    }

    public void genererArbre(Mot[] mots) {

        // HEAD TEMP PERMET DE SAUVEGARDER LE NOEUD DE L'ARBRE  SUR LEQUEL ON EST
        BinaryTree noeud = null;

        // initialisation de l'arbre
        setRacine(new BinaryTree(new BinaryTree(), new BinaryTree()));
        racine = getRacine();

        for (int i = 0; i < mots.length; i++) {
            char[] mot = mots[i].getMot().toCharArray();
            // on crée l'objet mot 

            //System.out.println();
            noeud = BinaryTree.getRacine();
            for (int j = 0; j < mot.length; j++) {

                // charger la tete de l'arbre
                if (i == 0 && j < mot.length) {
                    noeud.setElement(mot[j]);
                    int poid = noeud.getWeight();
                    noeud = noeud.getRight();
                    noeud.creerfils();
                    noeud.incrementerWeight(poid);

                } else {
                    noeud = noeud.getNode(mot[j]);
                }
            }
            // Mettre sur le dernier noeud le token du mot 
            int token = noeud.getToken(mots[i].getMot());
            noeud.setWeight(token);
        }

    }

    public void splitFichier(BufferedReader fichier, String pathern) throws IOException {

        String line;
        Mot lexiqueTable[] = new Mot[87548];

        int i = 0;
        while ((line = fichier.readLine()) != null) {

            String[] mot_token = line.split(" ");
            Mot mot = new Mot(mot_token[0], Integer.parseInt(mot_token[1]));
            lexiqueTable[i] = mot;
            i++;
        }
        fichier.close();
        BinaryTree.setLexique(lexiqueTable);
    }

    public Map genererLexique(String fichier) {
        Map<String, Integer> lexique = new HashMap<String, Integer>();

        try {
            InputStream flux = new FileInputStream(fichier);
            InputStreamReader ipsr = new InputStreamReader(flux);
            BufferedReader buff = new BufferedReader(ipsr);

            this.splitFichier(buff, fichier);

        } catch (IOException e) {
            System.out.println(e.toString());
            System.out.println("Impossible de lire le fichier");
        }

        return lexique;
    }

    // MAIN
    public static void main(String[] args) throws FileNotFoundException {
        BinaryTree binaryTree = new BinaryTree();
        Mot[] mot_token = null;

        String fichier = "src/binary/tree/lexique.txt";

        // on génère le lexique
        binaryTree.genererLexique(fichier);

        // on récupère seulement les mots du lexique
        mot_token = BinaryTree.getLexique();

        binaryTree.genererArbre(mot_token);
        System.out.println(BinaryTree.getLexique()[0].getMot());
        System.out.println("");

        //System.out.println(lexique.get("zouloues"));
        System.out.println("  MOT " + binaryTree.getRacine().getRight().getRight().getElement());
    }
}
