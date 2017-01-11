package com.applefish.smartshop.classes;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by Ghiath on 1/11/2017.
 */

public class SwipeListAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<Object> objectsList;
    private String[] bgColors;

    public SwipeListAdapter(Activity activity, List<Object> objectsList) {
        this.activity = activity;
        this.objectsList = objectsList;
        //bgColors = activity.getApplicationContext().getResources().getStringArray(R.array.movie_serial_bg);
    }

    @Override
    public int getCount() {
        return objectsList.size();
    }

    @Override
    public Object getItem(int location) {
        return objectsList.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        if (convertView == null)
         //   convertView = inflater.inflate(R.layout.list_row, null);

       /* TextView serial = (TextView) convertView.findViewById(R.id.serial);
        TextView title = (TextView) convertView.findViewById(R.id.title);
*/
      /*  serial.setText(String.valueOf(movieList.get(position).id));
        title.setText(movieList.get(position).title);

        String color = bgColors[position % bgColors.length];
        serial.setBackgroundColor(Color.parseColor(color));*/

        return convertView;
    }

}
