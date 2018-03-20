package com.hanksudo.yconnectandroiddemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener {

    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = findViewById(R.id.signin_btn);
        btn.setOnClickListener(this);
    }

    public void onClick(View v) {
        if (v.getId() == R.id.signin_btn) {
            Intent intent = new Intent();
            intent.setClassName("com.hanksudo.yconnectandroiddemo",
                    "com.hanksudo.yconnectandroiddemo.YConnectImplicitActivity");
            startActivity(intent);
        }
    }
}
