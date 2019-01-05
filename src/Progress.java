import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
/*
Generator to run simulation. Also contains all reading and writing function.
@author Jared Polonitza
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
    public static int year = 525600;
    public static int month = 44640;
    public static int week = 10080;
    public static int day = 24 * 60;
    public static double lambda;
    public static boolean set = false;
    public static int[] chunks = new int[]{day, week, month, year};
    private static ClockWork c;
    /*
    Global metric used for comparing doubles to zero
     */
    public final static double EPSILON = .000001;

    /*
    Files to manage data output
     */
    private static String E;
    private static String C;
    private static String R;
    private static String P;
    private static String F;
    private static BufferedWriter EW;
    private static BufferedWriter CW;
    private static BufferedWriter RW;
    private static BufferedWriter PW;
    private static BufferedWriter FW;

    /*
    Main method for running the simulation once over a month long period
     */
    public static void main(String[] args) throws IOException {
        //Read in information from config file
        ArrayList<ArrayList<ArrayList<ArrayList<ArrayList<ArrayList<ArrayList<ArrayList>>>>>>> bigDeal = loadConfig();
        //Track progress of the simulation
        long startTime = System.currentTimeMillis();
        //Create ClockWork Module
        c = new ClockWork(bigDeal);

        //Create directories to handle output
        String path = System.getProperty("user.dir");
        File outPut = new File(path + "/outPutData/");
        outPut.mkdir();
        File ED = new File(path + "/outPutData/E");
        ED.mkdir();
        File CD = new File(path + "/outPutData/C");
        CD.mkdir();
        File RD = new File(path + "/outPutData/R");
        RD.mkdir();
        File PD = new File(path + "/outPutData/P");
        PD.mkdir();
        File FD = new File(path + "/outPutData/F");
        FD.mkdir();
        //Choose which version of simulation to run
        if (select == 0) {
            E = "EM";
            EW = new BufferedWriter(new FileWriter(E,true));
            C = "CM";
            CW = new BufferedWriter(new FileWriter(C,true));
            R = "RM";
            RW = new BufferedWriter(new FileWriter(R,true));
            F = "FM";
            FW = new BufferedWriter(new FileWriter(F,true));
            P = "PM";
            PW = new BufferedWriter(new FileWriter(P,true));
            c.motion(chunks[chunk]);
        } else if (select == 1) {
            E = "ENM";
            EW = new BufferedWriter(new FileWriter(E,true));
            C = "CNM";
            CW = new BufferedWriter(new FileWriter(C,true));
            R = "RNM";
            RW = new BufferedWriter(new FileWriter(R,true));
            F = "FNM";
            FW = new BufferedWriter(new FileWriter(F,true));
            P = "PNM";
            PW = new BufferedWriter(new FileWriter(P,true));
            c.lessMotion(chunks[chunk]);
        }
        //Collect statistical dump
        Stats s = new Stats(c);
        System.out.println(s.results());
        //Total execution time
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        EW.close();
        CW.close();
        RW.close();
        PW.close();
        FW.close();
        System.out.println(elapsedTime);
    }

    public static void append(String energy, String cost, String rev, String perf, String fail) throws IOException{
        ArrayList<ArrayList<Double>> regionData = new ArrayList<>();
        ArrayList<ArrayList<Double>> regionJobPerf = new ArrayList<>();
        for (Interconnection I : c.getPowerGrid()) {
            for (IsoRegion i : I.getIsoRegions()) {
                regionData.add(i.compileMStats());
                regionJobPerf.add(i.compileJStats());
            }
        }
        File test = new File(energy);
        if (!test.exists()) {
            EW.close();
            CW.close();
            RW.close();
            FW.close();
            PW.close();
            EW = new BufferedWriter(new FileWriter(energy,true));
            CW = new BufferedWriter(new FileWriter(cost,true));
            RW = new BufferedWriter(new FileWriter(rev,true));
            FW = new BufferedWriter(new FileWriter(perf,true));
            PW = new BufferedWriter(new FileWriter(fail,true));
        }
        ArrayList<String> write = gridLock(regionData);
        ArrayList<String> jobs = jobLock(regionJobPerf);
        String e = write.get(0) + "\n";
        String c = write.get(1) + "\n";
        String r = write.get(2) + "\n";
        String p = jobs.get(0) + "\n";
        String f = jobs.get(1) + "\n";
        EW.write(e);
        CW.write(c);
        RW.write(r);
        PW.write(p);
        FW.write(f);
    }

    /*
    Take 2d Arraylist of values and aggregate into a 1d arraylist of string values
     */
    public static ArrayList<String> gridLock(ArrayList<ArrayList<Double>> data) {
        ArrayList<String> holders = new ArrayList<>();
        ///Create holder
        Double E = 0.0;
        Double C = 0.0;
        Double R = 0.0;
        for (int i = 0; i < data.size(); i++) {
            for (int j = 0; j < data.get(i).size(); j++) {
                if (j % 3 == 0) {
                    E += data.get(i).get(j);
                } else if (j % 3 == 1) {
                    C += data.get(i).get(j);
                } else {
                    R += data.get(i).get(j);
                }
            }
        }
        E = E / data.size();
        C = C / data.size();
        R = R / data.size();
        holders.add(E.toString());
        holders.add(C.toString());
        holders.add(R.toString());
        return holders;
    }

    public static ArrayList<String> jobLock(ArrayList<ArrayList<Double>> data) {
        ArrayList<String> holders = new ArrayList<>();
        Double T =  0.0;
        Double F = 0.0;
        for (int i = 0; i < data.size(); i++) {
            for (int j = 0; j < data.get(i).size(); j++) {
                if (j % 2 == 0) {
                    T += data.get(i).get(j);
                } else{
                    F += data.get(i).get(j);
                }
            }
        }
        T = T / (double)data.size();
        F = F / (double)data.size();
        holders.add(T.toString());
        holders.add(F.toString());
        return holders;
    }

    /*
    Read config file
    Change config file name to select chosen config file
     */
    public static ArrayList<ArrayList<ArrayList<ArrayList<ArrayList<ArrayList<ArrayList<ArrayList>>>>>>> loadConfig() {
        //CONFIG FILE NAME
        String configFileName = "/config/AccurateDist";
        String path = System.getProperty("user.dir") + configFileName;
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
                if (line.contains("//ARRIVAL RATE")) {
                    line = inputFile.nextLine();
                    if (line.contains("Arrival Rate:")) {
                        String[] hold = line.split(": ");
                        if (!hold[1].equals("null")) {
                            lambda = Double.parseDouble(hold[1]);
                            set = true;
                        } else {
                            lambda = 0;
                        }
                    } else {
                        lambda = 0;
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