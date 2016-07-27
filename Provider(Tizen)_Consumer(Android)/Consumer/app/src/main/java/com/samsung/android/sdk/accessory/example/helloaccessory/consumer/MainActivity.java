package com.samsung.android.sdk.accessory.example.helloaccessory.consumer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private boolean mIsBound = false;
    public ConsumerService mConsumerService = null;
    Button _shortTraining, _fullTraining, _ownTraining, _freeTraining;
    public static TextView connectionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        connectionView = (TextView) findViewById(R.id.textView2);
        _shortTraining = (Button) findViewById(R.id.shortTraining);
        _shortTraining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, Exercise.class);
                myIntent.putExtra("exercise", "Russische Drehung");
                myIntent.putExtra("training",3);
                MainActivity.this.startActivity(myIntent);
            }
        });

        _fullTraining = (Button) findViewById(R.id.fullTraining);
        _fullTraining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent myIntent = new Intent(MainActivity.this, FreeTraining.class);
                //myIntent.putExtra("key",value);
                //MainActivity.this.startActivity(myIntent);
                _fullTraining.setText("#demnächst Verfügbar");
            }
        });

        _ownTraining = (Button) findViewById(R.id.ownTraining);
        _ownTraining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent myIntent = new Intent(MainActivity.this, FreeTraining.class);
                //myIntent.putExtra("key",value);
                //MainActivity.this.startActivity(myIntent);
                _ownTraining.setText("#demnächst Verfügbar");
            }
        });

        _freeTraining = (Button) findViewById(R.id.freeTraining);
        _freeTraining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, FreeTraining.class);
                //myIntent.putExtra("key",value);
                MainActivity.this.startActivity(myIntent);
            }
        });

        // Bind service
        mIsBound = bindService(new Intent(MainActivity.this, ConsumerService.class), mConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_refresh:
                if (mIsBound == true && mConsumerService != null) {
                    mConsumerService.findPeers();
                }
                Toast.makeText(this, "Waiting for Connection...", Toast.LENGTH_SHORT)
                        .show();
                break;
            default:
                break;
        }

        return true;
    }

    @Override
    protected void onDestroy() {
        // Clean up connections
        if (mIsBound == true && mConsumerService != null) {
            if (mConsumerService.closeConnection() == false) {
                connectionView.setText("Disconnected");
            }
        }
        // Un-bind service
        if (mIsBound) {
            unbindService(mConnection);
            mIsBound = false;
        }
        super.onDestroy();
    }

    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mConsumerService = ((ConsumerService.LocalBinder) service).getService();
            connectionView.setText("onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            mConsumerService = null;
            mIsBound = false;
            connectionView.setText("onServiceDisconnected");
        }
    };
}