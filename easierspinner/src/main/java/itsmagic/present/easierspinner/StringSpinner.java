package itsmagic.present.easierspinner;

import android.content.Context;
import android.util.AttributeSet;

import itsmagic.present.easierspinner.adapter.EasierSpinnerAdapterCore;

/**
 * Created by Alvin Rusli on 5/24/2016.
 * <p/>
 * A basic {@link EasierSpinnerCore} object.
 * This uses a {@link String} as its item type.
 */
public class StringSpinner extends EasierSpinnerCore<String> {

    public StringSpinner(Context context) {
        super(context);
    }

    public StringSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onPrepare() {
        // No need to initialize anything
    }
}
