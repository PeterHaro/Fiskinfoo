package fiskinfoo.no.sintef.fiskinfoo.Implementation;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.AvailableSubscriptionItem;
import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.SubscriptionEntry;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.ApiErrorType;
import fiskinfoo.no.sintef.fiskinfoo.R;

/**
 * Created by erlendstav on 11/04/2018.
 */

public class DownloadListAdapter extends RecyclerView.Adapter<DownloadListAdapter.DownloadsViewHolder> {

    private List<AvailableSubscriptionItem> mDataSet;
    public DownloadSelectionListener mListener;

    public static class DownloadsViewHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {

        public TextView dataText;
        public TextView lastUpdatedTextView;
        public CheckBox subscribedCheckBox;
        public Button downloadButton;
        public ImageView notificationImageView;

        public DownloadsViewHolder(View itemView) {
            super(itemView);
            dataText = (TextView) itemView.findViewById(R.id.recycler_child_item_title_text_view);
            lastUpdatedTextView = (TextView) itemView.findViewById(R.id.recycler_child_last_updated_text_view);
            subscribedCheckBox = (CheckBox) itemView.findViewById(R.id.recycle_child_view_subscribed_check_box);
            downloadButton = (Button) itemView.findViewById(R.id.recycler_item_download_map_button);
            notificationImageView = (ImageView) itemView.findViewById(R.id.recycle_child_notification_image_view);
        }

        @Override
        public void onClick(View view) {

        }
    }

    public DownloadListAdapter(List<AvailableSubscriptionItem> dataSet, DownloadSelectionListener listener) {
        super();
        mDataSet = dataSet;
        mListener = listener;
    }

    public interface DownloadSelectionListener {
        void onTitleClicked(AvailableSubscriptionItem item);
        void onDownloadButton(AvailableSubscriptionItem item);
        void onSubscribed(AvailableSubscriptionItem item);
    }


    @Override
    public DownloadsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DownloadsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_layout_child, parent, false));
    }

    @Override
    public void onBindViewHolder(DownloadsViewHolder holder, int position) {
        Context context = holder.lastUpdatedTextView.getContext();
        final AvailableSubscriptionItem entry = mDataSet.get(position);

        if(entry.getErrorType() == ApiErrorType.WARNING) {
            holder.notificationImageView.setVisibility(View.VISIBLE);
            holder.notificationImageView.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_error_outline_black_24dp));
            //holder.notificationImageView.setOnClickListener(entry.getErrorNotificationOnClickListener());

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.notificationImageView.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.material_icon_black_active_tint_color));
            }
        } else if(entry.getErrorType() == ApiErrorType.ERROR) {
            holder.notificationImageView.setVisibility(View.VISIBLE);
            holder.notificationImageView.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_warning_black_24dp));
            //holder.notificationImageView.setOnClickListener(entry.getErrorNotificationOnClickListener());

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.notificationImageView.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.material_icon_black_active_tint_color));
            }
        } else {
            holder.notificationImageView.setVisibility(View.GONE);
        }

        holder.dataText.setText(entry.getTitle());
        //holder.dataText.setOnClickListener(childrenOnClickListener);

        holder.lastUpdatedTextView.setText(entry.getLastUpdated());
        holder.lastUpdatedTextView.setGravity(Gravity.CENTER);
        holder.subscribedCheckBox.setChecked(entry.isSubscribed());


        holder.downloadButton.setOnClickListener( new View.OnClickListener() {
                                                      @Override
                                                      public void onClick(View v) {
                                                          mListener.onDownloadButton(entry);
                                                      }
                                                  });
       //holder.subscribedCheckBox.setOnClickListener(entry.getSubscribeSwitchOnClickListener());

        if(!(new FiskInfoUtility().isNetworkAvailable(context))) {
            holder.subscribedCheckBox.setEnabled(false);
            holder.downloadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(view.getContext())
                            .setIcon(R.drawable.ic_info_outline_black_24dp)
                            .setTitle(view.getContext().getString(R.string.download_title))
                            .setMessage(view.getContext().getString(R.string.error_cannot_download_without_internet_access))
                            .setPositiveButton(view.getContext().getString(R.string.ok), null)
                            .show();
                }
            });

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.downloadButton.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.material_icon_black_active_tint_color));
            }
        } else if(!entry.isAuthorized() && !holder.dataText.getText().toString().equals(context.getString(R.string.fishing_facility_name))) {
            holder.subscribedCheckBox.setEnabled(false);
            holder.downloadButton.setEnabled(false);
//            holder.downloadButton.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_info_outline_black_24dp));

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.downloadButton.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.material_icon_black_disabled_tint_color));
            }
        } else {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.downloadButton.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.material_icon_black_active_tint_color));
            }
        }

    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }


}
