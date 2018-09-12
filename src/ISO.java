//ISO Class: serves the same function as the TSO class but uses real data
public class ISO {
    private ISOVal iso;
    private double[] rates;

    public ISO(ISOVal iso) {
        this.iso = iso;
        rateData r = new rateData(iso);
        rates = r.getData();
    }

    public double getRate() {
        return rates[(ClockWork.t % 60)];
    }

    public ISOVal getIso() {
        return iso;
    }
}
