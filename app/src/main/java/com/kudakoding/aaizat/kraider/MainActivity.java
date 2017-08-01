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
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
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
    public String joyysend;
    public String joyxsend;
    public String joysend;
    public boolean sendnow = false;

    public JoyStick joy1;
    public JoyStick joy2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        joy1 = (JoyStick) findViewById(R.id.joy1);
        joy2 = (JoyStick) findViewById(R.id.joy2);

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
    //    intent.putExtra("ip", ipvalue);
    //    intent.putExtra("mode", modestring);
    //    intent.putExtra("joy", joystring);
        startActivity(intent);
    }

     public void addListenerOnButton()
    {
        btnStart = (Button) findViewById(R.id.startbtn);
        int speedval = 0;

        btnStart.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {


              dashboardetc();
              btnStart.setHighlightColor(Color.BLUE);

              getJoySettings(joystring);
              startUpConnection(ipvalue,portvalue);
              sendnow = true;
          }
      });

  }
  public void startRun(View view)
  {

  }

  public void startUpConnection(final String ipvalue, final int portvalue)
  {
      Log.d(TAG, "Connecting to " + ipvalue + ":" + portvalue);
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
      Log.d(TAG, "Sending data  " + joymode);
      if (joymode == "solo")
      {
          joy1.setType(JoyStick.TYPE_4_AXIS);
          joy1.invalidate();
      }
      else if(joymode == "dual")
      {
          joy1.setType(JoyStick.TYPE_2_AXIS_UP_DOWN);
          joy2.setType(JoyStick.TYPE_2_AXIS_LEFT_RIGHT);
          joy1.invalidate();
          joy2.invalidate();
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
        //valmode = (TextView) findViewById(R.id.modeval);
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
        joyysend = ydirection + String.valueOf((int)joyyvalue);
        valspeed.setText(joyysend + "%");

        if (joyxdirection == 4){
            xdirection = "+";
        }
        if (joyxdirection == 0){
            xdirection = "-";
        }
        else{
            xdirection = "";
        }
        joyxsend = xdirection + String.valueOf((int)joyxvalue);
        valsteer.setText(joyxsend + "%");

        if (sendnow == true) {
            joysend = modestring + ", " + joyysend + ", " + joyxsend;
            //Log.d(TAG, "Sending data  " + joysend);
            sendData(joysend);
        }

    }
    public void dashboardetc()
    {


        valmode = (TextView) findViewById(R.id.modeval);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            ipvalue = extras.getString("ipvalue");
            modestring = extras.getString("modestring");
            joystring = extras.getString("joystring");
        }
        if (extras == null)
        {
            ipvalue = "192.168.0.1";
            modestring = "manual";
            joystring = "dual";
        }


        valmode.setText(modestring);
    }
    private void sendData(String data) {

        if (!socket.isConnected() || socket.isClosed()) {
            Toast.makeText(getApplicationContext(), "Joystick is disconnected...",
                    Toast.LENGTH_LONG).show();
            return;
        }

        try {
            OutputStream outputStream = socket.getOutputStream();

            byte [] arr = data.getBytes();
            byte [] cpy = ByteBuffer.allocate(arr.length+1).array();

            for (int i = 0; i < arr.length; i++) {
                cpy[i] = arr[i];
            }

            //Terminating the string with null character
            cpy[arr.length] = 0;

            outputStream.write(cpy);
            outputStream.flush();

            Log.d(TAG, "Sending data  " + data);
        } catch (IOException e) {
            Log.e(TAG, "IOException while sending data "
                    + e.getMessage());
            e.printStackTrace();
        } catch (NullPointerException e) {
            Log.e(TAG, "NullPointerException while sending data "
                    + e.getMessage());
        }
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
