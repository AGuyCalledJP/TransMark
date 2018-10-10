import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class YearProgress {
    public static void main(String[]args){
        long startTime = System.currentTimeMillis();
        ArrayList<ArrayList<ArrayList<ArrayList<ArrayList<ArrayList<ArrayList<ArrayList>>>>>>> bigDeal = loadConfig();
        ClockWork c = new ClockWork(bigDeal);
        int select = 1;
        int year = 525600;
        if (select == 0) {
            c.motion(year);
        }
        else if (select == 1) {
            c.lessMotion(year);
        }
        else if (select == 2) {
            c.VIP(year);
        }
        else {
            c.minimalist(year);
        }
        Stats s = new Stats(c);
        System.out.println(s.results());
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println(elapsedTime);
        ArrayList<ArrayList<ArrayList<Double>>> regionData = new ArrayList<>();
        ArrayList<ISOVal> regions = new ArrayList<>();
        for (Interconnection I : c.getPowerGrid()) {
            for (IsoRegion i : I.getIsoRegions()) {
                regionData.add(i.compileStats());
                regions.add(i.getAuthority().getIso());
            }
        }
        ArrayList<String> yump = gridLock(regionData);
        if (select == 0) {
            for (String str : yump) {
                try {
                    write(str, "outPutDataMH");
                } catch (IOException writeError) {
                    System.out.println("Unable to write");
                }
            }
            for (ISOVal v : regions) {
                try {
                    write(v.toString(), "RegionMH");
                } catch (IOException writeError) {
                    System.out.println("Unable to write");
                }
            }
        }
        else if (select == 1) {
            for (String str : yump) {
                try {
                    write(str, "outPutDataMH");
                } catch (IOException writeError) {
                    System.out.println("Unable to write");
                }
            }
            for (ISOVal v : regions) {
                try {
                    write(v.toString(), "RegionMH");
                } catch (IOException writeError) {
                    System.out.println("Unable to write");
                }
            }
        }
        else if (select == 2) {
            for (String str : yump) {
                try {
                    write(str, "outPutDataMH");
                } catch (IOException writeError) {
                    System.out.println("Unable to write");
                }
            }
            for (ISOVal v : regions) {
                try {
                    write(v.toString(), "RegionMH");
                } catch (IOException writeError) {
                    System.out.println("Unable to write");
                }
            }
        }
        else {
            for (String str : yump) {
                try {
                    write(str, "outPutDataMH");
                } catch (IOException writeError) {
                    System.out.println("Unable to write");
                }
            }
            for (ISOVal v : regions) {
                try {
                    write(v.toString(), "RegionMH");
                } catch (IOException writeError) {
                    System.out.println("Unable to write");
                }
            }
        }
    }

    public static ArrayList<String> gridLock(ArrayList<ArrayList<ArrayList<Double>>> data) {
        ArrayList<String> holders = new ArrayList<>();
        for (int i = 0; i < data.size() - 1; i++) {
            int winner = 0;
            for (int j = 0; j < data.get(i).size(); j++) {
                if (data.get(i).get(j).size() > winner) {
                    winner = data.get(i).get(j).size();
                }
            }
//            System.out.println("winner: " + winner);
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
                        double total = E[k] + data.get(i).get(j).get(k);
                        E[k] = total;
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
//        System.out.println(E.length);
//        System.out.println(C.length);
//        System.out.println(R.length);
            String str = "";
            str += Arrays.toString(E) + "\n";
            str += Arrays.toString(C) + "\n";
            str += Arrays.toString(R) + "\n";
            holders.add(str);
        }
        return holders;
    }

    public static void write(String str, String name)
            throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(name));
        writer.write(str);
        writer.close();
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
