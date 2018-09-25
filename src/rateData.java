import java.util.ArrayList;

public class rateData {
    private ISOVal iso;
    private SPPIsoData S = new SPPIsoData();
    private PJMData P = new PJMData();
    private MISOData M = new MISOData();
    private CAISOData C = new CAISOData();
    private NYIso N = new NYIso();
    public rateData(ISOVal iso) {
        this.iso = iso;
    }

    public double[] getData(int month) {
        switch (iso) {
            case PJM:
                return P.getMonthPJM(month);
            case SPP:
                return S.getMonthSPP(month);
            case MISO:
                return M.getMonthMISO(month);
            case CAISO:
                return C.getMonthCAISO(month);
            case NYISO:
                return N.getMonthNYISO(month);
            default:
                return new double[] {0};
        }
    }
}
