import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class MultiProgress {

    public static void main(String[]args){
        ArrayList<ArrayList<ArrayList<Double>>> average = new ArrayList<>();
        int avg = 5;
        long startTime = System.currentTimeMillis();
        ClockWork c = new ClockWork();
        int select = 2;
        for (int i = 0; i < avg; i++) {
            if (select == 0) {
                c.motion();
            } else if (select == 1) {
                c.lessMotion();
            } else if (select == 2) {
                c.VIP();
            } else if (select == 3) {
                c.ultimatum();
            }
            else {
                c.minimalist();
            }
            average.add(c.collection());
            Stats s = new Stats(c);
            System.out.println(s.results());
        }
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println(elapsedTime);
        String holder = condense(average);
        System.out.println(holder);
        if (select == 0) {
            try {
                write(holder, "outPutDataMH");
            }
            catch (IOException writeError){
                System.out.println("Unable to write");
            }
        }
        else if (select == 1) {
            try {
                write(holder, "outPutDataNM");
            }
            catch (IOException writeError){
                System.out.println("Unable to write");
            }
        }
        else if (select == 2) {
            try {
                write(holder, "outPutDataC");
            }
            catch (IOException writeError){
                System.out.println("Unable to write");
            }
        }
        else {
            try {
                write(holder, "outPutDataU");
            }
            catch (IOException writeError){
                System.out.println("Unable to write");
            }
        }
    }

    public static void write(String str, String name)
            throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(name));
        writer.write(str);
        writer.close();
    }

    public static String condense(ArrayList<ArrayList<ArrayList<Double>>> average) {
        int winner = 0;
        //System.out.println(average);
        for (int i = 0; i < average.size(); i++) {
            for (int j = 0; j < average.get(i).size(); j++) {
                if (average.get(i).get(j).size() > winner) {
                    winner = average.get(i).get(j).size();
                }
            }
        }
        System.out.println("winner: " + winner);
        Double[] E = new Double[winner];
        Double[] C = new Double[winner];
        Double[] R = new Double[winner];
        for (int x = 0; x < E.length; x++) {
            E[x] = 0.0;
            C[x] = 0.0;
            R[x] = 0.0;
        }
        System.out.println(E[0]);
        System.out.println(C[0]);
        System.out.println(R[0]);
        for (int i = 0; i < average.size() - 1; i++) {
            for (int j = 0; j < average.get(i).size() - 1; j++) {
                for (int k = 0; k < average.get(i).get(j).size() - 1; k++) {
                    if (j % 3 == 0) {
                        double total = E[k] + average.get(i).get(j).get(k);
                        E[k] = total;
                    }
                    else if (j % 3 == 1) {
                        double total = C[k] + average.get(i).get(j).get(k);
                        C[k] = total;
                    }
                    else {
                        double total = R[k] + average.get(i).get(j).get(k);
                        R[k] = total;
                    }
                }
            }
        }
        for (int l = 0; l < E.length; l++) {
            E[l] = E[l] / average.size();
            C[l] = C[l] / average.size();
            R[l] = R[l] / average.size();
        }
//        System.out.println(E.length);
//        System.out.println(C.length);
//        System.out.println(R.length);
        String str = "";
        str += Arrays.toString(E) + "\n";
        str += Arrays.toString(C) + "\n";
        str += Arrays.toString(R) + "\n";
        return str;
    }
}
