package fiskinfoo.no.sintef.fiskinfoo.Baseclasses;

import android.view.View;
import android.widget.TextView;

public class ExpandableListChildObject {
    private String mChildText;
    private TextView horribleHack;


    public ExpandableListChildObject() {
    }

    public String getChildText() {
        return mChildText;
    }


    public void setChildText(String childText) {
        mChildText = childText;
    }

    public void setHorribleHack(TextView horribleHack) {
        this.horribleHack = horribleHack;
    }

    public void setHorribleHackOnClickHacker(View.OnClickListener onClickHacker) {
        if (horribleHack != null && onClickHacker != null) {
            horribleHack.setOnClickListener(onClickHacker);
        }
    }

}
