package com.example.oriolgasset.weatherforecast;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
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
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.undo.SimpleSwipeUndoAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.undo.UndoAdapter;

import java.util.HashSet;
import java.util.Set;

public class EditPlacesActivity extends AppCompatActivity {

    private DynamicListView listView;
    private SharedPreferences sharedPreferences;
    private Set<String> cities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_edit_places);

        listView = (DynamicListView) findViewById (R.id.edit_places_view);
        sharedPreferences = getSharedPreferences ("weatherForecastPreferences", MODE_PRIVATE);
        String defaultCity = sharedPreferences.getString ("defaultCity", "");
        cities = sharedPreferences.getStringSet ("citiesList", new HashSet<String> ());
        if (!cities.isEmpty ()) {
            /* Setup the adapter */
            ArrayAdapter<String> adapter = new MyListAdapter (this);
            adapter.add (defaultCity);
            for (String city : cities) {
                if(!city.equals (defaultCity)){
                    adapter.add (city);
                }
            }
            SimpleSwipeUndoAdapter simpleSwipeUndoAdapter = new SimpleSwipeUndoAdapter (adapter, this, new MyOnDismissCallback (adapter));
            AlphaInAnimationAdapter animationAdapter = new AlphaInAnimationAdapter (simpleSwipeUndoAdapter);
            animationAdapter.setAbsListView (listView);
            listView.setAdapter (animationAdapter);

        /* Enable drag and drop functionality */
            listView.enableDragAndDrop ();
            listView.setDraggableManager (new TouchViewDraggableManager (R.id.list_row_draganddrop_textview));
            listView.setOnItemMovedListener (new MyOnItemMovedListener (adapter));
            listView.setOnItemLongClickListener (
                    new AdapterView.OnItemLongClickListener () {
                        @Override
                        public boolean onItemLongClick(final AdapterView<?> parent, final View view,
                                                       final int position, final long id) {
                            listView.startDragging (position);
                            return true;
                        }
                    }
            );

        /* Enable swipe to dismiss */
            listView.enableSimpleSwipeUndo ();
        }
    }

    @Override
    public void onBackPressed() {
        setResult (2);
        finish ();
    }

    private static class MyListAdapter extends ArrayAdapter<String> implements UndoAdapter {

        private final Context mContext;

        MyListAdapter(final Context context) {
            mContext = context;
        }

        @Override
        public long getItemId(final int position) {
            return getItem (position).hashCode ();
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = LayoutInflater.from (mContext).inflate (R.layout.list_row_dynamiclistview, parent, false);
            }

            ((TextView) view.findViewById (R.id.list_row_draganddrop_textview)).setText (getItem (position));

            return view;
        }

        @NonNull
        @Override
        public View getUndoView(final int position, final View convertView, @NonNull final ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = LayoutInflater.from (mContext).inflate (R.layout.undo_row, parent, false);
            }
            return view;
        }

        @NonNull
        @Override
        public View getUndoClickView(@NonNull final View view) {
            return view.findViewById (R.id.undo_row_undobutton);
        }

    }

    private class MyOnDismissCallback implements OnDismissCallback {

        private final ArrayAdapter<String> mAdapter;

        MyOnDismissCallback(final ArrayAdapter<String> adapter) {
            mAdapter = adapter;
        }

        @Override
        public void onDismiss(@NonNull final ViewGroup listView, @NonNull final int[] reverseSortedPositions) {
            for (int position : reverseSortedPositions) {
                String s = mAdapter.getItem (position);
                mAdapter.remove (s);
                cities.remove (s);
                SharedPreferences.Editor editor = sharedPreferences.edit ();
                if(s.equals (sharedPreferences.getString ("defaultCity",""))){
                    editor.putString ("defaultCity",mAdapter.getItem (0));
                }
                editor.putStringSet ("citiesList", cities);
                editor.commit ();
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
            if (newPosition == 0) {
                sharedPreferences.edit ().putString ("defaultCity", mAdapter.getItem (newPosition)).commit ();
                Toast.makeText (getBaseContext (), String.format ("Default city changed: %s", mAdapter.getItem (newPosition)), Toast.LENGTH_LONG).show ();
            }
        }
    }
}
