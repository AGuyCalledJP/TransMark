/*
Given an Interconnection, return the set of operating ISO's contained within it
@author Jared Polonitza
 */
public class IntConIsos {
    IntCon con;

    public IntConIsos(IntCon con) {
        this.con = con;
    }

    public ISOVal[] detISO() {
        switch (con) {
            case WESTERN:
                return new ISOVal[]{ISOVal.CAISO};
            case EASTERN:
                return new ISOVal[]{ISOVal.SPP, ISOVal.PJM, ISOVal.MISO, ISOVal.NYISO};
            default:
                return new ISOVal[]{};
        }
    }
}
