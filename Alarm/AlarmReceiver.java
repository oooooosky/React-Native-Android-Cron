import static android.content.Context.ALARM_SERVICE;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sh = context.getSharedPreferences("CJ", Context.MODE_PRIVATE);
        int hour = sh.getInt("cj_hour", -1);
        int minute = sh.getInt("cj_minute", -1);
        if (hour != -1) {
            try {
                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                String currentDate = df.format(c);
                currentDate+=" ";
                if(hour<10){
                    currentDate+="0";
                }
                currentDate+=hour;
                currentDate+=":";
                if(minute<10){
                    currentDate+="0";
                }
                currentDate+=minute;
                currentDate+=":00";
                DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                Date date = (Date)formatter.parse(currentDate);
                long serviceStartTime=date.getTime();
                if(serviceStartTime < System.currentTimeMillis()){
                    serviceStartTime+= 1000*60*60*24;
                }
                Intent i=new Intent(context, AlarmService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(i);
                } else {
                    context.startService(i);
                }
                Intent newIntent = new Intent(context, AlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        context.getApplicationContext(), 0, newIntent, PendingIntent.FLAG_IMMUTABLE);
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, serviceStartTime, pendingIntent);
            } catch (Exception e) {
                System.out.println("AlarmReceiver e = " + e);
            }
        }
    }
}
