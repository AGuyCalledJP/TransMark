import java.lang.reflect.Array;
import java.util.ArrayList;
/*
IsoRegion exists to associate a major Regional Transmissions Operator (ISO), with the set of states that receive energy
rate data from said ISO.
 */
public class IsoRegion {
    //Enum val for the ISO in control of this territory
    private ISOVal iso;
    //Instance of ISO class generated with respect to ISOVal
    private ISO authority;
    //All states served by the enumerated authority
    private ArrayList<State> states = new ArrayList<>();

    public IsoRegion(ISOVal iso, ArrayList<ArrayList<ArrayList>> theWorld) {
        //Receive and create the associated ISO for this region
        this.iso = iso;
        authority = new ISO(iso);
        //Get list of all states serviced by said ISO
        IsoRegions i = new IsoRegions(iso);
        StateVal[] stateVals = i.region();
        //Create instances of States, based off list of state vals
        for (int k = 0; k < theWorld.size(); k++) {
            String s =theWorld.get(k).get(0).get(0).toString();
            s = s.replace("[","");
            s = s.replace("]","");
            for (StateVal S : stateVals) {
                if (s.equals(S.toString())) {
                    states.add(new State(S,theWorld.get(k)));
                }
            }
        }
    }

    //Get all states in region
    public ArrayList<State> getStates() {
        return states;
    }

    //Get enum rep of ISO in area
    public ISOVal getIso() {
        return iso;
    }

    //Retrieve ISO Object
    public ISO getAuthority() {
        return authority;
    }

    //Compile usage stats for all centers across the region
    public ArrayList<ArrayList<Double>> compileMStats() {
        ArrayList<ArrayList<Double>> holster = new ArrayList<>();
        for (State S : states) {
            for (DataCenter d : S.getClientele()) {
                holster.add(d.getPriceLog());
                holster.add(d.getEnergyLog());
                holster.add(d.getTRevenueLog());
            }
        }
        return holster;
    }

    public ArrayList<ArrayList<Double>> compileTStats() {
        ArrayList<ArrayList<Double>> holster = new ArrayList<>();
        for (State S : states) {
            for (DataCenter d : S.getClientele()) {
                holster.add(d.getTPriceLog());
                holster.add(d.getTEnergyLog());
                holster.add(d.getRevenueLog());
            }
        }
        return holster;
    }

    public ArrayList<ArrayList<Double>> compileJStats() {
        ArrayList<ArrayList<Double>> holster = new ArrayList<>();
        for (State S : states) {
            for (DataCenter d : S.getClientele()) {
                holster.add(d.getJobThroughput());
                holster.add(d.getFailureLog());
            }
        }
        return holster;
    }
}
