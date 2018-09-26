package fiskinfoo.no.sintef.fiskinfoo.UtilityRows;

import android.content.Context;

import fiskinfoo.no.sintef.fiskinfoo.Implementation.ICoordinateRow;

public abstract class CoordinateRow extends BaseTableRow implements ICoordinateRow{
    public CoordinateRow(Context context, int layoutId) {
        super(context, layoutId);
    }
}
