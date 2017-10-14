package com.artemis.hermes.android;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 *  Class to customize the adapter on Listview
 *
 * @author  Jorge Quan
 * @since   2017-10-14
 */
public class ListAdapter extends ArrayAdapter<String> {

    customButtonListener customListener;

    private ArrayList<String> data = new ArrayList<>();

    private Context context;

    /**
     * Constructor
     *
     */
    public ListAdapter(Context context, ArrayList<String> dataItem) {
        super(context, R.layout.restaurant_listview, dataItem);
        this.data = dataItem;
        this.context = context;
    }

    /**
     *  Provides the interface methods
     *
     */
    public interface customButtonListener {
        public void onNavigateButtonClickListener(int position,String value);

        public void onFeedbackButtonClickListener(int position,String value);
    }

    public void setCustomButtonListener(customButtonListener listener) {
        this.customListener = listener;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.restaurant_listview, null);
            viewHolder = new ViewHolder();
            viewHolder.text = (TextView) convertView
                    .findViewById(R.id.restaurantTextview);
            viewHolder.nagivateButton = (Button) convertView
                    .findViewById(R.id.navigateButton);
            viewHolder.feedbackButton = (Button) convertView
                    .findViewById(R.id.feedbackButton);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final String temp = getItem(position);
        viewHolder.text.setText(temp);
        viewHolder.nagivateButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (customListener != null) {
                    customListener.onNavigateButtonClickListener(position,temp);
                }

            }
        });

        viewHolder.feedbackButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (customListener != null) {
                    customListener.onFeedbackButtonClickListener(position,temp);
                }

            }
        });

        return convertView;
    }

    public class ViewHolder {
        TextView text;
        Button nagivateButton;
        Button feedbackButton;
    }
}
