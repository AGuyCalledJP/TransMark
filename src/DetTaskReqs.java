import java.util.Random;
/*
Given a task type Enum, Return a string in the range of said task type
@author Jared Polonitza
 */
public class DetTaskReqs {
    private TaskTypes t;
    private int speed;
    private String pull;
    private Random rand = new Random();

    public DetTaskReqs(TaskTypes t, int speed) {
        this.t = t;
        this.speed = speed;
        pull = t.toString();
    }

    //How long?
    public int detDuration() {
        if (pull.substring(0,1).equals("S")) {
            return rand.nextInt(speed * 57) + speed * 3; //run between 3 minutes and 30 minutes
        }
        else {
            return rand.nextInt(speed * 360) + speed * 1080; //run between 18 and 24 hours
        }
    }

    //How much CPu
    public double detCore() {
        if (pull.substring(1,2).equals("S")) {
            double y = rand.nextInt(20);
            return (y/100);
        }
        else if (pull.substring(1,2).equals("M")) {
            double y = (rand.nextInt(29) + 21);
            return (y/100);
        }
        else {
            double y = (rand.nextInt(50) + 50);
            return (y/100);
        }
    }

    //How much RAM
    public double detMem() {
        if (pull.substring(2,3).equals("S")) {
            double y = rand.nextInt(20);
            return (y/100);
        }
        else if (pull.substring(2,3).equals("M")) {
            double y = (rand.nextInt(29) + 21);
            return (y/100);
        }
        else {
            double y = (rand.nextInt(50) + 50);
            return (y/100);
        }
    }

    //How much Local Disk
    public double detLD() {
        if (pull.substring(3).equals("S")) {
            return (rand.nextDouble()/10000);
        }
        else if (pull.substring(3).equals("M")) {
            return (rand.nextDouble()/1000);
        }
        else {
            return (rand.nextDouble()/100);
        }
    }

}
