package com.android.notifynote.ui;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.notifynote.adapter.AlarmsAdapter;
import com.android.notifynote.R;
import com.android.notifynote.model.Alarm;
import com.android.notifynote.service.LoadAlarmsReceiver;
import com.android.notifynote.service.LoadAlarmsService;
import com.android.notifynote.util.AlarmUtils;

import java.util.ArrayList;

import static com.android.notifynote.ui.AddNoteActivity.ADD_ALARM;
import static com.android.notifynote.ui.AddNoteActivity.buildAddEditAlarmActivityIntent;

public class MainActivity extends AppCompatActivity implements LoadAlarmsReceiver.OnAlarmsLoadedListener {
    private LoadAlarmsReceiver mReceiver;
    private AlarmsAdapter mAdapter;

    @Override
    public void onAlarmsLoaded(ArrayList<Alarm> alarms) {
        mAdapter.setAlarms(alarms);
    }
    @Override
    public void onStart() {
        super.onStart();
        final IntentFilter filter = new IntentFilter(LoadAlarmsService.ACTION_COMPLETE);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, filter);
        LoadAlarmsService.launchLoadAlarmsService(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
    }
    //  private NotesListAdapter notesListAdapter;
//    private EmptyNoteListObserver noteListObserver;


    public static Intent launchIntent(Context context) {
        final Intent i = new Intent(context, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return i;
    }

    public void deleteNote(int notePos) {

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mReceiver = new LoadAlarmsReceiver(this);
        //  this.notesListAdapter = new NotesListAdapter(this, getSupportFragmentManager());
        mAdapter = new AlarmsAdapter();
        RecyclerView noteListView = (RecyclerView) findViewById(R.id.notes_recycler_view);
        noteListView.setLayoutManager(new LinearLayoutManager(this));
        noteListView.setAdapter(mAdapter);
        //  this.noteListObserver = new EmptyNoteListObserver(noteListView, findViewById(R.id.empty_text_view));

        findViewById(R.id.add_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlarmUtils.checkAlarmPermissions(MainActivity.this);
                final Intent i = buildAddEditAlarmActivityIntent(MainActivity.this, ADD_ALARM, new Alarm());
                startActivity(i);
            }
        });
    }

}
