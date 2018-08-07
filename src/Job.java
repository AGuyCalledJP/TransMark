import java.util.*;

//ADD IN MIGRATION OVERHEAD
public class Job implements Comparable<Job>{
    private ArrayList<Task> tasks = new ArrayList<>(); //needs to be an array list so that a system can determine its ability to take the whole job on. It is also going to be more efficient when implementing the movement of jobs between centers
    private ArrayList<Task> finishedTasks = new ArrayList<>();
    private Random rand = new Random();
    public static final int MAXPROC = 21199 * 10;
    public static final double MAXCORES = 2.0;
    public static final double MAXRAMS = 2.0;
    public static final double MAXCOREM = 5.0;
    public static final double MAXRAMM = 5.0;
    public static final double MAXCOREL = 10.0;
    public static final double MAXRAML = 10.0;
    public static final double MAXDISKL = .1;
    public static final double MAXDISKM = .01;
    public static final double MAXDISKS = .001;
    public static final double MAXSPACE = 400;
    private double revenue;
    private int timeSensitive;
    private int estCompletionTime;
    private double migrationTime;
    private double coreCount;
    private double RAM;
    private double localDisk;
    private int processes;
    private int id;
    private int totalTasks;
    private int timeRented;
    private double weight;
    private double centerWeight;
    private boolean executing = false;
    private double relCost = 0;
    private int start;
    private String jobType;
    private String jobClass;
    private boolean transfered = false;
    private boolean failed = false;

    public Job(int currentTime, int id, TaskTypes t, int speed){
        //penalty = ((int)(rand.nextDouble() * (double)(reqCoreSpace)));
        double split = rand.nextDouble();
        if (split > .7) {
            timeSensitive = 1;
        }
        else {
            timeSensitive = 10;
        }
        this.id = id;
        jobClass = t.toString();
        jobType = t.toString().substring(0,1);
        int numTasks = rand.nextInt(9) + 1;
        for(int i = 0; i < numTasks; i++){
            tasks.add(new Task(t, Progress.idTask, id, speed));
            Progress.idTask++;
        }
        totalTasks = numTasks;
        calcReq();
        weight();
        calcMigTime();
        start = tasks.size();
//        System.out.println("time in mins to complete: " + estCompletionTime);
    }

    public void calcReq(){
        coreCount = 0;
        RAM = 0;
        localDisk = 0;
        processes = 0;
        estCompletionTime = 0;
        for(Task t : tasks){
            coreCount += t.getReqCoreSpace();
            RAM += t.getRequiredRam();
            localDisk += t.getReqDiskSpace();
            processes += t.getNumProcesses();
            estCompletionTime += t.getEstTimeToComplete();
        }
        estCompletionTime = estCompletionTime + 15;
    }

    public void calcMigTime() {
        double total = 0;
        for (Task t : tasks) {
            total += t.getRequiredRam();
            total += t.getDataIn();
        }
        total = total / (MAXRAML + MAXDISKL);
        migrationTime = total;
    }

    public boolean done(){
        if (totalTasks == finishedTasks.size()){
            return true;
        }
        else {
            return false;
        }
    }

    public void weight() {
        double total = 0;
        for (Task t : tasks) {
            total += t.getReqCoreSpace();
            total += t.getRequiredRam();
            total += t.getReqDiskSpace();
        }
        total = total / (MAXCOREL + MAXDISKL + MAXRAML);
        weight = total;
    }

    public void migPenalty(double band1, double band2) {
        double totalSize = migrationTime * MAXSPACE;
        double secondsToUpload = totalSize / band1;
        double secondsToDownload = totalSize / band2;
        double penalty = (secondsToDownload / 10) + (secondsToUpload / 10);
        int penaltyBox = 0;
        if (penalty > 1) {
            penaltyBox = (int)(penalty);
        }
        else {
            penaltyBox = 1;
        }
        timeRented = timeRented - penaltyBox;
    }

    public double getTotalSize() {
        return migrationTime * MAXSPACE;
    }

    //Getters
    public double getRevenue(){
        return revenue;
    }

    public double getCoreCount(){
        return coreCount;
    }

    public int getEstCompleteionTime(){
        return estCompletionTime;
    }

    public int getId() {return id;}

    public double getMigrationTime(){
        return migrationTime;
    }

    public double  getRAM() {
        return RAM;
    }

    public double getLocalDisk(){
        return localDisk;
    }

    public ArrayList<Task> getTasks() {
       return tasks;
    }

    public int numTasksFinished() {
        return finishedTasks.size();
    }

    public boolean timeUp() {
        if (timeRented <= 0) {
            return true;
        }
        else {
            return false;
        }
    }

    public int timeLeft() {
        return timeRented;
    }

    public double getWeight() {
        return weight;
    }

    public double getCenterWeight() {
        return centerWeight;
    }

    public boolean exe() {
        return executing;
    }

    //Setters
    public void setMigTime() {
        calcMigTime();
    }
    public void timeRented(int t, double rate) {
        timeRented = t;
        estCompletionTime = timeRented;
        if (timeRented < 60) {
            revenue = 1 * rate;
        }
        else {
            revenue = ((double) timeRented / 60.0) * rate;
        }
    }

    public int compareTo(Job j) {
        if(timeSensitive > j.getTimeSensitive()) {
            return 1;
        } else if (timeSensitive < j.getTimeSensitive()) {
            return -1;
        } else {
            return 0;
        }
    }

    public int getTimeSensitive() {
        return timeSensitive;
    }
    public void timePass() {
        timeRented = timeRented - 1;
    }

    public void finishedTask(Task t){
        finishedTasks.add(t);
    }

    public void setCenterWeight(double centerWeight) {
        this.centerWeight = centerWeight;
    }

    public void inExecution() {
        executing = true;
    }

    public void doneExe() {
        executing = false;
    }

    public void relativeCost(double maxCost) {
        relCost = maxCost * centerWeight;
    }

    public double getRelCost() {
        return relCost;
    }

    public String jobType() {
        return jobType;
    }

    public void setRevenue(double rev) {
        revenue = rev;
    }

    public void setTimeSensitive() {
        timeSensitive = timeSensitive - 1;
    }

    public boolean getTransfered() {
        return transfered;
    }

    public void setFailed() {
        failed = true;
    }

    public boolean isFailed() {
        return failed;
    }

    public void setTransfered() {
        transfered = true;
    }

    @Override
    public String toString() {
        String str = Integer.toString(id) + ": ";
        str += "time Remaining: " + timeLeft() + ", ";
//        str += "original tasks: " +tasks.size() + ", ";
//        str += "Finished: " + finishedTasks.size() + ", ";
        str += "job type: " + jobType() + ", ";
        str += "Full class: " + jobClass + ", ";
        str += "revenue: " + getRevenue() + ", ";
        str += "rel Cost: " + getRelCost() + ", ";
        str += "weight: " + getCenterWeight() + ", ";
        str += "transfered: " + transfered + ", ";
        str += "Migration time: " + getMigrationTime() + "\n";
       // str += "Req Core Space: " + getCoreCount() + ", ";
       // str += "Necessary RAM: " + getRAM() + ", ";
//        str += "Deadline: " + getDeadline() + ", ";
        //str += "Num tasks: " + tasks.size() + ", ";
       // str += "Local Disk Space Required: " + getLocalDisk() + ".\n";
        return str;
    }

}
