import java.util.Random;
import java.util.PriorityQueue;
import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Arrays;
/*

 */
public class DataCenter {
    //Utility
    private Random rand = new Random();
    //Accepted Jobs waiting to get into system schedule,
    private PriorityQueue<Job> jobs = new PriorityQueue<>();
    //Jobs currently in a cluster
    private ArrayList<Job> inProgress = new ArrayList<>();
    //Clusters in the system
    private ArrayList<Cluster> clusters = new ArrayList<>();
    //Job provider
    private JobMaster master;
    //Monthly budget
    private final double BUDGET;
    private double budget;
    //Jobs available to offload
    private int[] mu;
    //looping index for participation rate
    private int participation;
    //set interval to enter market
    private final int SETPART;
    //Track total revenue
    private double revenue;
    //Who am I?
    private int id;
    //Total jobs completed
    private int jobsProcessed;
    //Total jobs accepted
    private int totalJobs;
    //Accrued cost
    private double totalCost;
    //Set extra wattage to account for cooling
    private final double coolingPer = .33;
    //Total jobs Rejected or canceled by an end user
    private int jobsRejected = 0;
    //Jobs killed by the system scheduler
    private int forcedOut = 0;
    //Fee for flavor of cluster
    private double standardRate = 0.10;
    //Track energy rate for center
    private double currentRate = 0;
    //Total jobs that balk -----------> not registering for some reason
    private int balk = 0;
    //Total RAM in house
    private double totalRAM;
    //Total CPU in house
    private double totalCPU;
    //Total Local Disk in house
    private double totalDisk;
    //Maximum watt house possible achieveable by this data center
    private double maxWh;
    //Max cost possible based off max wattage
    private double maxCost;
    //Average speed of a cpu in house
    private int speed;
    //Revenue made in the last minute
    private double revAtm = 0;
    //Current cost available to bring on
    private double bringOn = 0;
    //Projected cost over participation rate
    private double projectedCost = 0;
    //Total jobs recieved from market
    private int jobsRecieved = 0;
    //Total jobs sent to market
    private int jobsSent = 0;
    //Market Flags
    private boolean buying = false;
    private boolean selling = false;
    private boolean available = false;
    //Notation Array <C,R,LD> for market
    private ArrayList<Double> offLoad = new ArrayList<>();
    private ArrayList<Double> onLoad = new ArrayList<>();
    //Total failed transferred jobs
    private int transferedFail = 0;
    //Arrival Rate
    private double lambda = 0;
    //Ceiling for job scheduler
    private double per;
    //Bandwidth available to house
    private double bandwidth;
    //Tracking statistics
    private ArrayList<Double> priceLog = new ArrayList<>();
    private ArrayList<Double> energyLog = new ArrayList<>();
    private ArrayList<Double> revenueLog = new ArrayList<>();
    private ArrayList<Double> failureLog = new ArrayList<>();
    private ArrayList<Double> jobThroughput = new ArrayList<>();
    private MarketHistory transactions = new MarketHistory();

    //Constructor for pre-calculated arrival rate
    public DataCenter(int id, double budget, int numClust, double bandwidth, int participation, double arrival, ArrayList theWorld){
        String[] a = theWorld.get(1).toString().split("],");
        ArrayList<String[]> clusterSpecs = new ArrayList<>();
        for (int x = 0; x < a.length; x++) {
            String s = a[x];
            s = s.replace("[","");
            s = s.replace("]","");
            s = s.replace(" ","");
            clusterSpecs.add(s.split(","));
        }
        //Set External Parameters
        this.id = id;
        this.budget = budget;
        BUDGET = this.budget;
        this.bandwidth = bandwidth;
        this.participation = participation;
        SETPART = participation;
        //Createe and spec out clusters
        for(int x = 0; x < numClust; x++){
            clusters.add(new Cluster(Progress.idCluster, clusterSpecs));
            Progress.idCluster++;
            totalRAM += clusters.get(x).getMaxRam();
            totalCPU += clusters.get(x).getMaxCPUSpace();
            totalDisk += clusters.get(x).getMaxLocalDiskSpace();
            speed += clusters.get(x).getAvgSpeed();
            for (int y = 0; y < clusters.get(x).getClusterCells().size(); y++) {
                maxWh += clusters.get(x).accessCell(y).getMaxPowerUse();
            }
        }
        //Calc average cluster speed
        speed = speed / clusters.size();
        maxCost = maxCost();
        //(1 - per) -> load capacity the server is being allowed to run at
        if (clusters.size() == 1 || clusters.size() == 2) {
            per = 0.1;
        }
        else if(clusters.size() > 2 && clusters.size() <= 4) {
            per = 0.3;
        }
        else {
            per = 0.5;
        }
        setJobWeights();
        double perShort = 0.9;
        //Job delivery factory
        master = new JobMaster(speed, perShort, numClust, arrival);
    }

    //Second constructor for non-calculated arrival rate
    public DataCenter(int id, double budget, int numClust, double bandwidth, int participation, ArrayList theWorld){
        String[] a = theWorld.get(1).toString().split("],");
        ArrayList<String[]> clusterSpecs = new ArrayList<>();
        for (int x = 0; x < a.length; x++) {
            String s = a[x];
            s = s.replace("[","");
            s = s.replace("]","");
            s = s.replace(" ","");
            clusterSpecs.add(s.split(","));
        }

        this.id = id;
        this.budget = budget;
        BUDGET = this.budget;
        this.bandwidth = bandwidth;
        this.participation = participation;
        SETPART = participation;

        for(int x = 0; x < numClust; x++){
            clusters.add(new Cluster(Progress.idCluster, clusterSpecs));
            Progress.idCluster++;
            totalRAM += clusters.get(x).getMaxRam();
            totalCPU += clusters.get(x).getMaxCPUSpace();
            totalDisk += clusters.get(x).getMaxLocalDiskSpace();
            speed += clusters.get(x).getAvgSpeed();
            for (int y = 0; y < clusters.get(x).getClusterCells().size(); y++) {
                maxWh += clusters.get(x).accessCell(y).getMaxPowerUse();
            }
        }

        speed = speed / clusters.size();
        maxCost = maxCost();

        if (clusters.size() == 1 || clusters.size() == 2) {
            per = 0.1;
        }
        else if(clusters.size() > 2 && clusters.size() <= 4) {
            per = 0.3;
        }
        else {
            per = 0.5;
        }
        setJobWeights();
        double perShort = 0.9;
        master = new JobMaster(speed, perShort, numClust);
    }

    //Reset budget on turn of month
    public void newMonth() {
        budget = BUDGET;
    }

    /*
    DATACENTER OPERATIONS
    */

    /*
    Add jobs the Data Center. Jobs balk according to usage percentage of the Center
     */
    public void addJobs(Queue<Job> newJobs){
        Queue<Job> hold = new LinkedList<>();
        double stress;
        double determine;
        int timeNeeeded;
        while (!newJobs.isEmpty()) {
            stress = centerStress();
            Job j = newJobs.poll();
            timeNeeeded = j.getEstCompleteionTime();
            j.timeRented(timeNeeeded, standardRate);
            //Whether job balks or not
            determine = rand.nextDouble();
            detWeight(j);
            int balk = 0;
            if (stress >= 0 && stress <= .3) {
                hold.add(j);
            } else if (stress > .3 && stress <= .6) {
                if (determine >= 0 && determine <= .6) {
                    balk += 1;
                } else {
                    hold.add(j);
                }
            } else {
                if (determine >= 0 && determine <= .3) {
                    balk++;
                } else {
                    hold.add(j);
                }
            }
        }
        jobs.addAll(hold);
        totalJobs += hold.size();
    }

    public void transfer(ArrayList<Job> j, int where) {
        for (Job J : j) {
            detWeight(J);
        }
        transactions.addTransaction(j, where);
        jobs.addAll(j);
        totalJobs += j.size();
        jobsRecieved += j.size();
    }

    public boolean canTake(Job j) {
        boolean yes = false;
        double totalC = 0;
        double totalR = 0;
        double totalD = 0;
        for (Cluster C : clusters) {
            totalC += (C.getMaxCPUSpace() - C.getAvailCPUSpace());
            totalR += (C.getMaxRam() - C.getAvailRAM());
            totalD += (C.getMaxLocalDiskSpace() - C.getAvailLocalDiskSpace());
        }
        if (totalC > j.getCoreCount() && totalD >= j.getLocalDisk() && totalR >= j.getRAM()) {
            yes = true;
        }
        return yes;
    }
    //Calculate and remove jobs that are done
    public void cleanHouse(){
        ArrayList<Queue<Job>> done = new ArrayList<>();
        int total = 0;
        for(Cluster c : clusters){
            done.add(c.cleanCells());
        }
        for(Queue<Job> q : done) {
            while(!q.isEmpty()) {
                Job j = q.poll();
                ArrayList<Job> remove = new ArrayList<>();
                for(Job k : inProgress) {
                    if(j.getId() == k.getId()) {
                        remove.add(k);
                        if (j.getTransfered()) {

                        }
                        else {
                            collectRev(k);
                        }
                        total++;
                    }
                }
                for(Job jR : remove) {
                    inProgress.remove(jR);
                }
            }
        }
        jobThroughput.add((double)total);
        if(total > 0) {
            jobsProcessed += total;
        }
    }

    //Place jobs into clusters
    public void reganomics(){
        boolean rejected = false;
        boolean[] scale = new boolean[clusters.size()];
        int ind = 0;
        for (Cluster check : clusters) {
            if (check.perCPU() < per || check.perRAM() < per) {
                scale[ind] = true;
            }
            ind++;
        }
        boolean s = true;
        for (boolean b : scale) {
            if (!b) {
                s = false;
            }
        }
        if (s && per < 0.1) {
            per = per - 0.1;
        }
        while(!rejected && !jobs.isEmpty()) {
            if (jobs.isEmpty()) {
                System.out.println("No jobs waiting to execute");
                rejected = true;
            } else {
                Job j = jobs.peek();
                j.calcReq();
                int numTries = 0;
                Boolean[] onward = new Boolean[clusters.size()];
                int index = 0;
                for (Cluster c : clusters) {
                    if (!jobs.isEmpty()) {
                        if (c.perCPU() < per || c.perRAM() < per) { //check to see if there is less than a designated amount of space available of the space on the cluster. If there is reject so that it has time to process jobs
                            if (c.more()) {
                                process();
                                wash();
                                onward[index] = true;
                            } else if (!c.more() && numTries == clusters.size() && !Arrays.asList(onward).contains(true)) {
                                rejected = true;
                            } else {
                                numTries++;
                            }
                        } else if (c.getAvailCPUSpace() > j.getCoreCount() && c.getAvailLocalDiskSpace() > j.getLocalDisk() && c.getAvailRAM() > j.getRAM()) {
                            j = jobs.poll();
                            // System.out.println("sending job: " + j);
                            inProgress.add(j);
                            c.sweep(j);
                            rejected = false;
                            c.primeThePump();
                            continue;
                        } else {
                            numTries++;
                        }
                        if (numTries == clusters.size()) {
                            rejected = true;
                        }
                        index++;
                    }
                }
            }
        }
        //System.out.println("Center Level post placement: " + jobs.size());
    }

    public void moveAlong() {
        ArrayList<Job> hold2 = new ArrayList<>();
        if (!inProgress.isEmpty()) {
            for (Job j : inProgress) {
                j.timePass();
                if (j.timeUp()) {
                    sAndD(j.getId());
                    hold2.add(j);
                }
            }
        }
        failureLog.add((double)(hold2.size()));
        for (Job j2 : hold2) {
            if (j2.getTransfered()) {
                transferedFail++;
            }
            if (j2.numTasksFinished() > 0) {
                j2.setFailed();
                revenue += j2.getRevenue();
                forcedOut++;
            }
            else {
                jobsRejected++;
            }
            inProgress.remove(j2);
        }
    }

    //Remove a job in processing that underestimated time needed
    public void sAndD(int id) {
        for (Cluster c : clusters) {
            c.find(id);
        }
    }

    //Remove a job in processing that underestimated time needed
    public void quaretine(int id) {
        jobsSent++;
        for (Cluster c : clusters) {
            c.find(id);
        }
        ArrayList<Job> hold = new ArrayList<>();
        for (Job j : inProgress) {
            if (j.getId() != id) {
                hold.add(j);
            }
        }
        inProgress = hold;
    }

    //Execute work on clusters
    public void wash() {
        //System.out.println(inProgress);
        for (Cluster c : clusters) {
            c.spin();
        }
        for (Job j : inProgress) {
            j.setMigTime();
        }
    }

    //Load jobs that are waiting at lower levels
    public void process() {
        for (Cluster c : clusters) {
            c.primeThePump();
            c.busy();
        }
    }

    /*
    CALCULATING POWER USAGE AND COSTS
    */
    //total power required by the house for all running tasks.
    public double powerUsage(){
        double convRate = 1000000.0;
        double watts = 0;
        for (Cluster c : clusters) {
            watts += c.cellPowerUse();
        }
        return (watts/convRate);
    }

    public double completeUsage() {
        double totalC = 0;
        double totalR = 0;
        double totalD = 0;
        for (Cluster C : clusters) {
            totalC += (C.getMaxCPUSpace() - C.getAvailCPUSpace());
            totalR += (C.getMaxRam() - C.getAvailRAM());
            totalD += (C.getMaxLocalDiskSpace() - C.getAvailLocalDiskSpace());
        }
        double total = ((totalC + totalR + totalD) / (totalCPU + totalRAM + totalDisk));
        return total;
    }

    //Calculate cost of execution in a data center as a function of executing work and climate in surrounding area
    public double coolingCost(double used) {
        return used * coolingPer;
    }

    public void incurredCost(double cost) {
        totalCost += cost;
    }

    public double centerStress() {
        double hold = 0;
        for (Cluster c : clusters) {
            hold += c.clusterStress();
        }
        return (hold/clusters.size());
    }

    /*
    WEIGHTING JOBS AND THE TOTAL POWER OF THE CENTER
     */
    //maximum percentage of weight that a given type of job could take in this center
    public double setJobWeights() {
        return ((Job.MAXCOREL +  Job.MAXRAML + Job.MAXDISKL)/(totalCPU + totalRAM + totalDisk));
    }

    //determine the total weight this job would require during execution in this center
    public void detWeight(Job j) {
        double totalPer = 1 * setJobWeights();
        j.setCenterWeight((j.getWeight() * totalPer));
    }


    //max cost the center could incur given the current rates this minute
    public double maxCost() {
        return ((maxWh + coolingCost(maxWh)) / 1000000) * currentRate;
    }

    /*
    PROJECTING FUTURE COSTS
    */
    //Calculate the total cost of running for a given time interval
    public double projectedCost(int interval) {
        double stress = completeUsage();
        int project = interval;
        double totalPrice = 0;
        int index = 0;
        double per = 0;
        int accountForFail = 2;
        ArrayList<Job> hold = new ArrayList<>();
        while (per < stress && index < inProgress.size()) {
            per += inProgress.get(index).getCenterWeight();
            totalPrice += inProgress.get(index).getRelCost();
            hold.add(inProgress.get(index));
            index++;
        }
        for (Job j : hold) {
            if (j.timeLeft() - project <= 0) {
                per = per - j.getCenterWeight();
            }
        }
        while (per < stress && index < inProgress.size()) {
            per += inProgress.get(index).getCenterWeight();
            totalPrice += inProgress.get(index).getRelCost();
            index++;
        }
        return ((totalPrice / accountForFail) * interval);
    }
    /*
    SETTING WHICH JOBS SHOULD BE OFFLOADED
     */
    //Holder method for launching the market agent
    public void reasonableIndulgence() {
        buying = false;
        selling = false;
        participation = SETPART;
        if (Math.abs(centerStress()) > Progress.EPSILON && ClockWork.t > 10) {
            int n = participation;
            double cost = projectedCost(n);
            double conv = budget / 43200; //monthly budget split by minute
            double budge = conv * n;
            double room = 0.1;
            if (Math.abs(cost) > Progress.EPSILON) {
                if (cost > (budge + (budge * room))) {
//                    System.out.println("buy");
                    balance(budge, inProgress, cost);
                } else if (cost < budge) {
//                    System.out.println("sell");
                    reload(cost, budge, n);
                }
            }
        }
    }

    /*
    BALANCING COST WITH BUDGET
    */
    //want to search through the list of jobs, finding the jobs currently in execution that would both satisfy the budget constraint while also returning the maximum profit in house
    private void balance(double ceiling, ArrayList<Job> candidates, double cost) {
        offLoad = new ArrayList<>();
        double total = 0;
        boolean aggregate = true;
        ArrayList<Job> offload = new ArrayList<>();
        MergeSortSlack ob = new MergeSortSlack(candidates);
        ob.sortGivenArray();
        ArrayList<Job> slack = ob.getSortedArray();
        MergeSortMig obj = new MergeSortMig(candidates);
        obj.sortGivenArray();
        ArrayList<Job> mig = obj.getSortedArray();
        int iter = 0;
        double searchPer = 0;
        while (aggregate && iter < 12) {
            for (int i = slack.size() - 1; i > 0; i = i - 1) {
                Job j = slack.get(i);
                if (!offload.contains(j)) {
                    if ((cost - total) > ceiling) {
                        if (iter == 0) {
                            searchPer = 0.1;
                        } else if (iter == 1) {
                            searchPer = 0.2;
                        } else if (iter == 2) {
                            searchPer = 0.3;
                        } else if (iter == 3) {
                            searchPer = 0.4;
                        } else if (iter == 4) {
                            searchPer = 0.5;
                        } else if (iter == 5) {
                            searchPer = 0.6;
                        } else if (iter == 6) {
                            searchPer = 0.7;
                        } else if (iter == 7) {
                            searchPer = 0.8;
                        } else if (iter == 8) {
                            searchPer = 0.9;
                        } else  {
                            searchPer = 1.0;
                        }
                        int start = (int)((mig.size()) * searchPer);
//                        System.out.println(start);
                        if (start == mig.size()) {
                            start = start - 1;
                        }
                        Job comp = mig.get(start);
                        if (j.getMigrationTime() <= comp.getMigrationTime()) {
                            offload.add(j);
                            total += j.getRelCost();
                        } else {
                            break;
                        }
                    } else {
                        aggregate = false;
                    }
                }
                else if (offload.size() == mig.size()) {
                    aggregate = false;
                }
            }
            iter++;
        }
        setMu(offload);
        ArrayList<Double> tuple = new ArrayList<>();
        double cs = 0;
        double ram = 0;
        double ld = 0;
        double costToMe = 0;
        for (Job j : offload) {
            cs += j.getCoreCount();
            ram += j.getRAM();
            ld += j.getLocalDisk();
            costToMe += j.getRelCost();
        }
        tuple.add(cs);
        tuple.add(ram);
        tuple.add(ld);
        tuple.add(costToMe);
        offLoad = tuple;
    }

    //Return a binary version of the inprogress arraylist. 0 signifies that it should stay, 1 if it should be offloaded
    public void setMu(ArrayList<Job> leave) {
        int[] hold =  new int[inProgress.size()];
        int i = 0;
        for (Job j : inProgress) {
            if (leave.contains(j)) {
                hold[i] = 1;
            }
            else {
                hold[i] = 0;
            }
            i++;
        }
        mu = hold;
        selling = true;
    }

    /*
    CALCULATE JOBS TO BRING ON
     */
    public void reload(double cost, double ceiling, int interval) {
        onLoad = new ArrayList<>();
        double canTake = (ceiling)/ maxCost;
        double currentlyTaking = cost / maxCost;
        bringOn = canTake - currentlyTaking;
        projectedCost = maxCost * bringOn;
        double cpu = totalCPU * bringOn;
        double ram = totalRAM * bringOn;
        double ld = totalDisk * bringOn;
        ArrayList<Double> hold = new ArrayList<>();
        hold.add(cpu);
        hold.add(ram);
        hold.add(ld);
        hold.add(projectedCost);
        onLoad = hold;
        buying = true;
    }

    /*
    MISC
    */
    public void tick() {
        for (Job j : jobs) {
            if (j.getTimeSensitive() > 1) {
                j.setTimeSensitive();
            }
        }
        participation = participation - 1;
    }

    public int getParticipation() {
        return participation;
    }

    public void setProcesses() {
        for (Cluster c : clusters) {
            c.reset();
        }
    }
    //monthly budget for this datacenter
    public double getBudget(){
        return budget;
    }


    public void collectRev(Job j) {
        double collect = j.getRevenue();
        revenue += collect;
        //System.out.println("t rev: " + revenue);
        revAtm += collect;
        //System.out.println("rev atm: " + revAtm);
    }

    public void logPrice(double cost) {
        priceLog.add(cost);
    }

    public void logEnergyUse(double expended) {
        energyLog.add(expended);
    }

    public void logRevenue(double gain) {
        revenueLog.add(gain);
    }

    //Getters
    public boolean isBuyer() {
        return buying;
    }

    public boolean isSeller() {
        return selling;
    }

    public ArrayList<Job> getInProgress() {
        return inProgress;
    }

    //list of jobs that need to be offloaded to reach peak profit
    public int[] getMu(){
        return mu;
    }

    public ArrayList<Double> getOffLoad() {
        return offLoad;
    }
    public ArrayList<Double> getOnLoad() {
        return onLoad;
    }

    public ArrayList<Cluster> getClusters(){
        return clusters;
    }

    public double getRevenue() {
        return revenue;
    }

    public double getRevAtm() {
        return revAtm;
    }

    public int numCluster() {
        return clusters.size();
    }

    public int jobsTransfere() {
        return jobsSent;
    }

    public int getTotalJobs() {
        return totalJobs;
    }

    public double incurredCost() {
        return totalCost;
    }

    public int getJobsRejected() {
        return jobsRejected;
    }

    public int getJobsSent() {
        return jobsSent;
    }

    public int getJobsRecieved() {
        return jobsRecieved;
    }

    public int getJobsProcessed() {
        return jobsProcessed;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public double getProfit() {
        return revenue - totalCost;
    }

    public int getId() {
        return id;
    }

    public void setRate(double rate) {
        currentRate = rate;
        maxCost = maxCost();
        for (Job j : inProgress) {
            j.relativeCost(maxCost);
        }
    }

    public void addRev(double rev) {
        revenue += rev;
    }

    public void setRevAtm() {
        revAtm = 0;
    }

    public JobMaster getMaster() {
        return master;
    }

    public double getBandwidth() {
        return bandwidth;
    }

    public ArrayList<Double> getPriceLog() {
        return priceLog;
    }

    public ArrayList<Double> getEnergyLog() {
        return energyLog;
    }

    public ArrayList<Double> getRevenueLog() {
        return revenueLog;
    }

    public double throughput(){
        double total = 0;
        for (double d : jobThroughput) {
            total += d;
        }
        return total / jobThroughput.size();
    }

    public double failureRate() {
        double totalF = 0;
        double totalJ = 0;
        for (int i = 0; i < failureLog.size(); i++) {
            totalF += failureLog.get(i);
            totalJ += failureLog.get(i) + jobThroughput.get(i);
        }
        totalF = totalF / failureLog.size();
        totalJ = totalJ / jobThroughput.size();
        return (totalF / (totalF + totalJ));
    }

    public void noThrough() {
        jobThroughput.add(0.0);
    }

    public void noFails() {
        failureLog.add(0.0);
    }

    public void tock() {
        jobThroughput.add(throughput());
        failureLog.add(failureRate());
    }

    public String toString() {
        int key = 1;
        String str = "Center: " + id + "\n";
//        str += "Jobs executing in this data center: " + "\n" + inProgress.size() + "\n";
//        str += "Jobs waiting to be executed: " + jobs.size() + "\n";
//        for(Cluster s : clusters) {
//            str += s.clusterStress() + "\n";
//            str += "Server " + s.getId() + ": " + s + "\n";
//        }
//        str += inProgress + "\n";
        str += "Num clusters: " + clusters.size() + "\n";
        str += "Arrival Rate: " + lambda + "\n";
        str += "Per: " + per + "\n";
        if (buying) {
            str += "Looking to buy right now \n";
        }
        if (selling) {
            str += "Looking to sell right now \n";
        }
        str += "Max Cost Right Now: " + maxCost() + "\n";
//        str += "balking rate: " + balk + "\n";
        str += "Jobs rejected: " + jobsRejected + "\n";
        str += "Miscalculations: " + forcedOut + "\n";
//        str += "Jobs Sent: " + jobsSent + "\n";
//        str += "Jobs Received: " + jobsRecieved + "\n";
//        str += "Transfer failures : " + transferedFail + "\n";
//        str += "Transfer Failure Rate : " + tFailRate() + "\n";
        str += "Total jobs taken on: " + totalJobs + "\n";
        str += "Jobs done: " + jobsProcessed + "\n";
        str += "budget: " + budget + "\n";
        str += "Total Cost incurred: " + totalCost + "\n";
        str += "Total Revenue: " + revenue + "\n";
        str += "Profit: " + (revenue - totalCost) + "\n";
//        str += "log of energy usage: " + energyLog + "\n";
//        str += "log of cost: " + priceLog + "\n";
        str += "Failed transfers: " + transactions.failPer() + "\n";
        str += "Avg Failure Rate: " + failureRate() + "\n";
        str += "Avg Job Throughput: " + throughput();
        return str;
    }
}

