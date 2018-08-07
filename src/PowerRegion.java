import java.util.ArrayList;
import java.util.Random;

public class PowerRegion {
    private ArrayList<State> states = new ArrayList<>();
    private ArrayList<TSO> authorities = new ArrayList<>();

    public PowerRegion(StateVal[] clients) {
        for (StateVal v : clients) {
            states.add(new State(v));
        }
        for (State s : states) {
            authorities.add(new TSO(s.getClientele(), s.getLocalTime(), Progress.idTSO, s.getStateName()));
        }
    }

    public ArrayList<State> getStates() {
        return states;
    }

    public ArrayList<TSO> getAuthorities() {
        return authorities;
    }

    public String toString(){
        return "working on it";
    }
}
