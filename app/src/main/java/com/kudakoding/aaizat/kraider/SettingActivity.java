package com.kudakoding.aaizat.kraider;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class SettingActivity extends AppCompatActivity {

    private  RadioGroup radioGmode;
    private RadioGroup radioGjoy;
    private RadioButton radioBmode;
    private RadioButton radioBjoy;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        addListenerOnButton();
    }

    public void addListenerOnButton ()
    {
        radioGmode = (RadioGroup) findViewById(R.id.groupmode);
        radioGjoy = (RadioGroup) findViewById(R.id.groupjoy);
        btnSave = (Button) findViewById(R.id.savebtn);


        btnSave.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v)
            {
                int selectedIdmode = radioGmode.getCheckedRadioButtonId();
                int selectedIdjoy = radioGjoy.getCheckedRadioButtonId();

                radioBmode = (RadioButton) findViewById(selectedIdmode);
                radioBjoy = (RadioButton) findViewById(selectedIdjoy);
                String results = radioBjoy.getText().toString() + " and " + radioBmode.getText().toString();
                Toast.makeText(SettingActivity.this,results , Toast.LENGTH_SHORT).show();

                //Toast.makeText(SettingActivity.this, radioBjoy.getText(), Toast.LENGTH_SHORT).show();
            }
        });



    }
}
