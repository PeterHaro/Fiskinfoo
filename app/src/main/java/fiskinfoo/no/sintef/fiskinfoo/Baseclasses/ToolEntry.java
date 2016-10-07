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

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import fiskinfoo.no.sintef.fiskinfoo.Implementation.GeometryType;

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
    private String ContactPersonEmail;
    private String ContactPersonPhone;
    private String ContactPersonName;
    private ToolType ToolTypeCode;
    private String Source;
    private String Comment;
    private String ShortComment;
    private String RemovedTime;
    private String SetupTime;
    private String ToolId;
    private String LastChangedDateTime;
    private ToolEntryStatus toolStatus;
    private int toolLogId;

    protected ToolEntry(Parcel in) {
        id = in.readString();
        coordinates = new ArrayList<Point>();
        in.readList(coordinates, Point.class.getClassLoader());
        geometry = GeometryType.createFromValue(in.readString());
        IMO = in.readString();
        IRCS = in.readString();
        MMSI = in.readString();
        RegNum = in.readString();
        VesselName = in.readString();
        VesselPhone = in.readString();
        ContactPersonEmail = in.readString();
        ContactPersonPhone = in.readString();
        ContactPersonName = in.readString();
        ToolTypeCode = ToolType.createFromValue(in.readString());
        Source = in.readString();
        Comment = in.readString();
        ShortComment = in.readString();
        RemovedTime = in.readString();
        SetupTime = in.readString();
        ToolId = in.readString();
        LastChangedDateTime = in.readString();
        toolStatus = ToolEntryStatus.createFromValue(in.readString());
        toolLogId = in.readInt();
    }

    public ToolEntry(List<Point> coordinates, String vesselName, String vesselPhone, ToolType toolType, String setupTime, String regNum, String contactPersonName, String contactPersonPhone, String contactPersonEmail) {
        this.coordinates = coordinates;
        this.geometry = coordinates.size() > 1 ? GeometryType.LINESTRING : GeometryType.POINT;
        this.VesselName = vesselName;
        this.VesselPhone = vesselPhone;
        this.ToolTypeCode = toolType;
        this.SetupTime = setupTime;
        this.toolStatus = ToolEntryStatus.STATUS_UNSENT;
        this.RegNum = regNum;
        this.ContactPersonName = contactPersonName;
        this.ContactPersonPhone = contactPersonPhone;
        this.ContactPersonEmail = contactPersonEmail;

        this.ToolId = UUID.randomUUID().toString(); //UUID.fromString("Fiskinfo" + VesselName + SetupTime).toString();
    }

    public ToolEntry(List<Point> coordinates, String imoNumber, String ircsNumber, String mmsiNumber, String registrationNumber, String vesselName, String vesselPhone, ToolType toolType, String comment, String shortComment, String setupTime) {
        this.coordinates = coordinates;
        this.geometry = coordinates.size() > 1 ? GeometryType.LINESTRING : GeometryType.POINT;
        this.IMO = imoNumber;
        this.IRCS = ircsNumber;
        this.MMSI = mmsiNumber;
        this.RegNum = registrationNumber;
        this.VesselName = vesselName;
        this.VesselPhone = vesselPhone;
        this.ToolTypeCode = toolType;
        this.Comment = comment;
        this.ShortComment = shortComment;
        this.SetupTime = setupTime;
        this.toolStatus = ToolEntryStatus.STATUS_UNSENT;

        this.ToolId = UUID.randomUUID().toString();
    }

    public JSONObject toGeoJson() {
        JSONObject geoJsonTool = new JSONObject();
        try {
            JSONObject geometry = new JSONObject();
            JSONArray Toolcoordinates = new JSONArray();

            for(Point currentPosition : this.coordinates) {
                JSONArray position = new JSONArray();
                position.put(currentPosition.getLongitude());
                position.put(currentPosition.getLatitude());

                Toolcoordinates.put(position);

            }

            geoJsonTool.put("id", this.id == null ? JSONObject.NULL : this.id);
            geometry.put("coordinates", Toolcoordinates == null ? JSONObject.NULL : Toolcoordinates);
            geometry.put("type", this.geometry == null ? JSONObject.NULL : this.geometry.toString());
            geometry.put("crs", JSONObject.NULL);
            geometry.put("bbox", JSONObject.NULL);

            geoJsonTool.put("geometry", geometry);

            JSONObject properties = new JSONObject();

            properties.put("IMO", this.IMO == null ? JSONObject.NULL : this.IMO);
            properties.put("IRCS", this.IRCS == null ? JSONObject.NULL : this.IRCS);
            properties.put("MMSI", this.MMSI == null ? JSONObject.NULL : this.MMSI);
            properties.put("VesselName", this.VesselName == null ? JSONObject.NULL : this.VesselName);
            properties.put("VesselPhone", this.VesselPhone == null ? JSONObject.NULL : this.VesselPhone);
            properties.put("ToolTypeCode", this.ToolTypeCode == null ? JSONObject.NULL : this.ToolTypeCode.getToolCode());
            properties.put("ToolTypeName", this.ToolTypeCode == null ? JSONObject.NULL : this.ToolTypeCode.toString());
            properties.put("Source", this.Source == null ? JSONObject.NULL : this.LastChangedDateTime);
            properties.put("Comment", this.Comment == null ? JSONObject.NULL : this.LastChangedDateTime);
            properties.put("ShortComment", this.ShortComment == null ? JSONObject.NULL : this.LastChangedDateTime);
            properties.put("RemovedTime", this.RemovedTime == null ? JSONObject.NULL : this.LastChangedDateTime);
            properties.put("SetupTime", this.SetupTime == null ? JSONObject.NULL : this.LastChangedDateTime);
            properties.put("ToolColor", "#" + Integer.toHexString(this.ToolTypeCode.getHexColorValue()).toUpperCase());
            properties.put("ToolId", this.ToolId == null ? JSONObject.NULL : this.ToolId);
            properties.put("LastChangedDateTime", this.LastChangedDateTime == null ? JSONObject.NULL : this.LastChangedDateTime);

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
        dest.writeString(ContactPersonEmail);
        dest.writeString(ContactPersonPhone);
        dest.writeString(ContactPersonName);
        dest.writeString(ToolTypeCode.toString());
        dest.writeString(Source);
        dest.writeString(Comment);
        dest.writeString(ShortComment);
        dest.writeString(RemovedTime);
        dest.writeString(SetupTime);
        dest.writeString(ToolId);
        dest.writeString(LastChangedDateTime);
        dest.writeString(toolStatus.toString());
        dest.writeInt(toolLogId);
    }

    public String getId() {
        return id;
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
        return IMO;
    }

    public void setIMO(String IMO) {
        this.IMO = IMO;
    }

    public String getIRCS() {
        return IRCS;
    }

    public void setIRCS(String IRCS) {
        this.IRCS = IRCS;
    }

    public String getMMSI() {
        return MMSI;
    }

    public void setMMSI(String MMSI) {
        this.MMSI = MMSI;
    }

    public String getRegNum() {
        return RegNum;
    }

    public void setRegNum(String regNum) {
        RegNum = regNum;
    }

    public String getVesselName() {
        return VesselName;
    }

    public void setVesselName(String vesselName) {
        VesselName = vesselName;
    }

    public String getVesselPhone() {
        return VesselPhone;
    }

    public void setVesselPhone(String vesselPhone) {
        VesselPhone = vesselPhone;
    }

    public ToolType getToolType() {
        return ToolTypeCode;
    }

    public void setToolType(ToolType toolType) {
        this.ToolTypeCode = toolType;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        if(comment == null || comment.isEmpty()) {
            return;
        }

        Comment = comment;
    }

    public String getShortComment() {
        return ShortComment;
    }

    public void setShortComment(String shortComment) {
        ShortComment = shortComment;
    }

    public String getRemovedTime() {
        return RemovedTime;
    }

    public void setRemovedTime(String removedTime) {
        RemovedTime = removedTime;
    }

    public String getSetupTime() {
        return SetupTime;
    }

    public void setSetupTime(String setupTime) {
        SetupTime = setupTime;
    }

    public String getToolId() {
        return ToolId;
    }

    public void setToolId(String toolId) {
        ToolId = toolId;
    }

    public String getLastChangedDateTime() {
        return LastChangedDateTime;
    }

    public void setLastChangedDateTime(String lastChangedDateTime) {
        LastChangedDateTime = lastChangedDateTime;
    }

    public ToolEntryStatus getToolStatus() {
        return toolStatus;
    }

    public void setToolStatus(ToolEntryStatus toolStatus) {
        this.toolStatus = toolStatus;
    }

    public String getContactPersonEmail() {
        return ContactPersonEmail;
    }

    public void setContactPersonEmail(String contactPersonEmail) {
        ContactPersonEmail = contactPersonEmail;
    }

    public String getContactPersonPhone() {
        return ContactPersonPhone;
    }

    public void setContactPersonPhone(String contactPersonPhone) {
        ContactPersonPhone = contactPersonPhone;
    }

    public String getContactPersonName() {
        return ContactPersonName;
    }

    public void setContactPersonName(String contactPersonName) {
        ContactPersonName = contactPersonName;
    }

    public String getSource() {
        return Source;
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

    @Override
    public int describeContents() {
        return 0;
    }
}
