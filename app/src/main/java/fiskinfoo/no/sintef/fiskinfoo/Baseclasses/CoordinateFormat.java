package fiskinfoo.no.sintef.fiskinfoo.Baseclasses;

import android.app.Activity;

import fiskinfoo.no.sintef.fiskinfoo.Interface.LocationProviderInterface;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.CoordinatesRow;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.DegreesDecimalMinutesRow;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.DegreesMinutesSecondsRow;

public enum CoordinateFormat {
    DEGREES_MINUTES_SECONDS, DEGREES_DECIMAL_MINUTES;

    @Override
    public String toString() {
        String retVal;
        switch(this) {
            case DEGREES_MINUTES_SECONDS:
                retVal = "Grader, minutt, sekunder";
                break;
            case DEGREES_DECIMAL_MINUTES:
                retVal = "Grader, desimalminutter";
                break;
            default:
                throw new UnsupportedOperationException("Coordinate format is not supported in the system");
        }
        return retVal;
    }

    public static CoordinateFormat createFromValue(String value) {
        CoordinateFormat retval = null;

        switch(value) {
            case "Grader, minutt, sekunder":
                retval = DEGREES_MINUTES_SECONDS;
                break;
            case "Grader, desimalminutter":
                retval = DEGREES_DECIMAL_MINUTES;
                break;
            default:
                throw new UnsupportedOperationException("Coordinate format is not supported in the system");
        }
        return retval;
    }

    public CoordinatesRow getCoordinateRow(Activity activity, LocationProviderInterface locationProviderInterface) {
        CoordinatesRow retVal;
        switch(this) {
            case DEGREES_MINUTES_SECONDS:
                retVal = new CoordinatesRow<>(activity, locationProviderInterface, new DegreesMinutesSecondsRow(activity, locationProviderInterface), DEGREES_MINUTES_SECONDS);
                break;
            case DEGREES_DECIMAL_MINUTES:
                retVal = new CoordinatesRow<>(activity, locationProviderInterface, new DegreesDecimalMinutesRow(activity, locationProviderInterface), DEGREES_DECIMAL_MINUTES);
                break;
            default:
                throw new UnsupportedOperationException("Coordinate format is not supported in the system");
        }
        return retVal;
    }

    public static String[] getValues() {
        return new String[] {DEGREES_MINUTES_SECONDS.toString(), DEGREES_DECIMAL_MINUTES.toString()};
    }
}
