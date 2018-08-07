import java.util.*;

public class JobMaster {
    private Poisson p = new Poisson();
    Random rand = new Random();
    private double lambda;
    private int arrivalsNow;
    private int speed;
    private int numClust;
    private Calendar cal = new Calendar();
    double taskSplit;

    public JobMaster(int speed, double taskSplit, int numClust){
        double lambda;
        if (numClust > 1) {
            lambda = rand.nextInt((numClust - 1)) + 1; //Want this value to be a calculation that gets scaled with the size of the current data center
        }
        else {
            lambda = 1.0;
        }
        this.lambda = lambda;
        this.speed = speed;
        this.taskSplit = taskSplit;
        arrivalsNow = p.poisson(lambda);
    }

    public Queue<Job> genJobs(){
        Queue<Job> jobs = new LinkedList<>();
        arrivalsNow = p.poisson(lambda);
        for(int i = 0; i < arrivalsNow; i++){
            TaskTypes t = findTaskType();
            jobs.add(new Job(ClockWork.t, Progress.idJob, t, speed));
            Progress.idJob++;
        }
        return jobs;
    }

    public TaskTypes findTaskType() {
        //assign each job a task class
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

    public void setLambda(){
        double lambda;
        if (numClust > 1) {
            lambda = rand.nextInt((numClust - 1)) + 1; //Want this value to be a calculation that gets scaled with the size of the current data center
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

    public void simArrival(int time) {
        int globalTime = cal.getHour(ClockWork.t);
        int localTime = (((globalTime +  time) % 24) + 24) % 24; //convert whatever time it is locally to UTC
        int localMin = ClockWork.t % 60;
        lambda =  detL(localTime, localMin);
//        System.out.println(lambda);
    }

    public double detL(int localT, int localM) {
        double scale = 1.0;
        double mins = 60;
        double maxArrivalRate = 0;
        if (numClust == 1) {
            maxArrivalRate = 1.0;
        }
        else if (numClust == 2) {
            maxArrivalRate = 1.5;
        }
        else if (numClust == 3) {
            maxArrivalRate = 2;
        }
        else if (numClust == 4) {
            maxArrivalRate = 2.5;
        }
        else {
            maxArrivalRate = 3;
        }
        if (localT < 8 || localT >= 20 ) {
            localT = (((localT - 12) % 12) + 12) % 12;
        }
        if (localT > 8 && localT < 20) {
            scale = (scale / 12);
            double minScale = (scale / mins);
            double mult = 0;
            for (int j = 0; j < localT; j++) {
                mult += scale;
            }
            for (int k = 0; k < localM; k++) {
                mult += minScale;
            }
            return (maxArrivalRate * mult);
        }
        else if (localT == 8) {
            return maxArrivalRate;
        }
        else {
            scale = (scale / 12);
            double minScale = (scale / mins);
            double mult = 0;
            for (int i = 0; i < localT; i++) {
                mult += scale;
            }
            for (int l = 0; l < localM; l++) {
                mult += minScale;
            }
            mult = 1 - mult;
            return  (maxArrivalRate * mult);
        }
    }

    public double getLambda() {
        return lambda;
    }
}
