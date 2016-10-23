package binary.tree;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class BinaryTree {

    private char element; // lettre du noeud 
    private BinaryTree left; // noeud à gauche du noeud
    private BinaryTree right; // noeud à droite du noeud 
    private int weight; // token permmetant de reconnaitre la fin d'un mot dans l'arbre
    private static BinaryTree racine; // la racine de l'arbre
    private static Mot[] lexiqueTable; // le lexique qui est tableau de mots : token => mot
    private static int indiceText = 0;

    // Constructeur par défaut
    public BinaryTree() {
        this.element = ' ';
        this.weight = -1;
    }

    // constructer pour définir la racine de l'arbre
    public BinaryTree(BinaryTree left, BinaryTree right) {
        this.element = ' ';
        this.left = left;
        this.right = right;
        this.weight = -1;
    }

    // GETTERS AND SETTERS
    public static int getIndiceText() {
        return indiceText;
    }

    public static void setIndiceText(int indiceText) {
        BinaryTree.indiceText = indiceText;
    }

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

    public void creerfils() {
        this.setLeft(new BinaryTree());
        this.setRight(new BinaryTree());
    }

    // FONCTIONS
    /**
     * Récupère le prochain noeud de l'arbre
     *
     * @param element La lettre recherhé dans l'arbre.
     * @param dernierNoeud Permet d'informer la fonction si elle doit renvoyer
     * le prochain noeud à parcourir ou le noeud de la dernière lettre
     *
     * @return Une instance de noeud
     *
     */
    public BinaryTree getNode(char element, boolean dernierNoeud) {
        BinaryTree noeud = this;
        if (dernierNoeud == false) {
            if (this.getElement() == element) {
                noeud = this.getRight();

            } else if (this.getElement() == ' ') {

                this.creerfils();
                this.setElement(element);
                noeud = this.getRight();

            } else if (this.getElement() != element) {
                noeud = this.getLeft();
                noeud = noeud.getNode(element, false);
            }
        } else {
            if (this.getElement() == element) {
                noeud = this;

            } else if (this.getElement() == ' ') {

                this.creerfils();
                this.setElement(element);
                noeud = this;

            } else if (this.getElement() != element) {
                noeud = this.getLeft();
                noeud = noeud.getNode(element, true);
            }
        }

        // on cherche le token du mot
        return noeud;
    }

    /**
     * Générer Arbre
     *
     * @param mots Un tableau d' objet Mot
     *
     * @return Une instance d'arbre des mots passés en paramètre
     *
     */
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
                    // dernier noeud
                    if (j + 1 == lettres.length) {
                        noeudPrecedent = noeud.getNode(lettres[j], true);
                    } else {
                        noeudPrecedent = noeud;
                        noeud = noeud.getNode(lettres[j], false);
                    }
                }
            }
            // Mettre sur le dernier noeud le token du mot 
            int token = mots[i].getToken();
            noeudPrecedent.setWeight(token);
        }
    }

    /**
     * Split un fichier
     *
     * @param fichier La fichier à spliter
     * @param pathern Le pathern à utiliser pour split le fichier
     *
     * @return un lexique de mots que compose le fichier
     *
     */
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

    /**
     * Permet de générer le lexique
     *
     * @param fichier La fichier à spliter
     * @param pathern Le pathern à utiliser pour split le fichier
     *
     */
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

    /**
     * Permet de split le texte
     *
     * @param fichier La fichier à spliter
     *
     * @return L'ensemble des objets Mots du texte
     */
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

    /**
     * Permet de parcourir l'arbre
     *
     * @param lettre La lettre à trouver
     * @param finMot Informer la fonction si l'on est sur la dernière lettre
     *
     * @return Une instance de noeud
     *
     */
    public BinaryTree parcourirAbre(char lettre, boolean finMot) {
        BinaryTree noeud = this;
        if (finMot == false) {
            if (noeud.getElement() == lettre) {
                //System.out.println(noeud.getElement());
                //System.out.println(lettre);
                // alors on va à droite
                noeud = noeud.getRight();

                // le mot n'est pas dans le lexique
            } else if (noeud.getElement() == ' ') {
                noeud = null;

            } else if (noeud.getElement() != lettre) {
                //System.out.println("OK");
                // alors on va à gauche
                noeud = noeud.getLeft();
                noeud = noeud.parcourirAbre(lettre, false);
            }
        } else {
            if (noeud.getElement() == ' ') {
                noeud = null;
            } else if (noeud.getElement() != lettre) {
                //System.out.println("OK");
                // alors on va à gauche
                noeud = noeud.getLeft().parcourirAbre(lettre, true);
            }
        }
        return noeud;
    }

    /**
     * Compter les mots dans le texte
     *
     * @param text Le texte où l'on veut compter les mots
     *
     * @return Un hashmap des mots et dd nombres de fois qu'ils apparaissent
     */
    public Map<Integer, Integer> compterMot(String[] text) {
        Map<Integer, Integer> nbTokens = new HashMap<Integer, Integer>();
        BinaryTree noeud = null;

        int token = -2;
        int a = 0;
        boolean rechercheFrutueuse = false;
        //Pour chaque mot du texte

        for (int i = 0; i < text.length; i++) {

            noeud = BinaryTree.getRacine();

            char[] lettres = text[i].toCharArray();

            // Pour chaque lettres du mot du texte
            for (int j = 0; j < lettres.length; j++) {

                // dernier appel : fin du mot
                if (j + 1 == lettres.length) {
                    noeud = noeud.parcourirAbre(lettres[j], true);
                    //if (noeud.getRight().getElement() == '_') {
                    //Appel pour chercher les mots composés : ne fonctionne pas 
                        /* noeud = noeud.verifierMotCompose(text, i + 1);
                        
                     if(noeud != null){
                     token = noeud.getWeight();
                     i = BinaryTree.getIndiceText();
                     }*/
                    //}
                } // si on est pas a la fin du mot
                else {
                    if (noeud.getElement() == ' ') {
                        break;
                    }
                    noeud = noeud.parcourirAbre(lettres[j], false);
                    //Si le mot n'existe pas
                    if (noeud == null) {
                        break;
                    }
                }
            }
            if (token != -2) {
                nbTokens = compteurToken(noeud, nbTokens);
                token = -2;
            } else {
                nbTokens = compteurToken(noeud, nbTokens);
            }
        }
        return nbTokens;
    }

    /**
     * Permet de regarder si un mot est un mot composé
     *
     * @param text Le texte
     * @param indiceText L'indice du mot dans le texte
     *
     * @return Une instance de noeud et l'indice du text sur lequel on se trouve
     */
    public BinaryTree verifierMotCompose(String[] text, int indiceText) {
        BinaryTree noeud = this;
        boolean parcourir = true;
        int i = indiceText;
        BinaryTree saveNoeud = null;

        while (parcourir) {
            char[] lettres = text[i].toCharArray();

            for (int j = 0; j < lettres.length; j++) {
                if (j + 1 == lettres.length) {
                    noeud = noeud.parcourirAbre(lettres[j], true);
                    if (noeud == null) {
                        parcourir = false;
                        break;
                    }
                    if (noeud.getRight().getElement() == '_' && noeud.getWeight() == -1) {

                        noeud = verifierMotCompose(text, i++);
                    } else if (noeud.getRight().getElement() == '_' && noeud.getWeight() > -1) {
                        saveNoeud = noeud;
                        BinaryTree.setIndiceText(i);;
                        noeud = verifierMotCompose(text, i++);
                    }
                } else {
                    noeud = noeud.parcourirAbre(lettres[j], false);
                    //Si le mot n'existe pas
                    if (noeud == null) {
                        parcourir = false;
                        break;
                    }
                }
            }
        }
        return saveNoeud;
    }

    /**
     * Permet de compter les mots à l'aide de token
     *
     * @param noeud Une instance de noeud
     * @param nbTokens Une map de sauvegarde
     * 
     * @return Une instance de Map
     */
    public Map<Integer, Integer> compteurToken(BinaryTree noeud, Map<Integer, Integer> nbTokens) {

        if (noeud != null) {
            int token = noeud.getWeight();
            //System.out.println(token);
            if (nbTokens.get(token) != null) {

                int nb = nbTokens.get(token);
                nbTokens.put(token, nb + 1);

            } else {
                nbTokens.put(token, 1);
            }
        } else {
            // -1 est le token pour les mots qu'on ne connait pas = qui ne sont pas dans le lexique
            if (nbTokens.get(-1) != null) {
                int nb = nbTokens.get(-1);
                nbTokens.put(-1, nb + 1);

            } else {
                nbTokens.put(-1, 1);
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

        Map<Integer, Integer> nbTokens = binaryTree.compterMot(textTable);
        System.out.println(nbTokens.get(56384));

        //System.out.println(BinaryTree.getLexique()[0].getMot());
        //System.out.println("");
        //System.out.println(lexique.get("zouloues"));
        //System.out.println("  MOT " + binaryTree.getRacine().getRight().getRight().getElement());
    }

}
