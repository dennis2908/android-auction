package itsmagic.present.easierspinner;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;

import java.util.List;

import itsmagic.present.easierspinner.adapter.EasierSpinnerAdapterCore;
import itsmagic.present.easierspinner.adapter.EasierSpinnerStringAdapter;
import itsmagic.present.easierspinner.widget.CustomSpinner;

/**
 * Created by Alvin Rusli on 03/03/16.
 * <p/>
 * The core class for EasierSpinner
 */
public abstract class EasierSpinnerCore<DATA_TYPE> extends FrameLayout {

    /** The {@link CustomSpinner} widget */
    private CustomSpinner mSpinner;

    /** The adapter used to display the data as a {@link View} */
    private EasierSpinnerAdapterCore mSpinnerAdapter;

    /** The currently selected item */
    private DATA_TYPE mSelectedItem;

    /** The listener that receives a notification when spinner is opened / closed. */
    private OnSpinnerOpenedListener mOnSpinnerOpenedListener;

    /** The listener that receives a notification when an item is selected. */
    private OnItemSelectedListener<DATA_TYPE> mOnItemSelectionListener;

    /** Additional condition that determines if an item in this {@link android.widget.Spinner} is selected */
    private boolean isItemSelected = true;

    /** Additional condition for first time item selection listener */
    private boolean isInitialItemSelectionInitialization = true;

    public EasierSpinnerCore(Context context) {
        super(context);
        initEasierSpinner(context, null);
    }

    public EasierSpinnerCore(Context context, AttributeSet attrs) {
        super(context, attrs);
        initEasierSpinner(context, attrs);
    }

    private void initEasierSpinner(Context context, AttributeSet attrs) {
        if (attrs != null) {
            // Get the additional attributes first
            TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.EasierSpinner);

            // Additional attribute initialization
            initAttributeSet(context, attr);

            // Recycle the AttributeSet
            attr.recycle();
        }

        // Inflate the layout
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(initSpinnerLayoutRes(), this);

        // Initialize the layout
        mSpinner = findViewById(R.id.easierspinner_spinner);

        // Initialize the default ArrayAdapter
        setAdapter(new EasierSpinnerStringAdapter<>(context));

        // Set the item selected listener for the spinner
        mSpinner.setOnSpinnerEventsListener(new CustomSpinner.OnSpinnerEventsListener() {
            @Override
            public void onSpinnerOpened(Spinner spinner) {
                if (mOnSpinnerOpenedListener != null) mOnSpinnerOpenedListener.onSpinnerOpened(spinner);
            }

            @Override
            public void onSpinnerClosed(Spinner spinner) {
                if (mOnSpinnerOpenedListener != null) mOnSpinnerOpenedListener.onSpinnerClosed(spinner);
            }
        });

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!isInitialItemSelectionInitialization) {
                    if (!mSpinnerAdapter.getData().isEmpty()) {
                        dismissPlaceholder((DATA_TYPE) mSpinnerAdapter.getData().get(position));
                    }
                } else {
                    setInitialSelection(position);
                }

                if (mOnItemSelectionListener != null) {
                    mOnItemSelectionListener.onItemSelected(EasierSpinnerCore.this, position, getSelectedItem());
                }

                isInitialItemSelectionInitialization = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Prepare the spinner
        onInitialization();
        onPrepare();
    }

    /** Called when this widget is initializing */
    protected void onInitialization() {
        // Do nothing
    }

    /** Obtain the layout res to be inflated for this widget */
    protected int initSpinnerLayoutRes() {
        return R.layout.layout_easier_spinner;
    }

    /** Additional initialization for custom attributes */
    protected void initAttributeSet(Context context, TypedArray attr) {
        // To be overridden
    }

    /**
     * Sets the {@link android.widget.Spinner}'s {@link ArrayAdapter}.
     * App will try to use the default implementation of
     * a {@link EasierSpinnerStringAdapter} first time.
     * Remember that any data set before this method will be cleared.
     * @param adapter the specified {@link ArrayAdapter} that extends from {@link EasierSpinnerAdapterCore}.
     */
    public void setAdapter(EasierSpinnerAdapterCore adapter) {
        mSpinnerAdapter = adapter;
        mSpinnerAdapter.setEasierSpinner(this);
        mSpinner.setAdapter(mSpinnerAdapter);
    }

    /** Set the initial item selection with the specified position */
    @SuppressWarnings("unchecked")
    public final void setInitialSelection(int position) {
        if (!mSpinnerAdapter.getData().isEmpty()) {
            if (isItemSelected) setItemSelection((DATA_TYPE) mSpinnerAdapter.getData().get(position));
            else setItemSelection(null);
        }
    }

    /** Sets the item selected state and the selected item */
    private void setItemSelection(DATA_TYPE selectedItem) {
        if (selectedItem != null) {
            isItemSelected = true;
            mSelectedItem = selectedItem;
        } else {
            isItemSelected = false;
            mSelectedItem = null;
        }
    }

    /** Sets the placeholder item for the adapter */
    @SuppressWarnings("unchecked")
    public void setPlaceholder(DATA_TYPE placeholder) {
        mSpinnerAdapter.setPlaceholder(placeholder);
        mSpinnerAdapter.notifyDataSetChanged();
    }

    /** Show the placeholder item */
    public void showPlaceholder() {
        setItemSelection(null);
        mSpinnerAdapter.setSelected(false);
        mSpinnerAdapter.notifyDataSetChanged();

        // Also call the listener
        if (mOnItemSelectionListener != null) {
            mOnItemSelectionListener.onItemSelected(EasierSpinnerCore.this, 0, null);
        }
    }

    /** Dismiss the placeholder item */
    private void dismissPlaceholder(DATA_TYPE selectedItem) {
        setItemSelection(selectedItem);
        mSpinnerAdapter.setSelected(true);
        mSpinnerAdapter.notifyDataSetChanged();
    }

    /** Obtain the data for the {@link CustomSpinner} */
    @SuppressWarnings("unchecked")
    public List<DATA_TYPE> getData() {
        return mSpinnerAdapter.getData();
    }

    /** Sets the data to be used by the {@link CustomSpinner} */
    @SuppressWarnings("unchecked")
    public void setData(List<DATA_TYPE> data) {
        mSpinnerAdapter.setData(data);
    }

    /**
     * Get the Spinner count.
     * @return The data count of the Spinner
     */
    public int getCount() {
        return mSpinnerAdapter.getCount();
    }

    /**
     * Get the selected item.
     * @return The selected item, may be null
     */
    public DATA_TYPE getSelectedItem() {
        if (isItemSelected) return mSelectedItem;
        else return null;
    }

    /**
     * Set the selection with the item at the specified position.
     * @param position The item position
     */
    public void setSelectedItem(int position) {
        isInitialItemSelectionInitialization = false;
        mSpinner.setSelection(position);
    }

    /**
     * Set the selection with the specified item.
     * @param item The item
     */
    public void setSelectedItem(DATA_TYPE item) {
        if (mSpinnerAdapter.getData().contains(item)) {
            isInitialItemSelectionInitialization = false;
            mSpinner.setSelection(mSpinnerAdapter.getData().indexOf(item));
        }
    }

    /**
     * Get the selected item position
     * @return The selected item position
     */
    public int getSelectedItemPosition() {
        if (isItemSelected) return 0;
        else return mSpinner.getSelectedItemPosition();
    }

    /**
     * Get the item at specified position.
     * @param position The specified item position
     * @return The item at the specified position
     */
    @SuppressWarnings("unchecked")
    public DATA_TYPE getItemAtPosition(int position) {
        return (DATA_TYPE) mSpinner.getItemAtPosition(position);
    }

    /** Display the spinner selection (as if the spinner is clicked / receives focus) */
    public void showSelection() {
        if (mSpinner.getWindowToken() != null) {
            mSpinner.performClick();
        }
    }

    /** Get the enabled state for the spinner */
    @Override
    public boolean isEnabled() {
        return mSpinner.isEnabled();
    }

    /** Set the enabled state for the spinner */
    @Override
    public void setEnabled(boolean isEnabled) {
        mSpinner.setEnabled(isEnabled);
    }

    /**
     * Removes the arrow drawable from the {@link Spinner} background.
     * @deprecated Invalid method naming, see {@link #removeSpinnerBackground()}
     * @see #removeSpinnerBackground()
     */
    @Deprecated
    public void removeSpinnerArrow() {
        Log.w(getClass().getSimpleName(), "Deprecated method, call #removeSpinnerBackground() instead");
        removeSpinnerBackground();
    }

    /**
     * Removes the default {@link Spinner} background.
     * The default background is different for each Android version,
     * call this method to completely remove the background.
     */
    public void removeSpinnerBackground() {
        mSpinner.setBackgroundColor(Color.TRANSPARENT);

        // Re-set the left and right padding (one side may have a padding for the dropdown arrow)
        if (getPaddingLeft() < getPaddingRight()) {
            mSpinner.setPadding(getPaddingLeft(), getPaddingTop(), getPaddingLeft(), getPaddingBottom());
        } else {
            mSpinner.setPadding(getPaddingRight(), getPaddingTop(), getPaddingRight(), getPaddingBottom());
        }
    }

    /** Removes the padding from {@link Spinner} */
    public void removeSpinnerPadding() {
        mSpinner.setPadding(0, 0, 0, 0);
    }

    /** Sets the item selection listener for the spinner */
    public void setOnItemSelectedListener(OnItemSelectedListener<DATA_TYPE> listener) {
        mOnItemSelectionListener = listener;
    }

    /** Sets the state listener for the spinner */
    public void setOnSpinnerOpenedListener(OnSpinnerOpenedListener listener) {
        mOnSpinnerOpenedListener = listener;
    }

    /** The item selection listener for the {@link CustomSpinner} */
    public interface OnItemSelectedListener<DATA_TYPE> {

        /** Called when an item from the {@link android.widget.Spinner} is selected */
        void onItemSelected(View v, int position, DATA_TYPE selectedItem);
    }

    /** The item selection listener for the {@link CustomSpinner} */
    public interface OnSpinnerOpenedListener {

        /** Callback triggered when the spinner was opened */
        void onSpinnerOpened(Spinner spinner);

        /** Callback triggered when the spinner was closed */
        void onSpinnerClosed(Spinner spinner);
    }

    /** Called when the {@link EasierSpinnerCore} object has been successfully prepared */
    public abstract void onPrepare();
}
