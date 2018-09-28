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

package fiskinfoo.no.sintef.fiskinfoo.Implementation;

import android.os.Parcel;
import android.os.Parcelable;

import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.CoordinateFormat;
import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.ToolType;

public class UserSettings implements Parcelable {
    private ToolType toolType;
    private String vesselName;
    private String vesselPhone;
    private String ircs;
    private String mmsi;
    private String imo;
    private String registrationNumber;
    private String ContactPersonEmail;
    private String ContactPersonPhone;
    private String ContactPersonName;
    private CoordinateFormat coordinateFormat;

    private boolean privacyPolicyConsent;

    public UserSettings() {

    }

    protected UserSettings(Parcel in) {
        vesselName = in.readString();
        vesselPhone = in.readString();
        ircs = in.readString();
        mmsi = in.readString();
        imo = in.readString();
        toolType = ToolType.createFromValue(in.readString());
        registrationNumber = in.readString();
        ContactPersonEmail = in.readString();
        ContactPersonPhone = in.readString();
        ContactPersonName = in.readString();
        String coordinateFormatString = in.readString();
        coordinateFormat = CoordinateFormat.createFromValue(coordinateFormatString != null ? coordinateFormatString : CoordinateFormat.DEGREES_MINUTES_SECONDS.toString());
        privacyPolicyConsent = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(vesselName);
        dest.writeString(vesselPhone);
        dest.writeString(ircs);
        dest.writeString(mmsi);
        dest.writeString(imo);
        dest.writeString(toolType.toString());
        dest.writeString(registrationNumber);
        dest.writeString(ContactPersonEmail);
        dest.writeString(ContactPersonPhone);
        dest.writeString(ContactPersonName);
        dest.writeString(getCoordinateFormat().toString());
        dest.writeByte((byte) (privacyPolicyConsent ? 1 : 0));
    }

    public static final Creator<UserSettings> CREATOR = new Creator<UserSettings>() {
        @Override
        public UserSettings createFromParcel(Parcel in) {
            return new UserSettings(in);
        }

        @Override
        public UserSettings[] newArray(int size) {
            return new UserSettings[size];
        }
    };

    public String getVesselName() {
        return vesselName != null ? vesselName : "";
    }

    public void setVesselName(String vesselName) {
        this.vesselName = vesselName;
    }

    public String getVesselPhone() {
        return vesselPhone != null ? vesselPhone : "";
    }

    public void setVesselPhone(String vesselPhone) {
        this.vesselPhone = vesselPhone;
    }


    public String getIrcs() {
        return ircs != null ? ircs : "";
    }

    public void setIrcs(String ircs) {
        this.ircs = ircs;
    }

    public String getMmsi() {
        return mmsi != null ? mmsi : "";
    }

    public void setMmsi(String mmsi) {
        this.mmsi = mmsi;
    }

    public String getImo() {
        return imo != null ? imo : "";
    }

    public void setImo(String imo) {
        this.imo = imo;
    }

    public ToolType getToolType() {
        return toolType;
    }

    public void setToolType(ToolType toolType) {
        this.toolType = toolType;
    }

    public String getRegistrationNumber() {
        return registrationNumber != null ? registrationNumber : "";
    }

    public void setRegistrationNumber(String registrationNumber)  {
        this.registrationNumber = registrationNumber;
    }

    public String getContactPersonEmail() {
        return ContactPersonEmail != null ? ContactPersonEmail : "";
    }

    public void setContactPersonEmail(String contactPersonEmail) {
        ContactPersonEmail = contactPersonEmail;
    }

    public String getContactPersonPhone() {
        return ContactPersonPhone != null ? ContactPersonPhone : "";
    }

    public void setContactPersonPhone(String contactPersonPhone) {
        ContactPersonPhone = contactPersonPhone;
    }

    public String getContactPersonName() {
        return ContactPersonName != null ? ContactPersonName : "";
    }

    public void setContactPersonName(String contactPersonName) {
        ContactPersonName = contactPersonName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public CoordinateFormat getCoordinateFormat() {
        return coordinateFormat != null ? coordinateFormat : CoordinateFormat.DEGREES_MINUTES_SECONDS;
    }

    public void setCoordinateFormat(CoordinateFormat coordinateFormat) {
        this.coordinateFormat = coordinateFormat;
    }

    public boolean getPrivacyPolicyConsent() {
        return privacyPolicyConsent;
    }

    public void setPrivacyPolicyConsent(boolean privacyPolicyConsent) {
        this.privacyPolicyConsent = privacyPolicyConsent;
    }
}
