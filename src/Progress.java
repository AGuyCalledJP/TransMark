import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

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
       c.motion();
       Stats s = new Stats(c);
       System.out.println(s.results());
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println(elapsedTime);
        try {
            write(c.dataDump(), "outPutDataM");
        }
        catch (IOException writeError){
            System.out.println("Unable to write");
        }
        try {
            write(c.centerDump(), "centersInvolvedM");
        }
        catch (IOException writeError){
            System.out.println("Unable to write");
        }
    }

    public static void write(String str, String name)
      throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(name));
        writer.write(str);
        writer.close();
        }
}
