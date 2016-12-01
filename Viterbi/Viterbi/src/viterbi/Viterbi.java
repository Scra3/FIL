/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package viterbi;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author b16007026
 */
public class Viterbi {

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

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {

        //DÉCLARATIONS
        final String cheminTreillis = "src/viterbi/treillis.txt"; // chemin du treillis
        Viterbi viterbi = new Viterbi();
        BufferedReader buff = null; // buffer pour lire le fichier treillis
        String strFile = ""; // fichier treillis
        LinkedHashMap<String, ArrayList<String>> treillis;
        // On récupère le contenu du fichier
        buff = viterbi.getBufferedReader(cheminTreillis);
        strFile = viterbi.readFile(buff);

        // Insérérer dans notre hashmap chaque étape retourne le treillis
        treillis = viterbi.insertTreillis(strFile);
        //viterbi.displayMap(treillis);

        System.out.println(viterbi.minEmission(treillis, "0"));
    }

}
