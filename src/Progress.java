import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
/*
Generator
 */
public class Progress {
    /*
     * Identification values for the major classes participating in the simulation
     */
    public static int idCell = 1;
    public static int idCluster = 1;
    public static int idDataCenter = 1;
    public static int idJob = 1;
    public static int idMachine = 1;
    public static int idTask = 1;
    public static int cellSpeed;
    public static int numCores;
    public static int IdlePC;
    public static int MaxPC;
    public static int select;
    public static int chunk;
    public static int aggregate;
    public static int year = 525600;
    public static int month = 44640;
    public static int week = 10080;
    public static int day = 24 * 60;
    public static int[] chunks = new int[]{day, week, month, year};
    /*
    Global metric used for comparing doubles to zero
     */
    public final static double EPSILON = .000001;
    /*
    Main method for running the simulation once over a month long period
     */
    public static void main(String[]args){
        //Read in information from config file
        ArrayList<ArrayList<ArrayList<ArrayList<ArrayList<ArrayList<ArrayList<ArrayList>>>>>>> bigDeal = loadConfig();
        if (aggregate == 1) {
            //Track progress of the simulation
            long startTime = System.currentTimeMillis();
            //Create ClockWork Module
            ClockWork c = new ClockWork(bigDeal);
            //Choose which version of simulation to run
            if (select == 0) {
                c.motion(chunks[chunk]);
            } else if (select == 1) {
                c.lessMotion(chunks[chunk]);
            }
            //Collect statistical dump
            Stats s = new Stats(c);
            System.out.println(s.results());
            long stopTime = System.currentTimeMillis();
            long elapsedTime = stopTime - startTime;
            //Total execution time
            System.out.println(elapsedTime);
            //Compile stats on revenue, cost, etc
            ArrayList<ArrayList<ArrayList<Double>>> regionMData = new ArrayList<>();
            ArrayList<ArrayList<ArrayList<Double>>> regionTData = new ArrayList<>();
            ArrayList<ArrayList<ArrayList<Double>>> regionJobPerf = new ArrayList<>();
            ArrayList<ISOVal> regions = new ArrayList<>();
            for (Interconnection I : c.getPowerGrid()) {
                for (IsoRegion i : I.getIsoRegions()) {
                    regionMData.add(i.compileMStats());
                    regionTData.add(i.compileTStats());
                    regionJobPerf.add(i.compileJStats());
                    regions.add(i.getAuthority().getIso());
                }
            }
            ArrayList<String> yump = gridLock(regionMData);
            ArrayList<String> yump2 = gridLock(regionTData);
            ArrayList<String> yump3 = jobLock(regionJobPerf);
            //Write data to txt file
            if (select == 0) {
                for (String str : yump) {
                    try {
                        write(str, "minByMinOutputM");
                    } catch (IOException writeError) {
                        System.out.println("Unable to write");
                    }
                }
                for (String str : yump2) {
                    try {
                        write(str, "tOutputM");
                    } catch (IOException writeError) {
                        System.out.println("Unable to write");
                    }
                }
                for (String str : yump3) {
                    try {
                        write(str, "JobPerformanceM");
                    } catch (IOException writeError) {
                        System.out.println("Unable to write");
                    }
                }
            } else if (select == 1) {
                for (String str : yump) {
                    try {
                        write(str, "minByMinOutputNM");
                    } catch (IOException writeError) {
                        System.out.println("Unable to write");
                    }
                }
                for (String str : yump2) {
                    try {
                        write(str, "tOutputNM");
                    } catch (IOException writeError) {
                        System.out.println("Unable to write");
                    }
                }
                for (String str : yump3) {
                    try {
                        write(str, "JobPerformanceNM");
                    } catch (IOException writeError) {
                        System.out.println("Unable to write");
                    }
                }
            }
        }
        else {
            System.out.println("Aggregating");
            ArrayList<ArrayList<ArrayList<Double>>> minAverage = new ArrayList<>();
            ArrayList<ArrayList<ArrayList<Double>>> tAverage = new ArrayList<>();
            ArrayList<ArrayList<ArrayList<Double>>> jAverage = new ArrayList<>();
            int avg = 5;
            long startTime = System.currentTimeMillis();
            ClockWork c = new ClockWork(bigDeal);
            for (int i = 0; i < avg; i++) {
                if (select == 0) {
                    c.motion(chunks[chunk]);
                } else if (select == 1) {
                    c.lessMotion(chunks[chunk]);
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
    }

    public static void write(String str, String name)
            throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(name));
        writer.write(str);
        writer.close();
    }

    /*
    Take 3d Arraylist of values and aggregate into a 1d arraylist of string values
     */
    public static ArrayList<String> gridLock(ArrayList<ArrayList<ArrayList<Double>>> data) {
        //Find largest tuple
        ArrayList<String> holders = new ArrayList<>();
        for (int i = 0; i < data.size() - 1; i++) {
            int winner = 0;
            for (int j = 0; j < data.get(i).size(); j++) {
                if (data.get(i).get(j).size() > winner) {
                    winner = data.get(i).get(j).size();
                }
            }
            ///Create holder
            Double[] E = new Double[winner];
            Double[] C = new Double[winner];
            Double[] R = new Double[winner];
            for (int x = 0; x < E.length; x++) {
                E[x] = 0.0;
                C[x] = 0.0;
                R[x] = 0.0;
            }
            for (int j = 0; j < data.get(i).size() - 1; j++) {
                for (int k = 0; k < data.get(i).get(j).size() - 1; k++) {
                    if (j % 3 == 0) {
                        E[k] = data.get(i).get(j).get(k);
                    } else if (j % 3 == 1) {
                        double total = C[k] + data.get(i).get(j).get(k);
                        C[k] = total;
                    } else {
                        double total = R[k] + data.get(i).get(j).get(k);
                        R[k] = total;
                    }
                }
            }
            for (int l = 0; l < E.length; l++) {
                E[l] = E[l] / data.get(i).size();
                C[l] = C[l] / data.get(i).size();
                R[l] = R[l] / data.get(i).size();
            }
            String str = "";
            str += Arrays.toString(E) + "\n";
            str += Arrays.toString(C) + "\n";
            str += Arrays.toString(R) + "\n";
            holders.add(str);
        }
        return holders;
    }

    public static ArrayList<String> jobLock(ArrayList<ArrayList<ArrayList<Double>>> data) {
        ArrayList<String> holders = new ArrayList<>();
        for (int i = 0; i < data.size() - 1; i++) {
            int winner = 0;
            for (int j = 0; j < data.get(i).size(); j++) {
                if (data.get(i).get(j).size() > winner) {
                    winner = data.get(i).get(j).size();
                }
            }
            Double[] T = new Double[winner];
            Double[] F = new Double[winner];
            for (int x = 0; x < T.length; x++) {
                T[x] = 0.0;
                F[x] = 0.0;
            }
            for (int j = 0; j < data.get(i).size() - 1; j++) {
                for (int k = 0; k < data.get(i).get(j).size() - 1; k++) {
                    if (j % 2 == 0) {
                        double total = T[k] + data.get(i).get(j).get(k);
                        T[k] = total;
                    } else{
                        double total = F[k] + data.get(i).get(j).get(k);
                        F[k] = total;
                    }
                }
            }
            for (int l = 0; l < T.length; l++) {
                T[l] = T[l] / data.get(i).size();
                F[l] = F[l] / data.get(i).size();
            }
            String str = "";
            str += Arrays.toString(T) + "\n";
            str += Arrays.toString(F) + "\n";
            holders.add(str);
        }
        return holders;
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
        Double[] E = new Double[winner];
        Double[] C = new Double[winner];
        Double[] R = new Double[winner];
        for (int x = 0; x < E.length; x++) {
            E[x] = 0.0;
            C[x] = 0.0;
            R[x] = 0.0;
        }
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
        for (int i = 0; i < average.size(); i++) {
            for (int j = 0; j < average.get(i).size(); j++) {
                if (average.get(i).get(j).size() > winner) {
                    winner = average.get(i).get(j).size();
                }
            }
        }
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
}                    f = inputFile.nextLine();
                    if (f.contains("Aggregate/Single: ")) {
                        String[] hold = f.split(": ");
                        String q = hold[1];
                        int l = Integer.parseInt(q);
                        aggregate = l;
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
