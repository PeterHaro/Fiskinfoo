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

public class Point implements java.io.Serializable{
    private double lat;
    private double lon;

    public Point() {

    }

    public void setNewPointValues(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public double getLatitude() {
        return lat;
    }

    public double getLongitude() {
        return lon;
    }

    public Point(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    /**
     * Calculates the azimuth between the two points according to the ellipsoid
     * model of WGS84. The azimuth is relative to true north. The Coordinates
     * object on which this method is called is considered the origin for the
     * calculation and the Coordinates object passed as a parameter is the
     * destination which the azimuth is calculated to. When the origin is the
     * North pole and the destination is not the North pole, this method returns
     * 180.0. When the origin is the South pole and the destination is not the
     * South pole, this method returns 0.0. If the origin is equal to the
     * destination, this method returns 0.0. The implementation shall calculate
     * the result as exactly as it can. However, it is required that the result
     * is within 1 degree of the correct result.
     *
     * @param to
     *            the Coordinates of the destination
     * @return the azimuth to the destination in degrees. Result is within the
     *         range [0.0 ,360.0).
     * @throws java.lang.NullPointerException
     *             if the parameter is null
     */
    @SuppressWarnings("unused")
    public float azimuthTo(Point to) {
        double lat1 = getLatitude();
        double lon1 = getLongitude();
        double lat2 = to.getLatitude();
        double lon2 = to.getLongitude();

        double dtor = Math.PI / 180.0;
        double rtod = 180.0 / Math.PI;

        double distance = rtod
                * Math.acos(Math.sin(lat1 * dtor) * Math.sin(lat2 * dtor)
                + Math.cos(lat1 * dtor) * Math.cos(lat2 * dtor)
                * Math.cos((lon2 - lon1) * dtor));

        double cosAzimuth = (Math.cos(lat1 * dtor) * Math.sin(lat2 * dtor) - Math
                .sin(lat1 * dtor)
                * Math.cos(lat2 * dtor) * Math.cos((lat2 - lon1) * dtor))
                / Math.sin(distance * dtor);

        double sinAzimuth = Math.cos(lat2 * dtor)
                * Math.sin((lat2 - lon1) * dtor) / Math.sin(distance * dtor);

        return (float) (rtod * Math.atan2(sinAzimuth, cosAzimuth));
    }


    /**
     * Calculate the great circle Distance between two points
     * Retrieved from <link>http://www.movable-type.co.uk/scripts/latlong.html</link>
     * @param to
     *            the Coordinates of the destination
     * @return the Distance to the destination in meters
     * @throws java.lang.NullPointerException
     *             if the parameter is null
     */
    public double distance(Point to) {
        double earthRadius = 6371;
        double lat1 = Math.toRadians(to.getLatitude());
        double lon1 = Math.toRadians(to.getLongitude());
        double lat2 = Math.toRadians(getLatitude());
        double lon2 = Math.toRadians(getLongitude());

        double deltaLongitude = (lon2 - lon1);
        double deltaLatitude = (lat2 - lat1);

        double a = Math.sin(deltaLatitude / 2) * Math.sin(deltaLatitude / 2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.sin(deltaLongitude / 2)
                * Math.sin(deltaLongitude / 2);

        double c = 2 * Math.asin(Math.min(1.0, Math.sqrt(a)));

        double km = earthRadius * c;

        return (km * 1000);
    }

    public boolean checkDistanceBetweenTwoPoints(Point point, double unacceptableDistance) {
        double distance = this.distance(point);
        return distance - unacceptableDistance < 0;

    }

    /**
     * Calculate _initial_ bearing, which is the same as the forward azimuth
     * @param to
     *      point
     * @return
     *      initial bearing
     */
    @SuppressWarnings("unused")
    public double bearing(Point to) {
        double initialPhi = Math.toRadians(lat), latidueInRadians = Math.toRadians(to.getLatitude());
        double deltaLambda = Math.toRadians((to.getLongitude()-this.lon));

        // see http://mathforum.org/library/drmath/view/55417.html
        double y = Math.sin(deltaLambda) * Math.cos(latidueInRadians);
        double x = Math.cos(initialPhi)*Math.sin(latidueInRadians) -
                Math.sin(initialPhi)*Math.cos(latidueInRadians)*Math.cos(deltaLambda);
        double tethaAngle = Math.atan2(y, x);
        tethaAngle = (tethaAngle * 180 / Math.PI);
        return (tethaAngle+360) % 360;
    }


    // Direct and inverse solutions of geodesics on the ellipsoid using Vincenty formulae

    /**
     * Calculates INVERSE vincenty, to support more than WSG-84 replace the cases of the magic numbers in f, a, b  with a datum representation scaling factor from wsg-84 to whatever datum you'd like. Probably as input param
     * @param to
     *      point
     * @return
     *
     * https://en.wikipedia.org/wiki/Vincenty%27s_formulae
     */
    public Distance inverseCalculation(Point to) {
        double phiOne = Math.toRadians(this.getLatitude()), lambda1 = Math.toRadians(this.getLongitude());
        double phi2 = Math.toRadians(to.getLatitude()), lambda2 = Math.toRadians(to.getLongitude());

        double f= 1/298.257223563;
        double a = 6378137;
        double b = 6356752.31425;

        double L = lambda2 - lambda1;
        double tanU1 = (1-f) * Math.tan(phiOne), cosU1 = 1 / Math.sqrt((1 + tanU1*tanU1)), sinU1 = tanU1 * cosU1;
        double tanU2 = (1-f) * Math.tan(phi2), cosU2 = 1 / Math.sqrt((1 + tanU2*tanU2)), sinU2 = tanU2 * cosU2;

        double sinusLambda, cosinusALpha, sinusAlpha, sinusAlphaa, cosinusAlpha, alpha, sinusalphaaa, cosinusSalpha, cosinus2Alpham, C;

        double lambda = L, inverseLambda, iterations = 0;
        do {
            sinusLambda = Math.sin(lambda);
            cosinusALpha = Math.cos(lambda);
            sinusAlpha = (cosU2*sinusLambda) * (cosU2*sinusLambda) + (cosU1*sinU2-sinU1*cosU2*cosinusALpha) * (cosU1*sinU2-sinU1*cosU2*cosinusALpha);
            sinusAlphaa = Math.sqrt(sinusAlpha);
            if (sinusAlphaa == 0) return null;  // co-incident points
            cosinusAlpha = sinU1*sinU2 + cosU1*cosU2*cosinusALpha;
            alpha = Math.atan2(sinusAlphaa, cosinusAlpha);
            sinusalphaaa = cosU1 * cosU2 * sinusLambda / sinusAlphaa;
            cosinusSalpha = 1 - sinusalphaaa*sinusalphaaa;
            cosinus2Alpham = cosinusAlpha - 2*sinU1*sinU2/cosinusSalpha;
            if (Double.isNaN(cosinus2Alpham)) cosinus2Alpham = 0;  // equatorial line: cosSqα=0 (§6)
            C = f/16*cosinusSalpha*(4+1*(4-3*cosinusSalpha));
            inverseLambda = lambda;
            lambda = L + (1-C) * f * sinusalphaaa * (alpha + C*sinusAlphaa*(cosinus2Alpham+C*cosinusAlpha*(-1+2*cosinus2Alpham*cosinus2Alpham)));
        } while (Math.abs(lambda- inverseLambda) > 1e-12 && ++iterations<200);
        if (iterations>=200) throw new Error("Formula failed to converge");

        double uSq = cosinusSalpha * (a*a - b*b) / (b*b);
        double A = 1 + uSq/16384*(4096+uSq*(-768+uSq*(320-175*uSq)));
        double B = uSq/1024 * (256+uSq*(-128+uSq*(74-47*uSq)));
        double deltaAlpha = B*sinusAlphaa*(cosinus2Alpham+B/4*(cosinusAlpha*(-1+2*cosinus2Alpham*cosinus2Alpham)-
                B/6*cosinus2Alpham*(-3+4*sinusAlphaa*sinusAlphaa)*(-3+4*cosinus2Alpham*cosinus2Alpham)));

        double s = b*A*(alpha-deltaAlpha);

        double alpha1 = Math.atan2(cosU2*sinusLambda,  cosU1*sinU2-sinU1*cosU2*cosinusALpha);
        double alpha2 = Math.atan2(cosU1*sinusLambda, -sinU1*cosU2+cosU1*sinU2*cosinusALpha);

        alpha1 = (alpha1 + 2*Math.PI) % (2*Math.PI); // normalise to 0...360
        alpha2 = (alpha2 + 2*Math.PI) % (2*Math.PI); // normalise to 0...360
        return new Distance(s, Math.toDegrees(alpha1), Math.toDegrees(alpha2));
    }


    /**
     *
     */
    private static final long serialVersionUID = 3L;

}