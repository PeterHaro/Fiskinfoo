package fiskinfoo.no.sintef.fiskinfoo.Baseclasses;

/**
 * Created by bardh on 15.05.2017.
 */

public enum IceConcentrationType {
    CLOSE_DRIFT_ICE, VERY_CLOSE_DRIFT_ICE, FAST_ICE, OPEN_DRIFT_ICE, VERY_OPEN_DRIFT_ICE, OPEN_WATER;



    @Override
    public String toString() {
        String retVal;
        switch(this) {
            case CLOSE_DRIFT_ICE:
                retVal = "Tett driftis";
                break;
            case VERY_CLOSE_DRIFT_ICE:
                retVal = "Veldig tett drivis";
                break;
            case FAST_ICE:
                retVal = "Fast is";
                break;
            case OPEN_DRIFT_ICE:
                retVal = "Åpen drivis";
                break;
            case VERY_OPEN_DRIFT_ICE:
                retVal = "Veldig åpen drivis";
                break;
            case OPEN_WATER:
                retVal = "Åpent vann";
                break;
            default:
                throw new UnsupportedOperationException("Ice concentration not recognised");
        }
        return retVal;
    }

    public static IceConcentrationType createFromValue(String value) {
        IceConcentrationType retval;

        switch(value) {
            case "Close Drift Ice":
                retval = IceConcentrationType.CLOSE_DRIFT_ICE;
                break;
            case "Very Close Drift Ice":
                retval = IceConcentrationType.VERY_CLOSE_DRIFT_ICE;
                break;
            case "Fast Ice":
                retval = IceConcentrationType.FAST_ICE;
                break;
            case "Open Drift Ice":
                retval = IceConcentrationType.OPEN_DRIFT_ICE;
                break;
            case "Very Open Drift Ice":
                retval = IceConcentrationType.VERY_OPEN_DRIFT_ICE;
                break;
            case "Open Water":
                retval = IceConcentrationType.OPEN_WATER;
                break;
            default:
                throw new UnsupportedOperationException("Ice concentration not recognised");
        }
        return retval;
    }
}
