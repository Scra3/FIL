package gram;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import static java.lang.Math.log;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

public class Gram {

    public String readFile(BufferedReader fichier, String add) throws IOException {

        String line;
        String text = "";
        try {
            while ((line = fichier.readLine()) != null) {
                text = text + line;
                text = text + add;
            }
        } catch (IOException io) {
            System.out.println(io + " Error file " + fichier);
        }

        fichier.close();
        return text;
    }

    public BufferedReader getBufferedReader(String fichier) {

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

    public String[] getToken(String strTokens) {
        String[] tokens = null;
        String[] strTokensSplit = null;
        int i = 0;
        try {
            System.out.println(strTokens);

            StringTokenizer st = new StringTokenizer(strTokens);

            tokens = new String[st.countTokens()];

            while (st.hasMoreTokens()) {

                tokens[i] = st.nextToken();
                i++;
            }

        } catch (NumberFormatException e) {
            System.out.println(e + " Error string");
        }

        return tokens;
    }

    public void displayTable(int[] tableau) {
        for (int i = 0; i < tableau.length; i++) {
            System.out.println(tableau[i]);
        }
    }

    public void displayTable(String[] tableau) {
        for (String tableau1 : tableau) {
            System.out.println(tableau1);
        }
    }

    public void displayHashMap(Map<String, Integer> map) {
        System.out.println("Affichage Map : ");
        for (Entry<String, Integer> entry : map.entrySet()) {
            String cle = entry.getKey();
            Integer valeur = entry.getValue();
            System.out.println(cle + " " + valeur);
        }
        System.out.println("Fin affichage");
    }

    public void displayHashMapDouble(Map<String, Double> map) {
        for (Entry<String, Double> entry : map.entrySet()) {
            String cle = entry.getKey();
            Double valeur = entry.getValue();
            System.out.println(cle + " " + valeur);
        }
    }

    public String parseTableStringToString(String[] tokens) {
        String strTok = "";
        for (int i = 0; i < tokens.length; i++) {
            if (i == 0) {
                strTok = strTok + tokens[i];

            } else {
                strTok = strTok + " " + tokens[i];
            }
        }
        return strTok;
    }

    public String[] getGram(String[] tokens, int n) {
        Gram gram = new Gram();
        int taille = 0;
        String[] strTok = null;
        int a = 0;
        boolean find = false;

        System.out.println("Get tokens");
        if (n > 1) {
            taille = tokens.length - (gram.searchGram("F", tokens) * 2);

            strTok = new String[taille];
            for (int i = 0; i < tokens.length; i = i + 1) {
                for (int j = 0; j < n; j++) {
                    if (tokens[i + j].indexOf("F") != -1) {
                        find = true;
                        break;
                    } else {
                        if (j == 0) {
                            strTok[a] = tokens[i + j];
                        } else {
                            strTok[a] = strTok[a] + " " + tokens[i + j];
                        }
                    }
                }
                if (find == true) {
                    find = false;
                    strTok[a] = null;
                } else {
                    System.out.println(" Token : " + strTok[a]);
                    a++;
                }
            }
        } else {
            taille = tokens.length - (gram.searchGram("F", tokens));
            strTok = new String[taille];
            for (int i = 0; i < tokens.length; i++) {
                if (tokens[i].indexOf("F") == -1) {
                    strTok[a] = tokens[i];
                    System.out.println("Token : " + strTok[a]);
                    a++;
                }
            }
        }
        System.out.println("fin tokens");
        return strTok;

    }

    public Map<String, Integer> getNGram(int n, String[] tokens) {
        Gram gram;

        Map<String, Integer> nbGram = new HashMap<String, Integer>();

        try {
            gram = new Gram();
            String[] t = gram.getGram(tokens, n);

            System.out.println("\nTokenisation : in map");

            for (int i = 0; i < t.length; i++) {
                System.out.println(t[i]);
                if (t[i] != null) {
                    if (nbGram.containsKey(t[i])) {
                        nbGram.put(t[i], nbGram.get(t[i]) + 1);
                    } else {
                        nbGram.put(t[i], 1);
                    }
                }
            }
            System.out.println("Fin Tokenisation : in map\n");

        } catch (Exception e) {
            System.out.println("Le dernier n-couple n'est pas de taille " + n);
        }

        return nbGram;
    }

    public void writeFile(String file, String contents) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(contents);
        } catch (Exception e) {
            System.out.println(e + " Impossible d' écrire dans le fichier " + file);
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                    System.out.println("Fichier " + file + " => ok");
                }
            } catch (IOException e) {
            }
        }
    }

    public String parseMapToString(Map<String, Integer> map) {
        String content = "";
        for (Map.Entry<String, Integer> e : map.entrySet()) {
            content = content + e.getKey();
            content = content + " " + e.getValue().toString();
            content = content + "\n";
        }
        return content;
    }

    public Map<String, Integer> parseStringToMap(String contenu) {

        Map<String, Integer> map = new HashMap<String, Integer>();
        String[] splited = null;
        String[][] endSplited = null;
        String result = "";
        splited = contenu.split("\n");

        endSplited = new String[splited.length][2];

        for (int i = 0; i < splited.length; i++) {

            String[] splitedThree = splited[i].split(" ");

            for (int j = 0; j < splitedThree.length - 1; j++) {
                result = result + " " + splitedThree[j];
            }

            map.put(result, Integer.parseInt(splitedThree[splitedThree.length - 1]));
            result = "";
        }

        return map;
    }

    public Map<String, Double> maximumVraissemblance(Map<String, Integer> map, String[] tokens, int n) {
        int count = 0;
        Gram gram = new Gram();
        Map<String, Integer> nGram = new HashMap<String, Integer>();

        Map<String, Double> mapProb = new HashMap<String, Double>();

        if (n > 1) {
            for (Entry<String, Integer> entrySet : map.entrySet()) {

                int a = entrySet.getValue();
                String result = "";
                String[] t = entrySet.getKey().split(" ");

                for (int j = 0; j < t.length - 1; j++) {
                    result = result + t[j];
                }
                int b = gram.searchGram(" " + result + " ", tokens);

                double r = (double) a / (double) b;

                mapProb.put(entrySet.getKey(), r);
            }
        } else {
            int t = (tokens.length - gram.searchGram("F", tokens) - 1);
            for (Entry<String, Integer> entrySet : map.entrySet()) {
                int a = entrySet.getValue();
                double b = (double) a / (double) t;
                mapProb.put(entrySet.getKey(), b);
            }
        }

        // we need to calculate C2
        return mapProb;
    }

    public int searchGram(String gramStr, String[] t) {
        Gram gram = new Gram();
        String str = "";
        str = gram.parseTableStringToString(t);
        String[] ts = str.split(gramStr);

        return ts.length - 1;
    }

    public Double probabilitySequence(Map<String, Double> map, String[] t, int n) {
        Double prob = 0.0;
        Double prob1 = 1.0;

        int compteur = 1;
        Gram gram = new Gram();
        int a;
        int nb;
        String sequence = "";
        // prob de p(w1)   
        for (int i = 0; i < n; i++) {
            if (i == 0) {
                sequence = sequence + t[i];

            } else {
                sequence = sequence + " " + t[i];
            }
        }
        nb = gram.searchGram(sequence, t);
        a = (t.length - gram.searchGram("F", t) - 1);

        prob1 = (double) nb / (double) a;
        // Double pw1 = -log();
        for (Entry<String, Double> entrySet : map.entrySet()) {
            // on saute le premier tour 
            System.out.println("prof d " + prob);
            if (compteur < 2) {
                compteur++;
            } else {
                Double value = entrySet.getValue();
                if (prob == 0.0) {
                    prob = - log(value);
                } else {
                    prob = -log(prob) - log(value);
                }
                System.out.println("V " + value);
                System.out.println("log " + -log(value));
                System.out.println("prob " + prob);
            }
        }
        return prob;
    }

    public static void main(String[] args) throws IOException {

        //Declaration 
        Gram gram = new Gram();
        BufferedReader buff = null;
        String strFile = "";
        String[] tokens = null;
        Map<String, Integer> nGram = null;
        Map<String, Double> nGramProb = null;

        String content = "";

        final int N = 2;

        // Args[0] est le nom du fichier 
        //String input = args[0];
        String input = "src/gram/tokens.txt";
        String compteFile = "src/gram/compte.txt";

        buff = gram.getBufferedReader(input);

        // le F signifie la fin d'une ligne
        strFile = gram.readFile(buff, " F\n");

        tokens = gram.getToken(strFile);

        nGram = gram.getNGram(N, tokens);

        gram.displayHashMap(nGram);

        content = gram.parseMapToString(nGram);

        // on écrit le compte dans un fichier
        gram.writeFile(compteFile, content);

        buff = gram.getBufferedReader(compteFile);

        strFile = gram.readFile(buff, "\n");

        nGram = gram.parseStringToMap(strFile);

        gram.displayHashMap(nGram);

        nGramProb = gram.maximumVraissemblance(nGram, tokens, N);

        Double probSeqence = gram.probabilitySequence(nGramProb, tokens, N);

        System.out.println("PROB");
        //gram.displayHashMapDouble(nGramProb);
        System.out.println("FINPROB");

        System.out.println(probSeqence);
    }

}
