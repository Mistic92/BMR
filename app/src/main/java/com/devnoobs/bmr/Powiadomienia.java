/*
 * Copyright (c) 2014 Łukasz Byjoś
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.devnoobs.bmr;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class Powiadomienia extends BroadcastReceiver {


    private Context c;
    private final String S = "com.devnoobs.bmr.Powiadomienia";

    Powiadomienia() {
    }

    public Powiadomienia(Context c) {
        this.c = c;
    }


    public void setAlarm(int h, int m) {
        Calendar calendar = przygotujCzas(h, m);

        AlarmManager am = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);

        Intent i = new Intent(S);
        PendingIntent pi = PendingIntent.getBroadcast(c, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
        IntentFilter intentFilter = new IntentFilter(S);
        Powiadomienia p = new Powiadomienia();
        c.registerReceiver(p, intentFilter);

        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000 * 60 * 10,
                pi); // Millisec * Sec * Min
        //am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
        // AlarmManager.INTERVAL_DAY, pi); //
        showToast("Powiadomienie wlaczone");
    }

    private Calendar przygotujCzas(int h, int m) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
        int hourNow = calendar.get(Calendar.HOUR_OF_DAY);
        int minNow = calendar.get(Calendar.MINUTE);
        if (hourNow > h || (hourNow == h && minNow > m)) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            //calendar.add(Calendar.MINUTE, minNow+5);
        }
        calendar.set(Calendar.HOUR_OF_DAY, h);
        calendar.set(Calendar.MINUTE, m);

        return calendar;
    }
    // 1379005926443

    public void cancelAlarm() {
        Intent i = new Intent(S);
        PendingIntent sender = PendingIntent.getBroadcast(c, 0, i, 0);
        AlarmManager alarmManager = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
        showToast("Powiadomienie wylaczone");
    }

    @Override
    public void onReceive(Context c, Intent i) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(c)
                        .setSmallIcon(R.drawable.ic_note)
                        .setContentTitle("Powiadomienie ")
                        .setContentText("Testowe powiadomienie o danej godzinie");
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(alarmSound);
        NotificationManager powiadomienie =
                (NotificationManager) c.getSystemService(c.NOTIFICATION_SERVICE);
        powiadomienie.notify(1, mBuilder.build());

    }//onReceive

    private void showToast(String tekst) {
        CharSequence text = tekst;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(c, text, duration);
        toast.show();
    }


}//class
