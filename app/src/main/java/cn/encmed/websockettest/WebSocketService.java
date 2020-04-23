package cn.encmed.websockettest;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

/**
 * Created by fanglin on 2020/4/23.
 */
public class WebSocketService extends Service {
    private WebSocketClient websocketClient;

    class LocalBinder extends Binder {
        WebSocketClient getWebSocketClient() {
            return websocketClient;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new LocalBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        websocketClient = WebSocketClient.getInstance();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (websocketClient != null) {
            websocketClient.close();
            websocketClient = null;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }
}
