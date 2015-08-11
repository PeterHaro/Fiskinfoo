package fiskinfoo.no.sintef.fiskinfoo.Implementation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class FiskInfoUtility {
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    // copy from InputStream
    // -----------------------------------------------------------------------
    /**
     * Copy bytes from an <code>InputStream</code> to an
     * <code>OutputStream</code>.
     * <p>
     * This method buffers the input internally, so there is no need to use a
     * <code>BufferedInputStream</code>.
     * <p>
     * Large streams (over 2GB) will return a bytes copied value of
     * <code>-1</code> after the copy has completed since the correct number of
     * bytes cannot be returned as an int. For large streams use the
     * <code>copyLarge(InputStream, OutputStream)</code> method.
     *
     * @param input
     *            the <code>InputStream</code> to read from
     * @param output
     *            the <code>OutputStream</code> to write to
     * @return the number of bytes copied
     * @throws NullPointerException
     *             if the input or output is null
     * @throws IOException
     *             if an I/O error occurs
     * @throws ArithmeticException
     *             if the byte count is too large
     */
    public int copy(InputStream input, OutputStream output) throws IOException {
        long count = copyLarge(input, output);
        if (count > Integer.MAX_VALUE) {
            return -1;
        }
        return (int) count;
    }

    /**
     * Copy bytes from a large (over 2GB) <code>InputStream</code> to an
     * <code>OutputStream</code>.
     * <p>
     * This method buffers the input internally, so there is no need to use a
     * <code>BufferedInputStream</code>.
     *
     * @param input
     *            the <code>InputStream</code> to read from
     * @param output
     *            the <code>OutputStream</code> to write to
     * @return the number of bytes copied
     * @throws NullPointerException
     *             if the input or output is null
     * @throws IOException
     *             if an I/O error occurs
     */
    public long copyLarge(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        long count = 0;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    // read toByteArray
    // -----------------------------------------------------------------------
    /**
     * Get the contents of an <code>InputStream</code> as a <code>byte[]</code>.
     * <p>
     * This method buffers the input internally, so there is no need to use a
     * <code>BufferedInputStream</code>.
     *
     * @param input
     *            the <code>InputStream</code> to read from
     * @return the requested byte array
     * @throws NullPointerException
     *             if the input is null
     * @throws IOException
     *             if an I/O error occurs
     */
    public byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(input, output);
        return output.toByteArray();
    }


    /**
     * Truncates a double value to the number of decimals given
     *
     * @param number
     *            The number to truncate
     * @param numberofDecimals
     *            Number of decimals of the truncated number
     * @return
     */
    public Double truncateDecimal(double number, int numberofDecimals) {
        if (number > 0) {
            return new BigDecimal(String.valueOf(number)).setScale(numberofDecimals, BigDecimal.ROUND_FLOOR).doubleValue();
        } else {
            return new BigDecimal(String.valueOf(number)).setScale(numberofDecimals, BigDecimal.ROUND_CEILING).doubleValue();
        }
    }

    /**
     * Appends a item from a JsonArray to a <code>ExpandableListAdapater</code>
     *
     * @param subscriptions
     *            A JSON array containing all the available subscriptions
     * @param field
     *            The field name in the <code>ExpandableListAdapater</code>
     * @param fieldsToExtract
     *            The fields from the subscriptions to retrieve and store in the
     *            <code>ExpandableListAdapater</code>
     */
    public void appendSubscriptionItemsToView(JSONArray subscriptions, List<String> field, List<String> fieldsToExtract) {
        if ((subscriptions == null) || (subscriptions.isNull(0))) {
            return;
        }

        String title = "";
        for (int i = 0; i < subscriptions.length(); i++) {
            try {
                JSONObject currentSubscription = subscriptions.getJSONObject(i);
                for (String fieldValue : fieldsToExtract) {

                    title += (fieldValue.equals("LastUpdated") ? currentSubscription.getString(fieldValue).replace('T', ' ')
                            : currentSubscription.getString(fieldValue)) + "\n";
                }
                title = title.substring(0, title.length() - 1);
                field.add(title);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            title = "";
        }
    }

    public static Date iso08601ParseDate(String input) throws java.text.ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ssz");
        if (input.endsWith("Z")) {
            input = input.substring(0, input.length() - 1) + "GMT-00:00";
        } else {
            int inset = 6;
            String start = input.substring(0, input.length() - inset);
            String end = input.substring(input.length() - inset, input.length());
            input = start + "GMT" + end;
        }
        return sdf.parse(input);
    }

    public static String iso08601ToString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ssz");
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        sdf.setTimeZone(timeZone);
        String output = sdf.format(date);
        int insetFirst = 9;
        int insetLast = 6;
        String retval = output.substring(0, output.length() - insetFirst) + output.substring(output.length() - insetLast, output.length());
        return retval.replaceAll("UTC", "+00:00");
    }

    /**
     *
     */
    public boolean checkCoordinates(String coordinates, String projection) {
        System.out.println("coords: " + coordinates);
        System.out.println("projection: " + projection);

        if (coordinates.length() == 0) {

            return false;
        } else {
            switch (projection) {
                case "EPSG:3857":
                    if (checkProjectionEPSG3857(coordinates) == true) {
                        break;
                    }
                case "EPSG:4326":
                    if (checkProjectionEPSG4326(coordinates) == true) {
                        break;
                    }
                case "EPSG:23030":
                    if (checkProjectionEPSG23030(coordinates) == true) {
                        break;
                    }
                case "EPSG:900913":
                    if (checkProjectionEPSG900913(coordinates) == true) {
                        break;
                    }
                default:
                    return false;
            }
            return true;
        }
    }

    /**
     * Checks that the given string contains coordinates in a valid format in
     * regards to the given projection.
     *
     * @param coordinates
     *            the coordinates to be checked.
     * @return true if coordinates are in a valid format.
     */

    private boolean checkProjectionEPSG3857(String coordinates) {
        try {
            int commaSeperatorIndex = coordinates.indexOf(",");
            double latitude = Double.parseDouble(coordinates.substring(0, commaSeperatorIndex - 1));
            double longitude = Double.parseDouble(coordinates.substring(commaSeperatorIndex + 1, coordinates.length() - 1));

            double EPSG3857MinX = -20026376.39;
            double EPSG3857MaxX = 20026376.39;
            double EPSG3857MinY = -20048966.10;
            double EPSG3857MaxY = 20048966.10;

            if (latitude < EPSG3857MinX || latitude > EPSG3857MaxX || longitude < EPSG3857MinY || longitude > EPSG3857MaxY) {
                return false;
            }

            return true;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        } catch (StringIndexOutOfBoundsException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean checkProjectionEPSG4326(String coordinates) {
        try {
            int commaSeperatorIndex = coordinates.indexOf(",");
            double latitude = Double.parseDouble(coordinates.substring(0, commaSeperatorIndex - 1));
            double longitude = Double.parseDouble(coordinates.substring(commaSeperatorIndex + 1, coordinates.length() - 1));
            double EPSG4326MinX = -180.0;
            double EPSG4326MaxX = 180.0;
            double EPSG4326MinY = -90.0;
            double EPSG4326MaxY = 90.0;

            if (latitude < EPSG4326MinX || latitude > EPSG4326MaxX || longitude < EPSG4326MinY || longitude > EPSG4326MaxY) {
                return false;
            }

            return true;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        } catch (StringIndexOutOfBoundsException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean checkProjectionEPSG23030(String coordinates) {
        try {
            int commaSeperatorIndex = coordinates.indexOf(",");
            double latitude = Double.parseDouble(coordinates.substring(0, commaSeperatorIndex - 1));
            double longitude = Double.parseDouble(coordinates.substring(commaSeperatorIndex + 1, coordinates.length() - 1));
            double EPSG23030MinX = 229395.8528;
            double EPSG23030MaxX = 770604.1472;
            double EPSG23030MinY = 3982627.8377;
            double EPSG23030MaxY = 7095075.2268;

            if (latitude < EPSG23030MinX || latitude > EPSG23030MaxX || longitude < EPSG23030MinY || longitude > EPSG23030MaxY) {
                return false;
            }

            return true;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        } catch (StringIndexOutOfBoundsException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean checkProjectionEPSG900913(String coordinates) {
        try {
            int commaSeperatorIndex = coordinates.indexOf(",");
            double latitude = Double.parseDouble(coordinates.substring(0, commaSeperatorIndex - 1));
            double longitude = Double.parseDouble(coordinates.substring(commaSeperatorIndex + 1, coordinates.length() - 1));

			/*
			 * These are based on the spherical metricator bounds of OpenLayers
			 * and as we are currently using OpenLayer these are bounds to use.
			 */
            double EPSG900913MinX = -20037508.34;
            double EPSG900913MaxX = 20037508.34;
            double EPSG900913MinY = -20037508.34;
            double EPSG900913MaxY = 20037508.34;

            if (latitude < EPSG900913MinX || latitude > EPSG900913MaxX || longitude < EPSG900913MinY || longitude > EPSG900913MaxY) {
                return false;
            }

            return true;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        } catch (StringIndexOutOfBoundsException e) {
            e.printStackTrace();
            return false;
        }
    }
}
