package fiskinfoo.no.sintef.fiskinfoo.Baseclasses;


import android.view.View;

public class SummaryViewItem extends DefaultCardViewViewHolder{
    private String title;
    private String subtitle; //Optional
    private String descriptionText;
    private String description;
    private String positiveActionButtonText;
    private String negativeActionButtonText;
    private int imageResource;

    private View.OnClickListener positiveButtonOnClickListener;
    private View.OnClickListener negativeButtonOnClickListener;

    public SummaryViewItem(String title, String subtitle, String descriptionText, String description) {
        this.title = title;
        this.subtitle = subtitle;
        this.descriptionText = descriptionText;
        this.description = description;
    }

    public SummaryViewItem(String title, String subtitle, String descriptionText, String description, int imageResource ) {
        this.title = title;
        this.subtitle = subtitle;
        this.descriptionText = descriptionText;
        this.description = description;
        this.imageResource = imageResource;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getContentText() {
        return descriptionText;
    }

    public void setDescriptionText(String descriptionText) {
        this.descriptionText = descriptionText;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getImageResource() {return imageResource;}

    public void setImageResource(int imageResource) {this.imageResource = imageResource;}

    public String getPositiveActionButtonText() {
        return positiveActionButtonText;
    }
    public void setPositiveActionButtonText(String positiveActionButtonText) {
        this.positiveActionButtonText = positiveActionButtonText;
    }

    public void setPositiveButtonOnClickListener(View.OnClickListener positiveButtonOnClickListener) {
        this.positiveButtonOnClickListener = positiveButtonOnClickListener;
    }

    public void setNegativeActionButtonText(String negativeActionButtonText) {
        this.negativeActionButtonText = negativeActionButtonText;
    }

    @Override
    public String getNegativeActionButtonText() {
        return negativeActionButtonText;
    }

    public void setNegativeActionButtonOnClickListener(View.OnClickListener negativeActionButtonOnClickListener) {
        this.negativeButtonOnClickListener = negativeActionButtonOnClickListener;
    }

    @Override
    public View.OnClickListener getPositiveActionButtonListener() {
        return positiveButtonOnClickListener;
    }

    @Override
    public View.OnClickListener getNegativeActionButtonListener() {
        return negativeButtonOnClickListener;
    }
}
