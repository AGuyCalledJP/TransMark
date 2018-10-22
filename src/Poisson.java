import java.util.Random;
/*
Generate a Poisson arrival rate
 */
public class Poisson {
    public int poisson(double mean) {
        Random random = new Random();
        int r = 0;
        double a = random.nextDouble();
        double p = Math.exp(-mean);

        while (a > p) {
            r++;
            a = a - p;
            p = p * mean / r;
        }
        return r;
    }
}
