package cn.encmed.websockettest;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by fanglin on 2020/4/26.
 */
public class SocketIOService extends Service {
    private static final String TAG = "socketio";
    private static final String uri = "http://192.168.0.102:9090";

    private Socket socket;
    private WebSocketCallback webSocketCallback;
    private int reconnectTimeout = 5000;

    private Handler handler = new Handler();

    class LocalBinder extends Binder {
        SocketIOService getService() {
            return SocketIOService.this;
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
        try {
            socket = IO.socket(uri);
            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d(TAG, "EVENT_CONNECT");
                    if (webSocketCallback != null) {
                        webSocketCallback.onOpen();
                    }

                    send("joinRoom", "scan_code_push");

                }
            }).on("scan_code_push", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d(TAG, "event");
                    for (int i = 0; i < args.length; i++) {
                        Log.d(TAG, args[i].toString());
                    }
                    if (webSocketCallback != null && args.length > 0) {
                        webSocketCallback.onMessage(args[0].toString());
                    }
                }
            }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d(TAG, "EVENT_DISCONNECT");
                    if (webSocketCallback != null) {
                        webSocketCallback.onClosed();
                    }
                    reconnect();
                }
            }).on(Socket.EVENT_ERROR, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d(TAG, "EVENT_ERROR");
                    for (int i = 0; i < args.length; i++) {
                        Log.d(TAG, args[i].toString());
                    }
//                    reconnect();
                }
            }).on(Socket.EVENT_MESSAGE, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d(TAG, "EVENT_MESSAGE");
                }
            }).on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d(TAG, "EVENT_CONNECT_ERROR");
                }
            }).on(Socket.EVENT_CONNECT_TIMEOUT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d(TAG, "EVENT_CONNECT_TIMEOUT");
                }
            }).on(Socket.EVENT_CONNECTING, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d(TAG, "EVENT_CONNECTING");
                }
            }).on(Socket.EVENT_RECONNECTING, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d(TAG, "EVENT_RECONNECTING");
                }
            }).on(Socket.EVENT_PING, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d(TAG, "EVENT_PING");
                }
            }).on(Socket.EVENT_PONG, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d(TAG, "EVENT_PONG");
                }
            });

            connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    private void connect() {
        if (socket != null) {
            Log.d(TAG, "connect");
            socket.connect();
        }
    }

    private void reconnect() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (socket != null && !socket.connected()) {
                    Log.d(TAG, "reconnect...");
                    connect();
                    handler.postDelayed(this, reconnectTimeout);
                }
            }
        }, reconnectTimeout);
    }

    public void send(String event, String text) {
        if (socket != null) {
            Log.d(TAG, "send " + text);
            socket.emit(event, text);
        }
    }

    public void close() {
        Log.d(TAG, "close");
        if (socket != null) {
            socket.disconnect();
            socket = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        close();
    }

    public interface WebSocketCallback {
        void onMessage(String text);

        void onOpen();

        void onClosed();
    }

    public void setWebSocketCallback(WebSocketCallback webSocketCallback) {
        this.webSocketCallback = webSocketCallback;
    }
}
