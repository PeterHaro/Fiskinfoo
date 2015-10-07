package fiskinfoo.no.sintef.fiskinfoo.Baseclasses;

import android.os.Parcel;
import android.os.Parcelable;

public class ToolInfo implements Parcelable{
    private int toolID;
    private Tool toolType;
    private String vesselName;
    private String vesselPhone;
    private String setupDateTime;
    private String ircs;
    private String mmsi;
    private String imo;
    private String vesselemail;


    public ToolInfo(Tool toolType, String vesselName, String vesselPhone, String setupDateTime, String ircs, String mmsi, String imo, String vesselemail) {
        this.toolType = toolType;
        this.vesselName = vesselName;
        this.vesselPhone = vesselPhone;
        this.setupDateTime = setupDateTime;
        this.ircs = ircs;
        this.mmsi = mmsi;
        this.imo = imo;
        this.vesselemail = vesselemail;
        toolID = 0;
    }

    protected ToolInfo(Parcel in) {
        toolID = in.readInt();
        vesselName = in.readString();
        vesselPhone = in.readString();
        setupDateTime = in.readString();
        ircs = in.readString();
        mmsi = in.readString();
        imo = in.readString();
        vesselemail = in.readString();
        toolType = Tool.createFromValue(in.readString());
    }

    public static final Creator<ToolInfo> CREATOR = new Creator<ToolInfo>() {
        @Override
        public ToolInfo createFromParcel(Parcel in) {
            return new ToolInfo(in);
        }

        @Override
        public ToolInfo[] newArray(int size) {
            return new ToolInfo[size];
        }
    };

    public int getToolID() {
        return toolID;
    }

    public void setToolID(int toolID) {
        this.toolID = toolID;
    }

    public String getVesselemail() {
        return vesselemail;
    }

    public String getImo() {
        return imo;
    }

    public Tool getToolType() {
        return toolType;
    }

    public String getVesselName() {
        return vesselName;
    }

    public String getVesselPhone() {
        return vesselPhone;
    }

    public String getSetupDateTime() {
        return setupDateTime;
    }

    public String getIrcs() {
        return ircs;
    }

    public String getMmsi() {
        return mmsi;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(toolID);
        dest.writeString(vesselName);
        dest.writeString(vesselPhone);
        dest.writeString(setupDateTime);
        dest.writeString(ircs);
        dest.writeString(mmsi);
        dest.writeString(imo);
        dest.writeString(vesselemail);
        dest.writeString(toolType.toString());
    }
}