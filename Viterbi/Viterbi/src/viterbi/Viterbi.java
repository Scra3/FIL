package viterbi;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Viterbi {

    //variables globales 
    static int tailleCorpus; // 631991
    static HashMap<String, String> estimationModele;
    static Double[][] alpha;
    static Double[][] beta;
    static String corpusTokenize;
    
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

    // Permet de lire un fichier de le sotcker dans un string
    static String readFileQuick(String path, Charset encoding)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
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
    private void initViterbi(int n, LinkedHashMap<String, ArrayList<String>> treillis) {
        alpha = new Double[1][n];
        beta = new Double[1][n];

        for (int i = 0; i < treillis.get("0").size(); i++) {
            alpha[1][i] = probabiliteInitiale(getMot(treillis.get("0").get(i))) * getPE(treillis.get("0").get(i));
            beta[1][i] = 0.0;
        }
    }
    
    //Suite de viterbi
    private void viterbi(LinkedHashMap<String, ArrayList<String>> treillis,Double [][] matrice){
        // On ne fait pas l'étape une car on vient de la faire préalablement avec initVIterbi
        for (int i = 1; i < treillis.size(); i++) {
            int indiceMin = trouveMinAlpha(i-1);
            for (int j = 0; j < alpha.length; j++) {
                int a = compterMots(treillis.get(Integer.toString(i)).get(j));
                int b = compterMots(treillis.get(Integer.toString(i-1)).get(indiceMin));
                double P = (double)a/(double)b;
                alpha[i][j]= alpha[i-1][indiceMin]*getPE(treillis.get(Integer.toString(i)).get(j)) * P;
                
                beta[i][j]= (double)indiceMin;
            }
        }
        
    }
    
    //La fonction trouve_min_alpha(i) permet de trouver l’indice m de la plus petite valeur de alpha à la position i
    private int trouveMinAlpha(int indice){
        int i = 0;
        
        return i;
    }

    //calcule la proba initiale du mot
    private double probabiliteInitiale(String motW) {
        double probabilite = 0;
        double a = Double.parseDouble(estimationModele.get(motW));
        probabilite = a / tailleCorpus;
        return probabilite;
    }

    // on va chercher la probabilité d'émission
    private String getMot(String motWandProb) {
       return  motWandProb.split(" ")[0];
    }
      private Double getPE(String motWandProb) {
        return Double.parseDouble(motWandProb.split(" ")[1]);
    }

    private void compterMots() {
        tailleCorpus = corpusTokenize.split("\\\\s").length;
    }
    
      private int compterMots(String mot) {
         String[] t = corpusTokenize.split("\\\\s");
        tailleCorpus = t.length;
        int compteur = 0;
          for (int i = 0; i < tailleCorpus; i++) {
              if (t[i].equals(mot)) {
                  compteur ++;
              }
          }
          return compteur;
    }
    //Charger tous les bigramme dans une hashmap
    private void initHashMap(String bigrammes) {
        String[] lignes = bigrammes.split("\n");
        String a, b; // tokens
        String c; // nb occurences
        HashMap<String, String> estima = new HashMap<>();

        for (int i = 0; i < lignes.length; i++) {
            String[] sequence = lignes[i].split(" ");
            a = sequence[0];
            b = sequence[1];
            c = sequence[2];
            estima.put(a + " " + b, c);
        }
        estimationModele = estima;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {

        //DÉCLARATIONS
        final String cheminTreillis = "src/viterbi/treillis.txt"; // chemin du treillis
        final String cheminCorpusToken = "src/viterbi/corpusTokens.txt";
        final String cheminBigrammes = "src/viterbi/bigramme.txt";

        Viterbi viterbi = new Viterbi();
        BufferedReader buff = null; // buffer pour lire le fichier treillis
        String strFile = ""; // fichier treillis
        String bigrammes = ""; // fichier corpusTokenize
        LinkedHashMap<String, ArrayList<String>> treillis;
        String[] allMinEm;
        int T = 4; //longueur du treillis
        int N = 4; // mots possibles à chaque étape du treillis
        String alpha[][] = new String[T][];
        String beta[][] = new String[T][];

        // On récupère le contenu du fichier
        strFile = viterbi.readFileQuick(cheminTreillis, StandardCharsets.UTF_8);
        // On récupère le contenu du token

        corpusTokenize = viterbi.readFileQuick(cheminCorpusToken, StandardCharsets.UTF_8);

        // mots du corpus
        viterbi.compterMots(corpusTokenize);

        // On récupère le contenu du token
        bigrammes = viterbi.readFileQuick(cheminBigrammes, StandardCharsets.UTF_8);

        // Initialiser HashMap avec tous les tokens
        viterbi.initHashMap(bigrammes);
        // Insérérer dans notre hashmap chaque étape  : retourne le treillis
        treillis = viterbi.insertTreillis(strFile);
        //viterbi.displayMap(treillis);
        // on get avec la prob d émisison
        allMinEm = viterbi.getAllMinEMissionEtape(treillis);
        viterbi.displayTable(allMinEm);
    }
}
