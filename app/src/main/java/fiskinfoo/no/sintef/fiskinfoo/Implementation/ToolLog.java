package fiskinfoo.no.sintef.fiskinfoo.Implementation;


import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.ToolEntry;

public class ToolLog implements Parcelable{
    public NavigableMap<String, ArrayList<ToolEntry>> myLog;

    public ToolLog() {
        myLog = new TreeMap<>(new dateComperator());

    }

    public void addTool(ToolEntry tool, String date) {
        if (!myLog.containsKey(date)) {
            myLog.put(date, new ArrayList<ToolEntry>());
        }

        int toolLogId = 1;

        for(ToolEntry toolEntry : myLog.get(date)) {
            if(toolEntry.getToolLogId() >= toolLogId) {
                toolLogId = toolEntry.getToolLogId() + 1;
            }
        }

        tool.setToolLogId(toolLogId);
        myLog.get(date).add(tool);

    }

    public void removeTool(String date, int toolId) {
        if (myLog.get(date) == null) {
            throw new IndexOutOfBoundsException("Invalid date, no Hauls on the specified date: " + date);
        }

        boolean containsTool = false;

        for(int i = 0; i < myLog.get(date).size(); i++) {
            if(myLog.get(date).get(i).getToolLogId() == toolId) {
                containsTool = true;
                myLog.get(date).remove(i);
            }
        }

        if(toolId != 0 && !containsTool) {
            throw new IndexOutOfBoundsException("Invalid index to get entry. the specified id: " + toolId + " does not exist in the log");
        }
    }

    public ArrayList<ToolEntry> get(String date) {
        if (!myLog.containsKey(date)) {
            return new ArrayList<>();
        }
        return myLog.get(date);
    }

    public ToolEntry get(String date, int id) {
        if (myLog.get(date) == null) {
            throw new IndexOutOfBoundsException("Invalid date, no Hauls on the specified date: " + date);
        }

        ToolEntry retval = null;

        for(ToolEntry tool : myLog.get(date)) {
            if(tool.getToolLogId() == id) {
                retval = tool;
            }
        }

        if(retval == null) {
            throw new ArrayIndexOutOfBoundsException("Invalid id to get entry. the specified id: " + id + " does not exist in the log");
        }

        return retval;

    }

    protected ToolLog(Parcel in) {
        myLog = new TreeMap<>(new dateComperator());
        final int size = in.readInt();
        for (int i = 0; i < size; i++) {
            final String key = in.readString();
            final int listLen = in.readInt();

            final ArrayList<ToolEntry> list = new ArrayList<ToolEntry>(listLen);
            for (int j = 0; j < listLen; j++) {
                final ToolEntry value = (ToolEntry)in.readParcelable(ToolEntry.class.getClassLoader());
                list.add(value);
            }
            myLog.put(key, list);
        }
    }

    public static final Creator<ToolLog> CREATOR = new Creator<ToolLog>() {
        @Override
        public ToolLog createFromParcel(Parcel in) {
            return new ToolLog(in);
        }

        @Override
        public ToolLog[] newArray(int size) {
            return new ToolLog[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(myLog.size());
        for (Map.Entry<String, ArrayList<ToolEntry>> entry : myLog.entrySet()) {
            dest.writeString(entry.getKey());
            final ArrayList<ToolEntry> toolsInfoList = entry.getValue();
            final int listLen = toolsInfoList.size();

            dest.writeInt(listLen);
            for (ToolEntry item : toolsInfoList) {
                dest.writeParcelable(item, 0);
            }
        }
    }
}

class dateComperator implements Comparator<String> {

    @Override
    public int compare(String arg0, String arg1) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = null;
        Date date2 = null;
        try {
            date1 = format.parse(arg0);
            date2 = format.parse(arg1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date1.compareTo(date2);
    }

}