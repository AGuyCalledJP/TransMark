import java.util.Random;
/*
Model a single core linux based task
 */
public class Task {
    //How much of each do I need
    private double reqCoreSpace;
    private double requiredRam;
    private double reqDiskSpace;
    //How much data will I start with?
    private double dataIn;
    //How much data will I finish with?
    private double dataOut;
    //How much do I grow by per minute
    private double increment;
    //How long will it take for me to finish?
    private int numProcesses;
    private Random rand = new Random();
    private int id;
    //What job do I belong too>
    private int parentId;
    //How long should it take for me to complete given the speed of a cpu
    private double estTimeToComplete;

    public Task(TaskTypes t, int id, int parentId, int speed){
        //Get what type of task I am
        DetTaskReqs d = new DetTaskReqs(t, speed);
        reqCoreSpace = d.detCore();
        requiredRam = d.detMem();
        reqDiskSpace = d.detLD();
        numProcesses = d.detDuration();
        estTimeToComplete = numProcesses / speed;
        this.id = id;
        this.parentId = parentId;
        dataOut = reqDiskSpace;
        double me = rand.nextDouble();
        if (me < .5) {
            dataIn = (dataOut * me);
        }
        else {
            dataIn = (dataOut * .6);
        }
        increment = ((dataOut - dataIn)/estTimeToComplete);
    }

    //Work done to a job after it has reached the machine level in the data center
    public void setDataGenerated() {
        dataIn += increment;
    }

    //Getters & Setters
    public double getReqCoreSpace(){
        return reqCoreSpace;
    }

    public double getEstTimeToComplete() {
        return estTimeToComplete;
    }

    public double getRequiredRam(){
        return requiredRam;
    }

    public double getReqDiskSpace(){
        return reqDiskSpace;
    }

    public int getNumProcesses() {
        return numProcesses;
    }

    public int getParentId() {
        return parentId;
    }

    public int getId() {
        return id;
    }

    public double getDataIn() {
        return dataIn;
    }

    public void workDone(int tasksDone){
        numProcesses = numProcesses - tasksDone;
    }

    public String toString() {
        String str = "";
        str += "Task Id: " + id + "\n";
        str = "This task requires: " + getReqCoreSpace() + " core space, ";
        str +=  getRequiredRam() + " RAM, ";
        str += "This task belongs to: " + getParentId() + "\n";
        return str;
    }
}
