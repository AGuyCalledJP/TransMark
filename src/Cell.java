import java.util.*;

public class Cell {
    private ArrayList<Machine> machines = new ArrayList<>();
    private Queue<Task> tasks = new LinkedList<>();
    private ArrayList<Task> inProgress = new ArrayList<>();
    private ArrayList<Queue<Task>> completed = new ArrayList<Queue<Task>>();
    private int maxTasks;
    private double totalCPU;
    private double totalRam;
    private int basePowerUse;
    private int maxPowerUse;
    private Random rand = new Random();
    private int id;

    public Cell(int id, String[] specs) {
        int speed = Integer.parseInt(specs[0]);
        int theoMax = (speed * 10); //max tasks per minute
        maxTasks = speed;
        int numMachines = Integer.parseInt(specs[1]);

        //Idle wattage vs max wattage
        basePowerUse = Integer.parseInt(specs[2]);
        maxPowerUse = Integer.parseInt(specs[3]);

        //total measures of performance for the machines of the cpu
        int machinePower = (theoMax/numMachines);
        int machineIdle = (basePowerUse/numMachines);
        int maxPower = (maxPowerUse/numMachines);

        //How many machines do I have? They all have 10 for now
        for(int i = 0; i < numMachines; i++){
            machines.add(new Machine(machinePower,machineIdle,maxPower,Progress.idMachine));
            Progress.idMachine++;
            totalCPU += machines.get(i).getCapacityCPU();
            totalRam += machines.get(i).getCapacityRAM();
        }

        this.id = id;
    }

    public Cell(int id) {
        this.id = id;
    }

    //translate and add together the total energy being used by all the machines
    //machine power is calculated by translating total percentage of machine used 0 -> 100 to power usage 161 -> 230
    public double machinePowerUse(){
        double watts = 0;
        for(Machine m : machines) {
            watts += m.powerUsage();
        }
        return watts;
    }

    public double cpuInUse(){
        double totalPer = 0;
        for(Machine m : machines){
            totalPer += m.getCPUUsed();
        }
        return totalPer;
    }

    public double ramInUse(){
        double totalPer = 0;
        for(Machine m : machines){
            totalPer += m.getRamUsed();
        }
        return totalPer;
    }

    //returns total available cpu room on server
    public double availCPU(){
        if (Math.abs(totalCPU) < Progress.EPSILON) {
            return 0;
        }
        else{
            return totalCPU;
        }
    }

    public double availRAM(){
        if (Math.abs(totalRam) < Progress.EPSILON) {
            return 0;
        }
        else{
            return totalRam;
        }
    }


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

    public void cycle(){
        for (Machine m : machines) {
            m.exe();
        }
    }

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

    public double cellStress() {
        double hold = 0;
        for (Machine m : machines) {
            hold += m.machineStress();
        }
        return (hold / machines.size());
    }

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

    public void machineRemoval(int id) {
        for (Machine m : machines) {
            m.pullOff(id);
        }
    }

    public int tax() {
        int total = 0;
        for (Machine m : machines) {
            total += m.collector();
        }
        return total;
    }


    //Getters
    public int getNumMachines(){
        return machines.size();
    }

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
       int key = 1;
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
