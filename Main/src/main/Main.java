import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by thom1 on 24/10/2016.
 */
public class Main {

    public static void main(String [] args){
        new Main(args);
    }
    private HashMap<String,Integer> coucou;
    private HashMap<String,Integer> tokenCoupleHashMap;
    public Main(String [] args){
        tokenCoupleHashMap = new HashMap<>();
        String tokens = "1054 7815 4238 9297 6283\n" +
                "5831 1054 4554 688 9296 7960 8104 9297 757 8908 1203 6861 2624 4691 7802 4772 18 789 7815 4238 9297 6283\n" +
                "4691 7802 4771 4772 18 789 3311 9297 5964 2279 5818\n" +
                "5909\n";

        String[] lines = tokens.split("\n");
        String[][] mots = new String [lines.length][];
        for(int i = 0; i<lines.length; i++){
            //System.out.println(lines[i]);
            mots[i] = lines[i].split(" ");
        }
        for(int i=0; i< mots.length; i++){
            for(int j = 1; j< mots[i].length; j++){
                if(mots[i].length>1) {
                    String mot1 = mots[i][j - 1];
                    String mot2 = mots[i][j];
                    String key = mot1+" "+mot2;
                    if(tokenCoupleHashMap.containsKey(key)){
                        tokenCoupleHashMap.put(key,tokenCoupleHashMap.get(key)+1);
                    }else{
                        tokenCoupleHashMap.put(key,1);
                    }
                }
            }
        }
        Set cles = tokenCoupleHashMap.keySet();
        Iterator it = cles.iterator();
        while (it.hasNext()){
            Object cle = it.next();
                System.out.println("clef : "+cle+"\tmot : "+tokenCoupleHashMap.get(cle));
        }
    }

    private class TokenCouple{
        private int mot1;
        private int mot2;
        public TokenCouple(int mot1, int mot2){
            this.mot1 = mot1;
            this.mot2 = mot2;
        }
        public int hashCode() {
            return Integer.parseInt(this.mot1 +""+ this.mot2);
        }

        public int getMot1() {
            return mot1;
        }

        public void setMot1(int mot1) {
            this.mot1 = mot1;
        }

        public int getMot2() {
            return mot2;
        }

        public void setMot2(int mot2) {
            this.mot2 = mot2;
        }
    }
}