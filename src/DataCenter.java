import java.util.*;
//NEXT STEP -> Offload all jobs whos finihsedTasks.size() == totalTasks and collect the revenue for completing the task
//DataCenter object. These are the centers that exist within the Market.
public class DataCenter {
    private Random rand = new Random();
    private PriorityQueue<Job> jobs = new PriorityQueue<>(); //waiting jobs
    private ArrayList<Job> inProgress = new ArrayList<>(); //jobs being worked on
    private ArrayList<Cluster> clusters = new ArrayList<>();
    private JobMaster master;
    private double budget;
    private int[] mu;
    private int balk;
    private int[] gone;
    private int participation;
    private final int SETPART;
    private double revenue;
    private int id;
    private int jobsProcessed;
    private int totalJobs;
    private double totalCost;
    private final double coolingPer = .33;
    private int jobsRejected = 0;
    private int forcedOut = 0;
    private double standardRate = 0.10;
    private double currentRate = 0;
    private double maxL = 0;
    private double totalRAM;
    private double totalCPU;
    private double totalDisk;
    private double maxWh;
    private double maxCost;
    private int speed;
    private double revAtm = 0;
    private double bringOn = 0;
    private double projectedCost = 0;
    private int jobsRecieved = 0;
    private int jobsSent = 0;
    private boolean buying = false;
    private boolean selling = false;
    private boolean available = false;
    private ArrayList<Double> offLoad = new ArrayList<>();
    private ArrayList<Double> onLoad = new ArrayList<>();
    private int transferedFail = 0;
    private double lambda = 0;
    private double per = 0;
    private double bandwidth;
    private double costRatio;
    private MarketHistory transactions = new MarketHistory();
    private ArrayList<Double> priceLog = new ArrayList<>();
    private ArrayList<Double> energyLog = new ArrayList<>();
    private ArrayList<Double> revenueLog = new ArrayList<>();

    public DataCenter(int id, int numClust){
        speed = Progress.standardSpeed;
        this.id = id;
        participation = rand.nextInt(10) + 60; //set this so centers will be in stride for now
        SETPART = participation;
        for(int x = 0; x < numClust; x++){
            clusters.add(new Cluster(Progress.idCluster));
            Progress.idCluster++;
            totalRAM += clusters.get(x).getMaxRam();
            totalCPU += clusters.get(x).getMaxCPUSpace();
            totalDisk += clusters.get(x).getMaxLocalDiskSpace();
            maxWh += Progress.standardMax * 5;
            maxCost = maxCost();
        }
        double band = rand.nextDouble();
        if (band < .25) {
            bandwidth = Progress.quarterInternetSpeed;
        }
        else if (band < .5) {
            bandwidth = Progress.halfInternetSpeed;
        }
        else {
            bandwidth = Progress.maxInternetSpeed;
        }
        //1 - per gives the load capacity the server is being allowed to run at
        if (clusters.size() == 1 || clusters.size() == 2) {
            per = 0.1;
        }
        else if(clusters.size() > 2 && clusters.size() <= 4) {
            per = 0.3;
        }
        else {
            per = 0.5;
        }
        setBudget();
        setJobWeights();
        double perShort = 0.9;
        master = new JobMaster(speed, perShort, numClust);
    }

    public void setBudget() {
     double flip = rand.nextDouble();
        if (flip < .5) {
            if (numCluster() == 1) {
                budget = rand.nextInt(10) + 85;
            } else if (numCluster() == 2) {
                budget = rand.nextInt(10) + 175;
            } else if (numCluster() == 3) {
                budget = rand.nextInt(10) + 265;
            } else if (numCluster() == 4) {
                budget = rand.nextInt(10) + 375;
            } else {
                budget = rand.nextInt(10) + 455;
            }
        }
        else {
            if (numCluster() == 1) {
                budget = rand.nextInt(25) + 300;
            } else if (numCluster() == 2) {
                budget = rand.nextInt(50) + 500;
            } else if (numCluster() == 3) {
                budget = rand.nextInt(75) + 700;
            } else if (numCluster() == 4) {
                budget = rand.nextInt(100) + 900;
            } else {
                budget = rand.nextInt(150) + 1000;
            }
        }
    }

    /*
    DATACENTER OPERATIONS
    */
    //Add new jobs to the data center
    public void addJobs(Queue<Job> newJobs){
        Queue<Job> hold = new LinkedList<>();
        double stress = 0;
        int determine = 0;
        int timeNeeeded = 0;
        while (!newJobs.isEmpty()) {
            stress = centerStress();
            Job j = newJobs.poll();
            timeNeeeded = j.getEstCompleteionTime();
            j.timeRented(timeNeeeded, standardRate);
            //Whether job balks or not
            determine = rand.nextInt(99) + 1;
            detWeight(j);
            if (stress >= 0 && stress <= .3) {
                hold.add(j);
            } else if (stress > .3 && stress <= .6) {
                if (determine <= 33 && determine >= 0) {
                    balk++;
                } else {
                    hold.add(j);
                }
            } else {
                if (determine <= 66 && determine >= 0) {
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
//            System.out.println("Christ");
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
    public void setJobWeights() {
        maxL = ((Job.MAXCOREL +  Job.MAXRAML + Job.MAXDISKL)/(totalCPU + totalRAM + totalDisk));
        //maxM = ((Job.MAXCORES +  Job.MAXRAMS + Job.MAXDISKSL)/(totalCPU + totalRAM + totalDisk));
        //maxS = ((Job.MAXCORES +  Job.MAXRAMS + Job.MAXDISKSL)/(totalCPU + totalRAM + totalDisk));
//        System.out.println("Center: " + id + " cpu " + totalCPU + " ram " + totalRAM + " disk " + totalDisk);
//        System.out.println("MaxL: " + maxL);
//        System.out.println("MaxM: " + maxM);
//        System.out.println("MaxS: " + maxS);
    }

    //determine the total weight this job would require during execution in this center
    public void detWeight(Job j) {
        double totalPer = 1 * maxL;
        j.setCenterWeight((j.getWeight() * totalPer));
    }

    public double compCost(Job j) {
        double per = 1 * maxL;
        return (maxCost * (j.getWeight() * per));
    }

    public double compWeight(Job j) {
        double per = 1 * maxL;
        return (j.getWeight() * per);
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
//        System.out.println(completeUsage());
//        System.out.println(centerStress());
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
//        System.out.println(totalPrice / 2);
        return ((totalPrice / accountForFail) * interval);
    }

    public double projectedRevenue() {
        double totalRev = 0;
        if (mu.length < inProgress.size()) {
            for (int i = 0; i < mu.length - 1; i++) {
                if (mu[i] == 1) {
                    totalRev += inProgress.get(i).getRevenue();
                }
            }
        }
        else {
            for (int i = 0; i < inProgress.size() - 1; i++) {
                if (mu[i] == 1) {
                    totalRev += inProgress.get(i).getRevenue();
                }
            }
        }
        return totalRev;
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
//            System.out.println("Time: " + ClockWork.t);
//            System.out.println(numCluster());
//            System.out.println(budget);
//            System.out.println(participation);
//            System.out.println("avail to spend: " + budge);
//            System.out.println("Spending right now: " + cost);
//            System.out.println(centerStress());
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
//        System.out.println("Slack: " + slack);
        MergeSortMig obj = new MergeSortMig(candidates);
        obj.sortGivenArray();
        ArrayList<Job> mig = obj.getSortedArray();
//        System.out.println("Mig: " + mig);
        int iter = 0;
        double searchPer = 0;
//        System.out.println("Current: " + (cost - total));
//        System.out.println("Target: " + ceiling);
        while (aggregate && iter < 12) {
//            System.out.println(offLoad.size());
            for (int i = slack.size() - 1; i > 0; i = i - 1) {
                Job j = slack.get(i);
//                System.out.println("Testing " + j);
//                System.out.println(offload);
//                System.out.println(offload.size());
//                System.out.println(mig.size());
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
//        System.out.println("Current: " + (cost - total));
//        System.out.println("Target: " + ceiling);
//        System.out.println("going: " + offload);
//        System.out.println("Out: " + tuple);
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
//        hold.add(projectedCost  + (projectedCost * charge));
        onLoad = hold;
//        System.out.println("in: " + onLoad);
        buying = true;
    }

    /*
    Market V2
     */

    public void uberEverywhere() {
        buying = false;
        selling = false;
        participation = SETPART;
        int n = participation;
        double cost = projectedCost(n);
        double conv = budget / 43200; //monthly budget split by minute
        double budge = conv * n;
        pool(cost, budge, n);
    }

    public void pool(double cost, double ceiling, int interval) {
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
//        hold.add(projectedCost  + (projectedCost * charge));
        onLoad = hold;
        costRatio = (projectedCost /  (cpu + ram + ld));
//        System.out.println("in: " + onLoad);
        available = true;
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

    public int processesDone() {
        int total = 0;
        for (Cluster c : clusters) {
            total += c.collectProcesses();
        }
        return total;
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

    //given an input in kWh, calculate an integer value for how many servers I can use in that amount of power or less
    public int extraSpace(double x){
        return 0;
    }

    //calculate which bids to accept based on needing to offload excess
    public void whereTo(){
        System.out.println("send me somewhere");
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

    public int[] getGone() {
        return gone;
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

    public int getClustSpeed() {
        return speed;
    }

    public int numCluster() {
        return clusters.size();
    }

    public double getBringOn() {
        return bringOn;
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

    public boolean isAvailable() {
        return available;
    }

    public double getCostRatio() {
        return costRatio;
    }

    public double getProjectedCost() {
        return projectedCost;
    }

    public double tFailRate() {
        if (jobsRecieved > 0) {
            return (double)(transferedFail / jobsRecieved);
        }
        else {
            return 0;
        }
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

    @Override
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
        str += "Cost Ratio: " + costRatio + "\n";
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
        str += "Failed transfers: " + transactions.failPer();
        return str;
    }
}

/*
GRAVEYARD
 */
//    //Identify the jobs that should be left on the server and the jobs that should be offloaded
//    public void offLoad(double conv, int interval, double cost) {
//        ArrayList<Job> candidates = new ArrayList<>();
//        double stress = completeUsage();
//        double strain = 0.0;
//        int inIndex = 0;
////        while (strain < stress && inIndex < inProgress.size()) {
////            candidates.add(inProgress.get(inIndex));
////            strain += candidates.get(inIndex).getCenterWeight();
//////            totalPossCost += candidates.get(inIndex).getRelCost();
////            inIndex++;
////        }
////        for (Job j : candidates) {
////            if (j.timeLeft() - interval <= 0) {
////                strain = strain - j.getCenterWeight();
////            }
////        }
////        while (strain < stress && inIndex < inProgress.size()) {
////            candidates.add(inProgress.get(inIndex));
////            strain += candidates.get(inIndex).getCenterWeight();
//////            totalPossCost += candidates.get(inIndex).getRelCost();
////            inIndex++;
////        }
//        double ceiling = conv; //as much as I can spend right now
////        System.out.println(inProgress);
////        System.out.println(candidates);
//        offLoad = balance(ceiling, inProgress, cost);
//    }

