package com.kudakoding.aaizat.kraider;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.app.ActivityManager;
import android.widget.Button;
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
    private Button btnStart;
    private String TAG = "MainActivity";
    private long TIMEOUT = 10l;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        JoyStick joy1 = (JoyStick) findViewById(R.id.joy1);
        JoyStick joy2 = (JoyStick) findViewById(R.id.joy2);

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
        double joyyvalue;
        double joyxvalue;
        int joyydirection;
        int joyxdirection;
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
}
