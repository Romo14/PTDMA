package com.example.oriolgasset.weatherforecast;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Oriol-Sony Vaio on 22/4/2016.
 */
public class ForecastAdapter extends BaseExpandableListAdapter {

    private Context context;
    private HashMap<String,List<String>> forecast;
    private List<String> forecastTitle;


    public ForecastAdapter(Context ctx, HashMap<String,List<String>> forecast, List<String> forecastTitle) {
        this.context = ctx;
        this.forecast = forecast;
        this.forecastTitle = forecastTitle;
    }

    @Override
    public int getGroupCount() {
        return 0;
    }

    @Override
    public int getChildrenCount(int i) {
        return 0;
    }

    @Override
    public Object getGroup(int i) {
        return null;
    }

    @Override
    public Object getChild(int parent, int child) {
        return forecast.get(forecastTitle.get(parent)).get(child);
    }

    @Override
    public long getGroupId(int i) {
        return 0;
    }

    @Override
    public long getChildId(int parent, int child) {
        return child;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        return null;
    }

    @Override
    public View getChildView(int parent, int child, boolean lastChild, View view, ViewGroup viewGroup) {
        String child_title = (String) getChild(parent,child);
        if(view == null) {
            LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflator.inflate(R.layout.child_layout, viewGroup,false);
        }

        return null;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
}
