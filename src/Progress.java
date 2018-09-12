import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Progress {
    public static int idCell = 1;
    public static int idCluster = 1;
    public static int idDataCenter = 1;
    public static int idJob = 1;
    public static int idMachine = 1;
    public static int idTask = 1;
    public static int idTSO = 1;
    public static int standardSpeed = 1200;
    public static int standardIdle = 161;
    public static int standardMax = 230;
    public static int standardNumCores = 10;
    public static int standardNumCells = 5;
    public static double maxInternetSpeed = 2; //2 gb per second internet
    public static double halfInternetSpeed = 1; //2 gb per second internet
    public static double quarterInternetSpeed = .5; //2 gb per second internet
    public final static double EPSILON = .000001;

    public static void main(String[]args){
        long startTime = System.currentTimeMillis();
       ClockWork c = new ClockWork();
       int select = 1;
       if (select == 0) {
           c.motion();
       }
       else if (select == 1) {
           c.lessMotion();
       }
       else if (select == 2) {
           c.VIP();
       }
       else {
           c.ultimatum();
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
}
