package fiskinfoo.no.sintef.fiskinfoo.Implementation;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StreamCorruptedException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.FiskInfoPolygon2D;
import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.SubscriptionExpandableListChildObject;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.ApiErrorType;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.models.PropertyDescription;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.models.Subscription;
import fiskinfoo.no.sintef.fiskinfoo.MapFragment;
import fiskinfoo.no.sintef.fiskinfoo.MyPageFragment;
import fiskinfoo.no.sintef.fiskinfoo.R;

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
    public static int copy(InputStream input, OutputStream output) throws IOException {
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
    public static long copyLarge(InputStream input, OutputStream output) throws IOException {
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
    public static byte[] toByteArray(InputStream input) throws IOException {
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
     * Checks that the given string is a valid E-mail address
     *
     * @param email
     *            the address to check
     * @return true if address is a valid E-mail address, false otherwise.
     */
    public boolean isEmailValid(String email) {
        String regex = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public Fragment createFragment(String tag, User user, String currentTag) {
        Bundle userBundle = new Bundle();
        userBundle.putParcelable("user", user);
        switch(tag) {
            case MyPageFragment.TAG:
                MyPageFragment myPageFragment = new MyPageFragment();
                myPageFragment.setArguments(userBundle);
                return myPageFragment;
            case MapFragment.TAG:
                MapFragment mapFragment = new MapFragment();
                mapFragment.setArguments(userBundle);
                return mapFragment;
            default:
                Log.d(currentTag, "Trying to create invalid fragment with TAG: " + tag);
        }
        return null;
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

    public int subscriptionApiNameToIconId(String apiName) {
        int retVal = -1;

        switch(apiName.toLowerCase()) {
            case "fishingfacility":
                retVal = R.drawable.ikon_kystfiske;
                break;
            case "iceedge":
                retVal = R.drawable.ikon_is_tjenester;
                break;
            case "npdfacility":
                retVal = R.drawable.ikon_olje_og_gass;
                break;
            case "npdsurveyplanned":
                retVal = R.drawable.ikon_olje_og_gass;
                break;
            case "npdsurveyongoing":
                retVal = R.drawable.ikon_olje_og_gass;
                break;
        }

        return retVal;
    }

    /**
     * Checks if external storage is available for read and write.
     *
     * @return True if external storage is available, false otherwise.
     */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        } else {
            return false;
        }
    }

    public void writeMapLayerToExternalStorage(Context context, byte[] data, String writableName, String format, String downloadSavePath) {
        String filePath;
        OutputStream outputStream = null;
        filePath = downloadSavePath;

        File directory = filePath == null ? null : new File(filePath);

        if(directory == null) {
            String directoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
            String directoryName = "FiskInfo";
            filePath = directoryPath + "/" + directoryName + "/";
            Toast.makeText(context, "Kunne ikke nå valgt filplassering, lagrer fil til /Download/FiskInfo", Toast.LENGTH_LONG).show();
        }

        try {
            outputStream = new FileOutputStream(new File(filePath + writableName + "." + format));
            outputStream.write(data);

            Toast.makeText(context, R.string.disk_write_completed, Toast.LENGTH_LONG).show();
            System.out.println("file write complete");

        } catch (IOException e) {
            Toast.makeText(context, R.string.disk_write_failed, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Get the contents of an <code>FiskInfoPolygon2D</code> and writes it to
     * disk This method catches all <code>Exceptions</code> internally,
     * therefore it should be usable directly
     *
     * @param path
     *            the <code>Path</code> of the file to write
     * @param polygon
     *            the <code>FiskInfoPolygon2d</code> which is written to disk
     */
    public void serializeFiskInfoPolygon2D(String path, FiskInfoPolygon2D polygon) {
        try {
            FileOutputStream fileOut = new FileOutputStream(path);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(polygon);
            out.close();
            fileOut.close();
            Log.d("FiskInfo", "Serialization successfull, the data should be stored in the specified path");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieve a serialized <code>FiskInfoPolygon2D</code> from disk as a
     * <code>FiskInfoPolygon2D class</code>
     *
     * @param path
     *            the <code>Path</code> to read from
     * @return the Requested <code>FiskInfoPolygon2D</code>
     */
    public FiskInfoPolygon2D deserializeFiskInfoPolygon2D(String path) {
        FiskInfoPolygon2D polygon = null;
        FileInputStream fileIn;
        ObjectInputStream in;

        try {
            System.out.println("Starting deserializing");
            fileIn = new FileInputStream(path);
            in = new ObjectInputStream(fileIn);
            polygon = (FiskInfoPolygon2D) in.readObject();


            in.close();
            fileIn.close();
            Log.d("FiskInfo", "Deserialization successful, the data should be stored in the input class");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            Log.d("Utility", "Deserialization failed, couldn't find class");
            e.printStackTrace();
        }

        return polygon;
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
     * Checks that the given string contains coordinates in a valid format in
     * regards to the given projection.
     *
     * @param coordinates
     * @param projection
     * @return
     */
    public boolean checkCoordinates(String coordinates, ProjectionType projection) {
        boolean retVal = false;
        System.out.println("coords: " + coordinates);
        System.out.println("projection: " + projection);

        if (coordinates.length() == 0) {
            retVal = false;
        } else {
            switch (projection) {
                case EPSG_3857:
                    retVal = checkProjectionEPSG3857(coordinates);
                    break;
                case EPSG_4326:
                    retVal = checkProjectionEPSG4326(coordinates);
                    break;
                case EPSG_23030:
                    retVal = checkProjectionEPSG23030(coordinates);
                    break;
                case EPSG_900913:
                    retVal = checkProjectionEPSG900913(coordinates);
                    break;
                default:
                    return retVal;
            }
        }

        return retVal;
    }

    private boolean checkProjectionEPSG3857(String coordinates) {
        try {
            int commaSeparatorIndex = coordinates.indexOf(",");
            double latitude = Double.parseDouble(coordinates.substring(0, commaSeparatorIndex - 1));
            double longitude = Double.parseDouble(coordinates.substring(commaSeparatorIndex + 1, coordinates.length() - 1));

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
            int commaSeparatorIndex = coordinates.indexOf(",");
            double latitude = Double.parseDouble(coordinates.substring(0, commaSeparatorIndex - 1));
            double longitude = Double.parseDouble(coordinates.substring(commaSeparatorIndex + 1, coordinates.length() - 1));
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
            int commaSeparatorIndex = coordinates.indexOf(",");
            double latitude = Double.parseDouble(coordinates.substring(0, commaSeparatorIndex - 1));
            double longitude = Double.parseDouble(coordinates.substring(commaSeparatorIndex + 1, coordinates.length() - 1));
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
            int commaSeparatorIndex = coordinates.indexOf(",");
            double latitude = Double.parseDouble(coordinates.substring(0, commaSeparatorIndex - 1));
            double longitude = Double.parseDouble(coordinates.substring(commaSeparatorIndex + 1, coordinates.length() - 1));

			/*
			 * These are based on the spherical metricator bounds of OpenLayers
			 * and as we are currently using OpenLayer these are the bounds to use.
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
