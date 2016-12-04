package viterbi;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class Viterbi extends GramVF{

    //variables globales 
    static int tailleCorpus; // 631991
    static HashMap<String, String> estimationModele;
    static Double[][] alpha;
    static Double[][] beta;
    static String corpusTokenize;
    static LinkedHashMap<String, ArrayList<String>> treillis;
    static String lexique;

    protected static void callGram() {
        Viterbi viterbi = new Viterbi();
        /**
         * ************************************************
        */
        final int N = 3; // correspond au model N-gram
        String texte = "59771 55931 66831";// TEXTE À METTRE DANS L'ORDRE , attention aux espaces

        /**
         * ************************************************
        */

        // On génère les n-grams
        HashMap<String, Integer> pair = viterbi.getModeles(N);

        // Permutations de la phrases 
        // calcul de la perplexité
        String[] Ttexte = texte.split(" ");
        HashMap<String, Double> textePermute = new HashMap<String, Double>();

        // on cherche toute les permutations possible
        textePermute = viterbi.anagramma(Ttexte,0, textePermute);

        /*On calcul la perplexité pour chaque permutation*/
        Set cles = textePermute.keySet();
        Iterator it = cles.iterator();


        while (it.hasNext()) {
            texte = (String) it.next();
            double prob = viterbi.maximumVraissemblanceLissageLaplace(tailleCorpus, texte,pair, N);
            double plogEvaluation = viterbi.perplexite(tailleCorpus, prob, texte);
            textePermute.put(texte, plogEvaluation);
        }

        //on chercher la perplexité la plus faible
        double valueSave = 1000000000; // permet d'initialiser la premiere valeur
        String texteEnOrdre = "";
        for (String mapKey : textePermute.keySet()) {
            double value = textePermute.get(mapKey);
            if (value <= valueSave) {
                valueSave = value;
                texteEnOrdre = mapKey;
            }
        }
        System.out.println("Voici les tokens dans le bon ordre : ");
        System.out.println(texteEnOrdre);

        //On recherche dans le lexique leurs correspondance

        //On traduit la phrase
        String phrase = viterbi.translateTokens(lexique, texteEnOrdre);
        System.out.println("Phrase traduite : ");
        System.out.println(phrase);
    }

    private static void initFiles() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
   

   
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
       //DÉCLARATIONS
        Viterbi viterbi = new Viterbi();

        //Chemins des fichiers
        final String cheminTreillis = "src/viterbi/treillis.txt"; // chemin du treillis
        final String cheminCorpusToken = "src/viterbi/corpusTokens.txt";
        final String cheminBigrammes = "src/viterbi/bigramme.txt";    
        final String cheminLexique = "src/viterbi/lexique_np.fr.code";
        
        //String des fichiers
         // On récupère le contenu du fichier
        String strTreillis = viterbi.readFileQuick(cheminTreillis, StandardCharsets.UTF_8);
        corpusTokenize = viterbi.readFileQuick(cheminCorpusToken, StandardCharsets.UTF_8);
        String bigrammes = viterbi.readFileQuick(cheminBigrammes, StandardCharsets.UTF_8);
        lexique = viterbi.readFileQuick(cheminLexique, StandardCharsets.UTF_8);
        
        // INIT des variables statics 
        // mots du corpus
        viterbi.compterMots();
        // Initialiser HashMap avec tous les tokens
        viterbi.initHashMap(bigrammes);
        viterbi.insertTreillis(strTreillis);
        
        /////////////////////////////////////////////////////////////////////
        
        
        callGram();
        ////////////////////////////////////////////////////////////////////////
        String[] allMinEm;
        int T = 4; //longueur du treillis
        int N = 4; // mots possibles à chaque étape du treillis
        String alpha[][] = new String[T][];
        String beta[][] = new String[T][];

       
        // On récupère le contenu du token


      
  

  
        // Insérérer dans notre hashmap chaque étape  : retourne le treillis
        //viterbi.displayMap(treillis);
        // on get avec la prob d émisison
        allMinEm = viterbi.getAllMinEMissionEtape(treillis);
        viterbi.displayTable(allMinEm);
    }
}