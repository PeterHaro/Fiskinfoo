package fiskinfoo.no.sintef.fiskinfoo.Implementation;

import android.app.Activity;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;

import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.CoordinateFormat;
import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.Point;
import fiskinfoo.no.sintef.fiskinfoo.Interface.LocationProviderInterface;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.CoordinateRow;

public interface ICoordinateRow<T extends CoordinateRow> {

    T initRow(Activity activity, LocationProviderInterface gpsLocationTracker);

    Point getCoordinates();

    void setCoordinates(Point position);

    String getLatitude();

    void setLatitude(String latitude);

    String getLongitude();

    void setLongitude(String longitude);

    void SetPositionButtonOnClickListener(View.OnClickListener onClickListener);

    void setTextWatcher(TextWatcher watcher);

    void setCardinalDirectionSwitchOnCheckedChangedListener(CompoundButton.OnCheckedChangeListener onCheckedChangeListener);

    void setEditable(boolean editable);

    CoordinateFormat getCoordinateFormat();
}
