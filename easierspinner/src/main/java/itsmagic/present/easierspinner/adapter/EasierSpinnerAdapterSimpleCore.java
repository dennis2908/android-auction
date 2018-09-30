package itsmagic.present.easierspinner.adapter;

import android.content.Context;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Alvin Rusli on 5/26/2016.
 * <p/>
 * A core adapter class for this library.
 * This class holds a single {@link EasierSpinnerViewHolder} that is used for all 3 item states.
 */
public abstract class EasierSpinnerAdapterSimpleCore<DATA_TYPE, VIEW_HOLDER extends EasierSpinnerViewHolder> extends EasierSpinnerAdapterCore<DATA_TYPE, VIEW_HOLDER, VIEW_HOLDER, VIEW_HOLDER> {

    /**
     * The constructor for this class.
     * @param context The context
     */
    @SuppressWarnings("unchecked")
    public EasierSpinnerAdapterSimpleCore(Context context) {
        super(context);
    }

    @Override
    public VIEW_HOLDER onCreatePlaceholderViewHolder(ViewGroup parent) {
        return onCreateViewHolder(parent);
    }

    @Override
    public VIEW_HOLDER onCreateSelectionViewHolder(ViewGroup parent) {
        return onCreateViewHolder(parent);
    }

    @Override
    public VIEW_HOLDER onCreateDropdownViewHolder(ViewGroup parent) {
        return onCreateViewHolder(parent);
    }

    @Override
    public void onBindPlaceholderViewHolder(VIEW_HOLDER holder, DATA_TYPE placeholder) {
        onBindViewHolder(holder, 0, placeholder, true);
    }

    @Override
    public void onBindSelectionViewHolder(VIEW_HOLDER holder, int position, DATA_TYPE data) {
        onBindViewHolder(holder, position, data, false);
    }

    @Override
    public void onBindDropdownViewHolder(VIEW_HOLDER holder, int position, DATA_TYPE data, boolean isPlaceholder) {
        onBindViewHolder(holder, position, data, isPlaceholder);
    }

    /**
     * Creates the {@link EasierSpinnerViewHolder} for placeholder item.
     * @return The placeholder view holder for the adapter
     */
    public abstract VIEW_HOLDER onCreateViewHolder(ViewGroup parent);

    /**
     * Sets the view for the {@link android.widget.Spinner} selected item adapter.
     * @param holder The inflated view holder
     * @param position The item position
     * @param data The current data
     */
    public abstract void onBindViewHolder(VIEW_HOLDER holder, int position, DATA_TYPE data, boolean isPlaceholder);
}