package com.applefish.smartshop.activities;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.applefish.smartshop.R;
import com.applefish.smartshop.classes.ConnectChecked;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class SplashScreenActivity extends AppCompatActivity {

    private int count = 0;
    private TextView connectState;
    Thread connectThread;

    private Handler handler = new Handler( Looper.getMainLooper() );
    private Timer splashTimer = new Timer();
    private TimerTask splashTimerTask;
    private int timerCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView( R.layout.activity_splash_screen );

        connectState = (TextView)findViewById( R.id.connectState );

        connectThread = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(500);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if(count == 0)
                                {connectState.setText("Connect .");}
                                else if(count == 1)
                                {connectState.setText("Connect ..");}
                                else if(count == 2)
                                {connectState.setText("Connect ...");}
                                else if(count == 3)
                                {connectState.setText("Connect ....");}
                                else if(count == 4)
                                {connectState.setText("Connect .....");}
                                else if(count == 5)
                                    count=-1;
                                count++;

                            }
                        });
                    }
                } catch (InterruptedException e) {
                    Log.d("yes","connectThread noooooooooooooooooo" +e);
                }
            }
        };




        splashTimerTask = new TimerTask() {
            @Override
            public void run() {

                handler.postDelayed( new Runnable() {

                    public void run() {

                        try {

                            if ( !ConnectChecked.isNetworkAvailable(getBaseContext()) && !ConnectChecked.isOnline() ) {

                                if(timerCount<3)
                                {
                                    timerCount++;
                                }
                                else
                                {

                                    connectThread.interrupt();

                                    splashTimer.cancel();

                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            connectState.setText("Sorry ,can't Connect");

                                        }
                                    },200);

                                }
                            }

                            else {

                                connectThread.interrupt();
                                connectThread.join();
                                splashTimer.cancel();
                                Intent intent = new Intent();
                                intent.setClass( getBaseContext(), MainActivity.class );
                                startActivity( intent );

                            }

                        } catch (Exception e) {
                            Log.d("yes","splashTimerTask noooooooooooooooooo");
                        }
                    }
                }, 3000);
            }
        };

        splashTimer.schedule(splashTimerTask, 0, 3500);
        connectThread.start();
    }




}
