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
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author scra
 */
public class GramVF {

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
    private List getModeles(String[] lignes, int N) {
        GramVF gram = new GramVF();
        boolean find = false;
        int tailleModeles = 0;
        List modeles = new LinkedList();

        // Pour chaque ligne
        for (int i = 0; i < lignes.length; i++) {
            String[] modele = gram.getModele(lignes[i], N);
            // pour chaque modele de chaque lignes
            for (int j = 0; j < modele.length; j++) {
                // on vérifie si le modele existe déja
                for (int k = 0; k < modeles.size(); k++) {
                    Pair occurence = (Pair) modeles.get(k);
                    if (occurence.getModele().equals(modele[j])) {
                        int n = occurence.getCompteur() + 1;
                        occurence.setCompteur(n);
                        find = true;
                        break;
                    }
                }
                if (find == false) {
                    // on doit vérifier que le modele soit de la taille de N
                    Pair occurence = new Pair(modele[j], 1);
                    modeles.add(occurence);

                } else {
                    find = false;
                }
            }
        }

        return modeles;
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

    private double[] maximumVraissemblanceLissageLaplace(String texte, String[] lignes, List pair, int N) {
        GramVF gram = new GramVF();
        final double alphaLaplace = 1;
        double nombreMotsCorpus;
        double a, b;
        double[] prob = null;
        boolean find = false;
        int indice = 0;
        // on compte le nombre de mot du corpus 
        nombreMotsCorpus = gram.countAll(lignes);
        // on compte le nombre de tokens
        String Ttexte[] = gram.getModele(texte, N);
        prob = new double[Ttexte.length];
        for (int i = 0; i < Ttexte.length; i++) {
            // on cherche un modele semblable
            for (int j = 0; j < pair.size(); j++) {

                Pair value = (Pair) pair.get(i);
                String mod = value.getModele();
                if (mod.equals(Ttexte[i])) {
                    if (mod.split(" ").length <= N - 1) {
                        b = gram.countAll(lignes);
                        a = value.getCompteur();
                    } else {
                        String wmoins1 = mod.split(" ")[0];
                        b = gram.count(lignes, wmoins1.trim());
                        a = value.getCompteur();
                    }
                    double P = (a + alphaLaplace) / (b + nombreMotsCorpus * alphaLaplace);
                    prob[indice] = P;
                    find = true;
                    break;
                }
            }
            if (find == false) {
                prob[indice] = (double)alphaLaplace / nombreMotsCorpus * alphaLaplace;
            }else{
                find = false;
            }
            indice++;
        }
        return prob;
    }

    public static void main(String[] args) throws IOException {

        BufferedReader buff = null;
        GramVF gram = new GramVF();
        final String input = "src/gramvf/tokens.txt";
        final String compteFile = "src/gramvf/compte.txt";
        final int N = 2; // pour 3 , 4 etc ca ne marche pas il faut termine d'implémenter certaines fonctionnalités
        String strFile = "";
        String[] modele = null;
        String texte = "1054 7815 4238 9297 6283";

        // On récupère le contenu du fichier
        buff = gram.getBufferedReader(input);
        strFile = gram.readFile(buff);

        // On génère les n-grams
        String[] lignes = gram.wrapLine(strFile);
        List pair = gram.getModeles(lignes, N);

        double[] prob = gram.maximumVraissemblanceLissageLaplace(texte, lignes, pair, N);

        gram.displayList(pair);
        for (int i = 0; i < prob.length; i++) {
            System.out.println(prob[i]);
        }

    }

}
