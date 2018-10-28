import java.util.Random;
/*
Simulate weather patterns for a given state
Currently unused
 */
public class Weather {
    StateVal s;
    int high;
    int low;
    int mid;
    double dayHigh;
    double dayLow;
    private Calendar c = new Calendar();
    private Random rand = new Random();

    public Weather(StateVal s) {
        this.s = s;
        StateWeather sw = new StateWeather(s);
        double[] weather = sw.getTemp();
        high = (int)(10 * weather[0]);
        low = (int)(10 * weather[1]);
        mid = high - ((high - low)/2);
    }

    //Set the day high temp and low temp. Called at the start of every day
    public void setExtremes() {
        int month = c.getMonth(ClockWork.t);
        int cand1 = 0;
        int cand2 = 0;
        if (month == 11 || month == 0 || month == 1) {
            cand1 = rand.nextInt(50) + (low + 10);
            cand2 = rand.nextInt(50) + (low - 25);
        }
        else if (month == 2 || month == 3 || month == 4 || month == 8 || month == 9 || month == 10) {
            cand1 = rand.nextInt(high-low) + (mid + 10);
            cand2 = rand.nextInt(high-low) + (low - 10);
        }
        else {
            cand1 = rand.nextInt(50) + (high + 10);
            cand2 = rand.nextInt(50) + (high - 25);
        }
        if (cand2 > cand1){
            setExtremes();
        }
        else {
            dayHigh = (cand1 / 10);
            dayLow = (cand2 / 10);
        }
    }

    //Returns a breakdown of the temperatures for the entire day. this is eventually the structure the TSO engine will be shifted to. This allows for the intake of outside data into the system
    public double[] temperature() {
        double [] temp = new double[1440];
        int up = 720;
        int down = 1440;
        double incrememnt = (dayHigh - dayLow) / up;
        double base = dayLow;
        for (int i = 0; i < up; i++) {
            if(i == 0) {
                temp[i] = base;
                base += incrememnt;
            }
            temp[i] = base;
            base += incrememnt;
        }
        base = dayHigh;
        for (int j = up; j < down; j++) {
            if(j == up) {
                temp[j] = base;
                base = base - incrememnt;
            }
            temp[j] = base;
            base = base - incrememnt;
        }
        return temp;
    }

    public double getDayHigh() {
        return dayHigh;
    }

    public double getDayLow() {
        return dayLow;
    }
}
