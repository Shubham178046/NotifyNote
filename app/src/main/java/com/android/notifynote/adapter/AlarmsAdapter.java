package com.android.notifynote.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.notifynote.R;
import com.android.notifynote.model.Alarm;
import com.android.notifynote.ui.AddNoteActivity;
import com.android.notifynote.util.AlarmUtils;

import java.util.List;

import static com.android.notifynote.ui.AddNoteActivity.buildAddEditAlarmActivityIntent;

public final class AlarmsAdapter extends RecyclerView.Adapter<AlarmsAdapter.ViewHolder> {

    private List<Alarm> mAlarms;
    private String[] mDays;
    private int mAccentColor = -1;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final Context c = parent.getContext();
        final View v = LayoutInflater.from(c).inflate(R.layout.note, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final Context c = holder.itemView.getContext();

        final Alarm alarm = mAlarms.get(position);

        holder.time.setText(AlarmUtils.getReadableTime(alarm.getTime()));
        holder.label.setText(alarm.getLabel());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Context c = view.getContext();
                final Intent launchEditAlarmIntent =
                        buildAddEditAlarmActivityIntent(
                                c, AddNoteActivity.EDIT_ALARM, alarm
                        );
                launchEditAlarmIntent.putExtra(AddNoteActivity.ALARM_EXTRA, alarm);
                c.startActivity(launchEditAlarmIntent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return (mAlarms == null) ? 0 : mAlarms.size();
    }


    public void setAlarms(List<Alarm> alarms) {
        mAlarms = alarms;
        notifyDataSetChanged();
    }

    static final class ViewHolder extends RecyclerView.ViewHolder {

        final TextView time, label;

        ViewHolder(View itemView) {
            super(itemView);

            time = itemView.findViewById(R.id.note_text);
            label = itemView.findViewById(R.id.note_title);
        }
    }
}
