import javax.print.DocFlavor;

public class IntConVal {
    IntCon con;

    public IntConVal(IntCon con) {
        this.con = con;
    }

    public StateVal[][] detCon() {
        StateVal[][] regions;

        switch (con) {
            case WESTERN:
                regions = new StateVal[][]{{StateVal.CA}, {StateVal.AZ, StateVal.NM}, {StateVal.CO, StateVal.ID, StateVal.MT, StateVal.NV,
                        StateVal.OR, StateVal.UT, StateVal.WA, StateVal.WY}};
                return regions;
            case EASTERN:
                regions = new StateVal[][]{{StateVal.ND, StateVal.SD, StateVal.NE, StateVal.KS, StateVal.OK}, {StateVal.AR, StateVal.LA},
                                          {StateVal.MN, StateVal.WI, StateVal.IA, StateVal.MI}, {StateVal.MO, StateVal.IL, StateVal.IN},
                                          {StateVal.MS, StateVal.TN}, {StateVal.AL, StateVal.GA}, {StateVal.FL}, {StateVal.NC, StateVal.SC},
                                          {StateVal.KY, StateVal.OH, StateVal.PA, StateVal.VA, StateVal.WV, StateVal.DE, StateVal.NJ, StateVal.MD},
                                          {StateVal.NY}, {StateVal.MA, StateVal.VT, StateVal.NH, StateVal.RI, StateVal.CT}};
                return regions;
            case TEXAS:
                regions = new StateVal[][]{{StateVal.TX}};
                return regions;
            default:
                regions = new StateVal[1][1];
                return regions;
        }
    }
}
