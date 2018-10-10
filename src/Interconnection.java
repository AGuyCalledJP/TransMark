import java.util.ArrayList;

public class Interconnection {
    IntCon con;
    String name;
    int time;
    StateVal[][] served;
    ArrayList<IsoRegion> isoRegions = new ArrayList<>();

    public Interconnection(IntCon con, String name, int time, ArrayList<ArrayList<ArrayList<ArrayList<ArrayList<ArrayList>>>>> theWorld){ //, ArrayList theWorld
        this.con = con;
        this.name = name;
        this.time = time;
         IntConIsos i = new IntConIsos(con);
         ISOVal[] isos = i.detISO();
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

    public ArrayList<IsoRegion> getPowerRegions() {
        return isoRegions;
    }
}
