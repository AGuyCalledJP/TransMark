import java.util.*;
/*
Agent that simulates a free market exchange grounds for data centers to transact jobs and free space for profit according
to adjusted rates
 */
public class Market {
    //List of centers currently looking to buy jobs
    ArrayList<DataCenter> buyers = new ArrayList<>();

    //Cost incurred for transfering a job to a new center
    private double transferCost = 0.03;

    public Market() {}

    //Add a new center to the list of buyers
    public void addBuyer(DataCenter d) {
        if (!buyers.contains(d)) {
            buyers.add(d);
        }
    }

    //Remove center from list of buyers
    public void removeBuyer(DataCenter d) {
        if (buyers.contains(d)) {
            buyers.remove(d);
        }
    }

    /*
    Begin the process of trading jobs, starting by finding the largest difference of operating costs between the seller
    and available buyers
     */
    public void silkRoad(DataCenter D) {
        int cpu = 0;
        int ram = 1;
        int disk = 2;
        int cost = 3;
        if (buyers.size() != 0) {
            //Get offload tuple
            ArrayList<Double> offload = D.getOffLoad();
            //Make difference of operating cost between seller and buyers
            ArrayList<ArrayList<Double>> diff = new ArrayList<>();
            //Which seller am I looking at
            int index = 0;
            //Did I find any deals
            boolean commit = false;
            for (DataCenter S : buyers) {
                //Get availability tuple
                ArrayList<Double> avail = S.getOnLoad();
                if (avail.size() > 0) {
                    //Calculate offload and onload percentages
                    double weight = 0;
                    if (avail.get(cpu) > offload.get(cpu)) {
                        weight = avail.get(cpu) - offload.get(cpu);
                        weight = ((avail.get(cpu) - weight) / avail.get(cpu));
                    } else {
                        weight = offload.get(cpu) - avail.get(cpu);
                        weight = ((offload.get(cpu) - weight) / offload.get(cpu));
                    }
                    double comp = offload.get(cost) * weight;
                    double cpuComp = offload.get(cpu) * weight;
                    double buyComp = avail.get(cpu) / cpuComp;
                    //Compare cost ratio
                    if (comp > avail.get(cost) * buyComp) {
                        double d = comp - (avail.get(cost) * buyComp);
                        double w = (double)index;
                        ArrayList<Double> make = new ArrayList<>();
                        make.add(w);
                        make.add(d);
                        diff.add(make);
                        commit = true;
                    }
                }
                index++;
            }
            //Who was the winner
            if (commit) {
                exchange(D, diff);
            }
        }
    }

    public void exchange(DataCenter D, ArrayList<ArrayList<Double>> diff) {
        //Where am I?
        int i = 0;
        //Best price differential goes first
        Collections.sort(diff,new ListComparator());
        //Who actually gets jobs
        ArrayList<Integer> send = new ArrayList<>();
        ArrayList<Double> offload = D.getOffLoad();
        //Get rid of as much as possible
        while (offload.get(0) > 0 && offload.get(1) > 0 && offload.get(2) > 0 && i < diff.size()) {
            int indMax = (int)((double)diff.get(i).get(0));
            send.add(indMax);
            offload.set(0, (offload.get(0) - buyers.get(indMax).getOnLoad().get(0)));
            offload.set(1, (offload.get(1) - buyers.get(indMax).getOnLoad().get(1)));
            offload.set(2, (offload.get(2) - buyers.get(indMax).getOnLoad().get(2)));
            diff.get(i).set(1, 0.0);
            i++;
        }
        //Send the jobs
        completeTheTrade(D, send);
    }

    public void completeTheTrade(DataCenter D, ArrayList<Integer> send) {
        //List of jobs that I have
        ArrayList<Job> jobs = D.getInProgress();
        //Jobs that are available vs not
        int[] available = D.getMu();
        //Which jobs have I already seen
        int jindex = 0;
        //List of job takers
        for (int where : send) {
            boolean moreRoom = true;
            DataCenter S = buyers.get(where);
            ArrayList<Job> sendEr = new ArrayList<>();
            //While I still have available jobs, offload as many as this center can take
            while (check(available) && jindex < (available.length - 1) && jindex < (jobs.size()) && moreRoom) {
                if (available[jindex] == 1) {
                    if (S.canTake(jobs.get(jindex))) {
                        //Split revenue
                        int start = jobs.get(jindex).getEstCompleteionTime();
                        int end = jobs.get(jindex).timeLeft();
                        double split = jobs.get(jindex).getRevenue();
                        double even = (double)(end / start);
                        //Revenue sent away
                        double sen = split * even;
                        //What I get to keep
                        double stay = split - sen;
                        //Pay me
                        D.addRev(stay);
                        //Subtract my pay from the revenue
                        jobs.get(jindex).setRevenue(sen);
                        sendEr.add(jobs.get(jindex));
                        jobs.get(jindex).setTransfered();
                        //The job is no longer on my server
                        D.quaretine(jobs.get(jindex).getId());

                        //subtract the migration time from the job
                        jobs.get(jindex).migPenalty(D.getBandwidth(), S.getBandwidth());

                        //Calculate cost to transfer
                        double size = jobs.get(jindex).getTotalSize();
                        D.addRev((size * - transferCost));
                    }
                    else {
                        //Can I still take more?
                        moreRoom = false;
                    }
                }
                jindex++;
            }
            S.transfer(sendEr, D.getId());
            //Go again incase I didnt get enough, or to take me off the selling list
            S.reasonableIndulgence();
        }
    }

    //More jobs available?
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
