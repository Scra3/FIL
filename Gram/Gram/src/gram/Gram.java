package gram;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

public class Gram {

    int[] tokens = null;

    public Gram() {
        this.tokens = new int[1];
    }

    public void setGram(int n) {
        this.tokens = new int[n];
    }

    public int[] getTokens() {
        return tokens;
    }

    public void setTokens(int[] tokens) {
        this.tokens = tokens;
    }

    public String readFile(BufferedReader fichier) throws IOException {

        String line;
        String text = "";
        try {
            while ((line = fichier.readLine()) != null) {
                text = text + " " + line;
            }
        } catch (IOException io) {
            System.out.println(io + " Error file");
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

    public int[] getToken(String strTokens) {
        int[] tokens = null;
        String[] strTokensSplit = null;
        int i = 0;
        try {
            StringTokenizer st = new StringTokenizer(strTokens);

            tokens = new int[st.countTokens()];
            // System.out.println(strTokens);

            while (st.hasMoreTokens()) {
                tokens[i] = Integer.parseInt(st.nextToken());
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
        for (Entry<String, Integer> entry : map.entrySet()) {
            String cle = entry.getKey();
            Integer valeur = entry.getValue();
            System.out.println(cle + " " + valeur);
        }
    }

    public Map<String, Integer> getNGram(int n, int[] tokens) {
        Gram gram;

        displayTable(tokens);
        Map<String, Integer> nbGram = new HashMap<String, Integer>();
        String strTok = "";

        try {
            for (int i = 0; i < tokens.length; i = i + 1) {
                gram = new Gram();
                gram.setGram(n);
                int[] tok = gram.getTokens();

                for (int j = 0; j < n; j++) {
                    strTok = strTok + " " + Integer.toString(tokens[i + j]);
                    tok[j] = tokens[i + j];
                    //System.out.println(tokens[i + j]);
                }
                //System.out.println(strTok);
                gram.setTokens(tok);

                if (nbGram.containsKey(strTok)) {
                    nbGram.put(strTok, nbGram.get(strTok) + 1);
                } else {
                    nbGram.put(strTok, 1);
                }
                strTok = "";
            }
        } catch (Exception e) {
            System.out.println("Le dernier n-couple n'est pas de taille " + n);
        }
        displayHashMap(nbGram);

        return nbGram;
    }

    public static void main(String[] args) throws IOException {

        //Declaration 
        Gram gram = new Gram();
        BufferedReader buff = null;
        String strFile = "";
        int[] tokens = null;
        Map<String, Integer> nGram = null;

        // Args[0] est le nom du fichier 
        //String input = args[0];
        String input = "src/gram/tokens.txt";

        buff = gram.getBufferedReader(input);

        strFile = gram.readFile(buff);

        tokens = gram.getToken(strFile);

        //gram.displayTable(tokens);
        nGram = gram.getNGram(2, tokens);

    }

}
