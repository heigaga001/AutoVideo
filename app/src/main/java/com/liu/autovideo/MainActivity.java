package com.liu.autovideo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    EditText name ;
    EditText time;
    Button btn ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        name = (EditText)findViewById(R.id.nickname);
        time = (EditText)findViewById(R.id.time);
        btn = (Button)findViewById(R.id.save);
        SharedPreferences sp = getSharedPreferences("com.liu",Context.MODE_PRIVATE);
        String nickname = sp.getString("nickname","");
        int timeS = sp.getInt("time",30);
        name.setText(nickname);
        time.setText(timeS+"");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nickName = name.getText().toString().trim();
                String timeStr = time.getText().toString().trim();
                if(TextUtils.isEmpty(nickName)||TextUtils.isEmpty(timeStr)){
                    Toast.makeText(MainActivity.this,"请输入正确的配置！！",Toast.LENGTH_LONG).show();
                    return;
                }
                int t = Integer.parseInt(timeStr);
                if(t<=0){
                    Toast.makeText(MainActivity.this,"请输入正确的时间间隔！！",Toast.LENGTH_LONG).show();
                    return;
                }

                SharedPreferences sp = getSharedPreferences("com.liu",Context.MODE_PRIVATE);
                sp.edit().putString("nickname",nickName).putInt("time",t).commit();
                startService(new Intent(MainActivity.this,AutoVideoService.class));
            }
        });
    }
}
