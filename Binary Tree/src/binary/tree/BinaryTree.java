package binary.tree;

import binary.tree.Mot;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

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

    public void genererArbre(Mot[] mots) {

        // HEAD TEMP PERMET DE SAUVEGARDER LE NOEUD DE L'ARBRE  SUR LEQUEL ON EST
        BinaryTree noeud = null;
        BinaryTree noeudPrecedent = null;

        // initialisation de l'arbre
        setRacine(new BinaryTree(new BinaryTree(), new BinaryTree()));
        racine = getRacine();

        for (int i = 0; i < mots.length; i++) {
            char[] lettres = mots[i].getMot().toCharArray();
            // on crée l'objet mot 

            //System.out.println();
            noeud = BinaryTree.getRacine();

            for (int j = 0; j < lettres.length; j++) {

                // charger la tete de l'arbre
                if (i == 0 && j < lettres.length) {
                    noeud.setElement(lettres[j]);
                    noeudPrecedent = noeud;
                    noeud = noeud.getRight();
                    noeud.creerfils();

                } else {
                    noeudPrecedent = noeud;
                    noeud = noeud.getNode(lettres[j]);
                }
            }
            // Mettre sur le dernier noeud le token du mot 
            int token = mots[i].getToken();
            noeudPrecedent.setWeight(token);
        }
    }

    public void splitFichier(BufferedReader fichier, String pathern) throws IOException {

        String line;
        Mot lexiqueTable[] = new Mot[87548];

        int i = 0;
        while ((line = fichier.readLine()) != null) {

            String[] mot_token = line.split(pathern);
            Mot mot = new Mot(mot_token[0], Integer.parseInt(mot_token[1]));
            lexiqueTable[i] = mot;
            i++;
        }
        fichier.close();
        BinaryTree.setLexique(lexiqueTable);
    }

    public String[] generateTable(String fichier, boolean choice) {
        String texte = " ";
        try {
            InputStream flux = new FileInputStream(fichier);
            InputStreamReader ipsr = new InputStreamReader(flux);
            BufferedReader buff = new BufferedReader(ipsr);
            if (choice == true) {
                this.splitFichier(buff, " ");
                return null;
            } else {
                return this.splitTexte(buff);
            }

        } catch (IOException e) {
            System.out.println(e.toString());
            System.out.println("Impossible de lire le fichier");
        }
        return null;
    }

    public String[] splitTexte(BufferedReader fichier) throws IOException {
        String[] mots = null;
        String line = " ";
        String text = " ";

        while ((line = fichier.readLine()) != null) {
            text += line + " ";
        }
        fichier.close();

        StringTokenizer st = new StringTokenizer(text);
        mots = new String[st.countTokens()];

        int i = 0;
        while (st.hasMoreTokens()) {
            mots[i] = st.nextToken();
            i++;
        }

        return mots;
    }

    public Map<Integer, Integer> compterMot(String[] text) {
        Map<Integer, Integer> nbTokens = new HashMap<Integer, Integer>();
        BinaryTree noeudPrecedent = null;
        for (int i = 0; i < text.length; i++) {
            BinaryTree noeud = BinaryTree.getRacine();

            char[] lettres = text[i].toCharArray();

            for (int j = 0; j < lettres.length; j++) {
                if (noeud.getElement() == lettres[j]) {
                    // alors on va à droite
                    noeudPrecedent = noeud;
                    noeud = noeud.getRight();
                } else if (noeud.getElement() != lettres[j]) {
                    // alors on va à gauche
                    noeudPrecedent = noeud;
                    noeud = noeud.getLeft();
                } else if (noeud.getElement() == ' ') {
                    // mot inconnu
                    noeudPrecedent = noeud;
                    break;
                }
            }
            int token = noeudPrecedent.getWeight();
            System.out.println(token);
            if (nbTokens.get(token) != null) {
                int nb = nbTokens.get(token);
                nbTokens.put(token, nb + 1);

            } else {
                nbTokens.put(token, 1);
            }
        }
        return nbTokens;
    }

    // MAIN
    public static void main(String[] args) throws FileNotFoundException {
        BinaryTree binaryTree = new BinaryTree();
        Mot[] mot_token = null;

        String fichier = "src/binary/tree/lexique.txt";
        String texte = "src/binary/tree/texte";

        // on génère le lexique
        binaryTree.generateTable(fichier, true);

        // on récupere le lexique
        mot_token = BinaryTree.getLexique();

        // on genere l'arbre
        binaryTree.genererArbre(mot_token);

        // on charge le texte dans un tableau de String
        String[] textTable = binaryTree.generateTable(texte, false);
        System.out.println(textTable[2]);

        Map<Integer, Integer> nbTokens = binaryTree.compterMot(textTable);

        //System.out.println(BinaryTree.getLexique()[0].getMot());
        //System.out.println("");
        //System.out.println(lexique.get("zouloues"));
        //System.out.println("  MOT " + binaryTree.getRacine().getRight().getRight().getElement());
    }

}
