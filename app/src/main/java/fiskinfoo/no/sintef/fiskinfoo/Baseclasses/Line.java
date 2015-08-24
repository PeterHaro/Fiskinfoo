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

package fiskinfoo.no.sintef.fiskinfoo.Baseclasses;

public class Line implements java.io.Serializable {
    Point start;
    Point stop;
    double dx;
    double dy;

    public Line(Point start, Point stop) {
        this.start = start;
        this.stop = stop;
        this.dx = stop.lat - start.lat;
        this.dy = stop.lon - start.lon;
    }

    public boolean checkDistanceWithLineAndReportStatus(Point position, double distance) {
        double denominator = Math.abs((this.dy * position.lat) - (this.dx * position.lon) - (this.start.lat * this.stop.lon) + (this.stop.lat * this.start.lon));
        double distanceInDegrees = denominator / Math.sqrt(((this.dx * this.dx) + (this.dy * this.dy)));
        double actualDistanceInNauticalMiles = computeLengthOfDegrees(distanceInDegrees);
        if (actualDistanceInNauticalMiles - distance < 0) {
            return true;
        }
        return false;

    }

    public void printLine() {
        System.out.println("line start: ");
        this.start.printPointValues();
        System.out.println("line stop: ");
        this.stop.printPointValues();
    }

    public double deg2rad(double degree) {
        double conversionFactor = (2 * Math.PI) / 360;
        return (degree * conversionFactor);
    }

    public double computeLengthOfDegrees(double degree) {
        double lat = deg2rad(degree);

        // Constants
        double m1 = 111132.92; // latitude calculation term 1
        double m2 = -559.82; // latitude calculation term 2
        double m3 = 1.175; // latitude calculation term 3
        double m4 = -0.0023; // latitude calculation term 4
        double p1 = 111412.84; // longitude calculation term 1
        double p2 = -93.5; // longitude calculation term 2
        double p3 = 0.118; // longitude calculation term 3

        double latLen = m1 + (m2 * Math.cos(2 * lat)) + (m3 * Math.cos(4 * lat)) + (m4 * Math.cos(6 * lat));
        double lonLen = (p1 * Math.cos(lat)) + (p2 * Math.cos(3 * lat)) + (p3 * Math.cos(5 * lat));

        double latMeters = Math.round(latLen);
        double nauticalMiles = latMeters / 1852;

        return nauticalMiles;

    }

    /**
     *
     */
    private static final long serialVersionUID = 2L;

}
