package mycompany.thistest.Fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.List;

import mycompany.thistest.R;
import mycompany.thistest.TFL.Arrival;

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
            LinearLayout linearLayout;
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
                viewToUse.setTag(holder);
            } else {
                viewToUse = convertView;
                holder = (ViewHolder) viewToUse.getTag();
            }
            holder.titleText = (TextView)viewToUse.findViewById(R.id.titleTextView);
            holder.linearLayout = (LinearLayout)viewToUse.findViewById(R.id.linLayout);
            //if(item.getUpdate())
            {
                holder.titleText.setText(item.getItemTitle());

                holder.linearLayout.removeAllViews();
                for (int i = 0; i < item.getArrivals().size(); i++) {
                    TextView tv = new TextView(getContext());
                    tv.setText(item.getArrivals().get(i) + "\n");
                    tv.setBackgroundColor(Color.WHITE);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                            ((int) ViewGroup.LayoutParams.MATCH_PARENT, (int) ViewGroup.LayoutParams.MATCH_PARENT);
                    params.gravity = Gravity.CENTER_HORIZONTAL;
                    tv.setLayoutParams(params);
                    tv.setGravity(Gravity.CENTER);

                    tv.requestLayout();
                    holder.linearLayout.addView(tv);
                }
                item.hasUpdated();
            }
            viewToUse.setBackgroundColor(item.getColor());

            return  viewToUse;
        }

    public void update(List<NextArrivalsItem> new_items){
        NextArrivalsItem item = (NextArrivalsItem)getItem(1);
    }






    }