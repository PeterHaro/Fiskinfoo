package fiskinfoo.no.sintef.fiskinfoo.Baseclasses;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public enum Tool {
    BUNNTRÅL, GARN, LINE, RINGNOT, SNURREVAD, TARETRÅL, HARPUN;

    @Override
    public String toString() {
        return Character.toUpperCase(name().charAt(0)) + name().toLowerCase().substring(1);
    }

    public static String[] getValues() {
        String[] retval = {"Bunntrål", "Garn", "Line", "Ringnot", "Snurrevad", "Taretrål", "Harpun"};
        return retval;
    }

    public String toFAO() {
        String retval;
        switch(this) {
            case BUNNTRÅL:
                retval = "OTB";
                break;
            case GARN:
                retval = "GN";
                break;
            case LINE:
                retval = "LL";
                break;
            case RINGNOT:
                retval = "PS";
                break;
            case SNURREVAD:
                retval = "SV";
                break;
            case TARETRÅL:
                retval = "DRB";
                break;
            case HARPUN:
                retval = "HAR";
                break;
            default:
                throw new InvalidParameterException();
        }
        return retval;
    }

    public static Tool createFromValue(String value) {
        if (value.equalsIgnoreCase("Bunntrål")) {
            return Tool.BUNNTRÅL;
        } else if (value.equalsIgnoreCase("garn")) {
            return Tool.GARN;
        } else if (value.equalsIgnoreCase("line")) {
            return Tool.LINE;
        } else if (value.equalsIgnoreCase("ringnot")) {
            return Tool.RINGNOT;
        } else if (value.equalsIgnoreCase("snurrevad")) {
            return Tool.SNURREVAD;
        } else if (value.equalsIgnoreCase("taretrål")) {
            return Tool.TARETRÅL;
        } else if (value.equalsIgnoreCase("harpun")) {
            return Tool.HARPUN;
        }
        return null;
    }
}
