package com.kudakoding.aaizat.kraider;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.app.ActivityManager;
import android.widget.Toast;

import com.erz.joysticklibrary.JoyStick;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        JoyStick joy1 = (JoyStick) findViewById(R.id.joy1);
        //joy1.setListener(this);
        joy1.setPadColor(Color.parseColor("#55ffffff"));
        joy1.setButtonColor(Color.parseColor("#55ff0000"));
        JoyStick joy2 = (JoyStick) findViewById(R.id.joy2);
        //joy1.setListener(this);
        joy2.setPadColor(Color.parseColor("#55ffffff"));
        joy2.setButtonColor(Color.parseColor("#55ff0000"));
    }

  public void startSetting (View view){
      Intent intent = new Intent(MainActivity.this, SettingActivity.class);
      startActivity(intent);
  }

  public void startRun(View view)
  {

  }

}
