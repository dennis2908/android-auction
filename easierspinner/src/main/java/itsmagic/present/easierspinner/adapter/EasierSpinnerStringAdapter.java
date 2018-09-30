package itsmagic.present.easierspinner.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Alvin Rusli on 4/11/2016.
 * <p/>
 * A simple adapter for this library that displays a simple String for all items.
 */
public class EasierSpinnerStringAdapter<DATA_TYPE> extends EasierSpinnerAdapterCore<DATA_TYPE, EasierSpinnerStringAdapter.ViewHolder, EasierSpinnerStringAdapter.ViewHolder, EasierSpinnerStringAdapter.ViewHolder> {

    public EasierSpinnerStringAdapter(Context context) {
        super(context);
    }

    @Override
    public EasierSpinnerStringAdapter.ViewHolder onCreatePlaceholderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_spinner_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public EasierSpinnerStringAdapter.ViewHolder onCreateSelectionViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_spinner_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public EasierSpinnerStringAdapter.ViewHolder onCreateDropdownViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindPlaceholderViewHolder(EasierSpinnerStringAdapter.ViewHolder holder, DATA_TYPE placeholder) {
        if (placeholder != null) {
            if (placeholder instanceof SpinnerText) {
                holder.mTextView.setText(((SpinnerText) placeholder).getSpinnerText());
            } else if (placeholder instanceof CharSequence) {
                holder.mTextView.setText((CharSequence) placeholder);
            } else {
                holder.mTextView.setText(placeholder.toString());
            }
        } else {
            holder.mTextView.setText(null);
        }
    }

    @Override
    public void onBindSelectionViewHolder(EasierSpinnerStringAdapter.ViewHolder holder, int position, DATA_TYPE data) {
        if (data != null) {
            if (data instanceof SpinnerText) {
                holder.mTextView.setText(((SpinnerText) data).getSpinnerText());
            } else if (data instanceof CharSequence) {
                holder.mTextView.setText((CharSequence) data);
            } else {
                holder.mTextView.setText(data.toString());
            }
        } else {
            holder.mTextView.setText(null);
        }
    }

    @Override
    public void onBindDropdownViewHolder(EasierSpinnerStringAdapter.ViewHolder holder, int position, DATA_TYPE data, boolean isPlaceholder) {
        if (data != null) {
            if (data instanceof SpinnerText) {
                holder.mTextView.setText(((SpinnerText) data).getSpinnerText());
            } else if (data instanceof CharSequence) {
                holder.mTextView.setText((CharSequence) data);
            } else {
                holder.mTextView.setText(data.toString());
            }
        } else {
            holder.mTextView.setText(null);
        }
    }

    public class ViewHolder extends EasierSpinnerViewHolder {

        private TextView mTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(android.R.id.text1);
        }
    }

    /** An interface to determine which text should be shown on the Spinner that uses this class */
    public interface SpinnerText {

        /** Determines the text to be shown */
        String getSpinnerText();
    }
}
