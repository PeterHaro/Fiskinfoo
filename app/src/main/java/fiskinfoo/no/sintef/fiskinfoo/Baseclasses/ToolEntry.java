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

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

import fiskinfoo.no.sintef.fiskinfoo.Implementation.GeometryType;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.GpsLocationTracker;
import fiskinfoo.no.sintef.fiskinfoo.R;

public class ToolEntry implements Parcelable {
    private String id;
    private List<Point> coordinates;
    private GeometryType geometry;
    private String IMO;
    private String IRCS;
    private String MMSI;
    private String RegNum;
    private String VesselName;
    private String VesselPhone;
    private String VesselEmail;
    private String ContactPersonEmail;
    private String ContactPersonPhone;
    private String ContactPersonName;
    private ToolType ToolTypeCode;
    private String Source;
    private String Comment;
    private String ShortComment;
    private String RemovedTime;
    private String SetupDateTime;
    private String ToolId;
    private String LastChangedDateTime;
    private String LastChangedBySource;
    private String toolLostReason;
    private String toolLostWeather;
    private ToolEntryStatus toolStatus;
    private int toolLogId;
    private boolean toolLost;

    protected ToolEntry(Parcel in) {
        id = in.readString();
        coordinates = new ArrayList<>();
        in.readList(coordinates, Point.class.getClassLoader());
        geometry = GeometryType.createFromValue(in.readString());
        IMO = in.readString();
        IRCS = in.readString();
        MMSI = in.readString();
        RegNum = in.readString();
        VesselName = in.readString();
        VesselPhone = in.readString();
        VesselEmail = in.readString();
        ContactPersonEmail = in.readString();
        ContactPersonPhone = in.readString();
        ContactPersonName = in.readString();
        ToolTypeCode = ToolType.createFromValue(in.readString());
        Source = in.readString();
        Comment = in.readString();
        ShortComment = in.readString();
        RemovedTime = in.readString();
        SetupDateTime = in.readString();
        ToolId = in.readString();
        LastChangedDateTime = in.readString();
        LastChangedBySource = in.readString();
        toolStatus = ToolEntryStatus.createFromValue(in.readString());
        toolLogId = in.readInt();
        toolLostReason = in.readString();
        toolLostWeather = in.readString();
        toolLost = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeList(coordinates);
        dest.writeString(geometry.toString());
        dest.writeString(IMO);
        dest.writeString(IRCS);
        dest.writeString(MMSI);
        dest.writeString(RegNum);
        dest.writeString(VesselName);
        dest.writeString(VesselPhone);
        dest.writeString(VesselEmail);
        dest.writeString(ContactPersonEmail);
        dest.writeString(ContactPersonPhone);
        dest.writeString(ContactPersonName);
        dest.writeString(ToolTypeCode.toString());
        dest.writeString(Source);
        dest.writeString(Comment);
        dest.writeString(ShortComment);
        dest.writeString(RemovedTime);
        dest.writeString(SetupDateTime);
        dest.writeString(ToolId);
        dest.writeString(LastChangedDateTime);
        dest.writeString(LastChangedBySource);
        dest.writeString(toolStatus.toString());
        dest.writeInt(toolLogId);
        dest.writeString(toolLostReason);
        dest.writeString(toolLostWeather);
        dest.writeByte((byte) (toolLost ? 1 : 0));
    }

    public ToolEntry(List<Point> coordinates, String vesselName, String vesselPhone, String vesselEmail, ToolType toolType, String setupDateTime, String regNum, String contactPersonName, String contactPersonPhone, String contactPersonEmail) {
        this.coordinates = coordinates;
        this.geometry = coordinates.size() > 1 ? GeometryType.LINESTRING : GeometryType.POINT;
        this.VesselName = vesselName;
        this.VesselPhone = vesselPhone;
        this.ToolTypeCode = toolType;
        this.toolStatus = ToolEntryStatus.STATUS_UNREPORTED;
        this.RegNum = regNum;
        this.ContactPersonName = contactPersonName;
        this.ContactPersonPhone = contactPersonPhone;
        this.ContactPersonEmail = contactPersonEmail;
        this.VesselEmail = vesselEmail;

        this.ToolId = UUID.randomUUID().toString(); //UUID.fromString("Fiskinfo" + VesselName + SetupDateTime).toString();
        this.id = this.ToolId;

        Date setupDate = null;
        Date lastChangedDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault());
        String lastChangedDateString;
        String setupDateString;

        sdf.setTimeZone(TimeZone.getDefault());

        try {
            setupDate = sdf.parse(setupDateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        setupDateString = sdf.format(setupDate);
        lastChangedDateString = sdf.format(lastChangedDate);

        this.SetupDateTime = setupDateString.substring(0, setupDateString.indexOf('.')).concat("Z");
        this.LastChangedBySource = lastChangedDateString.concat("Z");
        this.LastChangedDateTime = lastChangedDateString.concat("Z");

    }

    /**
     * Datetimes are assumed to be UTC
     * @param tool
     */
    public ToolEntry(JSONObject tool, Context context) {
        try {
            id = tool.getJSONObject("properties").getString("id");

            coordinates = new ArrayList<>();
            tool.getJSONObject("properties").getString("id");

            JSONArray toolJsonCoordinates = tool.getJSONObject("geometry").getJSONArray("coordinates");

            if(tool.getJSONObject("geometry").getString("type").equals(GeometryType.LINESTRING.toString())) {
                for(int i = 0; i < toolJsonCoordinates.length(); i++) {
                    JSONArray currentPoint = toolJsonCoordinates.getJSONArray(i);
                    Point point = new Point(currentPoint.getDouble(0), currentPoint.getDouble(1));
                    coordinates.add(point);
                }
            } else {
                Point point = new Point(toolJsonCoordinates.getDouble(1), toolJsonCoordinates.getDouble(0));
                coordinates.add(point);
            }

            geometry = !tool.getJSONObject("geometry").has("type") ? null : GeometryType.createFromValue(tool.getJSONObject("geometry").getString("type"));
            IMO = !tool.getJSONObject("properties").has("imo") || "null".equals(tool.getJSONObject("properties").getString("imo")) ?
                    null : tool.getJSONObject("properties").getString("imo");
            IRCS = !tool.getJSONObject("properties").has("ircs") || "null".equals(tool.getJSONObject("properties").getString("ircs")) ?
                    null : tool.getJSONObject("properties").getString("ircs");
            MMSI =  !tool.getJSONObject("properties").has("mmsi") || "null".equals(tool.getJSONObject("properties").getString("mmsi")) ?
                    null : tool.getJSONObject("properties").getString("mmsi");
            RegNum = !tool.getJSONObject("properties").has("regnum") || "null".equals(tool.getJSONObject("properties").getString("regnum")) ?
                    null : tool.getJSONObject("properties").getString("regnum");
            VesselName = !tool.getJSONObject("properties").has("vesselname") || "null".equals(tool.getJSONObject("properties").getString("vesselname")) ?
                    null : tool.getJSONObject("properties").getString("vesselname");
            VesselPhone = !tool.getJSONObject("properties").has("vesselphone") || "null".equals(tool.getJSONObject("properties").getString("vesselphone")) ?
                    null : tool.getJSONObject("properties").getString("vesselphone");
            VesselEmail = !tool.getJSONObject("properties").has("vesselemail") || "null".equals(tool.getJSONObject("properties").getString("vesselemail")) ?
                    null : tool.getJSONObject("properties").getString("vesselemail");
            ContactPersonEmail = !tool.getJSONObject("properties").has("contactpersonemail") || "null".equals(tool.getJSONObject("properties").getString("contactpersonemail")) ?
                    null : tool.getJSONObject("properties").getString("contactpersonemail");
            ContactPersonPhone = !tool.getJSONObject("properties").has("contactpersonphone") || "null".equals(tool.getJSONObject("properties").getString("contactpersonphone")) ?
                    null :  tool.getJSONObject("properties").getString("contactpersonphone");
            ContactPersonName = !tool.getJSONObject("properties").has("contactpersonname") || "null".equals(tool.getJSONObject("properties").getString("contactpersonname")) ?
                    null : tool.getJSONObject("properties").getString("contactpersonname");
            ToolTypeCode = !tool.getJSONObject("properties").has("tooltypecode") || "null".equals(tool.getJSONObject("properties").getString("tooltypecode")) ?
                    null : ToolType.createFromValue(tool.getJSONObject("properties").getString("tooltypecode"));
            Source = !tool.getJSONObject("properties").has("source") || "null".equals(tool.getJSONObject("properties").getString("source")) ?
                    null : tool.getJSONObject("properties").getString("source");
            Comment = !tool.getJSONObject("properties").has("comment") || "null".equals(tool.getJSONObject("properties").getString("comment")) ?
                    null : tool.getJSONObject("properties").getString("comment");
            ShortComment = !tool.getJSONObject("properties").has("shortcomment") || "null".equals(tool.getJSONObject("properties").getString("shortcomment")) ?
                    null : tool.getJSONObject("properties").getString("shortcomment");
            RemovedTime = !tool.getJSONObject("properties").has("removeddatetime") || "null".equals(tool.getJSONObject("properties").getString("removeddatetime")) ?
                    null : tool.getJSONObject("properties").getString("removeddatetime");
            ToolId = !tool.getJSONObject("properties").has("toolid") || "null".equals(tool.getJSONObject("properties").getString("toolid")) ?
                    null : tool.getJSONObject("properties").getString("toolid");

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
            SimpleDateFormat sdfMilliSeconds = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());

            Date serverSetupDateTime;
            Date serverUpdatedBySourceDateTime;
            Date serverLastUpdatedDateTime;

            if(tool.getJSONObject("properties").has("setupdatetime") && !"null".equals(tool.getJSONObject("properties").getString("setupdatetime"))) {
                sdf.setTimeZone(TimeZone.getTimeZone("GMT-1"));
                sdfMilliSeconds.setTimeZone(TimeZone.getTimeZone("GMT-1"));

                serverSetupDateTime = tool.getJSONObject("properties").getString("setupdatetime").length() == context.getResources().getInteger(R.integer.datetime_without_milliseconds_length) ?
                        sdf.parse(tool.getJSONObject("properties").getString("setupdatetime")) : sdfMilliSeconds.parse(tool.getJSONObject("properties").getString("setupdatetime"));

                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

                SetupDateTime = sdf.format(serverSetupDateTime);
            }

            if(tool.getJSONObject("properties").has("lastchangeddatetime") && !"null".equals(tool.getJSONObject("properties").getString("lastchangeddatetime"))) {
                sdf.setTimeZone(TimeZone.getTimeZone("GMT-1"));
                sdfMilliSeconds.setTimeZone(TimeZone.getTimeZone("GMT-1"));

                serverLastUpdatedDateTime = tool.getJSONObject("properties").getString("lastchangeddatetime").length() == context.getResources().getInteger(R.integer.datetime_without_milliseconds_length) ?
                        sdf.parse(tool.getJSONObject("properties").getString("lastchangeddatetime")) : sdfMilliSeconds.parse(tool.getJSONObject("properties").getString("lastchangeddatetime"));

                sdfMilliSeconds.setTimeZone(TimeZone.getTimeZone("UTC"));

                LastChangedDateTime = sdfMilliSeconds.format(serverLastUpdatedDateTime);
            }
            if(tool.getJSONObject("properties").has("lastchangedbysource") && !"null".equals(tool.getJSONObject("properties").getString("lastchangedbysource"))) {
                sdf.setTimeZone(TimeZone.getTimeZone("GMT-1"));
                sdfMilliSeconds.setTimeZone(TimeZone.getTimeZone("GMT-1"));

                serverUpdatedBySourceDateTime = tool.getJSONObject("properties").getString("lastchangedbysource").length() == context.getResources().getInteger(R.integer.datetime_without_milliseconds_length) ?
                        sdf.parse(tool.getJSONObject("properties").getString("lastchangedbysource")) : sdfMilliSeconds.parse(tool.getJSONObject("properties").getString("lastchangedbysource"));

                sdfMilliSeconds.setTimeZone(TimeZone.getTimeZone("UTC"));

                LastChangedBySource = sdfMilliSeconds.format(serverUpdatedBySourceDateTime);
            }

            toolStatus =  ToolEntryStatus.STATUS_RECEIVED;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Datetimes are assumed to be UTC
     * @param tool
     */
    public void updateFromGeoJson(JSONObject tool, Context context) {
        try {
            ToolJsonHandler jsonHandler = new ToolJsonHandler(context);

            id = jsonHandler.getProperty(tool, context.getString(R.string.json_tool_property_id), id);
            coordinates = jsonHandler.getCoordinates(tool, new ArrayList<>());
            geometry = jsonHandler.getGeometryType(tool, geometry);
            IMO = jsonHandler.getProperty(tool, context.getString(R.string.json_tool_property_imo), IMO);
            IRCS = jsonHandler.getProperty(tool, context.getString(R.string.json_tool_property_ircs), IRCS);
            MMSI = jsonHandler.getProperty(tool, context.getString(R.string.json_tool_property_mmsi), MMSI);
            RegNum = jsonHandler.getProperty(tool, context.getString(R.string.json_tool_property_regnum), RegNum);
            VesselName = jsonHandler.getProperty(tool, context.getString(R.string.json_tool_property_vessel_name), VesselName);
            VesselPhone = jsonHandler.getProperty(tool, context.getString(R.string.json_tool_property_vessel_phone), VesselPhone);
            VesselEmail = jsonHandler.getProperty(tool, context.getString(R.string.json_tool_property__vessel_email), VesselEmail);
            ContactPersonEmail = jsonHandler.getProperty(tool, context.getString(R.string.json_tool_property_contactperson_email), ContactPersonEmail);
            ContactPersonPhone = jsonHandler.getProperty(tool, context.getString(R.string.json_tool_property_contactperson_phone), ContactPersonPhone);
            ContactPersonName = jsonHandler.getProperty(tool, context.getString(R.string.json_tool_property_contactperson_name), ContactPersonName);
            Source = jsonHandler.getProperty(tool, context.getString(R.string.json_tool_property_source), Source);
            Comment = jsonHandler.getProperty(tool, context.getString(R.string.json_tool_property_comment), Comment);
            ShortComment = jsonHandler.getProperty(tool, context.getString(R.string.json_tool_property_short_comment), ShortComment);
            RemovedTime = jsonHandler.getProperty(tool, context.getString(R.string.json_tool_property_removed_time), RemovedTime);
            ToolTypeCode = jsonHandler.getToolType(tool, context.getString(R.string.json_tool_property_tool_type_code), ToolTypeCode);

//            IMO = !tool.getJSONObject("properties").has("imo") ? IMO :
//                    (tool.getJSONObject("properties").getProperty("imo") == null || "null".equals(tool.getJSONObject("properties").getProperty("imo"))) ?
//                            IMO : tool.getJSONObject("properties").getProperty("imo");
//            IRCS = !tool.getJSONObject("properties").has("ircs") ? IRCS :
//                    (tool.getJSONObject("properties").getProperty("ircs") == null || "null".equals(tool.getJSONObject("properties").getProperty("ircs"))) ?
//                            IRCS : tool.getJSONObject("properties").getProperty("ircs");
//            MMSI =  !tool.getJSONObject("properties").has("mmsi") ? MMSI :
//                    (tool.getJSONObject("properties").getProperty("mmsi") == null || "null".equals(tool.getJSONObject("properties").getProperty("mmsi"))) ?
//                            MMSI : tool.getJSONObject("properties").getProperty("mmsi");
//            RegNum = !tool.getJSONObject("properties").has("regnum") ? RegNum :
//                    (tool.getJSONObject("properties").getProperty("regnum") == null || "null".equals(tool.getJSONObject("properties").getProperty("regnum"))) ?
//                            RegNum : tool.getJSONObject("properties").getProperty("regnum");
//            VesselName = !tool.getJSONObject("properties").has("vesselname") ? VesselName :
//                    (tool.getJSONObject("properties").getProperty("vesselname") == null || "null".equals(tool.getJSONObject("properties").getProperty("vesselname"))) ?
//                            VesselName : tool.getJSONObject("properties").getProperty("vesselname");
//            VesselPhone = !tool.getJSONObject("properties").has("vesselphone") ? VesselPhone :
//                    (tool.getJSONObject("properties").getProperty("vesselphone") == null || "null".equals(tool.getJSONObject("properties").getProperty("vesselphone"))) ?
//                            VesselPhone : tool.getJSONObject("properties").getProperty("vesselphone");
//            VesselEmail = !tool.getJSONObject("properties").has("vesselemail") ? VesselPhone :
//                    (tool.getJSONObject("properties").getProperty("vesselemail") == null || "null".equals(tool.getJSONObject("properties").getProperty("vesselemail"))) ?
//                            VesselPhone : tool.getJSONObject("properties").getProperty("vesselemail");
//            ContactPersonEmail = !tool.getJSONObject("properties").has("contactpersonemail") ? ContactPersonEmail :
//                    (tool.getJSONObject("properties").getProperty("contactpersonemail") == null || "null".equals(tool.getJSONObject("properties").getProperty("contactpersonemail"))) ?
//                            ContactPersonEmail : tool.getJSONObject("properties").getProperty("contactpersonemail");
//            ContactPersonPhone = !tool.getJSONObject("properties").has("contactpersonphone") ? ContactPersonPhone :
//                    (tool.getJSONObject("properties").getProperty("contactpersonphone") == null || "null".equals(tool.getJSONObject("properties").getProperty("contactpersonphone"))) ?
//                            ContactPersonPhone : tool.getJSONObject("properties").getProperty("contactpersonphone");
//            ContactPersonName = !tool.getJSONObject("properties").has("contactpersonname") ? ContactPersonName :
//                    (tool.getJSONObject("properties").getProperty("contactpersonname") == null || "null".equals(tool.getJSONObject("properties").getProperty("contactpersonname"))) ?
//                            ContactPersonName : tool.getJSONObject("properties").getProperty("contactpersonname");
//            ToolTypeCode = !tool.getJSONObject("properties").has("tooltypecode") ? ToolTypeCode :
//                    (tool.getJSONObject("properties").getProperty("tooltypecode") == null || "null".equals(tool.getJSONObject("properties").getProperty("tooltypecode"))) ?
//                            ToolTypeCode : ToolType.createFromValue(tool.getJSONObject("properties").getProperty("tooltypecode"));
//            Source = !tool.getJSONObject("properties").has("source") ? Source :
//                    (tool.getJSONObject("properties").getProperty("source") == null || "null".equals(tool.getJSONObject("properties").getProperty("source"))) ?
//                            Source : tool.getJSONObject("properties").getProperty("source");
//            Comment = !tool.getJSONObject("properties").has("comment") ? Comment :
//                    (tool.getJSONObject("properties").getProperty("comment") == null || "null".equals(tool.getJSONObject("properties").getProperty("comment"))) ?
//                            Comment : tool.getJSONObject("properties").getProperty("comment");
//            ShortComment = !tool.getJSONObject("properties").has("shortcomment") ? ShortComment :
//                    (tool.getJSONObject("properties").getProperty("shortcomment") == null || "null".equals(tool.getJSONObject("properties").getProperty("shortcomment"))) ?
//                            ShortComment : tool.getJSONObject("properties").getProperty("shortcomment");
//            RemovedTime = !tool.getJSONObject("properties").has("removeddatetime") ? RemovedTime :
//                    (tool.getJSONObject("properties").getProperty("removeddatetime") == null || "null".equals(tool.getJSONObject("properties").getProperty("removeddatetime"))) ?
//                            RemovedTime : tool.getJSONObject("properties").getProperty("removeddatetime");

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
            SimpleDateFormat sdfMilliSeconds = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());

            Date serverSetupDateTime;
            Date serverUpdatedBySourceDateTime;
            Date serverLastUpdatedDateTime;

            if(tool.getJSONObject("properties").has("setupdatetime") && !"null".equals(tool.getJSONObject("properties").getString("setupdatetime"))) {
                sdf.setTimeZone(TimeZone.getTimeZone("GMT-1"));
                sdfMilliSeconds.setTimeZone(TimeZone.getTimeZone("GMT-1"));

                serverSetupDateTime = tool.getJSONObject("properties").getString("setupdatetime").length() == context.getResources().getInteger(R.integer.datetime_without_milliseconds_length) ?
                        sdf.parse(tool.getJSONObject("properties").getString("setupdatetime")) : sdfMilliSeconds.parse(tool.getJSONObject("properties").getString("setupdatetime"));

                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

                SetupDateTime = sdf.format(serverSetupDateTime);
            }

            if(tool.getJSONObject("properties").has("lastchangeddatetime") && !"null".equals(tool.getJSONObject("properties").getString("lastchangeddatetime"))) {
                sdf.setTimeZone(TimeZone.getTimeZone("GMT-1"));
                sdfMilliSeconds.setTimeZone(TimeZone.getTimeZone("GMT-1"));

                serverLastUpdatedDateTime = tool.getJSONObject("properties").getString("lastchangeddatetime").length() == context.getResources().getInteger(R.integer.datetime_without_milliseconds_length) ?
                        sdf.parse(tool.getJSONObject("properties").getString("lastchangeddatetime")) : sdfMilliSeconds.parse(tool.getJSONObject("properties").getString("lastchangeddatetime"));

                sdfMilliSeconds.setTimeZone(TimeZone.getTimeZone("UTC"));

                LastChangedDateTime = sdfMilliSeconds.format(serverLastUpdatedDateTime);
            }
            if(tool.getJSONObject("properties").has("lastchangedbysource") && !"null".equals(tool.getJSONObject("properties").getString("lastchangedbysource"))) {
                sdf.setTimeZone(TimeZone.getTimeZone("GMT-1"));
                sdfMilliSeconds.setTimeZone(TimeZone.getTimeZone("GMT-1"));

                serverUpdatedBySourceDateTime = tool.getJSONObject("properties").getString("lastchangedbysource").length() == context.getResources().getInteger(R.integer.datetime_without_milliseconds_length) ?
                        sdf.parse(tool.getJSONObject("properties").getString("lastchangedbysource")) : sdfMilliSeconds.parse(tool.getJSONObject("properties").getString("lastchangedbysource"));

                sdfMilliSeconds.setTimeZone(TimeZone.getTimeZone("UTC"));

                LastChangedBySource = sdfMilliSeconds.format(serverUpdatedBySourceDateTime);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public JSONObject toGeoJson(GpsLocationTracker gpsLocationTracker) {
        JSONObject geoJsonTool = new JSONObject();
        try {
            JSONObject geometry = new JSONObject();
            JSONObject properties = new JSONObject();
            JSONArray Toolcoordinates = new JSONArray();

            if(this.geometry == GeometryType.POINT) {
                Toolcoordinates.put(coordinates.get(0).getLongitude());
                Toolcoordinates.put(coordinates.get(0).getLatitude());
            } else {
                for(Point currentPosition : this.coordinates) {
                    JSONArray position = new JSONArray();
                    position.put(currentPosition.getLongitude());
                    position.put(currentPosition.getLatitude());

                    Toolcoordinates.put(position);
                }
            }

            geoJsonTool.put("id", this.id == null ? JSONObject.NULL : this.id);
            geometry.put("coordinates", Toolcoordinates.length() == 0 ? JSONObject.NULL : Toolcoordinates);
            geometry.put("type", this.geometry == null ? JSONObject.NULL : this.geometry.toString());
            geometry.put("crs", JSONObject.NULL);
            geometry.put("bbox", JSONObject.NULL);
            geoJsonTool.put("geometry", geometry);


            properties.put("IMO", this.IMO == null ? JSONObject.NULL : this.IMO);
            properties.put("IRCS", this.IRCS == null ? JSONObject.NULL : this.IRCS);
            properties.put("MMSI", this.MMSI == null ? JSONObject.NULL : this.MMSI);
            properties.put("REGNUM", this.RegNum == null ? JSONObject.NULL : this.RegNum);
            properties.put("VesselName", this.VesselName == null ? JSONObject.NULL : this.VesselName);
            properties.put("VesselPhone", this.VesselPhone == null ? JSONObject.NULL : this.VesselPhone);
            properties.put("VesselEmail", this.VesselEmail == null ? JSONObject.NULL : this.VesselEmail);
            properties.put("ToolTypeCode", this.ToolTypeCode == null ? JSONObject.NULL : this.ToolTypeCode.getToolCode());
            properties.put("ToolTypeName", this.ToolTypeCode == null ? JSONObject.NULL : this.ToolTypeCode.toString());
            properties.put("Source", this.Source == null ? JSONObject.NULL : this.Source);
            properties.put("Comment", this.Comment == null ? JSONObject.NULL : this.Comment);
            properties.put("ShortComment", this.ShortComment == null ? JSONObject.NULL : this.ShortComment);
            properties.put("RemovedTime", this.RemovedTime == null ? JSONObject.NULL : this.RemovedTime);
            properties.put("SetupTime", this.SetupDateTime == null ? JSONObject.NULL : this.SetupDateTime);
            properties.put("ToolColor", "#" + Integer.toHexString(this.ToolTypeCode.getHexColorValue()).toUpperCase());
            properties.put("ToolId", this.ToolId == null ? JSONObject.NULL : this.ToolId);
            properties.put("LastChangedDateTime", this.LastChangedDateTime == null ? JSONObject.NULL : this.LastChangedDateTime);
            properties.put("LastChangedBySource", this.LastChangedBySource == null ? JSONObject.NULL : this.LastChangedBySource);
            properties.put("ContactPersonName", this.ContactPersonName == null ? JSONObject.NULL : this.ContactPersonName);
            properties.put("ContactPersonPhone", this.ContactPersonPhone == null ? JSONObject.NULL : this.ContactPersonPhone);
            properties.put("ContactPersonEmail", this.ContactPersonEmail == null ? JSONObject.NULL : this.ContactPersonEmail);

            if(this.toolLost) {
                properties.put("ToolLostReason", this.toolLostReason == null ? JSONObject.NULL : this.toolLostReason);
                properties.put("ToolLostWeather", this.toolLostWeather == null ? JSONObject.NULL : this.toolLostWeather);
                properties.put("ToolLost", true);
            }

            Date toolReportDate = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            double toolReportingLatitude = gpsLocationTracker.getLatitude();
            double toolReportingLongitude = gpsLocationTracker.getLongitude();

            properties.put("CurrentTime", sdf.format(toolReportDate));
            properties.put("CurrentPositionLat ", toolReportingLatitude);
            properties.put("CurrentPositionLon", toolReportingLongitude);

            if(this.toolStatus == ToolEntryStatus.STATUS_REMOVED_UNCONFIRMED && this.RemovedTime != null) {
                properties.put("removeddatetime", this.RemovedTime);
            }

            geoJsonTool.put("properties", properties);

            geoJsonTool.put("type", "Feature");
            geoJsonTool.put("crs", JSONObject.NULL);
            geoJsonTool.put("bbox", JSONObject.NULL);

            return geoJsonTool;
        } catch (JSONException e) {
            e.printStackTrace();

            return null;
        }
    }

    public static final Creator<ToolEntry> CREATOR = new Creator<ToolEntry>() {
        @Override
        public ToolEntry createFromParcel(Parcel in) {
            return new ToolEntry(in);
        }

        @Override
        public ToolEntry[] newArray(int size) {
            return new ToolEntry[size];
        }
    };

    public String getId() {
        return id == null ? "" : id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Point> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<Point> coordinates) {
        this.coordinates = coordinates;
        this.geometry = coordinates.size() > 1 ? GeometryType.LINESTRING : GeometryType.POINT;
    }

    public GeometryType getGeometry() {
        return geometry;
    }

    public void setGeometry(GeometryType geometry) {
        this.geometry = geometry;
    }

    public String getIMO() {
        return IMO == null ? "" : IMO;
    }

    public void setIMO(String IMO) {
        this.IMO = IMO;
    }

    public String getIRCS() {
        return IRCS == null ? "" : IRCS;
    }

    public void setIRCS(String IRCS) {
        this.IRCS = IRCS;
    }

    public String getMMSI() {
        return MMSI == null ? "" : MMSI;
    }

    public void setMMSI(String MMSI) {
        this.MMSI = MMSI;
    }

    public String getRegNum() {
        return RegNum == null ? "" : RegNum;
    }

    public void setRegNum(String regNum) {
        RegNum = regNum;
    }

    public String getVesselName() {
        return VesselName == null ? "" : VesselName;
    }

    public void setVesselName(String vesselName) {
        VesselName = vesselName;
    }

    public String getVesselPhone() {
        return VesselPhone == null ? "" : VesselPhone;
    }

    public void setVesselPhone(String vesselPhone) {
        VesselPhone = vesselPhone;
    }

    public String getVesselEmail() {
        return VesselEmail == null ? "" : VesselEmail;
    }

    public void setVesselEmail(String vesselEmail) {
        VesselEmail = vesselEmail;
    }

    public ToolType getToolType() {
        return ToolTypeCode;
    }

    public void setToolType(ToolType toolType) {
        this.ToolTypeCode = toolType;
    }

    public String getComment() {
        return Comment == null ? "" : Comment;
    }

    public void setComment(String comment) {
        if(comment == null || comment.isEmpty()) {
            return;
        }

        Comment = comment;
    }

    public String getShortComment() {
        return ShortComment == null ? "" : ShortComment;
    }

    public void setShortComment(String shortComment) {
        ShortComment = shortComment;
    }

    public String getRemovedTime() {
        return RemovedTime == null ? "" : RemovedTime;
    }

    public void setRemovedTime(String removedTime) {
        RemovedTime = removedTime;
    }

    public String getSetupDateTime() {
        return SetupDateTime == null ? "" : SetupDateTime;
    }

    public String getSetupDate() {
        return SetupDateTime == null ? "" : SetupDateTime.substring(0, 10);
    }

    public void setSetupDateTime(String setupDateTime) {
        SetupDateTime = setupDateTime;
    }

    public String getToolId() {
        return ToolId == null ? "" : ToolId;
    }

    public void setToolId(String toolId) {
        ToolId = toolId;
    }

    public String getLastChangedDateTime() {
        return LastChangedDateTime == null ? "" : LastChangedDateTime;
    }

    public void setLastChangedDateTime(String lastChangedDateTime) {
        LastChangedDateTime = lastChangedDateTime;
    }

    public String getLastChangedBySource() {
        return LastChangedBySource == null ? "" : LastChangedBySource;
    }

    public void setLastChangedBySource(String lastChangedDateTime) {
        LastChangedBySource = lastChangedDateTime;
    }

    public ToolEntryStatus getToolStatus() {
        return toolStatus;
    }

    public void setToolStatus(ToolEntryStatus toolStatus) {
        this.toolStatus = toolStatus;
    }

    public String getContactPersonEmail() {
        return ContactPersonEmail == null ? "" : ContactPersonEmail;
    }

    public void setContactPersonEmail(String contactPersonEmail) {
        ContactPersonEmail = contactPersonEmail;
    }

    public String getContactPersonPhone() {
        return ContactPersonPhone == null ? "" : ContactPersonPhone;
    }

    public void setContactPersonPhone(String contactPersonPhone) {
        ContactPersonPhone = contactPersonPhone;
    }

    public String getContactPersonName() {
        return ContactPersonName == null ? "" : ContactPersonName;
    }

    public void setContactPersonName(String contactPersonName) {
        ContactPersonName = contactPersonName;
    }

    public String getSource() {
        return Source == null ? "" : Source;
    }

    public void setSource(String source) {
        Source = source;
    }

    public ToolType getToolTypeCode() {
        return ToolTypeCode;
    }

    public void setToolTypeCode(ToolType toolTypeCode) {
        ToolTypeCode = toolTypeCode;
    }

    public int getToolLogId () {
        return toolLogId;
    }

    public void setToolLogId(int id) {
        toolLogId = id;
    }

    public String getToolLostReason() {
        return toolLostReason;
    }

    public void setToolLostReason(String toolLostReason) {
        this.toolLostReason = toolLostReason;
    }

    public String getToolLostWeather() {
        return toolLostWeather;
    }

    public void setToolLostWeather(String toolLostWeather) {
        this.toolLostWeather = toolLostWeather;
    }

    public boolean isToolLost() {
        return toolLost;
    }

    public void setToolLost(boolean toolLost) {
        this.toolLost = toolLost;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    private class ToolJsonHandler {
        Context context;
        private final String properties;
        private final String geometry;
        private final String geometryType;
        private final String coordinates;
        private final String nullString;

        public ToolJsonHandler(Context context) {
            this.context = context;
            this.geometry = context.getString(R.string.json_tool_property_geometry);
            this.geometryType = context.getString(R.string.json_tool_property_geometry_type);
            this.properties = context.getString(R.string.json_tool_properties);
            this.coordinates = context.getString(R.string.json_tool_property_coordinates);
            this.nullString = context.getString(R.string.json_tool_property_null_string_value);
        }

        public String getProperty(JSONObject tool, String name, String defaultValue) {
            String retval = defaultValue;
            try {
                retval = !tool.getJSONObject(properties).has(name) ?
                        defaultValue : (tool.getJSONObject(properties).getString(name) == null || nullString.equals(tool.getJSONObject(properties).getString(name))) ?
                        defaultValue : tool.getJSONObject(properties).getString(name);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return retval;
        }

        public ToolType getToolType(JSONObject tool, String name, ToolType defaultValue) {
            ToolType retval = defaultValue;
            try {
                retval = !tool.getJSONObject(properties).has(name) ? defaultValue :
                        (tool.getJSONObject(properties).getString(name) == null || nullString.equals(tool.getJSONObject(properties).getString(name))) ?
                                defaultValue : ToolType.createFromValue(tool.getJSONObject(properties).getString(name));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return retval;
        }

        public GeometryType getGeometryType(JSONObject tool, GeometryType defaultValue) {
            GeometryType retval = defaultValue;
            try {
                retval = !tool.getJSONObject(geometry).has(geometryType) ? defaultValue :
                        (tool.getJSONObject(geometry).getString(geometryType) == null || nullString.equals(tool.getJSONObject(geometry).getString(geometryType))) ?
                                defaultValue : GeometryType.createFromValue(tool.getJSONObject(geometry).getString(geometryType));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return retval;
        }

        public List<Point> getCoordinates(JSONObject tool, ArrayList defaultValue) {
            List<Point> retval = defaultValue;

            try {
                JSONArray toolJsonCoordinates = tool.getJSONObject(geometry).getJSONArray(coordinates);

                if(GeometryType.LINESTRING.toString().equals(tool.getJSONObject(geometry).getString(geometryType))) {
                    for(int i = 0; i < toolJsonCoordinates.length(); i++) {
                        JSONArray currentPoint = toolJsonCoordinates.getJSONArray(i);
                        Point point = new Point(currentPoint.getDouble(1), currentPoint.getDouble(0));
                        retval.add(point);
                    }
                } else {
                    Point point = new Point(toolJsonCoordinates.getDouble(1), toolJsonCoordinates.getDouble(0));
                    retval.add(point);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return retval;
        }
    }
}