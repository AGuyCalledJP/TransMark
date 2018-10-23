import java.util.Comparator;

class JobComparator implements Comparator<Job>{
    public int compare(Job j1, Job j2) {
        if (j1.getTimeSensitive() < j2.getTimeSensitive())
            return 1;
        else if (j1.getTimeSensitive() > j2.getTimeSensitive())
            return -1;
        return 0;
    }
}
