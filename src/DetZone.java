public class DetZone {
    StateVal s;

    public DetZone(StateVal s) {
        this.s = s;
    }

    public TimeZone detZone(){
        switch (s){
            case CA:
                return TimeZone.PST;
            case WA:
                return TimeZone.PST;
            case OR:
                return TimeZone.PST;
            case NV:
                return TimeZone.PST;
            case AZ:
                return TimeZone.MST;
            case CO:
                return TimeZone.MST;
            case ID:
                return TimeZone.MST;
            case NM:
                return TimeZone.MST;
            case UT:
                return TimeZone.MST;
            case WY:
                return TimeZone.MST;
            case AL:
                return TimeZone.CST;
            case IA:
                return TimeZone.CST;
            case AR:
                return TimeZone.CST;
            case IL:
                return TimeZone.CST;
            case KS:
                return TimeZone.CST;
            case KY:
                return TimeZone.CST;
            case MS:
                return TimeZone.CST;
            case LA:
                return TimeZone.CST;
            case MN:
                return TimeZone.CST;
            case ND:
                return TimeZone.CST;
            case SD:
                return TimeZone.CST;
            case NE:
                return TimeZone.CST;
            case OK:
                return TimeZone.CST;
            case MO:
                return TimeZone.CST;
            case TN:
                return TimeZone.CST;
            case TX:
                return TimeZone.CST;
            default:
                return TimeZone.EST;
        }
    }
}
