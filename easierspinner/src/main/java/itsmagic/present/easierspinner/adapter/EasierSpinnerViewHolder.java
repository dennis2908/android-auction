package itsmagic.present.easierspinner.adapter;

import android.view.View;

/**
 * Created by Alvin Rusli on 8/30/2016.
 * <p/>
 * A ViewHolder describes an item view and metadata about its place within the Spinner.
 */
public abstract class EasierSpinnerViewHolder {

    /** The item view */
    public final View itemView;

    public EasierSpinnerViewHolder(View itemView) {
        if (itemView == null) throw new IllegalArgumentException("itemView may not be null");
        this.itemView = itemView;
    }

    public View getItemView() {
        return itemView;
    }
}