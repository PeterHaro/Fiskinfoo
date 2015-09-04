

package fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit;

public enum SubscriptionInterval {
    EVERY_DAY, EVERY_UPDATE;

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
            default:
                throw new UnsupportedOperationException("Interval does not exist in the system");
        }
        return retVal;
    }
}
