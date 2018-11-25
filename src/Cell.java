import java.util.*;
/*
Modeling the CPU from Google Cluster Trace Data
@author Jared Polonitza
 */
public class Cell {
    //My Cores
    private ArrayList<Machine> machines = new ArrayList<>();
    //My tasks that need to finish completion
    private Queue<Task> tasks = new LinkedList<>();
    //Tasks currently executing
    private ArrayList<Task> inProgress = new ArrayList<>();
    //Tasks completed
    private ArrayList<Queue<Task>> completed = new ArrayList<>();
    //How many tasks can I complete in a minute?
    private int maxTasks;
    private double totalCPU;
    private double totalRam;
    //How much do I use when idling
    private int basePowerUse;
    //How much do I use at max capacity
    private int maxPowerUse;
    private int id;

    public Cell(int id) {
        int speed = Progress.cellSpeed;
        int theoMax = (speed * 10); //max tasks per minute
        maxTasks = speed;
        int numMachines = Progress.numCores;

        //Idle wattage vs max wattage
        basePowerUse = Progress.IdlePC;
        maxPowerUse = Progress.MaxPC;

        //total measures of performance for the machines of the cpu
        int machinePower = (theoMax/numMachines);
        int machineIdle = (basePowerUse/numMachines);
        int maxPower = (maxPowerUse/numMachines);

        //How many machines do I have?
        for(int i = 0; i < numMachines; i++){
            machines.add(new Machine(machinePower,machineIdle,maxPower,Progress.idMachine));
            Progress.idMachine++;
            totalCPU += machines.get(i).getCapacityCPU();
            totalRam += machines.get(i).getCapacityRAM();
        }

        this.id = id;
    }

    //translate and add together the total energy being used by all the machines: 0 -> 100 to power usage x -> y
    public double machinePowerUse(){
        double watts = 0;
        for(Machine m : machines) {
            watts += m.powerUsage();
        }
        return watts;
    }

    //returns total available CPU room on server
    public double availCPU(){
        if (Math.abs(totalCPU) < Progress.EPSILON) {
            return 0;
        }
        else{
            return totalCPU;
        }
    }

    //returns total available RAM room on server
    public double availRAM(){
        if (Math.abs(totalRam) < Progress.EPSILON) {
            return 0;
        }
        else{
            return totalRam;
        }
    }

    //Remove Tasks that have completed execution in order of completion
    public ArrayList<Queue<Task>> clean(){
        for(Machine m : machines){
            Queue<Task> f = m.getFinished();
            ArrayList<Task> remove = new ArrayList<>();
            for (Task t : inProgress) {
                for(Task finished : f) {
                    if (t.getId() == finished.getId()) {
                        remove.add(t);
                    }
                }
            }
            for (Task r : remove) {
                inProgress.remove(r);
            }
            completed.add(f);
        }
        return completed;
    }

    //Add tasks to queue of tasks for this cell
    public void deploy(Task t){
        tasks.add(t);
    }

    //Move tasks down to machine level to get executed
    public void locked() {
        Machine dest = new Machine(0, 0, 0, -1);
        double least = 1.0;
        boolean end = false;
         while (!tasks.isEmpty() && !end) {
            for (Machine m : machines) {
                double used = m.machineStress();
                if (used < least) {
                    least = used;
                    dest = m;
                }
            }
            if (dest.getId() != -1) {
                if ((dest.getCapacityCPU() - dest.getCPUUsed()) > tasks.peek().getReqCoreSpace() && (dest.getCapacityRAM() - dest.getRamUsed()) > tasks.peek().getRequiredRam()) {
                    Task t = tasks.poll();
                    dest.addTask(t);
                    inProgress.add(t);
                    dest.andLoaded();
                }
                else {
                    end = true;
                }
            }
            else {
                end = true;
            }
        }
    }

    //Do work
    public void cycle(){
        for (Machine m : machines) {
            m.exe();
        }
    }

    //Do I still have work to do this minute?
    public boolean working() {
        boolean[] stillGoin = new boolean[machines.size()];
        int index = 0;
        for (Machine m : machines) {
            stillGoin[index] = m.getRunning();
            index++;
        }
        boolean working = false;
        for (boolean b : stillGoin) {
            if (b) {
                working = true;
            }
        }
        return working;
    }

    //How much work am I doing right now?
    public double cellStress() {
        double hold = 0;
        for (Machine m : machines) {
            hold += m.machineStress();
        }
        return (hold / machines.size());
    }

    //Get rid of killed tasks from CPU
    public void taskRemoval(int id) {
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
        ArrayList<Task> prog = new ArrayList<>();
        if (!inProgress.isEmpty()) {
            for (Task t: inProgress) {
                if (t.getParentId() == id) {
                    machineRemoval(t.getParentId());
                    prog.add(t);
                }
            }
        }
        for (Task t2 : prog) {
            inProgress.remove(t2);
        }
    }

    //Remove job from the machine
    public void machineRemoval(int id) {
        for (Machine m : machines) {
            m.pullOff(id);
        }
    }

    /*
    Getters & Setters
     */
    public int getSpeed() {
        return maxTasks;
    }

    public int getMaxTasks(){
        return maxTasks;
    }

    public ArrayList<Machine> getMachines() {
        return machines;
    }

    public double getTotalCPU(){
        return totalCPU;
    }

    public double getTotalRam(){
        return  totalRam;
    }

    public int getIdleSpeed() {
        return basePowerUse;
    }

    public int getMaxPowerUse() {
        return maxPowerUse;
    }

    public int getId() {
        return id;
    }

    public ArrayList<Task> getInprogress() {
        return inProgress;
    }

    //toString
    public String toString(){
       String str = "";
       str += "Waiting Tasks: " + tasks.size() + "\n";
       str += "Executing Tasks: " + inProgress.size() + "\n";
       for(Machine c : machines){
           str += c.machineStress() + "\n";
           str += "Machine " + c.getId() + ": " + c + "\n";
       }
       return str;
    }

}
