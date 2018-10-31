import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
/*
Secondary main, used to aggregate runs of the simulation
 */
public class MultiProgress {

    public static int cellSpeed;
    public static int numCores;
    public static int IdlePC;
    public static int MaxPC;
    public static int select;
    public static int chunk;

    public static void main(String[]args) {
        ArrayList<ArrayList<ArrayList<Double>>> minAverage = new ArrayList<>();
        ArrayList<ArrayList<ArrayList<Double>>> tAverage = new ArrayList<>();
        ArrayList<ArrayList<ArrayList<Double>>> jAverage = new ArrayList<>();
        int avg = 5;
        long startTime = System.currentTimeMillis();
        ArrayList<ArrayList<ArrayList<ArrayList<ArrayList<ArrayList<ArrayList<ArrayList>>>>>>> bigDeal = loadConfig();
        ClockWork c = new ClockWork(bigDeal);
        int select = 0;
        int day = 24*60;
        int month = 44640;
        int year = 525600;
        for (int i = 0; i < avg; i++) {
            if (select == 0) {
                c.motion(month);
            } else if (select == 1) {
                c.lessMotion(month);
            }
            minAverage.add(c.minCollection());
            tAverage.add(c.tCollection());
            jAverage.add(c.jCollection());
            Stats s = new Stats(c);
            System.out.println(s.results());
        }
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println(elapsedTime);
        String holder1 = condense(minAverage);
        String holder2 = condense(tAverage);
        String holder3 = fun(jAverage);
        if (select == 0) {
            try {
                write(holder1, "avgMinByMinOutputM");
            } catch (IOException writeError) {
                System.out.println("Unable to write");
            }
            try {
                write(holder2, "avgTOutputM");
            } catch (IOException writeError) {
                System.out.println("Unable to write");
            }
            try {
                write(holder3, "avgJobPerformanceM");
            } catch (IOException writeError) {
                System.out.println("Unable to write");
            }
        } else {
            try {
                write(holder1, "avgMinByMinOutputNM");
            } catch (IOException writeError) {
                System.out.println("Unable to write");
            }
            try {
                write(holder2, "avgTOutputNM");
            } catch (IOException writeError) {
                System.out.println("Unable to write");
            }
            try {
                write(holder3, "avgJobPerformanceNM");
            } catch (IOException writeError) {
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
        String str = "";
        str += Arrays.toString(E) + "\n";
        str += Arrays.toString(C) + "\n";
        str += Arrays.toString(R) + "\n";
        return str;
    }

    public static String fun(ArrayList<ArrayList<ArrayList<Double>>> average) {
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
        Double[] T = new Double[winner];
        Double[] F = new Double[winner];
        for (int x = 0; x < T.length; x++) {
            T[x] = 0.0;
            F[x] = 0.0;
        }
        for (int i = 0; i < average.size() - 1; i++) {
            for (int j = 0; j < average.get(i).size() - 1; j++) {
                for (int k = 0; k < average.get(i).get(j).size() - 1; k++) {
                    if (j % 2 == 0) {
                        double total = T[k] + average.get(i).get(j).get(k);
                        T[k] = total;
                    }
                    else {
                        double total = F[k] + average.get(i).get(j).get(k);
                        F[k] = total;
                    }
                }
            }
        }
        for (int l = 0; l < T.length; l++) {
            T[l] = T[l] / average.size();
            F[l] = F[l] / average.size();
        }
        String str = "";
        str += Arrays.toString(T) + "\n";
        str += Arrays.toString(F) + "\n";
        return str;
    }

    /*
     Read config file into 8d arraylist
      */
    public static ArrayList<ArrayList<ArrayList<ArrayList<ArrayList<ArrayList<ArrayList<ArrayList>>>>>>> loadConfig() {
        String path = System.getProperty("user.dir") + "/config/exampleConfig";
        File file = new File(path);
        String save = "";
        ArrayList<ArrayList<ArrayList<ArrayList<ArrayList<ArrayList<ArrayList<ArrayList>>>>>>> bigDeal = new ArrayList<>();
        try {
            Scanner inputFile = new Scanner(file);
            while (inputFile.hasNextLine()) {
                String line = inputFile.nextLine();
                if (line.equals("//WORLD SETTINGS")) {
                    String f = inputFile.nextLine();
                    if (f.contains("Length: ")) {
                        String[] hold = f.split(": ");
                        String l = hold[1];
                        chunk = Integer.parseInt(l);
                    }
                    f = inputFile.nextLine();
                    if (f.contains("Market/Non-Market: ")) {
                        String[] hold = f.split(": ");
                        String q = hold[1];
                        int l = Integer.parseInt(q);
                        select = l;
                    }
                }
                line = inputFile.nextLine();
                if (line.equals("//GLOBAL CELL")) {
                    String s = inputFile.nextLine();
                    if (s.contains("Speed:")) {
                        String[] hold = s.split(": ");
                        cellSpeed = Integer.parseInt(hold[1]);
                    }
                    s = inputFile.nextLine();
                    if (s.contains("Number of Cores:")) {
                        String[] hold = s.split(": ");
                        numCores = Integer.parseInt(hold[1]);
                    }
                    s = inputFile.nextLine();
                    if (s.contains("Idle Power Consumption:")) {
                        String[] hold = s.split(": ");
                        IdlePC = Integer.parseInt(hold[1]);
                    }
                    s = inputFile.nextLine();
                    if (s.contains("Max Power Consumption:")) {
                        String[] hold = s.split(": ");
                        MaxPC = Integer.parseInt(hold[1]);
                    }
                }
                line = inputFile.nextLine();
                if (line.equals("//INTERCONNECTION")) {
                    boolean connecting = true;
                    while (connecting) {
                        ArrayList<ArrayList<ArrayList<ArrayList<ArrayList<ArrayList>>>>> connection = new ArrayList<>();
                        String start = inputFile.nextLine();
                        ArrayList<String> con = new ArrayList<>();
                        con.add(start);
                        String l = inputFile.nextLine();
                        if (l.equals("//ISO REGION")) {
                            boolean regioning = true;
                            ArrayList<ArrayList<ArrayList<ArrayList<ArrayList>>>> region = new ArrayList<>();
                            while (regioning) {
                                ArrayList<String> authority = new ArrayList<>();
                                ArrayList<ArrayList<ArrayList>> states = new ArrayList<>();
                                String str = inputFile.nextLine();
                                authority.add(str);
                                String s = inputFile.nextLine();
                                if (s.equals("//STATE")) {
                                    boolean stating = true;
                                    while (stating) {
                                        ArrayList<ArrayList> state = new ArrayList<>();
                                        ArrayList<String> stateName = new ArrayList<>();
                                        stateName.add(inputFile.nextLine());
                                        ArrayList<ArrayList> dd = new ArrayList<>();
                                        dd.add(stateName);
                                        state.add(dd);
                                        if (inputFile.nextLine().equals("//DATA CENTER")) {
                                            boolean collecting = true;
                                            while (collecting) {
                                                ArrayList<ArrayList> center = new ArrayList<>();
                                                ArrayList specs = new ArrayList<>();
                                                String spec = inputFile.nextLine();
                                                if (spec.contains("Budget:")) {
                                                    String[] hold = spec.split(": ");
                                                    specs.add(Integer.parseInt(hold[1]));
                                                } else {
                                                    specs.add(null);
                                                }
                                                spec = inputFile.nextLine();
                                                if (spec.contains("Num Clusters:")) {
                                                    String[] hold = spec.split(": ");
                                                    specs.add(Integer.parseInt(hold[1]));
                                                } else {
                                                    specs.add(null);
                                                }
                                                spec = inputFile.nextLine();
                                                if (spec.contains("Band Width Speed:")) {
                                                    String[] hold = spec.split(": ");
                                                    specs.add(Double.parseDouble(hold[1]));
                                                } else {
                                                    specs.add(null);
                                                }
                                                spec = inputFile.nextLine();
                                                if (spec.contains("Participation Rate:")) {
                                                    String[] hold = spec.split(": ");
                                                    specs.add(Integer.parseInt(hold[1]));
                                                } else {
                                                    specs.add(null);
                                                }
                                                spec = inputFile.nextLine();
                                                if (spec.contains("Arrival Rate:")) {
                                                    String[] hold = spec.split(": ");
                                                    if (!hold[1].equals("null")) {
                                                        specs.add(Double.parseDouble(hold[1]));
                                                    }
                                                    else {
                                                        specs.add(null);
                                                    }
                                                } else {
                                                    specs.add(null);
                                                }
                                                ArrayList<ArrayList> holdSpecs = new ArrayList();
                                                holdSpecs.add(specs);
                                                center.add(holdSpecs);
                                                if (inputFile.nextLine().equals("//CLUSTER")) {
                                                    boolean clustering = true;
                                                    while (clustering) {
                                                        ArrayList<ArrayList> clusterSpecs = new ArrayList<>();
                                                        String numCell = inputFile.nextLine();
                                                        if (numCell.contains("Total Cells:")) {
                                                            String[] hold = numCell.split(": ");
                                                            ArrayList<Integer> holster = new ArrayList<>();
                                                            holster.add(Integer.parseInt(hold[1]));
                                                            clusterSpecs.add(holster);
                                                        }
                                                        save = inputFile.nextLine();
                                                        if (!save.equals("//CLUSTER")) {
                                                            clustering = false;
                                                            center.add(clusterSpecs);
                                                        } else {
                                                            center.add(clusterSpecs);
                                                        }
                                                    }
                                                }
                                                if (!save.equals("//DATA CENTER")) {
                                                    collecting = false;
                                                    state.add(center);
                                                } else {
                                                    state.add(center);
                                                }
                                            }
                                        }
                                        if (!save.equals("//STATE")) {
                                            stating = false;
                                            states.add(state);
                                        } else {
                                            states.add(state);
                                        }
                                    }
                                }
                                if (!save.equals("//ISO REGION")) {
                                    regioning = false;
                                    ArrayList<ArrayList<ArrayList<ArrayList>>> inbetween = new ArrayList<>();
                                    ArrayList<ArrayList> dd = new ArrayList<>();
                                    ArrayList<ArrayList<ArrayList>> ddd = new ArrayList<>();
                                    dd.add(authority);
                                    ddd.add(dd);
                                    inbetween.add(ddd);
                                    inbetween.add(states);
                                    region.add(inbetween);
                                    connection.add(region);
                                } else {
                                    ArrayList<ArrayList<ArrayList<ArrayList>>> inbetween = new ArrayList<>();
                                    ArrayList<ArrayList> dd = new ArrayList<>();
                                    ArrayList<ArrayList<ArrayList>> ddd = new ArrayList<>();
                                    dd.add(authority);
                                    ddd.add(dd);
                                    inbetween.add(ddd);
                                    inbetween.add(states);
                                    region.add(inbetween);
                                }
                            }
                        }
                        if (save.equals("%")) {
                            connecting = false;
                            ArrayList<ArrayList<ArrayList<ArrayList<ArrayList<ArrayList<ArrayList>>>>>> conn = new ArrayList<>();
                            ArrayList<ArrayList> dd = new ArrayList<>();
                            ArrayList<ArrayList<ArrayList>> ddd = new ArrayList<>();
                            ArrayList<ArrayList<ArrayList<ArrayList>>> dddd = new ArrayList<>();
                            ArrayList<ArrayList<ArrayList<ArrayList<ArrayList>>>> ddddd = new ArrayList<>();
                            ArrayList<ArrayList<ArrayList<ArrayList<ArrayList<ArrayList>>>>> dddddd = new ArrayList<>();
                            dd.add(con);
                            ddd.add(dd);
                            dddd.add(ddd);
                            ddddd.add(dddd);
                            dddddd.add(ddddd);
                            conn.add(dddddd);
                            conn.add(connection);
                            bigDeal.add(conn);
                        } else {
                            ArrayList<ArrayList<ArrayList<ArrayList<ArrayList<ArrayList<ArrayList>>>>>> conn = new ArrayList<>();
                            ArrayList<ArrayList> dd = new ArrayList<>();
                            ArrayList<ArrayList<ArrayList>> ddd = new ArrayList<>();
                            ArrayList<ArrayList<ArrayList<ArrayList>>> dddd = new ArrayList<>();
                            ArrayList<ArrayList<ArrayList<ArrayList<ArrayList>>>> ddddd = new ArrayList<>();
                            ArrayList<ArrayList<ArrayList<ArrayList<ArrayList<ArrayList>>>>> dddddd = new ArrayList<>();
                            dd.add(con);
                            ddd.add(dd);
                            dddd.add(ddd);
                            ddddd.add(dddd);
                            dddddd.add(ddddd);
                            conn.add(dddddd);
                            conn.add(connection);
                            bigDeal.add(conn);
                        }
                    }
                }
            }
            inputFile.close();
        }
        catch (FileNotFoundException message) {
            System.out.println(message);
        }
        return bigDeal;
    }
}
