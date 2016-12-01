package viterbi;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Viterbi {

    //variables globales 
    static int tailleCorpus; // 631991
    static HashMap<String, String> estimationModele;

    //Afficher un tableau de string
    private void displayTable(String[] tableau) {
        for (String tableau1 : tableau) {
            System.out.println(tableau1);
        }
    }

    // Afficher hashmap
    private void displayMap(LinkedHashMap<String, ArrayList<String>> treillis) {
        for (Map.Entry<String, ArrayList<String>> e : treillis.entrySet()) {
            ArrayList value = e.getValue();
            String key = e.getKey();
            System.out.println("Etape " + key);

            for (int i = 0; i < value.size(); i++) {
                System.out.println(value.get(i));
            }
            System.out.println("Fin étape\n");
        }
    }

    // Charger le fichier dans un buffer
    private BufferedReader getBufferedReader(String fichier) {

        BufferedReader buff = null;
        try {
            InputStream flux = new FileInputStream(fichier);
            InputStreamReader ipsr = new InputStreamReader(flux);
            buff = new BufferedReader(ipsr);

        } catch (IOException e) {
            System.out.println(e.toString());
            System.out.println(" Impossible de lire le fichier");
        } finally {
            return buff;
        }
    }

    //Permet de lire un fichier et de le retourner dans un string
    private String readFile(BufferedReader fichier) throws IOException {

        String line;
        String text = "";
        try {
            while ((line = fichier.readLine()) != null) {
                text = text + line;
                text = text + "\n";
            }
            line = "";
        } catch (IOException io) {
            System.out.println(io + " Error file " + fichier);
        } finally {
            if (!text.equals("")) {
                fichier.close();
            }
        }
        return text;
    }

    // retourne chaque étape dans un tableau
    private String[] wrapLine(String strFile) {
        return strFile.split("%col");
    }

    private LinkedHashMap insertTreillis(String strTreillis) {
        Viterbi viterbi = new Viterbi();
        String[] tabsTreillis;
        LinkedHashMap<String, ArrayList> treillis = new LinkedHashMap<String, ArrayList>();

        //Lire le treillis et le wrap par étapes
        tabsTreillis = viterbi.wrapLine(strTreillis);

        // on créer notre liste
        for (int i = 1; i < tabsTreillis.length; i++) {
            ArrayList<String> tokenLogProb = new ArrayList<String>(); // représente un string contenant un couple (token , prob)
            String[] couples = tabsTreillis[i].split("\n");

            for (int j = 1; j < couples.length; j++) {
                tokenLogProb.add(couples[j]);
            }
            treillis.put(couples[0].trim(), tokenLogProb);
        }

        return treillis;
    }

    // RETURN le mininum emission d'une étape prenant en param les étape et l'indice de l'étape
    private String minEmission(LinkedHashMap<String, ArrayList<String>> treillis, String etape) {
        String minEmission = null;
        double min;
        double minOc;
        // On récupère l'étape
        ArrayList a = treillis.get(etape);
        // On initialise le min et son couple
        String coupleStr = (String) a.get(0);
        min = Double.parseDouble(coupleStr.split(" ")[1]);
        minEmission = coupleStr;
        for (int i = 1; i < a.size(); i++) {
            coupleStr = (String) a.get(i);
            minOc = Double.parseDouble(coupleStr.split(" ")[1]);
            if (min > minOc) {
                minEmission = coupleStr;
                min = minOc;
            }
        }
        return minEmission;

    }

    // return tous les min emissions des étapes
    private String[] getAllMinEMissionEtape(LinkedHashMap<String, ArrayList<String>> treillis) {
        Viterbi viter = new Viterbi();
        int tailleTreillis = treillis.size();
        String[] mins = new String[tailleTreillis];
        for (int i = 0; i < tailleTreillis; i++) {
            mins[i] = viter.minEmission(treillis, Integer.toString(i));
        }
        return mins;
    }

    // initialise alpha et beta avec l'étape 1 du treillis
    private Double[] initViterbi(int n, ArrayList<String> etape1) {
        Double[] matrice = new Double[1];
        Double[][] alpha = new Double[1][n];
        Double[][] beta = new Double[1][n];

        for (int i = 0; i < etape1.size(); i++) {
            alpha[1][i] = probabiliteInitiale(etape1.get(i)) * probabiliteEmission(etape1.get(i), i);
            beta[1][i] = 0.0;
        }

        return matrice;
    }

    //calcule la proba initiale du mot
    private double probabiliteInitiale(String motW) {
        double probabilite = 0;
        double a = Double.parseDouble(estimationModele.get(motW));
        probabilite = a / tailleCorpus;
        return probabilite;
    }

    private double probabiliteEmission(String motW, int etape) {
        double probabilite = 0;
        return probabilite;
    }

    private void compterMots(String corpus) {
        tailleCorpus = corpus.split("\\\\s").length;
    }

    private void initHashMap(String bigrammes){
        String[] lignes = bigrammes.split("\n");
        String a; // tokens
        String b; // nb occurences
        for (int i = 0; i < lignes.length; i++) {
            String[] sequence = lignes[i].split(" ");
            a = sequence[0];
            b = sequence[1];
            estimationModele.put(b, a);                   
        }
    }
            
            
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {

        //DÉCLARATIONS
        final String cheminTreillis = "src/viterbi/treillis.txt"; // chemin du treillis
        final String chemintoken = "src/viterbi/modele_2g_ratp_fr.txt"; // chemin du token
        final String cheminCorpusToken = "src/viterbi/corpusToken.txt";
        final String cheminBigrammes = "src/viterbi/bigramme.txt";
        Viterbi viterbi = new Viterbi();
        BufferedReader buff = null; // buffer pour lire le fichier treillis
        String strFile = ""; // fichier treillis
        String token = ""; // fichier tokens
        String corpusTokenize = ""; // fichier corpusTokenize
        String bigrammes = ""; // fichier corpusTokenize
        LinkedHashMap<String, ArrayList<String>> treillis;
        String[] allMinEm;
        int T = 4; //longueur du treillis
        int N = 4; // mots possibles à chaque étape du treillis
        String alpha[][] = new String[T][];
        String beta[][] = new String[T][];

        // On récupère le contenu du fichier
        buff = viterbi.getBufferedReader(cheminTreillis);
        strFile = viterbi.readFile(buff);

        // On récupère le contenu du token
        buff = viterbi.getBufferedReader(chemintoken);
        token = viterbi.readFile(buff);

        // On récupère le contenu du token
        buff = viterbi.getBufferedReader(cheminCorpusToken);
        corpusTokenize = viterbi.readFile(buff);
        // mots du corpus
        viterbi.compterMots(corpusTokenize);
        
       
        // On récupère le contenu du token
        buff = viterbi.getBufferedReader(cheminBigrammes);
        bigrammes = viterbi.readFile(buff);

        // Insérérer dans notre hashmap chaque étape retourne le treillis
        treillis = viterbi.insertTreillis(strFile);
        //viterbi.displayMap(treillis);
        // on get avec la prob d émisison
        allMinEm = viterbi.getAllMinEMissionEtape(treillis);
        viterbi.displayTable(allMinEm);

    }
}

