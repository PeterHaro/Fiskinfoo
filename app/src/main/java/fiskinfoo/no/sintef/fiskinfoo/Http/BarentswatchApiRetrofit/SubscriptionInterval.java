

package fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit;

public enum SubscriptionInterval {
    EVERY_DAY, EVERY_UPDATE, FOUR_TIMES_A_DAY;

    @Override
    public String toString() {
        String retVal = "";
        switch(this) {
            case EVERY_DAY:
                retVal = "Hver dag";
                break;
            case EVERY_UPDATE:
                retVal = "Hver oppdatering";
                break;
            case FOUR_TIMES_A_DAY:
                retVal = "Fire ganger om dagen";
                break;
            default:
                throw new UnsupportedOperationException("Interval does not exist in the system");
        }
        return retVal;
    }

    public static SubscriptionInterval getType(String type) {
        SubscriptionInterval retVal = null;
        switch(type) {
            case "EveryDay":
                retVal = EVERY_DAY;
                break;
            case "EveryUpdate":
                retVal = EVERY_UPDATE;
                break;
            case "FourTimesADay":
                retVal = FOUR_TIMES_A_DAY;
                break;
            default:
                throw new UnsupportedOperationException("Interval does not exist in the system");
        }
        return retVal;
    }
}
