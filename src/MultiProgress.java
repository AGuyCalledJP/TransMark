import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class MultiProgress {

    public static void main(String[]args){
        ArrayList<ArrayList<ArrayList<Double>>> average = new ArrayList<>();
        int avg = 5;
        long startTime = System.currentTimeMillis();
        ArrayList<ArrayList<ArrayList<ArrayList<ArrayList<ArrayList<ArrayList<ArrayList>>>>>>> bigDeal = loadConfig();
        ClockWork c = new ClockWork(bigDeal);
        int select = 2;
        int month = 44640;
        int year = 525600;
        for (int i = 0; i < avg; i++) {
            if (select == 0) {
                c.motion(month);
            } else if (select == 1) {
                c.lessMotion(month);
            }
// else if (select == 2) {
//                c.VIP(month);
//            }
//            else {
//                c.minimalist(month);
//            }
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

    public static ArrayList<ArrayList<ArrayList<ArrayList<ArrayList<ArrayList<ArrayList<ArrayList>>>>>>> loadConfig() {
        String path = System.getProperty("user.dir") + "/config/exampleConfig";
        File file = new File(path);
        String save = "";
        ArrayList<ArrayList<ArrayList<ArrayList<ArrayList<ArrayList<ArrayList<ArrayList>>>>>>> bigDeal = new ArrayList<>();
        try {
            Scanner inputFile = new Scanner(file);
            while (inputFile.hasNextLine()) {
                String line = inputFile.nextLine();
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
                                                ArrayList<Integer> specs = new ArrayList<>();
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
                                                        if (inputFile.nextLine().equals("//CELL")) {
                                                            boolean cells = true;
                                                            while (cells) {
                                                                ArrayList<Integer> cellSpecs = new ArrayList<>();
                                                                String cellSpec = inputFile.nextLine();
                                                                if (cellSpec.contains("Speed:")) {
                                                                    String[] hold = cellSpec.split(": ");
                                                                    cellSpecs.add(Integer.parseInt(hold[1]));
                                                                }
                                                                cellSpec = inputFile.nextLine();
                                                                if (cellSpec.contains("Number of Cores:")) {
                                                                    String[] hold = cellSpec.split(": ");
                                                                    cellSpecs.add(Integer.parseInt(hold[1]));
                                                                }
                                                                cellSpec = inputFile.nextLine();
                                                                if (cellSpec.contains("Idle Power Consumption:")) {
                                                                    String[] hold = cellSpec.split(": ");
                                                                    cellSpecs.add(Integer.parseInt(hold[1]));
                                                                }
                                                                cellSpec = inputFile.nextLine();
                                                                if (cellSpec.contains("Max Power Consumption:")) {
                                                                    String[] hold = cellSpec.split(": ");
                                                                    cellSpecs.add(Integer.parseInt(hold[1]));
                                                                }
                                                                save = inputFile.nextLine();
                                                                if (!save.equals("//CELL")) {
                                                                    cells = false;
                                                                    clusterSpecs.add(cellSpecs);
                                                                } else {
                                                                    clusterSpecs.add(cellSpecs);
                                                                }
                                                            }
                                                        }
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
