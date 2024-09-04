package com.dev.ogawin;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import com.dev.ogawin.databasehelper.MyDatabaseHelper;
import com.dev.ogawin.databinding.ActiviteCreerRappelBinding;
import com.dev.ogawin.model.Rappel;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.Serializable;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;




public class ActiviteCreerRappel extends AppCompatActivity {
    private ActiviteCreerRappelBinding binding;

    Button bAnnulerCreation, bOk;
    EditText etTitre,etNotes;
    CalendarView cvDate;
    long selectedDate;
    TimePicker tpTime;
    Spinner spPriorite,spRecurrence;
    TextView tvDateSelected,tvTimeSelected;

    MyDatabaseHelper db ;
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActiviteCreerRappelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = new MyDatabaseHelper(getBaseContext());

        bAnnulerCreation= findViewById(R.id.cancelButton);
        bOk= findViewById(R.id.proceedButton);
        etTitre = findViewById(R.id.titre);
        etNotes = findViewById(R.id.notes);
        cvDate = findViewById(R.id.date);
        tpTime = findViewById(R.id.time);
        spPriorite= findViewById(R.id.priorite);
        spRecurrence = findViewById(R.id.recurrence);

        tpTime.setIs24HourView(true);
        tvDateSelected = findViewById(R.id.textview_selecteddate);
        tvTimeSelected = findViewById(R.id.textview_selectedtime);

        //On rend par defaut non clickable le choix de la recurrence du rappel
        spRecurrence.setEnabled(false);

        //If a title is specified, 'OK' button becomes clickable
        //if no title is psecified, 'OK' button become non clickable
        etTitre.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

                if (!"".equals(etTitre.getText())) {
                    bOk.setClickable(true);
                }
                else
                {
                    bOk.setClickable(false);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        cvDate.setOnDateChangeListener(new CalendarView.OnDateChangeListener(){
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                calendar.clear(Calendar.HOUR); //reset hour to zero
                calendar.clear(Calendar.MINUTE);
                calendar.clear(Calendar.SECOND);
                calendar.clear(Calendar.MILLISECOND);

                selectedDate = calendar.getTimeInMillis();

                SimpleDateFormat format = new SimpleDateFormat("E dd MMM yyyy");
                tvDateSelected.setText(format.format(calendar.getTime()).toString());

                //On rend clickable le choix de la recurrence du rappel
                spRecurrence.setEnabled(true);



            }
        });

        tpTime.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {


                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                calendar.clear(Calendar.SECOND); //reset seconds to zero
                Format formatter=new SimpleDateFormat("HH:mm");
                tvTimeSelected.setText(formatter.format(calendar.getTime()));

                //L'heure est spécifiée, on s assure que le spinner de la recurrence contient
                //l'option 'Horaire'

                //on tente avant de modifier l'adapter , de recupérer la valeur sélectionnée(si c'est le cas)
                //on pourra ainsi la réappliquer au nouvel adapter

                String old_selection = spRecurrence.getSelectedItem().toString();
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.recurrences_taches, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spRecurrence.setAdapter(adapter);

                int spinnerPosition = adapter.getPosition(old_selection);
                spRecurrence.setSelection(spinnerPosition);

                //L activation du choix de l'heure doit activer egalement la date et la mettre à aujourd'hui
                //si aucune autre date n'a été spécifiée
                if("Not specified".equals(tvDateSelected.getText()))
                {

                    Calendar calendar_date = Calendar.getInstance();
                    calendar_date.clear(Calendar.HOUR); //reset hour to zero
                    calendar_date.clear(Calendar.MINUTE);
                    calendar_date.clear(Calendar.SECOND);
                    calendar_date.clear(Calendar.MILLISECOND);

                    cvDate.setDate(calendar_date.getTime().getTime());
                    selectedDate = calendar_date.getTimeInMillis();

                    SimpleDateFormat format = new SimpleDateFormat("E dd MMM yyyy");
                    tvDateSelected.setText(format.format(calendar_date.getTime()).toString());

                    //On rend clickable le choix de la recurrence du rappel
                    spRecurrence.setEnabled(true);
                }


            }
        });

        //On pre-remplie les valeurs si rappel deja existant en bdd
        Intent intent = getIntent();
        if(intent != null)
        {
            String titre = intent.getStringExtra("title");
            etTitre.setText(titre);

            String notes = intent.getStringExtra("notes");
            if(notes != null)
            {
                etNotes.setText(notes);
            }

            LocalDate date =(LocalDate) intent.getSerializableExtra("date");
            if(date != null)
            {
                cvDate.setDate(date.toDate().getTime());
                selectedDate = date.toDate().getTime();

                SimpleDateFormat format = new SimpleDateFormat("E dd MMM yyyy");

                tvDateSelected.setText(format.format(date.toDate()).toString());

                //On rend clickable le choix de la recurrence du rappel
                spRecurrence.setEnabled(true);
            }

            LocalTime time =(LocalTime) intent.getSerializableExtra("time");
            if(time != null)
            {
                tpTime.setHour(time.getHourOfDay());
                tpTime.setMinute(time.getMinuteOfHour());

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, time.getHourOfDay());
                calendar.set(Calendar.MINUTE, time.getMinuteOfHour());
                calendar.clear(Calendar.SECOND); //reset seconds to zero
                Format formatter=new SimpleDateFormat("HH:mm");
                tvTimeSelected.setText(formatter.format(calendar.getTime()));


            }

            String recurrence = intent.getStringExtra("recurrence");
            if(recurrence != null)
            {
                if(!"Not specified".equals(tvTimeSelected.getText()))
                {
                    //L'heure est spécifiée, on s assure que le spinner de la recurrence contient
                    //l'option 'Horaire'

                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.recurrences_taches, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    int spinnerPosition = adapter.getPosition(recurrence);
                    spRecurrence.setSelection(spinnerPosition);
                }
                else
                {
                    //L'heure n'est pas spécifiée donc l'option 'horaire' du spinner de la
                    //recurrence n'est pas definie
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.recurrences_taches_sans_horaire, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    int spinnerPosition = adapter.getPosition(recurrence);
                    spRecurrence.setSelection(spinnerPosition);
                }

            }

            LocalDate date_fin_recurrence =(LocalDate) intent.getSerializableExtra("date_fin_recurrence");
            if(date_fin_recurrence != null)
            {
                //Plus tard implementer
            }

            String priorite = intent.getStringExtra("priorite");
            if(priorite != null)
            {
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.priorites_taches, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                int spinnerPosition = adapter.getPosition(priorite);
                spPriorite.setSelection(spinnerPosition);
            }

            List<String> rappelIdsSousTaches = (ArrayList<String>) intent.getParcelableExtra("ids_sous_taches");
            if(rappelIdsSousTaches != null)
            {
                //Implementer plus tard
            }
        }



        // handle the PROCEED button
        bOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //At least a title should be specified before creation of a 'rappel'
                String titre = etTitre.getText().toString();
                if (!"".equals(titre)) {

                    //Store 'rappel in database'
                    String notes = etNotes.getText().toString();
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    LocalDate rappelDate =LocalDate.parse(format.format(new Date(selectedDate)));
                    if("Not specified".equals(tvDateSelected.getText()))
                    {
                        //Time not explicitly specified  by user
                        rappelDate = null;
                    }

                    String hour = String.valueOf(tpTime.getHour());
                    String minute = String.valueOf(tpTime.getMinute());
                    DateTimeFormatter fmt = DateTimeFormat.forPattern("HH:mm");
                    LocalTime rappelTime =fmt.parseLocalTime(hour+ ":" + minute);
                    if("Not specified".equals(tvTimeSelected.getText()))
                    {
                        //Time not explicitly specified  by user
                        rappelTime = null;
                    }

                    String recurrence = spRecurrence.getSelectedItem().toString();
                    LocalDate dateFinRecurrence =null;
                    String priorite = spPriorite.getSelectedItem().toString();
                    List<String> rappelIdsSousTaches = new ArrayList<>();
                    Rappel newRappel = new Rappel(titre,notes,rappelDate,rappelTime,recurrence,dateFinRecurrence,priorite,rappelIdsSousTaches);

                    if("".equals(intent.getStringExtra("title")) || intent.getStringExtra("title") == null)
                    {
                        //nouveau rappel
                        newRappel.setRappelId((int) db.getMaxRappelId() +1);

                        db.addRappel(newRappel);

                        //Creation de l'alarme pour la notif.
                        ActiviteCreerRappel.creerUpdateAlarmePourNotification(getApplicationContext(),titre,newRappel,rappelDate,rappelTime,recurrence);



                    }
                    else
                    {
                        //Mise à jour d'un rappel existant
                        int  idRappel = intent.getIntExtra("id",0);
                        newRappel.setRappelId(idRappel);

                        db.updateRappel(newRappel);
                        //Mise à jour de l'alarme pour la notif(annuler la précédente).
                        ActiviteCreerRappel.creerUpdateAlarmePourNotification(getApplicationContext(),titre,newRappel,rappelDate,rappelTime,recurrence);


                        //implementer le changement de couleur du sosu titre dans mylistadapter quand lheure arrive et que l'utilisateur
                        //n'a pas rafraichit la page
                    }



                    //We terminate the activity for 'rappel' creation
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });

        bAnnulerCreation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });


    }

    public static void cancelAlarmeNotification(Context context,  Rappel rappel)
    {
        AlarmManager alarmMgr;
        PendingIntent alarmIntent;

        Intent intent = new Intent(context, BroadcastReceiverForNotifications.class);
        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmIntent = PendingIntent.getBroadcast(context, rappel.getRappelId(), intent, 0);
        alarmMgr.cancel(alarmIntent);

        //Mise à jour d'un rappel existant
        rappel.setRappelPlannedAlarmTimeInMillis(0L);
        MyDatabaseHelper  db = new MyDatabaseHelper(context.getApplicationContext());
        db.updateRappel(rappel);
    }
    public static  Long creerUpdateAlarmePourNotification(Context context,String titre,Rappel rappel,LocalDate date,LocalTime time,String recurrence)
    {
        Long millis=0L;
        AlarmManager alarmMgr;
        PendingIntent alarmIntent;


        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, BroadcastReceiverForNotifications.class);
        intent.putExtra("title",titre);

        int id = rappel.getRappelId();
        intent.putExtra("id",id);


        // Set the alarm to specified date
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        calendar.clear(Calendar.MILLISECOND);

        if(date != null)
        {
            calendar.set(Calendar.YEAR, date.getYear());
            calendar.set(Calendar.MONTH, date.getMonthOfYear()-1);
            calendar.set(Calendar.DAY_OF_MONTH, date.getDayOfMonth());

            calendar.clear(Calendar.HOUR_OF_DAY); //reset hour to zero
            calendar.clear(Calendar.MINUTE);
            calendar.clear(Calendar.SECOND);

        }
        if(time != null)
        {
            calendar.set(Calendar.HOUR_OF_DAY, time.getHourOfDay());
            calendar.set(Calendar.MINUTE, time.getMinuteOfHour());
            calendar.set(Calendar.SECOND, time.getSecondOfMinute());
        }

        millis = calendar.getTimeInMillis();

        //Mise à jour d'un rappel existant
        rappel.setRappelPlannedAlarmTimeInMillis(millis);
        MyDatabaseHelper  db = new MyDatabaseHelper(context.getApplicationContext());
        db.updateRappel(rappel);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");


        if(recurrence != null && !"Jamais".equals(recurrence))
        {
            //Récurrence du rappel
            //On définit une alarme
            intent.putExtra("calendar",calendar);
            intent.putExtra("recurrence",recurrence);

        }

        //create or update alarm by retrieving the corresponding pending intent through requestcode: rappel.id
        alarmIntent = PendingIntent.getBroadcast(context, rappel.getRappelId(), intent, 0);

        alarmMgr.setExact(AlarmManager.RTC_WAKEUP, millis,alarmIntent);

        return millis;
    }

}
