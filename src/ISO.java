//ISO Class: serves the same function as the TSO class but uses real data
public class ISO {
    private ISOVal iso;
    private double[] rates;
    private int month;

    public ISO(ISOVal iso) {
        this.iso = iso;
        rateData r = new rateData(iso);
        month = 0;
        rates = r.genData(month);
    }

    public double getRate() {
        return rates[(ClockWork.t % 60)];
    }

    public ISOVal getIso() {
        return iso;
    }

    public void giveMonth(int month) {
        this.month = month;
    }
}
