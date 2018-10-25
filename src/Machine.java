import java.util.*;

public class Machine {
    private Queue<Task> tasks = new LinkedList<>();
    private Queue<Task> executing = new LinkedList<>();
    private Queue<Task> finished = new LinkedList<>();
    private double percentageUsed;
    private double ramUsed;
    private double capacityCPU;
    private double capacityRAM;
    private int tProcesses;
    private int idle;
    private boolean runnin = true;
    private int max;
    private int id;
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
        int processesNow = gettProcesses(); //total actions that can be executed this minute
        runnin = true;
        boolean run = true;
        while (run) {
            if(!executing.isEmpty()) {
                Task t = executing.peek();
                int workLeft = t.getNumProcesses(); //amount of work that still needs to be done on this particular task

                if (workLeft > processesNow) { //if theres more work than can be done in a minute, remove a mins worth of work from this task
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
//            System.out.println("be strong");
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

    //Getters
    public double getCPUUsed() {
        if (Math.abs(percentageUsed) < Progress.EPSILON) {
            return 0;
        }
        else {
            return percentageUsed;
        }
    }

    public double getRamUsed() {
        if (Math.abs(ramUsed) < Progress.EPSILON) {
            return 0;
        }
        else {
            return ramUsed;
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

    public int collector() {
        return processesDone;
    }

    public double machineStress() {
        return (getCPUUsed() / capacityCPU);
    }

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

    public int idleSpeed() {
        return idle;
    }

    public int maxSpeed() {
        return max;
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
