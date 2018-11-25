import java.util.*;
/*
Modeling the core of a Google Cluster Trace Cell
@author Jared Polonitza
 */
public class Machine {
    //My work
    private Queue<Task> tasks = new LinkedList<>();
    private Queue<Task> executing = new LinkedList<>();
    private Queue<Task> finished = new LinkedList<>();
    //How full am I?
    private double percentageUsed;
    private double ramUsed;
    private double capacityCPU;
    private double capacityRAM;
    private int tProcesses;
    //Am I working?
    private boolean runnin = true;
    //How hard could I possibly work?
    private int idle;
    private int max;
    private int id;
    //How much have I done?
    private int tasksDone = 0;
    private int processesDone = 0;

    public Machine(int totalPower, int idle, int max, int id) {
        capacityCPU = 1.0;
        capacityRAM = 1.0;
        percentageUsed = 0;
        ramUsed = 0;
        tProcesses = totalPower;
        this.idle = idle;
        this.max = max;
        this.id = id;
    }

    //Run the Machine
    public void exe() {
        //total actions that can be executed this minute
        int processesNow = gettProcesses();
        runnin = true;
        boolean run = true;
        while (run) {
            if(!executing.isEmpty()) {
                Task t = executing.peek();
                //amount of work that still needs to be done on this particular task
                int workLeft = t.getNumProcesses();
                //if theres more work than can be done in a minute, remove a mins worth of work from this task
                if (workLeft > processesNow) {
                    t.workDone(processesNow);
                    t.setDataGenerated();
                    runnin = false;
                    run = false;
                    processesDone += processesNow;
                } else { //Finish this task then go see if the next task can be processed
                    processesNow = processesNow - workLeft;
                    t.workDone(workLeft);
                    t.setDataGenerated();
                    tasksDone++;
                    t = executing.poll();
                    calcSpace();
                    finished.add(t);
                    processesDone += workLeft;
                }
            }
            else {
                run = false;
                if (processesNow <= 0 || (tasks.isEmpty() && executing.isEmpty())) {
                    runnin = false;
                }
            }
        }
    }

    //Add task to machines waiting queue
    public void addTask(Task t) {
        tasks.add(t);
    }

    //method to run all the required tasks in the queue over time
    public void andLoaded() {
        boolean rejected = false;
        while (!rejected && !tasks.isEmpty()) {
            if (!tasks.isEmpty()) {
                Task t = tasks.peek();
                if ((getCapacityRAM() - getRamUsed()) > t.getRequiredRam() && (getCapacityCPU() - getCPUUsed()) > t.getReqCoreSpace()) {
                    t = tasks.poll();
                    executing.add(t);
                    calcSpace();
                } else {
                    rejected = true;
                }
            }
        }
    }

    //Scale the usage of this machine
    public double powerUsage() {
        double scale = (getCPUUsed() / capacityCPU);
        if (scale <= Progress.EPSILON) {
            return idle;
        }
        else {
            return ((capacityCPU-idle) * scale) + idle;
        }
    }

    public void pullOff(int id) {
        Queue<Task> hold = new LinkedList<>();
        if (!tasks.isEmpty()) {
            Task t;
            for (int i = 0; i < tasks.size(); i++) {
                t = tasks.poll();
                if (t.getParentId() != id) {
                    hold.add(t);
                }
            }
        }
        tasks = hold;
        Queue<Task> hold2 = new LinkedList<>();
        if (!executing.isEmpty()) {
            Task t2;
            for (int i = 0; i < executing.size(); i++) {
                t2 = executing.poll();
                if (t2.getParentId() != id) {
                    hold2.add(t2);
                }
            }
        }
        calcSpace();
        executing = hold2;
    }

    //Total CPU used at this moment
    public double getCPUUsed() {
        if (Math.abs(percentageUsed) < Progress.EPSILON) {
            return 0;
        }
        else {
            return percentageUsed;
        }
    }

    //Total RAM used at this moment
    public double getRamUsed() {
        if (Math.abs(ramUsed) < Progress.EPSILON) {
            return 0;
        }
        else {
            return ramUsed;
        }
    }

    public double machineStress() {
        return (getCPUUsed() / capacityCPU);
    }

    /*
    Getters & Setters
     */
    public int gettProcesses() {
        return tProcesses;
    }

    public double getCapacityCPU() {
        return capacityCPU;
    }

    public double getCapacityRAM() {
        return capacityRAM;
    }

    public boolean getRunning() {
        return runnin;
    }

    public int getId() {
        return id;
    }

    public Queue<Task> getFinished() {
        return finished;
    }

    public void setProcUsed() {
        processesDone = 0;
    }

    public void calcSpace() {
        double cpu = 0;
        double ram = 0;
        for (Task t : executing) {
            cpu += t.getReqCoreSpace();
            ram += t.getRequiredRam();
        }
        percentageUsed = cpu;
        ramUsed = ram;
    }

    public String toString() {
        String str = "";
        str += "Tasks Done: " + tasksDone + "\n";
        str += executing;
        return str;
    }
}
