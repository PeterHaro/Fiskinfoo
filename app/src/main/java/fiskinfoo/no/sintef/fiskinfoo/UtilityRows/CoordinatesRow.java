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

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.Point;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.GpsLocationTracker;
import fiskinfoo.no.sintef.fiskinfoo.R;

public class CoordinatesRow extends BaseTableRow {
    private TextView header;
    private Button helpButton;
    private Button addCoordinateRowButton;
    private Button removeCoordinateRowButton;
    private TextView helpTextView;
    private LinearLayout latLonViewContainer;
    private GpsLocationTracker locationTracker;
    private List<LatLonRow> latLonRows = new ArrayList<>();

    public CoordinatesRow(Context context, GpsLocationTracker gpsLocationTracker) {
        super(context, R.layout.utility_row_coordinates_row);

        header = (TextView) getView().findViewById(R.id.utility_coordinates_row_header_text_view);
        helpButton = (Button) getView().findViewById(R.id.utility_coordinates_row_help_button);
        addCoordinateRowButton = (Button) getView().findViewById(R.id.utility_coordinates_row_add_position_button);
        removeCoordinateRowButton = (Button) getView().findViewById(R.id.utility_coordinates_row_remove_position_button);
        latLonViewContainer = (LinearLayout) getView().findViewById(R.id.utility_coordinates_row_lat_lon_container);
        helpTextView = (TextView) getView().findViewById(R.id.utility_coordinates_row_help_text_view);
        this.locationTracker = gpsLocationTracker;

        LatLonRow coordinatesRow = new LatLonRow(context, locationTracker);

        latLonRows.add(coordinatesRow);
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
                LatLonRow row = new LatLonRow(v.getContext(), locationTracker);

                latLonRows.add(row);
                latLonViewContainer.addView(row.getView());
            }
        });

        removeCoordinateRowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(latLonRows.size() == 1) {
                    Toast.makeText(getView().getContext(), getView().getContext().getString(R.string.error_minimum_coordinate_limit), Toast.LENGTH_LONG).show();
                    return;
                }

                latLonViewContainer.removeViewAt(latLonViewContainer.getChildCount() - 1);
                latLonRows.remove(latLonRows.size() - 1);
            }
        });
    }

    public void setHeader(String headerText) {
        header.setText(headerText);
    }

    public List<Point> getCoordinates() {
        List<Point> coordinates = new ArrayList<>();

        for(LatLonRow coordinateRow : latLonRows) {
            Point point = coordinateRow.getCoordinates();

            if(point == null) {
                return null;
            } else {
                coordinates.add(point);
            }
        }

        return coordinates;
    }

    public void setCoordinates(List<Point> coordinates) {
        latLonRows.clear();
        latLonViewContainer.removeAllViews();

        for(Point position : coordinates) {
            LatLonRow latLonRow = new LatLonRow(getView().getContext(), locationTracker);
            latLonRow.setCoordinates(position);
            latLonRows.add(latLonRow);
            latLonViewContainer.addView(latLonRow.getView());
        }
    }
}
