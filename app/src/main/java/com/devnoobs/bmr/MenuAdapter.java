package com.devnoobs.bmr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Lukasz on 2015-05-30.
 */
public class MenuAdapter extends ArrayAdapter<com.devnoobs.bmr.MenuItem> {

    private Context context;
    private ArrayList<com.devnoobs.bmr.MenuItem> menuItems;

//    public MenuAdapter(Context context, int resource, List<com.devnoobs.bmr.MenuItem> objects) {
//        super(context, resource, objects);
//        this.context = context;
//        this.menuItems = objects;
//    }

    public MenuAdapter(Context context, ArrayList<com.devnoobs.bmr.MenuItem> menuItems) {
        super(context, R.layout.drawer_list_item, menuItems);

        this.context = context;
        this.menuItems = menuItems;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // 1. Create inflater
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 2. Get rowView from inflater

        View rowView = null;

        rowView = inflater.inflate(R.layout.drawer_list_item, parent, false);

        // 3. Get icon,title & counter views from the rowView
        ImageView imgView = (ImageView) rowView.findViewById(R.id.menuIcon);
        TextView titleView = (TextView) rowView.findViewById(R.id.menuText);

        // 4. Set the text for textView
        imgView.setImageResource(menuItems.get(position).getIcon());
        titleView.setText(menuItems.get(position).getTytul());

        return rowView;
    }

}
