package com.dev.ogawin;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.dev.ogawin.databasehelper.MyDatabaseHelper;
import com.dev.ogawin.databinding.ActiviteAproposBinding;
import com.dev.ogawin.model.Rappel;
import com.dev.ogawin.swipemenulistview.SwipeMenu;
import com.dev.ogawin.swipemenulistview.SwipeMenuCreator;
import com.dev.ogawin.swipemenulistview.SwipeMenuItem;
import com.dev.ogawin.swipemenulistview.SwipeMenuListView;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.TypedValue;
import android.view.View;


import androidx.cardview.widget.CardView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.dev.ogawin.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.SearchView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private CardView carview_today;
    private CardView carview_all;
    private TextView tv_nb_task_today;
    private TextView tv_nb_task_all;
    private SearchView simpleSearchView;
    MyDatabaseHelper db;

    SwipeMenuListView list;
    private  List<Rappel> rappelListFiltered = new ArrayList<Rappel>();
    MyListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        db = MyDatabaseHelper.getInstanceDb((this));

        carview_all = findViewById(R.id.cardView_all);
        carview_today = findViewById(R.id.cardView_today);
        tv_nb_task_today = findViewById(R.id.nb_task_today);
        tv_nb_task_all = findViewById(R.id.nb_task_all);

        simpleSearchView = (SearchView) findViewById(R.id.searchview);


        adapter=new MyListAdapter(this, rappelListFiltered);
        list=(SwipeMenuListView)findViewById(R.id.list);
        list.setAdapter(adapter);

        tv_nb_task_all.setText(String.valueOf(db.getRappelsCount()));
        tv_nb_task_today.setText(String.valueOf(db.getRappelsCountToday()));
        //When clicking on All cardview
        carview_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(MainActivity.this, ActiviteTout.class);
                //myIntent.putExtra("key", value); //Optional parameters
                MainActivity.this.startActivity(myIntent);
            }
        });

        //When clicking on Today cardview
        carview_today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(MainActivity.this, ActiviteToday.class);
                MainActivity.this.startActivity(myIntent);
            }
        });

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getBaseContext(), ActiviteCreerRappel.class);
               //myIntent.putExtra("key", value); //Optional parameters
                MainActivity.this.startActivity(myIntent);
            }
        });

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
                detailsItem.setTitle("Détails");
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
                deleteItem.setTitle("Supprimer");
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

                        MainActivity.this.startActivityForResult(myIntent,0);
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



        simpleSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // do something on text submit
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // do something when text changes
                if(!newText.isEmpty())
                {
                    //Something to search
                    //show filtered list of to do tasks
                    if(list.getVisibility() == View.INVISIBLE)
                    {
                        list.setVisibility(View.VISIBLE);
                        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
                        anim.setDuration(500);
                        anim.setRepeatMode(Animation.REVERSE);
                        list.startAnimation(anim);
                    }





                    carview_today.setVisibility(View.INVISIBLE);
                    carview_all.setVisibility(View.INVISIBLE);

                    List<Rappel> listRap=  db.SearchAllRappelsByKeyword(newText);
                    rappelListFiltered.clear();

                    rappelListFiltered.addAll(listRap);
                    rappelListFiltered = MyListAdapter.sortByDate(rappelListFiltered);
                    //refresh list view
                    adapter.notifyDataSetChanged();

                }
                else
                {
                    //nothing to search
                    //show default home page
                    list.setVisibility(View.INVISIBLE);

                    carview_today.setVisibility(View.VISIBLE);
                    carview_all.setVisibility(View.VISIBLE);

                    //We refresh the number of tasks shown in cardviews
                    tv_nb_task_all.setText(String.valueOf(db.getRappelsCount()));
                    tv_nb_task_today.setText(String.valueOf(db.getRappelsCountToday()));
                }
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            Intent myIntent = new Intent(getBaseContext(), ActiviteAPropos.class);
            //myIntent.putExtra("key", value); //Optional parameters
            MainActivity.this.startActivityForResult(myIntent,0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View view) {

    }

    @Override
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();

        tv_nb_task_all.setText(String.valueOf(db.getRappelsCount()));
        tv_nb_task_today.setText(String.valueOf(db.getRappelsCountToday()));
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }
}