import java.util.Random;
import java.util.ArrayList;

//Transmission State Operator
public class TSO {
    private int time;
    private int id;
    private StateVal stateServed;
    private int maxPriceToday;
    private Calendar cal = new Calendar();
    private Random rand = new Random();

    public TSO(ArrayList<DataCenter> clients, int time, int id, StateVal stateServed){
        this.time = time;
        this.id = id;
        this.stateServed = stateServed;
        maxPriceToday = 50; //How do i determine this value/
    }

    public double energyPrice() {
        int globalTime = cal.getHour(ClockWork.t);
        int localTime = (((globalTime +  time) % 24) + 24) % 24; //convert whatever time it is locally to UTC
        int localMin = ClockWork.t % 60;
        return detP(localTime, localMin);
    }

    public double detP(int localT, int localM) {
        double scale = 1.0;
        double mins = 60;
        if (localT < 8 || localT >= 20 ) {
            localT = (((localT - 12) % 12) + 12) % 12;
        }
        if (localT > 8 && localT < 20) {
            scale = (scale / 5);
            double minScale = (scale / mins);
            double mult = 0;
            for (int j = 0; j < localT; j++) {
                mult += scale;
            }
            for (int k = 0; k < localM; k++) {
                mult += minScale;
            }
            return (maxPriceToday * mult);
        }
        else if (localT == 8) {
            return maxPriceToday;
        }
        else {
            scale = (scale / 9);
            double minScale = (scale / mins);
            double mult = 0;
            for (int i = 0; i < localT; i++) {
                mult += scale;
            }
            for (int l = 0; l < localM; l++) {
                mult += minScale;
            }
            mult = 1 - mult;
            return  (maxPriceToday * mult);
        }
    }

    public StateVal getStateServed() {
        return stateServed;
    }

    public String toString(){
        return  "";
    }
}
