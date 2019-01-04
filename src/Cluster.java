import java.util.*;

/*
Control the distribution of jobs from the Data Center down to the Cells, as progress allows
@author Jared Polonitza
 */
public class Cluster {
    // jobs that are waiting to be processed
    public Queue<Job> clusterJobs = new LinkedList<>();
    //jobs that are actively being processed
    public ArrayList<Job> inProgress = new ArrayList<>();
    public Queue<Job> finished = new LinkedList<>();
    //All cells that this cluster contains
    private ArrayList<Cell> clusterCells = new ArrayList<>();
    private Random rand = new Random();
    private double RAM;
    private double localDiskSpace;
    //Total CPU I have access too
    private double maxCPUSpace;
    //Total work I can do each minute
    private double maxCPUTasks;
    //How much [x] do I have available?
    private double availRAM;
    private double availLocalDiskSpace;
    private double availCPUSpace;
    //How fast am I?
    private double avgSpeed;
    private int id;
    private int jobsDone = 0;

    public Cluster(int id, ArrayList<String[]> specs) {
        int r = Integer.parseInt(specs.get(0)[0]);
        for (int i = 0; i < r; i++) {
            clusterCells.add(new Cell(Progress.idCell));
            Progress.idCell++;
            maxCPUTasks += clusterCells.get(i).getMaxTasks();
            maxCPUSpace += clusterCells.get(i).getTotalCPU();
            RAM += clusterCells.get(i).getTotalRam();
            avgSpeed += clusterCells.get(i).getSpeed();
        }
        this.id = id;
        localDiskSpace = 1;
        availRAM = RAM;
        availLocalDiskSpace = localDiskSpace;
        availCPUSpace = maxCPUSpace;
        avgSpeed = avgSpeed / clusterCells.size();

    }

    //Set the amount of total space in this cluster, based off number of cells
    public void availSpace() {
        double totalUsedR = 0;
        double totalUsedD = 0;
        double totalUsedC = 0;
        for (Cell c : clusterCells) {
            for (Task t : c.getInprogress()) {
                totalUsedC += t.getReqCoreSpace();
                totalUsedD += t.getReqDiskSpace();
                totalUsedR += t.getRequiredRam();
            }
        }
        if (Math.abs(totalUsedC) < Progress.EPSILON) {
            totalUsedC = 0;
        }
        if (Math.abs(totalUsedD) < Progress.EPSILON) {
            totalUsedD = 0;
        }
        if (Math.abs(totalUsedR) < Progress.EPSILON) {
            totalUsedR = 0;
        }
        availCPUSpace = maxCPUSpace - totalUsedC;
        availLocalDiskSpace = localDiskSpace - totalUsedD;
        availRAM = RAM - totalUsedR;
    }

    //Get to one of my cells
    public Cell accessCell(int index) {
        return clusterCells.get(index);
    }

    //Return completed tasks to their proper jobs
    public Queue<Job> cleanCells() {
        finished = new LinkedList<>();
        for (Cell c : clusterCells) {
            ArrayList<Queue<Task>> takeMeHome = c.clean();
            for (Queue<Task> q : takeMeHome) {
                while (!q.isEmpty()) {
                    Task done = q.poll();
                    for (Job j : inProgress) {
                        if (done.getParentId() == j.getId()) {
                            j.finishedTask(done);
                            break;
                        }
                    }
                }
            }
            ArrayList<Job> remove = new ArrayList<>();
            for (Job j : inProgress) {
                if (j.done()) {
                    j.doneExe();
                    finished.add(j);
                    remove.add(j);
                }
            }
            for (Job jR : remove) {
                inProgress.remove(jR);
            }
            jobsDone += remove.size();
        }
        availSpace();
        return finished;
    }


    //Method to send the head of the queue of jobs to a cell
    public void primeThePump() {
        boolean runnin = true;
        while (!clusterJobs.isEmpty() && runnin) {
            //check to see if the head of the list can be taken into the next level of the system
            Job j = clusterJobs.peek();
            if (availCPUSpace > j.getCoreCount() && availRAM > j.getRAM()) {
                //pop the job
                j = clusterJobs.poll();
                j.inExecution();
                // move the job into the active phase
                inProgress.add(j);
                //send the next jobs tasks into the cpu for the cpu to manage
                for (int k = 0; k < j.getTasks().size(); k++) {
                    /*
                    Check to see if the machines have enough total space to bring on the job.
                    If they do start sending the tasks. There may be tasks that are too large
                    to get immediately serviced, and for these we will just send them to a random machine
                    */
                    Cell dest = new Cell(-1);
                    int min = 0;
                    double per = 1.0;
                    for (int i = 0; i < clusterCells.size(); i++) {
                        if (clusterCells.get(i).cellStress() < per) {
                            per = clusterCells.get(i).cellStress();
                            min = i;
                        }
                    }
                    dest = clusterCells.get(min);
                    if (dest.getId() != -1) {
                        if (clusterCells.get(min).availCPU() > j.getTasks().get(k).getReqCoreSpace() && clusterCells.get(min).availRAM() > j.getTasks().get(k).getRequiredRam()) {
                            clusterCells.get(min).deploy(j.getTasks().get(k));
                            clusterCells.get(min).locked();
                        }
                    }
                    else {
                        int send = rand.nextInt(clusterCells.size());
                        clusterCells.get(send).deploy(j.getTasks().get(k));
                        clusterCells.get(send).locked();
                        break;
                    }
                }
            }
            else {
                runnin = false;
            }
        }
        availSpace();
    }

    //Add incoming jobs to the job queue
    public void sweep(Job j) {
        clusterJobs.add(j);
    }

    //Do work
    public void spin() {
        for (Cell c : clusterCells) {
            c.cycle();
        }
    }

    //Secondary method for executing jobs while the center is full
    public void busy() {
        for (Cell c : clusterCells) {
            c.locked();
        }
    }

    //Do the cpus have more work they can do this minute
    public boolean more() {
        boolean[] check = new boolean[clusterCells.size()];
        int index = 0;
        for (Cell c : clusterCells) {
            check[index] = c.working();
            index++;
        }
        boolean more = false;
        for (boolean b : check) {
            if (b) {
                more = true;
            }
        }
        return more;
    }

    //Determine total watt usage
    public double cellPowerUse() {
       double watts = 0;
       for (Cell c : clusterCells) {
           watts = c.machinePowerUse();
       }
       return watts;
    }

    //How stressed am I?
    public double clusterStress() {
        double hold = 0;
        for (Cell c : clusterCells) {
            hold += c.cellStress();
        }
        return (hold / clusterCells.size());
    }

    //Find and remove a Job
    public void find(int id) {
        Queue<Job> hold = new LinkedList<>();
        if (!clusterJobs.isEmpty()) {
            Job j;
            for (int i = 0; i < clusterJobs.size(); i++) {
                j = clusterJobs.poll();
                if (j.getId() != id) {
                    hold.add(j);
                }
            }
        }
        clusterJobs = hold;
        ArrayList<Job> hold2 = new ArrayList<>();
        if (!inProgress.isEmpty()) {
            for (Job j : inProgress) {
                if (j.getId() == id) {
                    deeper(j.getId());
                    hold2.add(j);
                }
            }
        }
        for (Job j2 : hold2) {
            inProgress.remove(j2);
        }
    }

    public void deeper(int id) {
        for(Cell c : clusterCells) {
            c.taskRemoval(id);
        }
    }

    //Give CPU's back their completable tasks
    public void reset() {
        for (Cell c : clusterCells) {
            for (Machine m : c.getMachines()) {
                m.setProcUsed();
            }
        }
    }

    //Percentage CPU currently available
    public double perCPU() {
        return (availCPUSpace / maxCPUSpace);
    }
    //Percentage RAM currently available
    public double perRAM() {
        return (availRAM / RAM);
    }

    //Getters & Setters
    public ArrayList<Cell> getClusterCells() {
        return clusterCells;
    }

    public double getAvgSpeed() {
        return avgSpeed;
    }

    public double getAvailRAM() {
        return availRAM;
    }

    public double getAvailLocalDiskSpace() {
        return availLocalDiskSpace;
    }

    public double getAvailCPUSpace() {
        return availCPUSpace;
    }

    public double getMaxCPUSpace() {
        return maxCPUSpace;
    }

    public double getMaxLocalDiskSpace() {
        return localDiskSpace;
    }

    public double getMaxRam() {
        return RAM;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        int key = 1;
        String str = "";
        str += "Jobs executing on this cluster: " + inProgress + "\n";
        str += "Jobs executed on this cluster: " + jobsDone + "\n";
        //str += "Jobs waiting to be executed: " + clusterJobs.size() + "\n";
        str += "This Cluster contains: ";
//        for (Cell cpu : clusterCells) {
//            str += cpu.cellStress() + "\n";
//            str += "Cell " + cpu.getId() + ": \n" + cpu + "\n";
//        }
        return str;
    }
}
