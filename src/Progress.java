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
       int select = 0;
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
        if (select == 0) {
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
        else if (select == 1) {
            try {
                write(c.dataDump(), "outPutDataNM");
            }
            catch (IOException writeError){
                System.out.println("Unable to write");
            }
            try {
                write(c.centerDump(), "centersInvolvedNM");
            }
            catch (IOException writeError){
                System.out.println("Unable to write");
            }
        }
        else if (select == 2) {
            try {
                write(c.dataDump(), "outPutDataC");
            }
            catch (IOException writeError){
                System.out.println("Unable to write");
            }
            try {
                write(c.centerDump(), "centersInvolvedC");
            }
            catch (IOException writeError){
                System.out.println("Unable to write");
            }
        }
        else {
            try {
                write(c.dataDump(), "outPutDataU");
            }
            catch (IOException writeError){
                System.out.println("Unable to write");
            }
            try {
                write(c.centerDump(), "centersInvolvedU");
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
}
