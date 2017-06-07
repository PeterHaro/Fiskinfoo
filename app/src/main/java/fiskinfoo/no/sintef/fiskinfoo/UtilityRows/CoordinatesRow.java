/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fiskinfoo.no.sintef.fiskinfoo.UtilityRows;

import android.app.Activity;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.Point;
import fiskinfoo.no.sintef.fiskinfoo.Interface.LocationProviderInterface;
import fiskinfoo.no.sintef.fiskinfoo.R;

public class CoordinatesRow extends BaseTableRow {
    private TextView header;
    private Button helpButton;
    private Button addCoordinateRowButton;
    private Button removeCoordinateRowButton;
    private TextView helpTextView;
    private LinearLayout latLonViewContainer;
    private TextWatcher watcher;
    private CompoundButton.OnCheckedChangeListener cardinalDirectionSwitchOnCheckedChangedListener;
    private List<DegreesMinutesSecondsRow> coordinateRows = new ArrayList<>();
    private boolean enabled = true;

    public CoordinatesRow(final Activity activity, final LocationProviderInterface locationProviderInterface) {
        super(activity, R.layout.utility_row_coordinates_row);

        header = (TextView) getView().findViewById(R.id.utility_coordinates_row_header_text_view);
        helpButton = (Button) getView().findViewById(R.id.utility_coordinates_row_help_button);
        addCoordinateRowButton = (Button) getView().findViewById(R.id.utility_coordinates_row_add_position_button);
        removeCoordinateRowButton = (Button) getView().findViewById(R.id.utility_coordinates_row_remove_position_button);
        latLonViewContainer = (LinearLayout) getView().findViewById(R.id.utility_coordinates_row_lat_lon_container);
        helpTextView = (TextView) getView().findViewById(R.id.utility_coordinates_row_help_text_view);

        DegreesMinutesSecondsRow coordinatesRow = new DegreesMinutesSecondsRow(activity, locationProviderInterface);

        coordinateRows.add(coordinatesRow);
        latLonViewContainer.addView(coordinatesRow.getView());

        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helpTextView.setVisibility(helpTextView.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
            }
        });

        addCoordinateRowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DegreesMinutesSecondsRow row = new DegreesMinutesSecondsRow(activity, locationProviderInterface);

                if(watcher != null) {
                    row.setTextWatcher(watcher);
                }
                if(cardinalDirectionSwitchOnCheckedChangedListener != null) {
                    row.setCardinalDirectionSwitchOnCheckedChangedListener(cardinalDirectionSwitchOnCheckedChangedListener);
                }

                coordinateRows.add(row);
                latLonViewContainer.addView(row.getView());
            }
        });

        removeCoordinateRowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(coordinateRows.size() == 1) {
                    Toast.makeText(getView().getContext(), getView().getContext().getString(R.string.error_minimum_coordinate_limit), Toast.LENGTH_LONG).show();
                    return;
                }

                latLonViewContainer.removeViewAt(latLonViewContainer.getChildCount() - 1);
                coordinateRows.remove(coordinateRows.size() - 1);
            }
        });
    }

    public void setHeader(String headerText) {
        header.setText(headerText);
    }

    public List<Point> getCoordinates() {
        List<Point> coordinates = new ArrayList<>();

        for(DegreesMinutesSecondsRow coordinateRow : coordinateRows) {
            Point point = coordinateRow.getCoordinates();

            if(point == null) {
                return null;
            } else {
                coordinates.add(point);
            }
        }

        return coordinates;
    }

    public void setCoordinates(Activity activity, List<Point> coordinates, LocationProviderInterface locationProviderInterface) {
        coordinateRows.clear();
        latLonViewContainer.removeAllViews();

        for(Point position : coordinates) {
            DegreesMinutesSecondsRow row = new DegreesMinutesSecondsRow(activity, locationProviderInterface);
            row.setCoordinates(position);
            row.setEnabled(enabled);

            if(watcher != null) {
                row.setTextWatcher(watcher);
            }
            if(cardinalDirectionSwitchOnCheckedChangedListener != null) {
                row.setCardinalDirectionSwitchOnCheckedChangedListener(cardinalDirectionSwitchOnCheckedChangedListener);
            }

            coordinateRows.add(row);
            latLonViewContainer.addView(row.getView());
        }
    }

    public void setPositionButtonOnClickListener(View.OnClickListener onClickListener) {
        for(DegreesMinutesSecondsRow row : coordinateRows) {
            row.SetPositionButtonOnClickListener(onClickListener);
        }
    }

    public void setTextWatcher(TextWatcher watcher) {
        this.watcher = watcher;

        for(DegreesMinutesSecondsRow row : coordinateRows) {
            row.setTextWatcher(this.watcher);
        }
    }

    public void setCardinalDirectionSwitchOnCheckedChangedListener(CompoundButton.OnCheckedChangeListener onCheckedChangedListener) {
        this.cardinalDirectionSwitchOnCheckedChangedListener = onCheckedChangedListener;

        for(DegreesMinutesSecondsRow row : coordinateRows) {
            row.setCardinalDirectionSwitchOnCheckedChangedListener(this.cardinalDirectionSwitchOnCheckedChangedListener);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        addCoordinateRowButton.setEnabled(enabled);
        removeCoordinateRowButton.setEnabled(enabled);
        latLonViewContainer.setEnabled(enabled);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if(enabled) {
                addCoordinateRowButton.setBackgroundTintList(ContextCompat.getColorStateList(getView().getContext(), R.color.material_icon_black_active_tint_color));
                removeCoordinateRowButton.setBackgroundTintList(ContextCompat.getColorStateList(getView().getContext(), R.color.material_icon_black_active_tint_color));
            } else {
                addCoordinateRowButton.setBackgroundTintList(ContextCompat.getColorStateList(getView().getContext(), R.color.material_icon_black_disabled_tint_color));
                removeCoordinateRowButton.setBackgroundTintList(ContextCompat.getColorStateList(getView().getContext(), R.color.material_icon_black_disabled_tint_color));
            }
        }

        for(DegreesMinutesSecondsRow row : coordinateRows) {
            row.setEnabled(enabled);
        }
    }
}