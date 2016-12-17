package cn.edu.pku.yinping.myweather;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

/**
 * Created by ag on 2016/12/17.
 */

public class Welcome extends Activity implements Runnable {

    //判断是否是第一次使用
    private boolean isFirstUse;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        new Thread(this).start();
    }

    public void run() {
        try {
            Thread.sleep(2000);
            SharedPreferences preferences = getSharedPreferences("isFirstUse",MODE_PRIVATE);
            isFirstUse = preferences.getBoolean("isFirstUse", true);

            if (isFirstUse) {
                startActivity(new Intent(Welcome.this, Guide.class));
            } else {
                startActivity(new Intent(Welcome.this, MainActivity.class));
            }
            finish();

            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("isFirstUse", false);
            editor.commit();


        } catch (InterruptedException e) {

        }
    }
}
