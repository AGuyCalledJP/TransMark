/*
Determine offset from UTC for the timezones of the US
 */
public class DetTime {
    TimeZone t;

    public DetTime(TimeZone t){
        this.t = t;
    }

    public int convTime(){
        switch(t){
            case CST:
                return -6;
            case EST:
                return -5;
            case MST:
                return -7;
            case PST:
                return -8;
            default:
                return 0;
        }
    }
}
