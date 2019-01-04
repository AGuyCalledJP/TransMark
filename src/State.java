import java.util.ArrayList;
/*
States within the simulation act as the link between timezones, ISOs, and Data Centerw. States take a state value (the name of the states)
and create an environment in which to house data centers. From this hub centers can interact with eachother, centers in other states, ISOs,
and the market.
@author Jared Polonitza
 */
public class State {
    //Enum value of State
    private StateVal s;
    //Enum value of time zone
    TimeZone zone;
    //local time in minutes
    int localTime;
    //List of all enclosed Data Centers
    private ArrayList<DataCenter> clientele = new ArrayList<>();
    //Total energy usage accrued by this state
    private double totalEnergy = 0;

    public State(StateVal s, ArrayList<ArrayList> theWorld){
        this.s = s;
        //Set timezone info
        DetZone dz = new DetZone(s);
        zone = dz.detZone();
        DetTime dt = new DetTime(zone);
        localTime = dt.convTime();
        /*
        Create n Data Center objects. Constructor 1 creates a Data Center based off a non-determined Lambda arrival rate.
        Constructor 2 creates a Data Center based off a pre-calculated lambda arrival rate.
         */
        for(int j = 1; j < theWorld.size(); j++){
            ArrayList center = theWorld.get(j);
            String a = center.get(0).toString();
            a = a.replace("[","");
            a = a.replace("]","");
            String[] split = a.split(", ");
            DataCenter D;
            if (Progress.set) {
                D = new DataCenter(Progress.idDataCenter, Integer.parseInt(split[0]), Integer.parseInt(split[1]), Double.parseDouble(split[2]), Integer.parseInt(split[3]), Progress.lambda, theWorld.get(j));
            }
            else {
                D = new DataCenter(Progress.idDataCenter, Integer.parseInt(split[0]), Integer.parseInt(split[1]), Double.parseDouble(split[2]), Integer.parseInt(split[3]), theWorld.get(j));
            }
            Progress.idDataCenter++;
            clientele.add(D);
        }
    }

    //Getters & Setters
    public int getLocalTime() {
        return localTime;
    }

    public void moveLocalTime() {localTime = localTime + 1;}

    public StateVal getStateName(){
        return s;
    }

    public void setTotalEnergy(double add) {
        totalEnergy += add;
    }

    public double getTotalEnergy() {
        return totalEnergy;
    }

    public int zone() {
        return new DetTime(zone).convTime();
    }

    public ArrayList<DataCenter> getClientele(){
        return clientele;
    }

}
