package com.kudakoding.aaizat.kraider;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.app.ActivityManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.erz.joysticklibrary.JoyStick;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements JoyStick.JoyStickListener{


    private Socket socket = new Socket();
    private Thread connectionThread;
    public String ipvalue;
    public String modestring;
    public String joystring;
    public int portvalue = 5050;
    private String TAG = "MainActivity";
    private long TIMEOUT = 10l;

    private LoopActivity loopactivity;

    private Button btnStart;
    private TextView valspeed;
    private TextView valsteer;
    private TextView valmode;
    private TextView valdis;
    private TextView valbattery;

    public double joyyvalue;
    public double joyxvalue;
    public int joyydirection;
    public int joyxdirection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final JoyStick joy1 = (JoyStick) findViewById(R.id.joy1);
        final JoyStick joy2 = (JoyStick) findViewById(R.id.joy2);

        joy1.setListener(this);
        joy1.enableStayPut(false);
        joy1.setType(JoyStick.TYPE_2_AXIS_UP_DOWN);
        //joy1.setPadBackground(R.drawable.cleanbaser);
        joy1.setButtonDrawable(R.drawable.joyl);
        //joy1.setPadColor(Color.parseColor("#55ffffff"));
        //joy1.setButtonColor(Color.parseColor("#55ff0000"));



        joy2.setListener(this);
        joy2.enableStayPut(false);
        joy2.setType(JoyStick.TYPE_2_AXIS_LEFT_RIGHT);
        //joy2.setPadBackground(R.drawable.cleanbasel);
        joy2.setButtonDrawable(R.drawable.joyr);
        //joy2.setPadColor(Color.parseColor("#55ffffff"));
        //joy2.setButtonColor(Color.parseColor("#55ff0000"));


        getJoySettings(joystring);
        addListenerOnButton();

        Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(50);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // update TextView here!
                                dashboardval();
                                joy1.invalidate();
                                joy2.invalidate();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        t.start();

    }

    protected void onDestroy()
    {
        super.onDestroy();
        try
        {
            socket.close();
            connectionThread.join(TIMEOUT);
            Log.d(TAG, "Connection is closed.");
        }
        catch (InterruptedException e)
        {
            Log.e(TAG, "Error occured while clossing the connection.");
            e.printStackTrace();
        }
        catch (IOException e)
        {
            Log.e(TAG, "Error occured while clossing the connection.");
            e.printStackTrace();
        }
    }

    public void startSetting (View view){
        Intent intent = new Intent(MainActivity.this, SettingActivity.class);
        startActivity(intent);
    }

     public void addListenerOnButton()
    {
        btnStart = (Button) findViewById(R.id.startbtn);
        int speedval = 0;

        btnStart.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              ipvalue = getIntent().getExtras().getString(SettingActivity.ipvalue);
              modestring = getIntent().getExtras().getString(SettingActivity.modestring);
              joystring = getIntent().getExtras().getString(SettingActivity.joystring);


              startUpConnection(ipvalue,portvalue);
          }
      });

  }
  public void startRun(View view)
  {

  }

  public void startUpConnection(final String ipvalue, final int portvalue)
  {
      Runnable runnable = new Runnable()
      {
          @Override
          public void run() {
              try
              {
                  socket.connect((new InetSocketAddress(InetAddress.getByName(ipvalue),portvalue)));
              } catch (IOException e)
              {
                  Log.e(TAG,"Connection Failed");
                  e.printStackTrace();
              }
          }
      };
      connectionThread = new Thread(runnable);
      connectionThread.start();
  }

  public void sendData(double Data) {

  }
  public void getJoySettings(String joymode){
      if (joymode == "solo")
      {
          //joy1.setType(JoyStick.TYPE_4_AXIS);
      }
      if(joymode == "dual")
      {
          //joy1.setType(JoyStick.TYPE_2_AXIS_UP_DOWN);
          //joy2.setType(JoyStick.TYPE_2_AXIS_LEFT_RIGHT);
      }
  }

    @Override
    public void onMove(JoyStick joyStick, double angle, double power, int direction)
    {


        switch (joyStick.getId())
        {
            case R.id.joy1:
                joyyvalue = power;
                joyydirection = direction;

                break;
            case R.id.joy2:
                joyxvalue = power;
                joyxdirection = direction;

                break;
        }

    }

    @Override
    public void onTap() {

    }

    @Override
    public void onDoubleTap() {

    }


    public void dashboardval()
    {
        valspeed = (TextView) findViewById(R.id.speedval);
        valsteer = (TextView) findViewById(R.id.steerval);
        valmode = (TextView) findViewById(R.id.modeval);
        valdis = (TextView) findViewById(R.id.disval);
        valbattery = (TextView) findViewById(R.id.batteryval);
        String ydirection = "";
        String xdirection  = "";

        if (joyydirection == 2){
            ydirection = "+";
        }
        if (joyydirection == 6){
            ydirection = "-";
        }
        else{
            ydirection = "";
        }
        valspeed.setText(ydirection+ String.valueOf((int)joyyvalue) + "%");

        if (joyxdirection == 4){
            xdirection = "+";
        }
        if (joyxdirection == 0){
            xdirection = "-";
        }
        else{
            xdirection = "";
        }
        valsteer.setText(xdirection+ String.valueOf((int)joyxvalue) + "%");
    }

    public void surfaceCreated(SurfaceHolder holder) {
        //loopactivity = new LoopActivity(this);
        loopactivity.setRunning(true);
        loopactivity.start();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        loopactivity.setRunning(false);
        loopactivity = null;
    }
}
