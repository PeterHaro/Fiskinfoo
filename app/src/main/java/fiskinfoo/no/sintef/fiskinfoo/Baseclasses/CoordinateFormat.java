package fiskinfoo.no.sintef.fiskinfoo.Baseclasses;

import android.app.Activity;

import fiskinfoo.no.sintef.fiskinfoo.Implementation.FiskInfoUtility;
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

    public String getCoordinateString(Point coordinate) {
        StringBuilder sb = new StringBuilder(coordinate.getLatitude() < 0 ? "S" : "N");
        String retval = null;

        switch(this) {
            case DEGREES_MINUTES_SECONDS:
                sb.append(FiskInfoUtility.decimalToDMS(coordinate.getLatitude()));
                sb.append(" ");
                sb.append(coordinate.getLongitude() < 0 ? "W" : "E");
                sb.append(FiskInfoUtility.decimalToDMS(coordinate.getLongitude()));

                retval = sb.toString();

                break;
            case DEGREES_DECIMAL_MINUTES:
                sb.append(FiskInfoUtility.decimalToDDM(coordinate.getLatitude(), 5));
                sb.append(" ");
                sb.append(coordinate.getLongitude() < 0 ? "W" : "E");
                sb.append(FiskInfoUtility.decimalToDDM(coordinate.getLongitude(), 5));

                retval = sb.toString();
                break;
            default:
                throw new UnsupportedOperationException("Coordinate format is not supported in the system");
        }

        return retval;
    }

    public static String[] getValues() {
        return new String[] {DEGREES_MINUTES_SECONDS.toString(), DEGREES_DECIMAL_MINUTES.toString()};
    }
}
