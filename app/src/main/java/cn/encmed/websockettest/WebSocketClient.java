package cn.encmed.websockettest;

import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

/**
 * Created by fanglin on 2020/4/23.
 */
public class WebSocketClient extends WebSocketListener {
    private static final String TAG = WebSocketClient.class.getSimpleName();
    private static WebSocketClient instance = null;

    private WebSocket webSocket;

    private WebSocketCallback webSocketCallback;

    private WebSocketClient() {

    }

    public static WebSocketClient getInstance() {
        if (instance == null) {
            synchronized (WebSocketClient.class) {
                if (instance == null) {
                    instance = new WebSocketClient();
                }
            }
        }
        return instance;
    }

    public void connect(String url) {
        Log.d(TAG, "connect " + url);
        OkHttpClient client = new OkHttpClient.Builder().build();
        Request request = new Request.Builder().url(url).build();
        webSocket = client.newWebSocket(request, this);
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        Log.d(TAG, "onOpen");
        if (webSocketCallback != null) {
            webSocketCallback.onOpen();
        }
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        Log.d(TAG, "onMessage " + text);
        if (webSocketCallback != null) {
            webSocketCallback.onMessage(text);
        }
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        Log.d(TAG, "onClosed");
        if (webSocketCallback != null) {
            webSocketCallback.onClose();
        }
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        Log.d(TAG, "onFailure " + t.getMessage());
        if (webSocketCallback != null) {
            webSocketCallback.onFail(t.getMessage() != null ? t.getMessage() : "onFailure");
        }
    }

    public void close() {
        if (webSocket != null) {
            webSocket.close(1000, "manual close");
        }
    }

    public void send(String text) {
        if (webSocket != null) {
            webSocket.send(text);
        }
    }

    public interface WebSocketCallback {
        void onMessage(String text);

        void onOpen();

        void onClose();

        void onFail(String message);
    }

    public void setWebSocketCallback(WebSocketCallback webSocketCallback) {
        this.webSocketCallback = webSocketCallback;
    }
}
