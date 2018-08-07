public class DetStateVal {
    StateVal state;

    public DetStateVal(StateVal state){
        this.state = state;
    }

    public int detVal(){
        switch(state) {
            case AL:
                return 1;
            case AZ:
                return 8;
            case AR:
                return 1;
            case CA:
                return 5;
            case CO:
                return 2;
            case FL:
                return 10;
            case GA:
                return 1;
            case ID:
                return 1;
            case IL:
                return 1;
            case IA:
                return 1;
            case KS:
                return 1;
            case KY:
                return 1;
            case MO:
                return 1;
            case MT:
                return 4;
            case NV:
                return 1;
            case NH:
                return 1;
            case NM:
                return 2;
            case NY:
                return 1;
            case NC:
                return 4;
            case OH:
                return 1;
            case SC:
                return 3;
            case TN:
                return 1;
            case TX:
                return 1;
            case OR:
                return 4;
            case WA:
                return 7;
            case WV:
                return 1;
            case WY:
                return 1;
            default:
                return 0;
        }
    }
}
