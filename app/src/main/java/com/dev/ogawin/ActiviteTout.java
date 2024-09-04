package com.dev.ogawin;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.os.Parcelable;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;


import com.dev.ogawin.databasehelper.MyDatabaseHelper;
import com.dev.ogawin.databinding.ActiviteToutBinding;
import com.dev.ogawin.model.Rappel;
import com.dev.ogawin.swipemenulistview.SwipeMenu;
import com.dev.ogawin.swipemenulistview.SwipeMenuCreator;
import com.dev.ogawin.swipemenulistview.SwipeMenuItem;
import com.dev.ogawin.swipemenulistview.SwipeMenuListView;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class ActiviteTout extends AppCompatActivity {

    private ActiviteToutBinding binding;
    SwipeMenuListView list;
    private final List<Rappel> rappelList = new ArrayList<Rappel>();
    MyListAdapter adapter;
    MyDatabaseHelper db;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActiviteToutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = MyDatabaseHelper.getInstanceDb((this));
        List<Rappel> listRap=  db.getAllRappels();
        this.rappelList.addAll(listRap);

        TextView textView = new TextView(this);
        textView.setText(getResources().getString(R.string.rappels));
        textView.setTextSize(36);
        textView.setTextColor(Color.BLUE);


        adapter=new MyListAdapter(this, rappelList);
        list=(SwipeMenuListView)findViewById(R.id.list);
        list.addHeaderView(textView);




        // Set the SwipeActionAdapter as the Adapter for your ListView
        list.setAdapter(adapter);




        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {

                // create "details" item
                SwipeMenuItem detailsItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                detailsItem.setBackground(new ColorDrawable(Color.rgb(100,
                        100, 100)));
                // set item width
                detailsItem.setWidth(dp2px(90));
                // set a icon
                //detailsItem.setIcon(R.drawable.ic_plus);
                detailsItem.setTitle(getResources().getString(R.string.details));
                detailsItem.setTitleSize(15);
                detailsItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(detailsItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(255,
                        51, 51)));
                // set item width
                deleteItem.setWidth(dp2px(90));
                deleteItem.setTitle(getResources().getString(R.string.supprimer));
                deleteItem.setTitleSize(15);
                deleteItem.setTitleColor(Color.WHITE);
                // set a icon
                //deleteItem.setIcon(R.drawable.ic_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

        // set creator
        list.setMenuCreator(creator);

        list.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {

                    case 0:
                        // details
                        Intent myIntent = new Intent(getApplicationContext(), ActiviteCreerRappel.class);

                        Rappel currentRappel = (Rappel) adapter.getItem(position);

                        myIntent.putExtra("id", currentRappel.getRappelId());
                        myIntent.putExtra("title", currentRappel.getRappelTitle());
                        myIntent.putExtra("notes", currentRappel.getRappelNotes());
                        myIntent.putExtra("date", currentRappel.getRappelDate());
                        myIntent.putExtra("time", currentRappel.getRappelTime());
                        myIntent.putExtra("recurrence", currentRappel.getRappelRecurrence());
                        myIntent.putExtra("date_fin_recurrence", currentRappel.getRappelDateFinRecurrence());
                        myIntent.putExtra("priorite", currentRappel.getRappelPriorite());
                        myIntent.putExtra("ids_sous_taches", (Serializable) currentRappel.getRappelIdsSousTaches());

                        ActiviteTout.this.startActivityForResult(myIntent,0);
                        break;
                    case 1:
                        // delete
                        adapter.remove(position);
                        break;


                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });





        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getBaseContext(), ActiviteCreerRappel.class);
                //myIntent.putExtra("key", value); //Optional parameters
                ActiviteTout.this.startActivityForResult(myIntent,0);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);

            if (resultCode  == RESULT_OK) {

                //New rappel has been added or updated
                //Refresh listview
                adapter.refreshForAll();
            }
        } catch (Exception ex) {

        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        adapter.refreshForAll();

    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }



}