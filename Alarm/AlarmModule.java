import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class AlarmModule extends ReactContextBaseJavaModule {

    private static final String CHANNEL_ID = "CRON";
    private static ReactApplicationContext reactContext;

    public AlarmModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "AlarmModule";
    }

    @ReactMethod
    public void startCron(int hour, int minute) {
        try {

            // 어플이 실행되자마자 특정 함수를 타게끔
            Intent i=new Intent(reactContext, AlarmService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                reactContext.startForegroundService(i);
            } else {
                reactContext.startService(i);
            }

            SharedPreferences sharedPreferences = reactContext.getSharedPreferences("CJ",Context.MODE_PRIVATE);
            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            myEdit.putInt("cj_hour", hour);
            myEdit.putInt("cj_minute", minute);
            myEdit.commit();

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

            AlarmManager alarmManager = (AlarmManager) this.reactContext.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(this.reactContext, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this.reactContext, 0, intent, PendingIntent.FLAG_MUTABLE);
            // 1000 * 60 * 60 * 24 : 24시간마다 반복
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, serviceStartTime, 1000 * 60 * 60 * 24, pendingIntent);
        } catch (Exception e) {
            System.out.println("e = " + e);
        }
    }

    @ReactMethod
    public void completeTask() {
        this.reactContext.stopService(new Intent(this.reactContext, AlarmService.class));
    }

}
