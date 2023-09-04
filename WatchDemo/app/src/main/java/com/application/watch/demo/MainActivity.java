package com.application.watch.demo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.nio.charset.StandardCharsets;
import java.util.Random;

import test.invoke.sdk.XiaomiWatchHelper;

/**
 * @author user
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView msg = findViewById(R.id.msg);
        XiaomiWatchHelper instance = XiaomiWatchHelper.getInstance(this);
        instance.setReceiver((id, message) -> {
            runOnUiThread(() -> msg.setText(new String(message, StandardCharsets.UTF_8)));
        });
        instance.setInitMessageListener((device) -> instance.sendMessageToWear("connected", obj -> {
            if (obj.isSuccess()) {
                Log.e(TAG, "Init message send");
            }
        }));

        instance.registerMessageReceiver();
        instance.sendUpdateMessageToWear();
        instance.sendNotify("Title", "This is test message",
                obj -> Log.e(TAG, "send notify ->" + obj.isSuccess()));

        msg.setOnClickListener(v -> instance.sendMessageToWear(String.valueOf(new Random().nextInt()), obj ->
                Log.e(TAG, "send -> " + obj.isSuccess())));
        msg.setOnLongClickListener(new View.OnLongClickListener() {
            private boolean disconnect = false;

            @Override
            public boolean onLongClick(View v) {
                if (!disconnect) {
                    instance.unRegisterWatchHelper();
                    instance.setReCheckConnectDevice();
                    Toast.makeText(MainActivity.this, "Disconnect", Toast.LENGTH_LONG).show();
                    disconnect = true;
                }
                return true;
            }
        });
    }
}