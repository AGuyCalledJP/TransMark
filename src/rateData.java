import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;

public class rateData {
    private ISOVal iso;
    public rateData(ISOVal iso) {
        this.iso = iso;
    }

   public double[] genData(int month){
        // Open the file.
       String path = System.getProperty("user.dir") + "/" + iso + "DATA/";
       String[] cat = new String[] {"JAN" , "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};
       double[] data;
       if (month == 0 || month == 2 || month == 4 || month == 6 || month == 7 || month == 9 || month == 11) {
           data = new double[744];
       }
       else if (month == 3 || month == 5 || month == 8 || month == 10) {
           data = new double[720];
       }
       else {
           data = new double[672];
       }
       int index = 0;
        File file = new File(path + iso + cat[month]);
        try {
            Scanner inputFile = new Scanner(file);
            while (inputFile.hasNextDouble() && index < data.length - 1) {
                data[index] = inputFile.nextDouble();
                index++;
            }
            inputFile.close();
        }
        catch (FileNotFoundException message) {
            System.out.println(message);
        }
       return data;
    }
}
