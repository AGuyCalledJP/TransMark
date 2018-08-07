import java.util.ArrayList;

public class MarketHistory {
    private ArrayList<ArrayList<Job>> transactions;
    private ArrayList<Integer> home;
    public MarketHistory() {
        transactions = new ArrayList<>();
        home = new ArrayList<>();
    }

    public void addTransaction(ArrayList<Job> transact, int here) {
        transactions.add(transact);
        home.add(here);
    }

    public double failPer() {
        double total = 0;
        double failed = 0;
        for (ArrayList<Job> a : transactions) {
            for (Job j : a) {
                total++;
                if (j.isFailed()) {
                    failed++;
                }
            }
        }
        if (total > 0) {
            return (failed / total);
        }
        else {
            return 0;
        }
    }
}
