package com.example.oriolgasset.weatherforecast;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.nhaarman.listviewanimations.ArrayAdapter;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;
import com.nhaarman.listviewanimations.itemmanipulation.dragdrop.OnItemMovedListener;
import com.nhaarman.listviewanimations.itemmanipulation.dragdrop.TouchViewDraggableManager;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.undo.TimedUndoAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.undo.UndoAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EditPlacesActivity extends AppCompatActivity {

    private DynamicListView listView;
    private SharedPreferences sharedPreferences;
    private Set<String> cities;
    private List<String> citiesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_places);

        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        ActivityManager.TaskDescription taskDesc = new ActivityManager.TaskDescription(getString(R.string.app_name), bm, ContextCompat.getColor(this, R.color.primary_dark));
        this.setTaskDescription(taskDesc);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        listView = (DynamicListView) findViewById(R.id.edit_places_view);
        sharedPreferences = getSharedPreferences("weatherForecastPreferences", MODE_PRIVATE);
        String defaultCity = sharedPreferences.getString("defaultCity", "");
        cities = sharedPreferences.getStringSet("citiesList", new HashSet<String>());
        citiesList = new ArrayList<>(cities);
        if (!cities.isEmpty()) {
            /* Setup the adapter */
            MyListAdapter adapter = new MyListAdapter(this);
            String defaultCityAux = defaultCity.split("=")[0];
            defaultCityAux += " (default)";
            adapter.add(defaultCityAux);
            for (String city : cities) {
                if (!city.equals(defaultCity)) {
                    String aux = city.split("=")[0];
                    adapter.add(aux);
                }
            }
            TimedUndoAdapter simpleSwipeUndoAdapter = new TimedUndoAdapter(adapter, this, new MyOnDismissCallback(adapter));
            AlphaInAnimationAdapter animationAdapter = new AlphaInAnimationAdapter(simpleSwipeUndoAdapter);
            animationAdapter.setAbsListView(listView);
            listView.setAdapter(animationAdapter);

        /* Enable drag and drop functionality */
            listView.enableDragAndDrop();
            listView.setDraggableManager(new TouchViewDraggableManager(R.id.list_row_draganddrop_textview));
            listView.setOnItemMovedListener(new MyOnItemMovedListener(adapter));
            listView.setOnItemLongClickListener(
                    new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(final AdapterView<?> parent, final View view,
                                                       final int position, final long id) {
                            listView.startDragging(position);
                            return true;
                        }
                    }
            );

        /* Enable swipe to dismiss */
            listView.enableSimpleSwipeUndo();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed ();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        setResult(2);
        finish();
    }

    private static class MyListAdapter extends ArrayAdapter<String> implements UndoAdapter {

        private final Context mContext;

        MyListAdapter(final Context context) {
            mContext = context;
        }

        @Override
        public long getItemId(final int position) {
            return getItem(position).hashCode();
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = LayoutInflater.from(mContext).inflate(R.layout.list_row_dynamiclistview, parent, false);
            }

            ((TextView) view.findViewById(R.id.list_row_draganddrop_textview)).setText(getItem(position));

            return view;
        }

        @NonNull
        @Override
        public View getUndoView(final int position, final View convertView, @NonNull final ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = LayoutInflater.from(mContext).inflate(R.layout.undo_row, parent, false);
            }
            return view;
        }

        @NonNull
        @Override
        public View getUndoClickView(@NonNull final View view) {
            return view.findViewById(R.id.undo_row_undobutton);
        }

    }

    private class MyOnDismissCallback implements OnDismissCallback {

        private final ArrayAdapter<String> mAdapter;

        MyOnDismissCallback(final ArrayAdapter<String> adapter) {
            mAdapter = adapter;
        }

        @Override
        public void onDismiss(@NonNull final ViewGroup listView, @NonNull final int[] reverseSortedPositions) {
            if(mAdapter.getCount () == 1){
                Toast.makeText (getBaseContext (),"Error! There have to be at least one city", Toast.LENGTH_SHORT).show ();
                return;
            }
            for (int position : reverseSortedPositions) {
                String s = mAdapter.getItem(position);
                mAdapter.remove(s);
                if (position == 0) {
                    s = s.split(" \\(")[0];
                }
                SharedPreferences.Editor editor = sharedPreferences.edit();
                String aux = "";
                for (String a : citiesList) {
                    if (a.contains(s)) {
                        aux = a;
                    }
                }
                cities.remove(aux);
                editor.remove(aux);
                citiesList.remove(aux);
                if (!citiesList.isEmpty() && !mAdapter.isEmpty() && sharedPreferences.getString("defaultCity", "").contains(s)) {
                    editor.putString("defaultCity", citiesList.get(0));
                    String s1 = mAdapter.getItem (0) + " (default)";
                    mAdapter.remove (0);
                    mAdapter.add (0,s1);
                }
                editor.putStringSet("citiesList", cities);
                editor.commit();
            }
        }
    }

    private class MyOnItemMovedListener implements OnItemMovedListener {

        private final ArrayAdapter<String> mAdapter;

        MyOnItemMovedListener(final ArrayAdapter<String> adapter) {
            mAdapter = adapter;
        }

        @Override
        public void onItemMoved(final int originalPosition, final int newPosition) {
            String oldCity = citiesList.get(originalPosition);
            citiesList.add(originalPosition, citiesList.get(newPosition));
            citiesList.add(newPosition, oldCity);
            if (newPosition == 0) {
                String newDefault = mAdapter.getItem(newPosition) + " (default)";
                mAdapter.remove(newPosition);
                mAdapter.add(newPosition, newDefault);
                String oldDefault = mAdapter.getItem(1).split(" \\(")[0];
                mAdapter.remove(1);
                mAdapter.add(1, oldDefault);
                sharedPreferences.edit().putString("defaultCity", citiesList.get(newPosition)).commit();
                Toast.makeText(getBaseContext(), String.format("Default city changed: %s", newDefault.split("\\(")[0]), Toast.LENGTH_LONG).show();
            } else if (originalPosition == 0) {
                String oldDefault = mAdapter.getItem(originalPosition) + " (default)";
                mAdapter.remove(originalPosition);
                mAdapter.add(originalPosition, oldDefault);
                String newDefault = mAdapter.getItem(newPosition).split(" \\(")[0];
                mAdapter.remove(newPosition);
                mAdapter.add(newPosition, newDefault);
                sharedPreferences.edit().putString("defaultCity", citiesList.get(newPosition)).commit();
                Toast.makeText(getBaseContext(), String.format("Default city changed: %s", oldDefault.split("\\(")[0]), Toast.LENGTH_LONG).show();
            }
        }
    }
}
