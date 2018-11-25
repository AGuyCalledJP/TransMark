/*
ISO models a major Regional Transmissions Operator, taking in a time value, and returning the given energy rate according to that time
@author Jared Polonitza
 */
public class ISO {
    //Enum for associated ISO
    private ISOVal iso;
    //List of energy rates for the month
    private double[] rates;
    //Integer representation of the current month
    private int month;
    //Data retrieval tool
    private rateData r;

    public ISO(ISOVal iso) {
        //Which ISO am I modeling
        this.iso = iso;
        //Associate rateData with the given ISO
        r = new rateData(iso);
        //Set start month
        month = 0;
        //Pull rate data for the given month
        rates = r.genData(month);
    }

    //Return rate data for a given minute
    public double getRate(int t) {
        return rates[t];
    }

    //Return enum value of ISO being modeled
    public ISOVal getIso() {
        return iso;
    }

    //Change month
    public void giveMonth(int month) {
        this.month = month;
        rates = r.genData(month);
    }
}
