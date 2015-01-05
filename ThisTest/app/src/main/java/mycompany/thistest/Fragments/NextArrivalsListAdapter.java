package mycompany.thistest.Fragments;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.List;

import mycompany.thistest.R;

public class NextArrivalsListAdapter extends ArrayAdapter {

        private Context context;
        private boolean useList = true;

        public NextArrivalsListAdapter(Context context, List items) {
            super(context, android.R.layout.simple_list_item_1, items);
            this.context = context;
        }

        /**
         * Holder for the list items.
         */
        private class ViewHolder{
            TextView titleText;
            LinearLayout listView;
        }

        /**
         *
         * @param position
         * @param convertView
         * @param parent
         * @return
         */
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            NextArrivalsItem item = (NextArrivalsItem)getItem(position);
            View viewToUse = null;

            // This block exists to inflate the settings list item conditionally based on whether
            // we want to support a grid or list view.
            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            if (convertView == null) {

                viewToUse = mInflater.inflate(R.layout.next_arrivals_layout, null);
                holder = new ViewHolder();
                holder.titleText = (TextView)viewToUse.findViewById(R.id.titleTextView);
                holder.listView = (LinearLayout)viewToUse.findViewById(R.id.linLayout);
                for(String s: item.getList()) {
                    TextView tv = new TextView(getContext());
                    tv.setText(s);
                    holder.listView.addView(tv);
                }
                viewToUse.setTag(holder);
            } else {
                viewToUse = convertView;
                holder = (ViewHolder) viewToUse.getTag();
            }

            holder.titleText.setText(item.getItemTitle());
            ArrayAdapter<String> myAdapter=new ArrayAdapter<String>(context,
                    android.R.layout.simple_list_item_1,
                    item.getList());
            // holder.listView.setAdapter(myAdapter);

            return viewToUse;
        }
    }