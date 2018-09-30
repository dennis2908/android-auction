package itsmagic.present.easierspinner.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.Spinner;

/**
 * Created by Alvin Rusli on 5/24/2016.
 * <p/>
 * A customized {@link Spinner} class that fires the selection method,
 * even when the same item is selected.
 */
public class CustomSpinner extends Spinner {

    private OnSpinnerEventsListener mListener;
    private boolean mOpenInitiated = false;

    public CustomSpinner(Context context) {
        super(context);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public CustomSpinner(Context context, int mode) {
        super(context, mode);
    }

    public CustomSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public CustomSpinner(Context context, AttributeSet attrs, int defStyleAttr, int mode) {
        super(context, attrs, defStyleAttr, mode);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CustomSpinner(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes, int mode) {
        super(context, attrs, defStyleAttr, defStyleRes, mode);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public CustomSpinner(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes, int mode, Resources.Theme popupTheme) {
        super(context, attrs, defStyleAttr, defStyleRes, mode, popupTheme);
    }

    @Override
    public void setSelection(int position, boolean animate) {
        super.setSelection(position, animate);
        callSelectedListener(position);
    }

    @Override
    public void setSelection(int position) {
        super.setSelection(position);
        callSelectedListener(position);
    }

    @Override
    public boolean performClick() {
        // register that the Spinner was opened so we have a status
        // indicator for when the container holding this Spinner may lose focus
        mOpenInitiated = true;
        if (mListener != null) mListener.onSpinnerOpened(this);
        return super.performClick();
    }

    /**
     * Propagate the closed Spinner event to the listener from outside if needed.
     */
    public void performClosedEvent() {
        mOpenInitiated = false;
        if (mListener != null) mListener.onSpinnerClosed(this);
    }

    /**
     * A boolean flag indicating that the Spinner triggered an open event.
     * @return true for opened Spinner
     */
    public boolean hasBeenOpened() {
        return mOpenInitiated;
    }

    public void onWindowFocusChanged (boolean hasFocus) {
        if (hasBeenOpened() && hasFocus) {
            performClosedEvent();
        }
    }

    /** Call the same item selected listener */
    private void callSelectedListener(int position) {
        boolean sameSelected = position == getSelectedItemPosition();
        if (sameSelected) {
            // Spinner does not call the OnItemSelectedListener if the same item is selected, so do it manually now
            if (getOnItemSelectedListener() != null)
                getOnItemSelectedListener().onItemSelected(this, getSelectedView(), position, getSelectedItemId());
        }
    }

    public OnSpinnerEventsListener getOnSpinnerEventsListener() {
        return mListener;
    }

    public void setOnSpinnerEventsListener(OnSpinnerEventsListener listener) {
        mListener = listener;
    }

    /**
     * An interface which a client of this Spinner could use to receive
     * open/closed events for this Spinner.
     */
    public interface OnSpinnerEventsListener {

        /** Callback triggered when the spinner was opened */
        void onSpinnerOpened(Spinner spinner);

        /** Callback triggered when the spinner was closed */
        void onSpinnerClosed(Spinner spinner);
    }
}
