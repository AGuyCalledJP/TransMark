import java.util.Random;
import java.util.PriorityQueue;
import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Arrays;
import java.util.Collections;
/*

 */
public class  DataCenter {
    //Utility
    private Random rand = new Random();
    //Accepted Jobs waiting to get into system schedule,
    private PriorityQueue<Job> jobs = new PriorityQueue<>(new JobComparator());
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
    //Min Fail rate (1.0), Max Profit (0.0)
    double alpha = 1.0;
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
        //Create and spec out clusters
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

    /*
    Accept and file transferred jobs into the system
     */
    public void transfer(ArrayList<Job> j, int where) {
        for (Job J : j) {
            detWeight(J);
        }
        transactions.addTransaction(j, where);
        jobs.addAll(j);
        totalJobs += j.size();
        jobsRecieved += j.size();
    }

    /*
    Calculate to see if I can take on a given job
     */
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

    /*
    Calculate and remove jobs that are done
     */
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

   /*
   Scan through clusters, distributing jobs to everywhere that has space
    */
    public void systemScheduler(){
        boolean rejected = false;
        boolean[] freeSpace = new boolean[clusters.size()];
        int ind = 0;
        for (Cluster check : clusters) {
            if (check.perCPU() < per || check.perRAM() < per) {
                //If there is free space
                freeSpace[ind] = true;
            }
            ind++;
        }
        boolean s = true;
        for (boolean b : freeSpace) {
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
                            //CPUs have more processing available to do
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
                             }
                        } else if (c.getAvailCPUSpace() > j.getCoreCount() && c.getAvailLocalDiskSpace() > j.getLocalDisk() && c.getAvailRAM() > j.getRAM()) { //There is space available
                            //Add job to processing on that cluster
                            j = jobs.poll();
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
    }

    /*
    Remove any jobs that are still in the system over the amount of time that they paid for
     */
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

    /*
    Remove a job in processing that underestimated time needed
     */
    public void sAndD(int id) {
        for (Cluster c : clusters) {
            c.find(id);
        }
    }

    /*
    Remove a job in processing to send to new center
     */
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

    /*
    Execute work on clusters
     */
    public void wash() {
        for (Cluster c : clusters) {
            c.spin();
        }
        for (Job j : inProgress) {
            j.setMigTime();
        }
    }

    /*
    Load jobs that are waiting at lower levels
     */
    public void process() {
        for (Cluster c : clusters) {
            c.primeThePump();
            c.busy();
        }
    }

    /*
    CALCULATING POWER USAGE AND COSTS
    */

    /*
    total power required by the house for all running tasks.
     */
    public double powerUsage(){
        double convRate = 1000000.0;
        double watts = 0;
        for (Cluster c : clusters) {
            watts += c.cellPowerUse();
        }
        return (watts/convRate);
    }

    /*
    Calculate total usage of the house at the given moment
     */
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

    /*
    Calculate cost of execution in a data center as a function of executing work and climate in surrounding area
     */
    public double coolingCost(double used) {
        return used * coolingPer;
    }

    /*
    Log aggregate cost incurred
     */
    public void incurredCost(double cost) {
        totalCost += cost;
    }

    /*
    Calc total percentage of CPU usage
     */
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

    /*
    Maximum percentage of weight that a given type of job could take in this center
     */
    public double setJobWeights() {
        return ((Job.MAXCOREL +  Job.MAXRAML + Job.MAXDISKL)/(totalCPU + totalRAM + totalDisk));
    }

    /*
    Determine the total weight this job would require during execution in this center
     */
    public void detWeight(Job j) {
        double totalPer = 1 * setJobWeights();
        j.setCenterWeight((j.getWeight() * totalPer));
    }


    /*
    Max cost the center could incur given the current rates this minute
     */
    public double maxCost() {
        return ((maxWh + coolingCost(maxWh)) / 1000000) * currentRate;
    }

    /*
    PROJECTING FUTURE COSTS
    */

    /*
    Calculate the total cost of running for a given time interval
     */
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

    /*
    Holder method for launching the market agent
     */
    public void reasonableIndulgence() {
        buying = false;
        selling = false;
        participation = SETPART;
        if (Math.abs(centerStress()) > Progress.EPSILON && ClockWork.t > 10) {
            int n = participation;
            double cost = projectedCost(n);
            //monthly budget split by minute
            double conv = budget / 43200;
            double budge = conv * n;
            double room = 0.1;
            if (Math.abs(cost) > Progress.EPSILON) {
                if (cost > (budge + (budge * room))) {
                    balance(budge, inProgress, cost);
                } else if (cost < budge) {
                    reload(cost, budge, n);
                }
            }
        }
    }

    /*
    BALANCING COST WITH BUDGET
    */

    /*
    Want to search through the list of jobs, finding the jobs currently in execution that would both satisfy the budget constraint while also returning the maximum profit in house
     */
    private void balance(double ceiling, ArrayList<Job> candidates, double cost) {
        int tSlack = 0;
        int tMig = 0;
        int tC = 0;
        int tR = 0;
        for (Job j : candidates) {
            tSlack += j.timeLeft();
            tMig += j.getMigrationTime();
            tC +=j.getRelCost() * j.timeLeft();
            tR += j.getRevenue() - (j.getRelCost() * j.timeLeft());
        }
        int norm1 = tSlack - tMig;
        int norm2 = tR - tC;
        double total = 0;
        ArrayList<Job> offload = new ArrayList<>();
        ArrayList<ArrayList<Double>> f = new ArrayList<>();
        int index = 0;
        int slack;
        double mig;
        double c;
        double r;
        double val;
        double val2;
        ArrayList<Double> holster;
        for (Job j : candidates) {
            slack = j.timeLeft();
            mig = j.getMigrationTime();
            c = j.getRelCost() * slack;
            r = j.getRevenue();
            val = alpha * (((slack - mig)/norm1));
            val2 = (1 - alpha) * (1/(((r - c)/norm2)));
            holster = new ArrayList<>();
            holster.add((double)index);
            holster.add(val + val2);
            f.add(holster);
            index++;
        }
        Collections.sort(f,new ListComparator());
        int ind = 0;
        double hold;
        while ((cost - total) > ceiling && ind < candidates.size() - 1) {
            hold = f.get(ind).get(0);
            Job j = candidates.get((int)hold);
            if (cost - (total + j.getRelCost()) > ceiling) {
                offload.add(j);
            }
            ind++;
        }
        setMu(offload);
    }

    /*
    Return a binary version of the inprogress arraylist. 0 signifies that it should stay, 1 if it should be offloaded
     */
    public void setMu(ArrayList<Job> leave) {
        ArrayList<Double> tuple = new ArrayList<>();
        double cs = 0;
        double ram = 0;
        double ld = 0;
        double costToMe = 0;
        for (Job j : leave) {
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

    /*
    Calculate and populate <C,R,LD> tuple
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

    /*
    Reset processes for each cluster
     */
    public void setProcesses() {
        for (Cluster c : clusters) {
            c.reset();
        }
    }

    /*
    STATISTICAL TRACKING
     */
    public void collectRev(Job j) {
        double collect = j.getRevenue();
        revenue += collect;
        revAtm += collect;
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

    /*
    Age jobs and move participation rate
    */
    public void tick() {
        for (Job j : jobs) {
            if (j.getTimeSensitive() > 1) {
                j.setTimeSensitive();
            }
        }
        participation = participation - 1;
    }

    /*
    Getters & Setters
     */
    public int getParticipation() {
        return participation;
    }

    public double getBudget(){
        return budget;
    }

    public boolean isBuyer() {
        return buying;
    }

    public boolean isSeller() {
        return selling;
    }

    public ArrayList<Job> getInProgress() {
        return inProgress;
    }

    /*
    List of jobs that need to be offloaded to reach peak profit
     */
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

