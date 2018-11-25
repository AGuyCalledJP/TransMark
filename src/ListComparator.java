import java.util.ArrayList;
import java.util.Comparator;
/*
Comparator used to sort a 2D ArrayList of doubles. Used to assoicate a value with a specific value in a given list
@author Jared Polonitza
 */
class ListComparator implements Comparator<ArrayList<Double>>{
    public int compare(ArrayList<Double> a1, ArrayList<Double> a2) {
        if (a1.get(1) < a2.get(1))
            return 1;
        else if (a1.get(1) > a2.get(1))
            return -1;
        return 0;
    }
}