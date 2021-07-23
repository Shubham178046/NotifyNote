package com.android.notifynote.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseBooleanArray;

public final class Alarm implements Parcelable{

    private Alarm(Parcel in) {
        id = in.readLong();
        time = in.readLong();
        label = in.readString();
        description = in.readString();
        date = in.readString();
        timeString = in.readString();
        isEnabled = in.readByte() != 0;
    }

    public static final Creator<Alarm> CREATOR = new Creator<Alarm>() {
        @Override
        public Alarm createFromParcel(Parcel in) {
            return new Alarm(in);
        }

        @Override
        public Alarm[] newArray(int size) {
            return new Alarm[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeLong(time);
        parcel.writeString(label);
        parcel.writeString(description);
        parcel.writeString(date);
        parcel.writeString(timeString);
        parcel.writeByte((byte) (isEnabled ? 1 : 0));
    }

    private static final long NO_ID = -1;

    private final long id;
    private long time;
    private String label;
    private String description;
    private String date;
    private String timeString;
    private boolean isEnabled;

    public Alarm() {
        this(NO_ID);
    }

    public Alarm(long id) {
        this(id, System.currentTimeMillis());
    }

    public Alarm(long id, long time) {
        this(id, time, null,null,null,null);
    }

    public Alarm(long id, long time, String label, String description, String date, String timeString) {
        this.id = id;
        this.time = time;
        this.label = label;
        this.description = description;
        this.date = date;
        this.timeString = timeString;
    }

    public long getId() {
        return id;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public void setLabel(String label) {
        this.label = label;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public void setTimeString(String timeString) {
        this.timeString = timeString;
    }
    public String getLabel() {
        return label;
    }
    public String getDescription() {
        return description;
    }
    public String getDate() {
        return date;
    }
    public String getTimeString() {
        return timeString;
    }
    public void setIsEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public int notificationId() {
        final long id = getId();
        return (int) (id^(id>>>32));
    }

    @Override
    public String toString() {
        return "Alarm{" +
                "id=" + id +
                ", time=" + time +
                ", label='" + label + '\'' +
                ", description='" + description + '\'' +
                ", date='" + date + '\'' +
                ", timeString='" + timeString + '\'' +
                ", isEnabled=" + isEnabled +
                '}';
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (int) (id^(id>>>32));
        result = 31 * result + (int) (time^(time>>>32));
        result = 31 * result + label.hashCode();
        result = 31 * result + description.hashCode();
        result = 31 * result + date.hashCode();
        result = 31 * result + timeString.hashCode();
        return result;
    }

}
