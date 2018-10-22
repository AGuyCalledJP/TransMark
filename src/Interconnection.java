import java.util.ArrayList;

public class Interconnection {
    IntCon con;
    String name;
    int time;
    //All regions contained within this Interconnection
    ArrayList<IsoRegion> isoRegions = new ArrayList<>();

    /*
    Interconnections are modeled after their name sake in the nations power grid. These are large networks of states that are serviced by a group of RTOs and ISOs.
    This clustering of ISOs is held in conjunction with all of the states that are contained in each of these major regions.
     */
    public Interconnection(IntCon con, String name, int time, ArrayList<ArrayList<ArrayList<ArrayList<ArrayList<ArrayList>>>>> theWorld){ //, ArrayList theWorld
        this.con = con;
        this.name = name;
        this.time = time;
        //Get all ISOs associated with the given interconnection
        IntConIsos i = new IntConIsos(con);
        ISOVal[] isos = i.detISO();
        //Create ISO regions that associate each ISO with the states they service
        for (int k = 0; k < theWorld.get(0).size(); k++) {
            String locale = theWorld.get(0).get(k).get(0).get(0).get(0).get(0).toString();
            for(ISOVal j : isos){
                if (j.toString().equals(locale)) {
                 isoRegions.add(new IsoRegion(j, theWorld.get(0).get(k).get(1)));
                }
            }
        }
    }

    public String toString(){
        int tot = 0;
        String str = "";
        str += "The " + name + " oversees the following States: " + "\n";
        for(int i = 0; i < isoRegions.size(); i++){
            for (int j = 0; j < isoRegions.get(i).getStates().size(); j++) {
                str += isoRegions.get(i).getStates().get(j).getStateName() + ", ";
                tot += isoRegions.get(i).getStates().get(j).getNumClients();
            }
        }
        str += "containing " + tot + " Data Centers. \n";
        for (IsoRegion p : isoRegions) {
            for(State s : p.getStates()){
                int key = 1;
                for(DataCenter d : s.getClientele()){
                    str += "DataCenter " + key + " In State " + s.getStateName() + ":\n" + d + "\n";
                    key++;
                }
            }
        }
        return str;
    }

    public ArrayList<IsoRegion> getIsoRegions() {
        return isoRegions;
    }
}
