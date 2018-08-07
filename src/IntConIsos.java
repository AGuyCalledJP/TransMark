public class IntConIsos {
    IntCon con;

    public IntConIsos(IntCon con) {
        this.con = con;
    }

    public ISOVal[] detISO() {
        ISOVal[] isoVal;

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
