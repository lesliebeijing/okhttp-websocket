package cn.encmed.websockettest;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    WebSocketService.LocalBinder webSocketService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindService(new Intent(this, WebSocketService.class), serviceConnection, BIND_AUTO_CREATE);

        Button btnConnect = findViewById(R.id.btn_connect);
        Button btnClose = findViewById(R.id.btn_close);
        Button btnSend = findViewById(R.id.btn_send);
        final EditText etValue = findViewById(R.id.et_value);
        final TextView tvMessage = findViewById(R.id.tv_message);

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webSocketService != null) {
                    webSocketService.getWebSocketClient().connect("ws://121.40.165.18:8800");

                    webSocketService.getWebSocketClient().setWebSocketCallback(new WebSocketClient.WebSocketCallback() {
                        @Override
                        public void onMessage(final String text) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tvMessage.setText(text);
                                }
                            });
                        }

                        @Override
                        public void onOpen() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tvMessage.setText("opend");
                                }
                            });
                        }

                        @Override
                        public void onClose() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tvMessage.setText("closed");
                                }
                            });
                        }

                        @Override
                        public void onFail(final String message) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tvMessage.setText(message);
                                }
                            });
                        }
                    });
                }
            }
        });
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webSocketService != null) {
                    webSocketService.getWebSocketClient().close();
                }
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webSocketService != null) {
                    webSocketService.getWebSocketClient().send(etValue.getText().toString().trim());
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            webSocketService = ((WebSocketService.LocalBinder) service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            webSocketService = null;
        }
    };
}
