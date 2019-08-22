package com.example.user.wifiservice;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.user.wifiservice.App.CHANNEL_ID;
import static java.lang.Thread.MAX_PRIORITY;
import static java.lang.Thread.sleep;

public class AppService extends Service {

    private Timer timer = new Timer();
    private Context context;
    private NotificationCompat.Builder builder;
    private WifiManager wifi;
    private ConnectivityManager connManager;
    private NetworkInfo mwifi;

    private int tryConnect = 0;
    private List<WifiConfiguration> list;

    @Override
    public void onCreate() {
        super.onCreate();

        context = this;
        wifi = (WifiManager) context.getSystemService(WIFI_SERVICE);
        connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        timer.schedule(new Task(), 0, 5000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .setPriority(MAX_PRIORITY);

        startForeground(1, builder.build());

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class Task extends TimerTask {
        public void run() {
            // handler.sendEmptyMessage(0);

            if (builder == null) return;

            mwifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);



            if (mwifi.isConnected()) {

                builder
                        .setContentTitle("Подключено")
                        .setColor(0xFF7cf77f)
                        .setSubText(wifi.getConnectionInfo().getSSID().replace("\"", ""))
                        .setSmallIcon(R.drawable.ic_wifi_on);

                startForeground(1, builder.build());
                return;
            }



            if (!wifi.isWifiEnabled()) {

                builder
                        .setContentTitle(null)
                        .setColor(0xFF6693a2)
                        .setSubText("Выключен")
                        .setSmallIcon(R.drawable.ic_wifi_off);

                startForeground(1, builder.build());
                // wifi.setWifiEnabled(true);
                return;
            }



            if (wifi.isWifiEnabled()) {

                builder
                        .setContentTitle("Поиск...")
                        .setColor(0xFF5fc3ed)
                        .setSubText(null)
                        .setSmallIcon(R.drawable.ic_wifi_search);
                startForeground(1, builder.build());



                if (tryConnect == 0 || list.size() <= tryConnect) {
                    tryConnect = 0;
                    list = wifi.getConfiguredNetworks();
                }

                wifi.enableNetwork(list.get(tryConnect).networkId, true);
                builder.setSubText(list.get(tryConnect).SSID.replace("\"", ""));
                startForeground(1, builder.build());

                tryConnect++;
            }
        }
    }

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // Toast.makeText(getApplicationContext(), "Служба работает", Toast.LENGTH_SHORT).show();
        }
    };
}
