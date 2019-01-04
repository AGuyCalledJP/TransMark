import java.util.*;
/*
Agent that simulates a free market exchange grounds for data centers to transact jobs and free space for profit according
to adjusted rates
@author Jared Polonitza
 */
public class Market {
    //List of centers currently looking to buy jobs
    ArrayList<DataCenter> buyers = new ArrayList<>();

    //Cost incurred for transfering a job to a new center
    private double maxTransCost = 8;

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
                    double weightOff = avail.get(cpu) / offload.get(cpu);
                    double weightOn = offload.get(cpu) / avail.get(cpu);
                    if (weightOff > 1) {
                        weightOff = 1;
                    }
                    if (weightOn > 1) {
                        weightOn = 1;
                    }
                    double comp = offload.get(cost) * weightOff;
                    double onComp = avail.get(cost) * weightOn;
                    //Compare cost ratio
                    if (comp > onComp) {
                        double d = comp - onComp;
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

    /*
    Determine where and how much to send
     */
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
            //Get index of buying center
            int indMax = (int)((double)diff.get(i).get(0));
            send.add(indMax);
            //Calculate total offload capacity
            offload.set(0, (offload.get(0) - buyers.get(indMax).getOnLoad().get(0)));
            offload.set(1, (offload.get(1) - buyers.get(indMax).getOnLoad().get(1)));
            offload.set(2, (offload.get(2) - buyers.get(indMax).getOnLoad().get(2)));
            diff.get(i).set(1, 0.0);
            i++;
        }
        //Send the jobs
        completeTheTrade(D, send);
    }

    /*
    Send
     */
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
            if (where < buyers.size()) {
                DataCenter S = buyers.get(where);
                ArrayList<Job> sendEr = new ArrayList<>();
                double totalCore = 0;
                //While I still have available jobs, offload as many as this center can take
                while (check(available) && jindex < (available.length - 1) && jindex < (jobs.size()) && moreRoom && totalCore < S.getOnLoad().get(0)) {
                    if (available[jindex] == 1) {
                        if (S.canTake(jobs.get(jindex))) {
                            //Split revenue
                            int start = jobs.get(jindex).getEstCompleteionTime();
                            int end = jobs.get(jindex).timeLeft();
                            double split = jobs.get(jindex).getRevenue();
                            //Something is going negative right here
                            double even = (double) (end / start);
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
                            double size = jobs.get(jindex).getMigrationTime();
                            D.incurredCost((size * maxTransCost));
                            D.addLogPrice(size * maxTransCost);

                            //Account for total weight of jobs
                            totalCore += jobs.get(jindex).getCoreCount();
                        } else {
                            //Can I still take more?
                            moreRoom = false;
                        }
                    }
                    jindex++;
                }
                S.transfer(sendEr);
                S.recalibrate(sendEr);
                if (S.isWaiting()) {
                    buyers.remove(S);
                }
            }
            else {
                System.out.println("Sorry not happening");
            }
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

    public boolean anyBuyers(DataCenter seller) {
        int size = buyers.size();
        if (size > 0) {
            if (size == 1) {
                if(buyers.get(0) == seller) {
                    return false;
                }
            }
            return true;
        }
        else {
            return false;
        }
    }
}
