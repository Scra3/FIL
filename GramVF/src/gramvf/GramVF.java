/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gramvf;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author scra
 */
public class GramVF {

    private static Object config;

    // Afficher une list de Pair
    private void displayList(List l) {
        for (int i = 0; i < l.size(); i++) {
            Pair pair = (Pair) l.get(i);

            System.out.println(i + " :  " + pair.getModele() + " => " + pair.getCompteur());
        }
    }

    //Afficher un tableau de string
    private void displayTable(String[] tableau) {
        for (String tableau1 : tableau) {
            System.out.println(tableau1);
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

    // Retourner l'ensemble des modele du corpus avec leurs nombres de fois qu'il apparait
    private HashMap<String, Integer> getModeles(String[] lignes, int N) {
        GramVF gram = new GramVF();
        boolean find = false;
        int tailleModeles = 0;
        HashMap<String, Integer> map = new HashMap<String, Integer>();

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

            }
        }

        return map;
    }

    private int count(String[] lignes, String modele) {
        int count = 0;
        for (int k = 0; k < lignes.length; k++) {
            String[] tokens = lignes[k].split(" ");
            for (int l = 0; l < tokens.length; l++) {
                if (modele.equals(tokens[l])) {
                    count++;
                }
            }
        }
        return count;
    }

    private int countAll(String[] lignes) {
        int count = 0;
        for (int i = 0; i < lignes.length; i++) {
            String[] t = lignes[i].split(" ");
            count = count + t.length;
        }
        return count;
    }

    private double maximumVraissemblanceLissageLaplace(String texte, String[] lignes, HashMap<String, Integer> pair, int N) {
        GramVF gram = new GramVF();
        final double alphaLaplace = 1;
        double nombreMotsCorpus;
        double a, b;
        double prob = 0.0;
        double P;
        // on compte le nombre de mot du corpus 
        nombreMotsCorpus = gram.countAll(lignes);
        // on met au format n-gram 
        String Ttexte[] = gram.getModele(texte, N);

        // texte modele par modele
        for (int i = 0; i < Ttexte.length; i++) {
            // on cherche un modele semblable

            if (pair.containsKey(Ttexte[i])) {

                if (Ttexte[i].split(" ").length <= N - 1) {
                    b = (double) gram.countAll(lignes);
                    a = (double) gram.count(lignes, Ttexte[i]);

                    //System.out.println("premier " + mod);
                } else {
                    String wmoins1 = Ttexte[i].split(" ")[0];
                    //System.out.println("second " + wmoins1);

                    b = (double) gram.count(lignes, wmoins1);
                    a = (double) pair.get(Ttexte[i]);
                }

                // si la séquence existe dans le corpus
                P = -Math.log((a + alphaLaplace) / (b + nombreMotsCorpus * alphaLaplace));

            } else {
                // si la séquence n'existe pas dans le corpus
                P = -Math.log(alphaLaplace / (nombreMotsCorpus * alphaLaplace));
            }
            prob = prob + P;
        }
        return prob;
    }

    private double logProb(double[] prob) {
        double plog = 0.0;
        for (int i = 0; i < prob.length; i++) {
            plog = plog - Math.log(prob[i]);
        }
        return plog;
    }

    private double perplexite(double prob, String texte, String[] corpus) {
        GramVF gram = new GramVF();
        String[] texteTable = new String[1];
        texteTable[0] = texte;
        double nombresMots = gram.countAll(corpus);
        double cal = (1 / (double) nombresMots) * prob;
        double plogEvaluation = Math.pow(2, cal);
        return plogEvaluation;

    }

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

    public static void main(String[] args) throws IOException {

        BufferedReader buff = null;
        GramVF gram = new GramVF();
        final String input = "src/gramvf/tokens.txt";
        final String compteFile = "src/gramvf/compte.txt";
        final int N = 2; // correspond au model N-gram
        String strFile = "";
        String[] modele = null;
        String texte = "56384 10276 28930 87086 47758"; // TEXTE À METTRE DANS L'ORDRE

        // On récupère le contenu du fichier
        buff = gram.getBufferedReader(input);
        strFile = gram.readFile(buff);

        // On génère les n-grams
        String[] lignes = gram.wrapLine(strFile);
        HashMap<String, Integer> pair = gram.getModeles(lignes, N);

        // Permutations de la phrases 
        // calcul de la perplexité
        String[] Ttexte = texte.split(" ");
        HashMap<String, Double> textePermute = new HashMap<String, Double>();

        // on cherche toute les permutations possible
        textePermute = gram.anagramma(Ttexte, 0, textePermute);

        /*On calcul la perplexité pour chaque permutation*/
        Set cles = textePermute.keySet();
        Iterator it = cles.iterator();
        while (it.hasNext()) {
            texte = (String) it.next();
            double prob = gram.maximumVraissemblanceLissageLaplace(texte, lignes, pair, N);
            double plogEvaluation = gram.perplexite(prob, texte, lignes);
            textePermute.put(texte, plogEvaluation);
        }

        //on chercher la perplexité la plus faible
        double valueSave = 1000000000; // permet d'initialise la premiere valeur
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
    }
}
