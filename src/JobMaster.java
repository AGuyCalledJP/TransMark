import java.util.*;
/*
JobMaster, given an average speed of CPU, will create a task to model a given split SSSS -> LLLL
@author Jared Polonitza
 */
public class JobMaster {
    private Poisson p = new Poisson();
    Random rand = new Random();
    private double lambda;
    private int arrivalsNow;
    private int speed;
    private int numClust;
    double taskSplit;

    public JobMaster(int speed, double taskSplit, int numClust){
        double lambda;
        //Want this value to be a calculation that gets scaled with the size of the current data center
        if (numClust > 1) {
            lambda = rand.nextInt((numClust - 1)) + 1;
        }
        else {
            lambda = 1.0;
        }
        this.lambda = lambda;
        this.speed = speed;
        this.taskSplit = taskSplit;
        this.numClust = numClust;
        arrivalsNow = p.poisson(lambda);
    }

    public JobMaster(int speed, double taskSplit, int numClust, double lambda){
        this.lambda = lambda;
        this.speed = speed;
        this.taskSplit = taskSplit;
        this.numClust = numClust;
        arrivalsNow = p.poisson(lambda);
    }

    //Generate a series of jobs based on your lambda and a Poisson rate
    public Queue<Job> genJobs(){
        Queue<Job> jobs = new LinkedList<>();
        arrivalsNow = p.poisson(lambda);
        for(int i = 0; i < arrivalsNow; i++){
            TaskTypes t = findTaskType();
            jobs.add(new Job(Progress.idJob, t, speed));
            Progress.idJob++;
        }
        return jobs;
    }

    //assign each job a task class
    public TaskTypes findTaskType() {
        TaskTypes[] small = new TaskTypes[]{TaskTypes.SSSS, TaskTypes.SMSS, TaskTypes.SMSM, TaskTypes.SMSL, TaskTypes.SMMS, TaskTypes.SMMM, TaskTypes.SMML, TaskTypes.SMLS, TaskTypes.SMLM, TaskTypes.SMLL, TaskTypes.SLSS, TaskTypes.SLSM, TaskTypes.SLMS, TaskTypes.SLMM, TaskTypes.SLLL};
        TaskTypes[] large= new TaskTypes[]{TaskTypes.LSSS, TaskTypes.LSLM, TaskTypes.LSLL, TaskTypes.LMSS, TaskTypes.LMSM, TaskTypes.LMMS, TaskTypes.LMMM, TaskTypes.LLSS, TaskTypes.LLSM, TaskTypes.LLMS, TaskTypes.LLMM, TaskTypes.LMLM, TaskTypes.LMLL, TaskTypes.LLML, TaskTypes.LLLM, TaskTypes.LLLL};
        double r = rand.nextDouble();
        double split = rand.nextDouble();
        if (r < taskSplit) {
            if (split < taskSplit) {
                int index = rand.nextInt(5);
                return small[index];
            }
            else {
                int index = rand.nextInt(8) + 6;
                return small[index];
            }
        }
        else {
            if (split < taskSplit) {
                int index = rand.nextInt(6);
                return large[index];
            }
            else {
                int index = rand.nextInt(9) + 7;
                return large[index];
            }
        }
    }

    //Want this value to be a calculation that gets scaled with the size of the current data center
    public void setLambda(){
        double lambda;
        if (numClust > 1) {
            lambda = rand.nextInt((numClust - 1)) + 1;
        }
        else {
            double fix = rand.nextDouble();
            if (fix > .5) {
                lambda = 1.0;
            }
            else {
                lambda = 0.0;
            }
        }
        this.lambda = lambda;
    }
}
