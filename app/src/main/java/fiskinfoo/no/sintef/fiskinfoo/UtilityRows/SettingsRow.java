package fiskinfoo.no.sintef.fiskinfoo.UtilityRows;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import fiskinfoo.no.sintef.fiskinfoo.R;

public class SettingsRow extends BaseTableRow {
    private ImageView iconImageView;
    private TextView titleTextView;

    public SettingsRow(Context context, String title, int iconId, View.OnClickListener onClickListener) {
        super(context, R.layout.material_settings_row_card_view);

        iconImageView = (ImageView) getView().findViewById(R.id.settings_row_icon_image_view);
        titleTextView = (TextView) getView().findViewById(R.id.settings_row_setting_title_text_view);

        titleTextView.setText(title);
        iconImageView.setBackgroundResource(iconId);
        getView().setOnClickListener(onClickListener);
    }

    public void setIcon(int iconId) {
        iconImageView.setBackgroundResource(iconId);
    }

    public String getTitle() {
        return titleTextView.getText().toString();
    }

    public void setTitle(String title) {
        titleTextView.setText(title);
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        getView().setOnClickListener(onClickListener);
    }

    @Override
    public void setEnabled(boolean enabled) {

    }

//    @Override
//    boolean validateInput() {
//        return false;
//    }
}
