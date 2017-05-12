package fiskinfoo.no.sintef.fiskinfoo.Interface;

import fiskinfoo.no.sintef.fiskinfoo.Implementation.User;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.UserSettings;

/**
 * Created by bardh on 09.05.2017.
 */

public interface UserInterface {
    User getUser();
    void updateUserSettings(UserSettings userSettings);
}
