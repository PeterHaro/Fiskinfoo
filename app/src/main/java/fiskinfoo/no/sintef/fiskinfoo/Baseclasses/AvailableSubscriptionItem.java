package fiskinfoo.no.sintef.fiskinfoo.Baseclasses;

import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.ApiErrorType;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.models.PropertyDescription;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.models.Subscription;

/**
 * Created by erlendstav on 11/04/2018.
 */

public class AvailableSubscriptionItem {

    private String title;
    private String lastUpdated;
    private boolean isSubscribed;
    private boolean isAuthorized;
    private ApiErrorType errorType;
    private PropertyDescription propertyDescription;
    private Subscription subscription;

    public AvailableSubscriptionItem() {
    }

    public AvailableSubscriptionItem(String title, String lastUpdated, boolean isSubscribed, boolean isAuthorized, ApiErrorType errorType) {
        this.title = title;
        this.lastUpdated = lastUpdated;
        this.isSubscribed = isSubscribed;
        this.isAuthorized = isAuthorized;
        this.errorType = errorType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public boolean isSubscribed() {
        return isSubscribed;
    }

    public void setSubscribed(boolean subscribed) {
        isSubscribed = subscribed;
    }

    public boolean isAuthorized() {
        return isAuthorized;
    }

    public void setAuthorized(boolean authorized) {
        isAuthorized = authorized;
    }

    public ApiErrorType getErrorType() {
        return errorType;
    }

    public void setErrorType(ApiErrorType errorType) {
        this.errorType = errorType;
    }

    public PropertyDescription getPropertyDescription() {
        return propertyDescription;
    }

    public void setPropertyDescription(PropertyDescription propertyDescription) {
        this.propertyDescription = propertyDescription;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }
}
