/*
Translate an integer into a date value
@author Jared Polonitza
 */
public class Calendar {
    //List of total number of days for a given month in the year
    private int[] daysInMonth = new int[]{31,28,31,30,31,30,31,31,30,31,30,31,0};

    public Calendar() {}

    /*
    Get month of the year given a random integer from 0-n
     */
    public int getMonth(int currentT){
        int hold = 0;
        int months = 0;
        int minPDay = 1440;
        while(hold <= currentT && months < daysInMonth.length){
            hold += (minPDay * daysInMonth[months]);
            months++;
        }
        return (months % 13);
    }

    /*
    Get day of the week in the current month given a random integer from 1 - n
     */
    public int getDayInMonth(int currentT) {
        System.out.println(currentT);
        int hold = 0;
        int month = 0;
        int day = 1;
        int totalDays = 0;
        while(totalDays < (currentT / 1440)){
            hold = daysInMonth[month];
            if (day % hold == 0) {
                day = 1;
                month++;
            }
            else {
                day++;
            }
            totalDays++;
        }
        return day;
    }

    /*
    Calculate week in the current month given a random integer from 1 - n
     */
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

    /*
    Calculate the day in the current month given an integer from 1 - n
     */
    public int getDay(int currentT){
        int hold = 0;
        int days = 0;
        while(hold < currentT){
            hold += 1440;
            days++;
        }
        return ((days % 365));
    }

    /*
    Calculate the hour in the current day given an integer from 1 - n
    */
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
