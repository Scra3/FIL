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
import java.util.LinkedHashMap;
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
        System.out.println("Affichage Map : ");
        for (Entry<String, Double> entry : map.entrySet()) {
            String cle = entry.getKey();
            Double valeur = entry.getValue();
            System.out.println(cle + " " + valeur);
        }
        System.out.println("Fin affichage");
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
        boolean find = true;

        System.out.println("Générer séquences");
        if (n > 1) {
            taille = tokens.length - 3;
            strTok = new String[taille];
            for (int i = 0; i < tokens.length; i++) {
                // Si le premier token de la séquence
                if (find == true) {
                    strTok[a] = tokens[i];
                    find = false;
                    // on save les indices du tableau des tokens et du tableau de séquence
                    a++;
                    i++;
                }
                for (int j = 0; j < n; j++) {
                    // si fin de ligne
                    if (tokens[i].indexOf("F") != -1) {
                        find = true;
                        break;
                    } else if (j == 0) {
                        strTok[a] = tokens[i];
                    } else {
                        // n > 2 vérifier que on ne cherche pas une case null
                        if (i - j < 0) {
                            break;
                        } else {
                            strTok[a] = strTok[a] + " " + tokens[i - j];
                        }
                    }
                }
                if (find == true) {
                    strTok[a] = null;
                } else {
                    a++;
                }
            }
        } else {
            taille = tokens.length - (gram.searchSequence("F", tokens));
            strTok = new String[taille];
            for (int i = 0; i < tokens.length; i++) {
                if (tokens[i].indexOf("F") == -1) {
                    strTok[a] = tokens[i];
                    System.out.println("Token : " + strTok[a]);
                    a++;
                }
            }
        }
        System.out.println("fin génération");
        return strTok;

    }

    public LinkedHashMap<String, Integer> getNGram(int n, String[] tokens) {
        Gram gram;

        LinkedHashMap<String, Integer> nbGram = new LinkedHashMap<String, Integer>();
        try {
            gram = new Gram();
            String[] t = gram.getGram(tokens, n);

            System.out.println("\nSéquence : in LinkedHashMap");
            for (int i = 0; i < t.length; i++) {
                if (t[i] != null) {
                    // on met la séquence dans l'odre pour calculer les probabilités
                    String seq = t[i];

                    // pour les séquences qui ne sont pas de longueur N de début de chaine
                    if (seq.split(" ").length < n) {
                        if (nbGram.containsKey(seq) != true) {
                            // on inverse l'odre pour la recherche
                            String nb[] = seq.split(" ");
                            String seqInv = "";
                            for (int j = nb.length - 1; j >= 0; j--) {
                                if (nb.length - 1 == j) {
                                    seqInv = nb[j];
                                } else {
                                    seqInv = seqInv + " " + nb[j];
                                }
                            }
                            nbGram.put(seq, gram.searchSequence(seqInv, tokens));
                        }
                    } else {
                        if (nbGram.containsKey(seq)) {
                            nbGram.put(seq, nbGram.get(seq) + 1);
                        } else {
                            nbGram.put(seq, 1);
                        }
                    }
                }
            }
            System.out.println("Fin in LinkedHashMap : in map\n");

        } catch (Exception e) {
            System.out.println(e + " " + n);
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

    public LinkedHashMap<String, Integer> parseStringToMap(String contenu) {

        LinkedHashMap<String, Integer> map = new LinkedHashMap<String, Integer>();
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

    public LinkedHashMap<String, Double> maximumVraissemblance(LinkedHashMap<String, Integer> map, String[] tokens, int n) {
        Gram gram = new Gram();

        LinkedHashMap<String, Double> mapProb = new LinkedHashMap<String, Double>();

        if (n > 1) {
            for (Entry<String, Integer> entrySet : map.entrySet()) {

                int a = entrySet.getValue();
                String result = "";
                String[] t;

                if (entrySet.getKey().split(" ").length == 2) {
                    t = new String[1];
                    t[0] = entrySet.getKey();
                } else {
                    t = entrySet.getKey().split(" ");
                    for (int j = 0; j < t.length - 1; j++) {
                        result = result + t[j];
                    }
                }
                double b = 0;

                if (t.length == 1) {
                    b = gram.searchSequence(t[0].trim(), tokens);
                    // nombre de tokens
                    double N = 0;
                    for (Entry<String, Integer> entrySet1 : map.entrySet()) {
                        N = N + entrySet1.getValue();
                    }
                    b = N;
                } else {
                    b = gram.searchSequence(" " + result + " ", tokens);
                }
                double r = (double) a / (double) b;

                mapProb.put(entrySet.getKey(), r);
            }
        } else {
            int t = (tokens.length - gram.searchSequence("F", tokens) - 1);
            for (Entry<String, Integer> entrySet : map.entrySet()) {
                int a = entrySet.getValue();
                double b = (double) a / (double) t;
                mapProb.put(entrySet.getKey(), b);
            }
        }

        // we need to calculate C2
        return mapProb;
    }

    public int searchSequence(String gramStr, String[] t) {
        Gram gram = new Gram();
        String str = "";
        str = gram.parseTableStringToString(t);
        String[] ts = str.split(gramStr);

        return ts.length - 1;
    }

    public Double probabilitySequence(LinkedHashMap<String, Double> mapProb,LinkedHashMap<String, Integer> mapAppartion) {
        double prob = 0;
        
        // pw(1)
        for (Entry<String, Integer> entrySet : mapAppartion.entrySet()) {
            String key = entrySet.getKey();
            Integer value = entrySet.getValue();
            double probKey = (double)mapProb.get(key);
            for (int i = 0; i < value; i++) {
                prob = prob -log(probKey);
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
        LinkedHashMap<String, Integer> nGram = null;
        LinkedHashMap<String, Double> nGramProb = null;
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

        content = gram.parseMapToString(nGram);

        // on écrit le compte dans un fichier
        gram.writeFile(compteFile, content);

        buff = gram.getBufferedReader(compteFile);

        strFile = gram.readFile(buff, "\n");

        nGram = gram.parseStringToMap(strFile);

        System.out.println("Nombre de fois qu' aparait le mot");

        gram.displayHashMap(nGram);

        nGramProb = gram.maximumVraissemblance(nGram, tokens, N);

        System.out.println("Estimation du n-gram");

        gram.displayHashMapDouble(nGramProb);

        Double probSeqence = gram.probabilitySequence(nGramProb, nGram);

        System.out.println("PROB");
        //gram.displayHashMapDouble(nGramProb);
        System.out.println("FINPROB");

        System.out.println(probSeqence);
    }

}
