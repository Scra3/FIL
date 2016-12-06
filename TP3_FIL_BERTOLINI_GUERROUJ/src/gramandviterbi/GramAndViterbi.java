/*
 *BERTOLINI Alban ET GUEROUJ David
 */
package gramandviterbi;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Alban BERTOLINI et David Guerroudj
 */
public class GramAndViterbi {

    //variables globales 
    static int tailleCorpus; // 631991
    static HashMap<String, String> estimationModele;
    static Double[][] alpha;
    static Double[][] beta;
    static String corpusTokenize;
    static LinkedHashMap<String, ArrayList<String>> treillis;
    static String lexique;
    static String corpus;
    static int nb2Gram;

    //Afficher un tableau de string
    private void displayTable(String[] tableau) {
        for (String tableau1 : tableau) {
            System.out.println(tableau1);
        }
    }

    // Permet de lire un fichier de le sotcker dans un string
    static String readFileQuick(String path, Charset encoding)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    // prend en parametre une ligne et la dimension du gram
    //retourne le modele n-gram 
    private String[] getModele(String ligneTokens, int N) {
        String[] splited = null;

        splited = ligneTokens.split(" ");
        String modele = null;
        String[] gram = new String[splited.length];

        // Calculon la borne ou les modeles du gram seront entier
        int borne = N - 1;
        // création du modele
        if (N > 1) {
            for (int i = 0; i < splited.length; i++) {
                // si le modele dépasse du tableau ( pour les premier éléments du tableau)
                if (i - borne < 0) {
                    // on génère le modele de i à 0 ( pour ne pas dépasser le tableau)
                    for (int j = i; j > -1; j--) {
                        if (modele == null) {
                            modele = splited[j];
                        } else {
                            modele = splited[j] + " " + modele;
                        }
                    }
                } else {
                    modele = splited[i];
                    for (int j = 1; j <= borne; j++) {
                        modele = splited[i - j] + " " + modele;
                    }
                }
                // on enregistre le modele 
                gram[i] = modele;
                modele = null;
            }
        } else {
            gram = splited;
        }
        return gram;
    }

    // retourne chaque ligne dans un tableau
    private String[] wrapLine(String strFile) {
        return strFile.split("\n");
    }

    private String[] wrapLineForTreilli(String strFile) {
        return strFile.split("%col");
    }

    // Retourner l'ensemble des modele du corpus avec leurs nombres de fois qu'il apparait
    private HashMap<String, Integer> getModeles(String[] lignes, int N) {
        GramAndViterbi gram = new GramAndViterbi();
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        nb2Gram = 0;
        // Pour chaque ligne
        for (int i = 0; i < lignes.length; i++) {
            String[] modele = gram.getModele(lignes[i], N);
            // pour chaque modele de chaque lignes
            for (int j = 0; j < modele.length; j++) {
                // on vérifie si le modele existe déja
                if (map.containsKey(modele[j])) {
                    map.put(modele[j], map.get(modele[j]) + 1);
                } else {
                    map.put(modele[j], 1);
                }
                // on compte les n grams
                if (j != 0) {
                    nb2Gram++;
                }
            }
        }

        return map;
    }

    // compter avec les regex
    private int countWithRegex(String[] lignes, String path) {
        int occur = 0;
        for (int i = 0; i < lignes.length; i++) {
            Matcher matcher = Pattern.compile(path).matcher(lignes[i]);
            while (matcher.find()) {
                occur++;
            }
        }
        return occur;
    }

    private void countAll() {
        String[] lignes = corpusTokenize.split("\n");
        int count = 0;
        for (int i = 0; i < lignes.length; i++) {
            String[] t = lignes[i].split(" ");
            count = count + t.length;
        }
        tailleCorpus = count;
    }

    private int countMotsPhrase(String phrase) {
        String[] lignes = phrase.split(" ");
        int count = 0;
        for (int i = 0; i < lignes.length; i++) {
            String[] t = lignes[i].split(" ");
            count = count + t.length;
        }
        return count;
    }

    // retourner le nombre d'occurence d'un char
    public int compteurChar(String str, char ch) {
        int compteur = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == ch) {
                compteur++;
            }
        }
        return compteur;
    }

    // permet d'appliquer la out.write(latency[i] + "\n");formule de maximum de vraissemblance avec le lissage de laplace
    private double maximumVraissemblanceLissageLaplace(String texte, String[] lignes, HashMap<String, Integer> pair, int N) {
        int nombreMotsCorpus = tailleCorpus;
        GramAndViterbi gram = new GramAndViterbi();
        final double alphaLaplace = 1;
        double a, b,x;
        double prob = 0.0;
        double P;
        // on compte le nombre de mot du corpus 
        // on met au format n-gram 
        String Ttexte[] = gram.getModele(texte, N);
        // texte modele par modele
        for (int i = 0; i < Ttexte.length; i++) {
            // on cherche un modele semblable

            if (pair.containsKey(Ttexte[i])) {

                // si la taille n'est pas de N-gram
                if (Ttexte[i].split(" ").length <= N - 1) {
                    a = (double) gram.countWithRegex(lignes, Pattern.quote(Ttexte[i]));
                    //b = (double) nombreMotsCorpus;
                    b = (double) nombreMotsCorpus - nb2Gram;
                    x = nombreMotsCorpus - nb2Gram;;
                    // si la taille es de N-gram
                } else {
                    String wmoins1 = Ttexte[i].split(" ")[0];

                    b = (double) gram.countWithRegex(lignes, Pattern.quote(wmoins1));
                    a = (double) pair.get(Ttexte[i]);
                    x = nb2Gram;
                }

                // si la séquence existe dans le corpus
                P = -Math.log((a + alphaLaplace) / (b + (x * alphaLaplace)));

            } else {
                // si la séquence n'existe pas dans le corpus
                P = -Math.log(alphaLaplace / (nombreMotsCorpus * alphaLaplace));
            }
            prob = prob + P;
        }
        return prob;
    }

    // calcul la perplexite
    private double perplexite(double prob, String texte) {
        String[] texteTable = new String[1];
        texteTable[0] = texte;
        double cal = (1 / (double) tailleCorpus) * prob;
        double plogEvaluation = Math.pow(2, cal);
        return plogEvaluation;

    }

    // permet de générer toutes les séquences de tokens possible
    public HashMap<String, Double> anagramma(String T[], int first, HashMap<String, Double> save) {
        if ((T.length - first) <= 1) {
            String saveStr = T[0];
            for (int i = 1; i < T.length; i++) {
                saveStr = saveStr + " " + T[i];
            }
            save.put(saveStr, 0.0);
        } else {
            for (int i = 0; i < T.length - first; i++) {
                round(T, first);
                save = anagramma(T, first + 1, save);
            }
        }
        return save;
    }

    private void round(String T[], int i) {
        String temp = T[i];
        for (int j = i; j < T.length - 1; j++) {
            T[j] = T[j + 1];
        }
        T[T.length - 1] = temp;
    }

    // lexique et phrase tokenise et retourne la phrase 
    private String translateTokens(String strFile, String texteEnOrdre) {
        String phraseTraduite = null;
        String[] tokens = texteEnOrdre.split(" ");
        String[] lines = strFile.split("\n");
        for (int a = 0; a < tokens.length; a++) {

            for (int i = 0; i < lines.length; i++) {
                String line = lines[i];
                String[] lexique = line.split(" ");
                if (lexique[1].equals(tokens[a])) {
                    if (phraseTraduite == null) {
                        phraseTraduite = lexique[0];
                    } else {
                        phraseTraduite = phraseTraduite + " " + lexique[0];
                    }
                }
            }
        }
        return phraseTraduite;
    }

    public String translatePhrase(String lexique, String phrase) {
        String phraseTokenise = null;
        String[] Tphrase = phrase.split(" ");
        String[] Tlexique = lexique.split("\n");
        for (int i = 0; i < Tphrase.length; i++) {
            for (int j = 0; j < Tlexique.length; j++) {
                String compare = Tlexique[j].split(" ")[0];
                String token = Tlexique[j].split(" ")[1];
                if (Tphrase[i].equals(compare)) {
                    if (phraseTokenise == null) {
                        phraseTokenise = token;
                    } else {
                        phraseTokenise = phraseTokenise + " " + token;
                    }
                }
            }
        }
        return phraseTokenise;
    }

    public void getLissageAndGram(String chemin, String texte, String[] lignes, HashMap<String, Integer> allGrams, int N) throws IOException {

        Iterator i = allGrams.keySet().iterator();
        String save = "";
        BufferedWriter out = new BufferedWriter(new FileWriter(chemin));

        while (i.hasNext()) {
            String clef = (String) i.next(); // 

            double prob = maximumVraissemblanceLissageLaplace(clef, lignes, allGrams, N);

            if (save == "") {
                save = clef + " " + prob + "\n";

            } else {
                save = save + clef + " " + prob + "\n";
            }
            out.write(save);
            save = "";
        }
        out.close();
    }

    public void writeFile(String texte, String chemin) {
        try {
            File ff = new File(chemin); // définir l'arborescence
            ff.createNewFile();
            FileWriter ffw = new FileWriter(ff);
            ffw.write(texte);  // écrire une ligne dans le fichier resultat.txt
            ffw.close(); // fermer le fichier à la fin des traitements
        } catch (Exception e) {
        }
    }

    // Afficher hashmap
    protected void displayMap(LinkedHashMap<String, ArrayList<String>> treillis) {
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

    //creation d'un nouveau treillis
    public void buildTreilli(String ratp_en_fr, String chemin) throws IOException {
        String treillis = "";
        int currentCol = -1;
        String[] lignes = ratp_en_fr.split("\n");
        BufferedWriter out = new BufferedWriter(new FileWriter(chemin));
        for (int i = 0; i < lignes.length; i++) {
            String[] mots = lignes[i].split(" ");
            Integer i1 = Integer.parseInt(mots[0]);
            String i2 = mots[1];
            String i3 = mots[2];

            if (i1 != currentCol) {
                currentCol = i1;
                treillis = treillis + "%col " + currentCol + "\n";
            }

            treillis = treillis + i2 + " " + i3 + "\n";
            out.write(treillis);
            treillis = "";
        }
        out.close();
    }

    protected void insertTreillis(String strTreillis) {
        String[] tabsTreillis;
        treillis = new LinkedHashMap<String, ArrayList<String>>();
        //Lire le treillis et le wrap par étapes
        tabsTreillis = wrapLineForTreilli(strTreillis);
        // on créer notre liste
        for (int i = 1; i < tabsTreillis.length; i++) {
            ArrayList<String> tokenLogProb = new ArrayList<String>(); // représente un string contenant un couple (token , prob)
            String[] couples = tabsTreillis[i].split("\n");
            for (int j = 1; j < couples.length; j++) {
                tokenLogProb.add(couples[j]);
            }
            treillis.put(couples[0].trim(), tokenLogProb);
        }
    }

    // RETURN le mininum emission d'une étape prenant en param les étape et l'indice de l'étape
    protected String minEmission(LinkedHashMap<String, ArrayList<String>> treillis, String etape) {
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

    // return tous les min emissions des étapes : ATTENTION FONCTIONNE SEULEMENT POUR LE TP VITERBI  => 2
    protected String[] getAllMinEMissionEtape() {
        int tailleTreillis = treillis.size();
        String[] mins = new String[tailleTreillis];
        for (int i = 0; i < tailleTreillis; i++) {
            mins[i] = minEmission(treillis, Integer.toString(i));
        }
        return mins;
    }

    //La fonction trouve_min_alpha(i) permet de trouver l’indice m de la plus petite valeur de alpha à la position i
    protected int trouveMinAlpha(int indice) {
        double i = alpha[indice][1];
        int save = 0;
        // trouver la plus petite valeur 
        for (int j = 0; j < treillis.get(Integer.toString(indice)).size(); j++) {
            //System.out.println(alpha[indice][j]);

            if (alpha[indice][j] < i) {
                i = alpha[indice][j];
                save = j;
            }
        }
        //System.out.println("ezfiefezfzeifezifezfihizef " + save);
        return save;
    }

    //calcule la proba initiale du mot
    protected double probabiliteInitiale(String motW) {
        double probabilite = 0;
        double a = Double.parseDouble(estimationModele.get(motW));
        probabilite = a / ((double) tailleCorpus);
        return probabilite;
    }

    // on va chercher la probabilité d'émission
    protected String getMot(String motWandProb) {
        String[] cas = motWandProb.split(" ");
        String newStr = cas[0];
        for (int i = 1; i < cas.length - 2; i++) {
            newStr = newStr + " " + cas[i];
        }

        return newStr;
    }

    protected Double getPE(String motWandProb) {
        String[] ca = motWandProb.split(" ");
        return Double.parseDouble(ca[ca.length - 1]);
    }

    protected void compterMots() {
        tailleCorpus = corpusTokenize.split("\\\\s").length;
    }

    protected int compterMot(String mot) {
        String[] t = corpusTokenize.split("\\\\s");
        tailleCorpus = t.length;
        int compteur = 0;
        for (int i = 0; i < tailleCorpus; i++) {
            if (t[i].equals(mot)) {
                compteur++;
            }
        }
        return compteur;
    }

    //Charger tous les bigramme dans une hashmap
    protected void initHashMap(String bigrammes) {
        String[] lignes = bigrammes.split("\n");
        String a, b; // tokens
        String c; // nb occurences
        HashMap<String, String> estima = new HashMap<>();

        for (int i = 0; i < lignes.length; i++) {
            String[] sequence = lignes[i].split(" ");
            if (sequence.length == 3) {
                a = sequence[0];
                b = sequence[1];
                c = sequence[2];
                estima.put(a + " " + b, c);
            } else {
                a = sequence[0];
                b = sequence[1];
                estima.put(a, b);
            }
        }
        estimationModele = estima;
    }

    // initialise alpha et beta avec l'étape 1 du treillis
    protected void initViterbi() {
        beta = null;
        alpha = null;
        int n = treillis.get("0").size();
        alpha = new Double[treillis.size()][1000];
        beta = new Double[treillis.size() + 1][1000]; // size+1 Pour trouver le derniere élément du treillis

        for (int i = 0; i < treillis.get("0").size(); i++) {

            if (estimationModele.containsKey(getMot(treillis.get("0").get(i)))) {
                alpha[0][i] = probabiliteInitiale(getMot(treillis.get("0").get(i))) + getPE(treillis.get("0").get(i));

            } else {
                double prob = -Math.log(1 / ((double) tailleCorpus));
                // on le met dans la map
                alpha[0][i] = prob + getPE(treillis.get("0").get(i));
                estimationModele.put(getMot(treillis.get("0").get(i)), String.valueOf(prob));
            }

            beta[0][i] = 0.0;
        }

        viterbi();
    }

    //Suite de viterbi
    protected void viterbi() {
        int indiceMin = 0;
        int i;
        // On ne fait pas l'étape une car on vient de la faire préalablement avec initVIterbi
        for (i = 1; i < treillis.size(); i++) {
            indiceMin = trouveMinAlpha(i - 1);

            for (int j = 0; j < treillis.get(Integer.toString(i)).size(); j++) {
                //ON vérifie que le mot existe , si il existe pas on applique le lissage de laplace
                // on fabrique
                String mot = getMot(treillis.get(Integer.toString(i - 1)).get(indiceMin)) + " " + getMot(treillis.get(Integer.toString(i)).get(j));

                if (estimationModele.containsKey(getMot(treillis.get(Integer.toString(i)).get(j))) == false) {

                    estimationModele.put(getMot(treillis.get(Integer.toString(i)).get(j)), Double.toString(-Math.log(1 / ((double) tailleCorpus))));
                }

                if (estimationModele.containsKey(mot) == false) {

                    estimationModele.put(mot, Double.toString(-Math.log(1 / ((double) tailleCorpus))));
                }

                double P = Double.parseDouble(estimationModele.get(mot));
                alpha[i][j] = alpha[i - 1][indiceMin] + getPE(treillis.get(Integer.toString(i)).get(j)) + P;

                beta[i][j] = (double) indiceMin;
            }
        }
        endViterbi(alpha[i - 1]);
    }

    public void endViterbi(Double[] alpha) {
        // on récupère le plus petit
        double min = 1000.0;
        double indice = 0;
        min = alpha[0];
        for (int i = 1; i < alpha.length; i++) {
            if (alpha[i] != null) {
                if (min > alpha[i]) {
                    min = alpha[i];
                    indice = i;
                }
            } else {
                break;
            }
        }
        int tailleTreillis = treillis.size();
        beta[tailleTreillis][0] = indice;
    }

    public String getAllMinEmmissionTP4(String phraseTokens) {
        String[] tokens = phraseTokens.split(" ");
        String bestTokens = null;
        double bestPT = 10000;
        int saveIndice = 0;
        for (int i = 0; i < tokens.length; i++) {
            // on doit chercher le col correspondant au token puis on recupere l'ensemble des tokens et on get le grand pLog
            if (treillis.get(tokens[i]) != null) {
                                  //  System.out.println("TOKENS "+ tokens[i]);

                // on récupere le meilleur PE dans l'array
                for (int j = 0; j < treillis.get(tokens[i]).size(); j++) {
                    //System.out.println( treillis.get(tokens[i]));
                    double verif = getPE(treillis.get(tokens[i]).get(j));
                    if (bestPT > verif) {
                        saveIndice = j;
                        bestPT = verif;
                    }
                }
                if (bestTokens == null) {
                    bestTokens = getMot(treillis.get(tokens[i]).get(saveIndice));
                } else {
                    bestTokens = bestTokens + " " + getMot(treillis.get(tokens[i]).get(saveIndice));

                }
                bestPT = 1000;
                saveIndice = 0;
            }
        }
        return bestTokens;
    }

    private void traducteurViterbi(String phraseToken, String tableTraduction, String cheminNewTreillis, String lexiqueFrancais) throws IOException {
        System.out.println(phraseToken);
        // on va reproduire un treilli uniquement pour viterbi 
        //IL FAUT CONSTRUIRE UN SRING SOUS FORME DE TREILLIS
        //%col 0
        //18956 1.25
        //65466 5.6 etc
        String[] TphraseToken = phraseToken.split(" ");
        String factoryTreillis = "";
        String save = "";
        String[] TtableTraduction = tableTraduction.split("\n");
        boolean find = false;
        for (int i = 0; i < TphraseToken.length; i++) {
            for (int j = 0; j < TtableTraduction.length; j++) {
                // on récupere le premiere token
                String[] elements = TtableTraduction[j].split(" ");
                if (TphraseToken[i].equals(elements[0])) {
                    factoryTreillis = factoryTreillis + "\n" + elements[1] + " " + elements[2];
                    find = true;
                }

                // pour ne pas parcourir pour rien car on deja trouve nos elements
                if (find == true && !TphraseToken[i].equals(elements[0])) {
                    find = false;
                    break;
                }
            }
            if (save.equals("")) {
                save = "%col " + i + factoryTreillis;

            } else {
                save = save + "\n%col " + i + factoryTreillis;
            }
            factoryTreillis = "";
        }

        //writeFile(save,cheminNewTreillis);
        insertTreillis(save);
        writeFile(save, cheminNewTreillis);
        initViterbi();

        //Affichage du texte trouvé avec viterbi  
        int a = (int) ((double) beta[1][0]);
        String tokens = getMot(treillis.get("0").get(a));
        for (int i = 2; i < beta.length; i++) {
            a = (int) ((double) beta[i][0]);
            tokens = tokens + " " + getMot(treillis.get(Integer.toString(i - 1)).get(a));
        }
        System.out.println("Tokens traduction  " + tokens);

        System.out.println(translateTokens(lexiqueFrancais, tokens));
    }

    private String traducteurNaif(String tableTraduction, String phraseToken, String cheminNewTreillis, String lexiqueFrancais) throws IOException {
        int le = phraseToken.length(); // taille de la phrasetoken
        String phraseTraduite = null;
        String phraseTraduiteEnFrancais = null;
        //ON GENERE LE TREILLIS
        // on fait le nouveau treillis A INSERER UNE SEUL FOIS  
        buildTreilli(tableTraduction, cheminNewTreillis);
        // on le récupere dans le fichier
        String newTreillis = readFileQuick(cheminNewTreillis, StandardCharsets.UTF_8);
        // on insert le nouveau treillis
        // ATENTION LE TREILLIS COMMENCE à 1
        insertTreillis(newTreillis);

        //RECUPERER LA MEILLEUR PROBABILITE DE TRANSITION POUR CHAQUE TREILLIS : les tokens sont en francais
        phraseTraduite = getAllMinEmmissionTP4(phraseToken);
        //ON TRADUIT LA PRASE QU ON RECUPERE
        System.out.println("Tokens traduction " + phraseTraduite);

        phraseTraduiteEnFrancais = translateTokens(lexiqueFrancais, phraseTraduite);
        return phraseTraduiteEnFrancais;
    }

    private String concatStringTable(String[] tableString) {
        String string = null;
        string = tableString[0];
        for (int i = 1; i < tableString.length; i++) {
            string = string + " " + tableString[i];
        }
        return string;
    }

    public static void main(String[] args) throws IOException {
        // On récupère le contenu du fichier

        GramAndViterbi gram = new GramAndViterbi();
        //Chemins des fichiers
        final String cheminTreillis = "src/gramandviterbi/treillis.txt"; // chemin du treillis
        final String cheminCorpusTokenize = "src/gramandviterbi/corpusTokens.txt";
        final String cheminBigrammes = "src/gramandviterbi/bigramme.txt";
        final String cheminLexique = "src/gramandviterbi/lexique_np.fr.code.txt";
        final String cheminCorpus_ratp_bilangEn = "src/gramandviterbi/corpus_ratp_bilang.en";
        final String cheminCorpus_ratp_bilangFr = "src/gramandviterbi/corpus_ratp_bilang.fr";
        final String cheminLexique_ratp_en = "src/gramandviterbi/lexique_ratp_en.txt";
        final String cheminLexique_ratp_fr = "src/gramandviterbi/lexique_ratp_fr.txt";
        final String cheminTable_ratp_en_fr = "src/gramandviterbi/table_ratp_en_fr_20iter.code";
        final String cheminNewTreillis = "src/gramandviterbi/newTreillis.txt";

        //Read file
        // On récupère le contenu du fichier
        corpusTokenize = gram.readFileQuick(cheminCorpusTokenize, StandardCharsets.UTF_8);
        lexique = gram.readFileQuick(cheminLexique, StandardCharsets.UTF_8);
        String strTreillis = gram.readFileQuick(cheminTreillis, StandardCharsets.UTF_8);
        String bigrammes = gram.readFileQuick(cheminBigrammes, StandardCharsets.UTF_8);
        String corpusAnglais = gram.readFileQuick(cheminCorpus_ratp_bilangEn, StandardCharsets.UTF_8);
        String corpusFrancais = gram.readFileQuick(cheminCorpus_ratp_bilangFr, StandardCharsets.UTF_8);
        String lexiqueAnglais = gram.readFileQuick(cheminLexique_ratp_en, StandardCharsets.UTF_8);
        String lexiqueFrancais = gram.readFileQuick(cheminLexique_ratp_fr, StandardCharsets.UTF_8);
        String tableTraduction = gram.readFileQuick(cheminTable_ratp_en_fr, StandardCharsets.UTF_8);

        // on compte le nombre de tokens dans le corpus
        gram.countAll();
        // Initialiser HashMap avec tous les tokens
        gram.initHashMap(bigrammes);
        gram.insertTreillis(strTreillis);

        /**
         * ************************************************
         */
        final int N = 2; // correspond au model N-gram 
        String texte = "29839 85115 29 59443";// TEXTE À METTRE DANS L'ORDRE , attention aux espaces
        // elle va à paris
        //29839 85115 29 59443
        /**
         * ************************************************
         */
        /**
         * *********LE RÉSULTAT DES LISSAGES DE LAPLACE des 2 NGRAMMES SONT
         * DANS LE FICHIER BIGRAMME******
         */
        //On génère les n-grams
        String[] lignes = gram.wrapLine(corpusTokenize);
        HashMap<String, Integer> allGrams = gram.getModeles(lignes, N);
        //texte tonkenize + le corpus sous formesDeLignes tous les grams du texte et l'indice de mon bigramme
        //gram.getLissageAndGram(cheminBigrammes, texte, lignes, allGrams, N);
        /**
         * ******************************Génération avec
         * perplexité*********************************************
         */
        //gram.displayTable(allGrams);
        // Permutations de la phrases 
        // calcul de la perplexité
        String[] Ttexte = texte.split(" ");
        HashMap<String, Double> textePermute = new HashMap<String, Double>();

        // on cherche toute les permutations possible
        textePermute = gram.anagramma(Ttexte, 0, textePermute);

        //On calcul la perplexité pour chaque permutation
        Set cles = textePermute.keySet();
        Iterator it = cles.iterator();

        while (it.hasNext()) {
            texte = (String) it.next();
            double prob = gram.maximumVraissemblanceLissageLaplace(texte, lignes, allGrams, N);
            double plogEvaluation = gram.perplexite(prob, texte);
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

        //********************************affichage**********************************
        System.out.println("Voici les tokens dans le bon ordre : ");
        System.out.println(texteEnOrdre);

        //On recherche dans le lexique leurs correspondance
        //On récupère le contenu du fichier
        //On traduit la phrase
        String phrase = gram.translateTokens(lexique, texteEnOrdre);
        System.out.println("Phrase traduite : ");
        System.out.println(phrase);

        //QUESTION P3 1 
        System.out.println("AVEC EMISSION Q1");

        String[] allMinEm = gram.getAllMinEMissionEtape();
        gram.displayTable(allMinEm);

        System.out.println(" \nAVEC EMISSION ET TRANSITION Q2");
        gram.initViterbi();

        // ON chercher les indices  
        String tokens = null;

        //Affichage du texte trouvé avec viterbi  
        int a = (int) ((double) beta[1][0]);
        tokens = gram.getMot(treillis.get("0").get(a));

        for (int i = 2; i < beta.length; i++) {
            a = (int) ((double) beta[i][0]);
            tokens = tokens + " " + gram.getMot(treillis.get(Integer.toString(i - 1)).get(a));
        }

        System.out.println(gram.translateTokens(lexique, tokens));

        /**
         * *******************************************************************
         */
        /* TP4                                                                */
        /**
         * *******************************************************************
         */
        /* TP4 */
        /* TP4 */
        //phrase 
        System.out.println("\nTP4");
        String phraseToken = "I want to go to Paris by bus or underground";

        System.out.println("La phrase : " + phraseToken);
        //nombre de mots dans la phrase
        int nombresMots = gram.countMotsPhrase(phraseToken);
        System.out.println("Il y a " + nombresMots + " mots");

        //TRADUCTEUR AUTOMATIQUE NAIF Q1 TP4
        //Table de traduction + phrase (tokeneise)
        // on traduit la phrase en token
        phraseToken = gram.translatePhrase(lexiqueAnglais, phraseToken);
        System.out.println(phraseToken);
        //ON AFFICHE LA PHRASE TRADUITE UNIQUEMENT AVEC LES PROBS DE TRADUCTION
        System.out.println(gram.traducteurNaif(tableTraduction, phraseToken, cheminNewTreillis, lexiqueFrancais));

        gram.traducteurViterbi(phraseToken, tableTraduction, cheminNewTreillis, lexiqueFrancais);
    }
}
