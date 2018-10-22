/*
Given a ISO, returns the states served
 */
public class IsoRegions {
    ISOVal iso;

    public IsoRegions(ISOVal iso) {
        this.iso = iso;
    }

    public StateVal[] region() {
        switch (iso) {
            case NYISO:
                return new StateVal[]{StateVal.NY};
            case CAISO:
                return new StateVal[]{StateVal.CA};
            case MISO:
                return new StateVal[]{StateVal.MN, StateVal.IA, StateVal. MO, StateVal.AR, StateVal.LA, StateVal.MS, StateVal.IL, StateVal.IN, StateVal.WI, StateVal.MI};
            case PJM:
                return new StateVal[]{StateVal.OH, StateVal.KY, StateVal.WV, StateVal.VA, StateVal.PA, StateVal.NJ, StateVal.DE, StateVal.MD};
            case SPP:
                return new StateVal[]{StateVal.OK, StateVal.KS, StateVal.NE, StateVal.SD, StateVal.ND};
            default:
                return new StateVal[]{};
        }
    }
}
