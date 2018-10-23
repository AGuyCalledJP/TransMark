import java.util.ArrayList;
import java.util.Comparator;

class ListComparator implements Comparator<ArrayList<Double>>{
    public int compare(ArrayList<Double> a1, ArrayList<Double> a2) {
        if (a1.get(1) < a2.get(1))
            return 1;
        else if (a1.get(1) > a2.get(1))
            return -1;
        return 0;
    }
}