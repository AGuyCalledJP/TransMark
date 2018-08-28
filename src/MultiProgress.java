import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class MultiProgress {

    public static void main(String[]args){
        ArrayList<ArrayList<ArrayList<Double>>> average = new ArrayList<>();
        int avg = 2;
        long startTime = System.currentTimeMillis();
        ClockWork c = new ClockWork();
        int select = 0;
        for (int i = 0; i < avg; i++) {
            if (select == 0) {
                c.motion();
            } else if (select == 1) {
                c.lessMotion();
            } else if (select == 2) {
                c.VIP();
            } else {
                c.ultimatum();
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
        Double[] E = new Double[average.get(0).get(0).size()];
        Double[] C = new Double[average.get(0).get(0).size()];
        Double[] R = new Double[average.get(0).get(0).size()];
        System.out.println(E.length);
        System.out.println(C.length);
        System.out.println(R.length);
        for (int i = 0; i < average.size(); i++) {
            for (int j = 0; j < average.get(i).size(); j++) {
                for (int k = 0; j < average.get(i).get(j).size(); k++) {
                    if (j % 3 == 0) {
                       E[k] = E[k] + average.get(i).get(j).get(k);
                    }
                    else if (j % 3 == 1) {
                        C[k] = C[k] + average.get(i).get(j).get(k);
                    }
                    else {
                        R[k] = R[k] + average.get(i).get(j).get(k);
                    }
                }
            }
        }
        for (int l = 0; l < E.length; l++) {
            E[l] = E[l] / average.size();
            C[l] = C[l] / average.size();
            R[l] = R[l] / average.size();
        }
        System.out.println(E.length);
        System.out.println(C.length);
        System.out.println(R.length);
        String str = "";
        str += E.toString() + "\n";
        str += C.toString() + "\n";
        str += R.toString() + "\n";
        return str;
    }
}
