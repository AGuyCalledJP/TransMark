/*
Model state weather patterns on a monthly basis
 */
public class StateWeather {
    StateVal s;

    public StateWeather(StateVal s) {
        this.s = s;
    }

    public double[] getTemp() {
        switch(s) {
            case AL:
                return new double[] {76.5, 53.5};
            case AZ:
                return new double[] {86.7, 63.4};
            case AR:
                return new double[] {72.8, 52.5};
            case CA:
                return new double[] {73.6, 48.3};
            case CO:
                return new double[] {65, 36.4};
            case CT:
                return new double[] {60.2, 44.5};
            case DE:
                return new double[] {76.2, 47.3};
            case FL:
                return new double[] {79.5, 55.7};
            case GA:
                return new double[] {71.9, 53.2};
            case ID:
                return new double[] {63.7, 41.3};
            case IL:
                return new double[] {59.3, 43.3};
            case IN:
                return new double[] {62.1, 41.3};
            case IA:
                return new double[] {60.3, 41.4};
            case KS:
                return new double[] {66, 44.1};
            case KY:
                return new double[] {65.3, 42};
            case LA:
                return new double[] {78, 61.1};
            case ME:
                return new double[] {54.5, 36.8};
            case MD:
                return new double[] {66.3, 50.6};
            case MA:
                return new double[] {58.7, 44.1};
            case MI:
                return new double[] {57.3, 53.5};
            case MN:
                return new double[] {56.5, 37.5};
            case MS:
                return new double[] {75.5, 53.6};
            case MO:
                return new double[] {65.8, 44.7};
            case MT:
                return new double[] {58.3, 32.7};
            case NE:
                return new double[] {61.5, 39.8};
            case NV:
                return new double[] {66, 35.8};
            case NH:
                return new double[] {57.8, 34.8};
            case NJ:
                return new double[] {63.3, 46.5};
            case NM:
                return new double[] {65, 34.8};
            case NY:
                return new double[] {62.3, 48};
            case NC:
                return new double[] {70.8, 48.8};
            case ND:
                return new double[] {54.8, 30.8};
            case OH:
                return new double[] {62.5, 43.3};
            case OK:
                return new double[] {72.2, 50.8};
            case OR:
                return new double[] {63.6, 42.4};
            case PA:
                return new double[] {64.7, 47};
            case RI:
                return new double[] {60.5, 42.5};
            case SC:
                return new double[] {75.3, 52.3};
            case SD:
                return new double[] {54.3, 31.3};
            case TN:
                return new double[] {72.4 , 53.6};
            case TX:
                return new double[] {79.8, 59};
            case UT:
                return new double[] {63.4, 46.2};
            case VT:
                return new double[] {55.2, 36.5};
            case VA:
                return new double[] {69.5, 48};
            case WA:
                return new double[] {60.3, 45};
            case WV:
                return new double[] {65.7, 45.7};
            case WI:
                return new double[] {55.4, 40.1};
            case WY:
                return new double[] {58.5, 34.3};
            default:
                return new double[] {0,0};
        }
    }
}
