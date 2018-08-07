import java.util.*;

public class Market {
    ArrayList<DataCenter> sellers = new ArrayList<>();
    private double transferCost = 0.03;

    public Market() {}

    public void addSeller(DataCenter d) {
        if (!sellers.contains(d)) {
            sellers.add(d);
        }
    }

    public void removeSeller(DataCenter d) {
        if (sellers.contains(d)) {
            sellers.remove(d);
        }
    }

    public void silkRoad(DataCenter D) {
        int cpu = 0;
        int ram = 1;
        int disk = 2;
        int cost = 3;
        if (sellers.size() != 0) {
            ArrayList<Double> offload = D.getOffLoad();
            int[] where = new int[sellers.size()];
            double[] diff = new double[sellers.size()];
            int index = 0;
            boolean commit = false;
            for (DataCenter S : sellers) {
                ArrayList<Double> avail = S.getOnLoad();
                if (avail.size() > 0) {
//                    System.out.println("Avail: " + avail);
//                    System.out.println("Offload: " + offload);
//                    System.out.println(D.projectedRevenue());
                    double weight = 0;
                    if (avail.get(cpu) > offload.get(cpu)) {
                        weight = avail.get(cpu) - offload.get(cpu);
                        weight = ((avail.get(cpu) - weight) / avail.get(cpu));
                    } else {
                        weight = offload.get(cpu) - avail.get(cpu);
                        weight = ((offload.get(cpu) - weight) / offload.get(cpu));
                    }
                    double comp = offload.get(cost) * weight;
                    if (comp > avail.get(cost)) {
                        diff[index] = comp - avail.get(cost);
                        where[index] = index;
                        commit = true;
                    }
                }
                index++;
            }
//            System.out.println("Where");
//            for (int d : where) {
//                System.out.println(d);
//            }
//            System.out.println("diff");
//            for (double e : diff) {
//                System.out.println(e);
//            }
//            System.out.println(commit);
            if (commit) {
                exchange(D, where, diff);
            }
        }
    }

    public void exchange(DataCenter D, int[] where, double[] diff) {
        int i = 0;
        ArrayList<Integer> send = new ArrayList<>();
        ArrayList<Double> offload = D.getOffLoad();
//        System.out.println("offload: " + offload);
        while (offload.get(0) > 0 && offload.get(1) > 0 && offload.get(2) > 0 && i < where.length) {
            boolean hit = false;
            double max = 0;
            int indMax = 0;
            for (int k = 0; k < (diff.length - 1); k++) {
                double j = diff[k];
                if (j > max) {
                    max = j;
                    indMax = k;
                    hit = true;
                }
            }
            if (hit) {
                send.add(indMax);
                offload.set(0, (offload.get(0) - sellers.get(where[indMax]).getOnLoad().get(0)));
                offload.set(1, (offload.get(1) - sellers.get(where[indMax]).getOnLoad().get(1)));
                offload.set(2, (offload.get(2) - sellers.get(where[indMax]).getOnLoad().get(2)));
                diff[indMax] = 0;
//                System.out.println("after commit: " + offload);
            }
            i++;
        }
//        System.out.println("options");
//        for (int d : where) {
//            System.out.println(d);
//        }
//        System.out.println(send);
//        System.out.println(offload);
        completeTheTrade(D, send);
    }

    public void completeTheTrade(DataCenter D, ArrayList<Integer> send) {
        ArrayList<Job> jobs = D.getInProgress();
        int[] available = D.getMu();
        int jindex = 0;
//        for (int i : available) {
//            System.out.println(i);
//        }
        for (int where : send) {
            boolean moreRoom = true;
            DataCenter S = sellers.get(where);
//            System.out.println(check(available));
//            System.out.println(jindex < available.length);
//            System.out.println(moreRoom);
            ArrayList<Job> sendEr = new ArrayList<>();
            while (check(available) && jindex < (available.length - 1) && jindex < (jobs.size()) && moreRoom) {
//                System.out.println("aqui?");
                if (available[jindex] == 1) {
//                    System.out.println("What about here");
                    if (S.canTake(jobs.get(jindex))) {
                        //Split revenue
                        int start = jobs.get(jindex).getEstCompleteionTime();
                        int end = jobs.get(jindex).timeLeft();
                        double split = jobs.get(jindex).getRevenue();
                        double even = (double)(end / start);
                        double sen = (jobs.get(jindex).getRevenue() * even);
                        double stay = jobs.get(jindex).getRevenue() - sen;
                        D.addRev(stay);
                        jobs.get(jindex).setRevenue(sen);
                        sendEr.add(jobs.get(jindex));
//                        S.transfer(jobs.get(jindex));
                        jobs.get(jindex).setTransfered();
                        D.quaretine(jobs.get(jindex).getId());

                        //Calculate time penalty
                        jobs.get(jindex).migPenalty(D.getBandwidth(), S.getBandwidth());

                        //Calculate cost to transfer
                        double size = jobs.get(jindex).getTotalSize();
                        D.addRev((size * - transferCost));
                    }
                    else {
                        moreRoom = false;
                    }
                }
                jindex++;
            }
            S.transfer(sendEr, D.getId());
            S.reasonableIndulgence();
        }
    }

    public boolean check(int[] check) {
        boolean fin = false;
        for (int i : check) {
            if (i == 1) {
                fin = true;
            }
        }
        return fin;
    }
}
