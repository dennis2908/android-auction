package itsmagic.present.easierspinner.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import itsmagic.present.easierspinner.EasierSpinnerCore;

/**
 * Created by Alvin Rusli on 5/26/2016.
 * <p/>
 * A core adapter class for this library.
 * This class holds 3 different {@link EasierSpinnerViewHolder} for 3 different item states.
 */
public abstract class EasierSpinnerAdapterCore<DATA_TYPE, VH1 extends EasierSpinnerViewHolder, VH2 extends EasierSpinnerViewHolder, VH3 extends EasierSpinnerViewHolder> extends ArrayAdapter {

    /** The {@link EasierSpinnerCore} that uses this adapter */
    private EasierSpinnerCore mEasierSpinner;

    /** The data list for this adapter */
    private List<DATA_TYPE> mData;

    /** The placeholder data */
    private DATA_TYPE mPlaceholder;

    /** Additional condition to determine if this adapter should display a placeholder or not */
    private boolean isSelected = true;

    /**
     * The constructor for this class.
     * @param context The context
     */
    public EasierSpinnerAdapterCore(Context context) {
        this(context, new ArrayList<DATA_TYPE>());
    }

    /** @return the {@link EasierSpinnerCore} that uses this adapter */
    public EasierSpinnerCore getEasierSpinner() {
        return mEasierSpinner;
    }

    /**
     * Sets the {@link EasierSpinnerCore} that uses this adapter
     * @param spinner the spinner
     */
    public void setEasierSpinner(EasierSpinnerCore spinner) {
        mEasierSpinner = spinner;
    }

    /**
     * The constructor for this class.
     * @param context The context
     * @param objects The data list
     */
    @SuppressWarnings("unchecked")
    private EasierSpinnerAdapterCore(final Context context, final List<DATA_TYPE> objects) {
        super(context, 0, objects);
        mData = objects;
    }

    @Override
    @SuppressWarnings("unchecked")
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        boolean isPlaceholder;
        EasierSpinnerViewHolder holder;

        // Generate the View for each item state using the different ViewHolder
        View newConvertView = convertView;
        if (!isSelected || getData().isEmpty()) {
            isPlaceholder = true;
            if (newConvertView == null) {
                holder = onCreatePlaceholderViewHolder(parent);
                newConvertView = holder.getItemView();
                newConvertView.setTag(holder);
            } else {
                holder = (EasierSpinnerViewHolder) newConvertView.getTag();
            }
        } else {
            isPlaceholder = false;
            if (newConvertView == null) {
                holder = onCreateSelectionViewHolder(parent);
                newConvertView = holder.getItemView();
                newConvertView.setTag(holder);
            } else {
                holder = (EasierSpinnerViewHolder) newConvertView.getTag();
            }
        }

        if (isPlaceholder) {
            onBindPlaceholderViewHolder((VH1) holder, getPlaceholder());
        } else {
            try {
                onBindSelectionViewHolder((VH2) holder, position, getData().get(position));
            } catch (IndexOutOfBoundsException e) {
                onBindPlaceholderViewHolder((VH1) holder, getPlaceholder());
            }
        }

        return newConvertView;
    }

    @Override
    @SuppressWarnings("unchecked")
    public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
        boolean isPlaceholder;
        EasierSpinnerViewHolder holder;

        // Don't display any dropdown if spinner doesn't have any data
        isPlaceholder = mData == null || mData.isEmpty();

        View newConvertView = convertView;
        if (newConvertView == null) {
            holder = onCreateDropdownViewHolder(parent);
            newConvertView = holder.getItemView();
            newConvertView.setTag(holder);
        } else {
            holder = (EasierSpinnerViewHolder) newConvertView.getTag();
        }

        if (isPlaceholder) {
            onBindDropdownViewHolder((VH3) holder, 0, getPlaceholder(), true);
        } else {
            onBindDropdownViewHolder((VH3) holder, position, getData().get(position), false);
        }

        return newConvertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public DATA_TYPE getItem(int position) {
        return mData.get(position);
    }

    @Override
    public int getCount() {
        if (mData == null || mData.isEmpty()) return 1;
        else return mData.size();
    }

    public List<DATA_TYPE> getData() {
        return mData;
    }

    public void setData(List<DATA_TYPE> data) {
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    /** Obtain the currently set placeholder */
    public DATA_TYPE getPlaceholder() {
        return mPlaceholder;
    }

    /** Sets the placeholder */
    public void setPlaceholder(DATA_TYPE placeholder) {
        mPlaceholder = placeholder;
    }

    /**
     * Creates the {@link EasierSpinnerViewHolder} for placeholder item.
     * @return The placeholder view holder for the adapter
     */
    public abstract VH1 onCreatePlaceholderViewHolder(ViewGroup parent);

    /**
     * Creates the {@link EasierSpinnerViewHolder} for the selected item.
     * @return The view holder for the adapter
     */
    public abstract VH2 onCreateSelectionViewHolder(ViewGroup parent);

    /**
     * Creates the {@link EasierSpinnerViewHolder} for dropdown items.
     * @return The dropdown view holder for the adapter
     */
    public abstract VH3 onCreateDropdownViewHolder(ViewGroup parent);

    /**
     * Sets the placeholder view for the {@link android.widget.Spinner} adapter.
     * @param holder The inflated view holder
     */
    public abstract void onBindPlaceholderViewHolder(VH1 holder, DATA_TYPE placeholder);

    /**
     * Sets the view for the {@link android.widget.Spinner} selected item adapter.
     * @param holder The inflated view holder
     * @param position The item position
     * @param data The current data
     */
    public abstract void onBindSelectionViewHolder(VH2 holder, int position, DATA_TYPE data);

    /**
     * Sets the view for the {@link android.widget.Spinner}'s dropdown adapter.
     * @param holder The inflated view holder
     * @param position The item position
     * @param data The current data
     */
    public abstract void onBindDropdownViewHolder(VH3 holder, int position, DATA_TYPE data, boolean isPlaceholder);
}