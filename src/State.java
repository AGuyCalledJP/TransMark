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
    public State(StateVal s){
        this.s = s;
        //Set timezone info
        DetZone dz = new DetZone(s);
        zone = dz.detZone();
        DetTime dt = new DetTime(zone);
        localTime = dt.convTime(); //subtract this value from time in hours to get the time zone
        Random rand = new Random();
        numClients = 1;//rand.nextInt(10) + 1; //set bound for number of data centers in a juridction
        for(int j = 0; j < numClients; j++){
            int numClust = rand.nextInt(5) + 1;
            DataCenter D = new DataCenter(Progress.idDataCenter, numClust);
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
