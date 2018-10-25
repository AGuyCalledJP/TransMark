import java.util.Random;

public class Task {
    private double reqCoreSpace;
    private double requiredRam;
    private double reqDiskSpace;
    private double dataIn;
    private double dataOut;
    private double increment;
    private int numProcesses;
    private Random rand = new Random();
    private int id;
    private int parentId;
    private double estTimeToComplete;

    public Task(TaskTypes t, int id, int parentId, int speed){
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

    //Getters
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

    //setters
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
