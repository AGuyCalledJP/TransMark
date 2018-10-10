import javax.swing.plaf.TableHeaderUI;
import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.Random;

//class that holds TSO objects and data centers, allowing their interaction.
public class State {

    private StateVal s;
    TimeZone zone;
    int localTime;
    private int numClients;
    private ArrayList<DataCenter> clientele = new ArrayList<>();
    private JobMaster jobMaster;
    private Weather weather;
    private double totalEnergy = 0;
    //constructor to generate random price signals
    public State(StateVal s, ArrayList<ArrayList> theWorld){
        this.s = s;
        //Set timezone info
        DetZone dz = new DetZone(s);
        zone = dz.detZone();
        DetTime dt = new DetTime(zone);
        localTime = dt.convTime(); //subtract this value from time in hours to get the time zone
        for(int j = 1; j < theWorld.size(); j++){
            ArrayList center = theWorld.get(j);
            String a = center.get(0).toString();
            a = a.replace("[","");
            a = a.replace("]","");
            String[] split = a.split(", ");
            DataCenter D;
            if (!split[4].equals("null")) {
                D = new DataCenter(Progress.idDataCenter, Integer.parseInt(split[0]), Integer.parseInt(split[1]), Double.parseDouble(split[2]), Integer.parseInt(split[3]), Double.parseDouble(split[4]), theWorld.get(j));
            }
            else {
                D = new DataCenter(Progress.idDataCenter, Integer.parseInt(split[0]), Integer.parseInt(split[1]), Double.parseDouble(split[2]), Integer.parseInt(split[3]), theWorld.get(j));
            }
            Progress.idDataCenter++;
            clientele.add(D);
        }
        weather = new Weather(s);
    }

    //Getters
    public int getNumClients(){
        return numClients;
    }

    public int getLocalTime() {
        return localTime;
    }

    public void moveLocalTime() {localTime = localTime + 1;}

    public Weather getWeather() {
        return weather;
    }

    public StateVal getStateName(){
        return s;
    }

    public void setTotalEnergy(double add) {
        totalEnergy += add;
    }

    public double getTotalEnergy() {
        return totalEnergy;
    }

    public ArrayList<DataCenter> getClientele(){
        return clientele;
    }

    public JobMaster getJobMaster(){
        return jobMaster;
    }

    public String toString(){
        String str = s + " contains " + numClients + " Data Centers: " + "\n";
        return str;
    }

}
