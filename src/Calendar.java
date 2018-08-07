public class Calendar {
    private int[] daysInMonth = new int[]{31,28,31,30,31,30,31,31,30,31,30,31};

    public Calendar() {}

    public int getMonth(int currentT){
        int hold = 0;
        int months = 0;
        int minPDay = 1440;
        int currentMo = 1;
        while(hold < currentT){
            currentMo = months % 12;
            hold += (minPDay * daysInMonth[currentMo]);
            months++;
        }
        return (months % 12);
    }

    public int getWeek(int currentT){
        double[] weeksInMo = new double[]{4,4.3,4.4};
        double[] minsInMo = new double[]{40320.0,43200.0, 44640.0};
        int hold = 0;
        int weeks = 0;
        while(hold < currentT){
            int daysInM = daysInMonth[getMonth(hold)];
            if(daysInM == 28){
               hold += minsInMo[0] / weeksInMo[0];
               weeks += weeksInMo[0];
            }
            else if(daysInM == 30) {
                hold += minsInMo[1] / weeksInMo[1];
                weeks += weeksInMo[1];
            }
            else {
                hold += minsInMo[2] / weeksInMo[2];
                weeks += weeksInMo[2];
            }
        }
        return ((weeks % 52));
    }

    public int getDay(int currentT){
        int hold = 0;
        int days = 0;
        while(hold < currentT){
            hold += 1440;
            days++;
        }
        return ((days % 365));
    }

    public int getHour(int currentT){
        int hold = 0;
        int hours = 0;
        while(hold < currentT){
            hold += 60;
            hours++;
        }
        return (hours % 24);
    }
}
