package com.kudakoding.aaizat.kraider;

import android.content.Intent;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class SettingActivity extends AppCompatActivity {

    private  RadioGroup radioGmode;
    private RadioGroup radioGjoy;
    private RadioButton radioBmode;
    private RadioButton radioBjoy;
    private Button btnSave;
    private Button btnBack;
    private Button btnKill;
    private EditText editipadd;
    public static String ipvalue = "192.168.0.1";
    public static String joystring = "manual";
    public static String modestring = "dual";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ipvalue = "192.168.0.1";
        addListenerOnButton();
    }

    public void addListenerOnButton ()
    {
        radioGmode = (RadioGroup) findViewById(R.id.groupmode);
        radioGjoy = (RadioGroup) findViewById(R.id.groupjoy);
        btnSave = (Button) findViewById(R.id.savebtn);
        btnBack = (Button) findViewById(R.id.backbtn);
        btnKill = (Button) findViewById(R.id.killbtn);
        editipadd = (EditText) findViewById(R.id.ipaddedit);


        btnSave.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(SettingActivity.this, MainActivity.class);
                int selectedIdmode = radioGmode.getCheckedRadioButtonId();
                int selectedIdjoy = radioGjoy.getCheckedRadioButtonId();

                radioBmode = (RadioButton) findViewById(selectedIdmode);
                radioBjoy = (RadioButton) findViewById(selectedIdjoy);

                intent.putExtra(ipvalue, editipadd.getText().toString());
                intent.putExtra(joystring, radioBjoy.getText().toString());
                intent.putExtra(modestring, radioBmode.getText().toString());
                startActivity(intent);


                String results = radioBjoy.getText().toString() + " and " + radioBmode.getText().toString();
                Toast.makeText(SettingActivity.this,results , Toast.LENGTH_SHORT).show();
                //Toast.makeText(SettingActivity.this, radioBjoy.getText(), Toast.LENGTH_SHORT).show();
            }
        });

        btnBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(SettingActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        btnKill.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }


}
