import java.util.ArrayList;

public class IsoRegion {
    private ISOVal iso;
    private ISO authority;
    private ArrayList<State> states = new ArrayList<>();

    public IsoRegion(ISOVal iso) {
        this.iso = iso;
        authority = new ISO(iso);
        IsoRegions i = new IsoRegions(iso);
        StateVal[] stateVals = i.region();
        for (StateVal s : stateVals) {
            states.add(new State(s));
        }
    }

    public ArrayList<State> getStates() {
        return states;
    }

    public ISO getAuthority() {
        return authority;
    }

    public void giveRate() {
        for (State s : states) {
            for (DataCenter d : s.getClientele()) {
                d.setRate(authority.getRate());
            }
        }
    }

    public ArrayList<ArrayList<Double>> compileStats() {
        ArrayList<ArrayList<Double>> holster = new ArrayList<>();
        for (State S : states) {
            for (DataCenter d : S.getClientele()) {
                holster.add(d.getPriceLog());
                holster.add(d.getEnergyLog());
                holster.add(d.getRevenueLog());
            }
        }
        return holster;
    }

    public String toString(){
        return "working on it";
    }
}
