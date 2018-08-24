package id.kpunikom.kinestattendance;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import cn.refactor.lib.colordialog.PromptDialog;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btScanner = findViewById(R.id.btnScanner);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);

        btScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new
                        Intent(MainActivity.this, ScannerActivity.class);
                startActivity(intent1);
            }
        });

        if (!isConnected(MainActivity.this)) {
            //buildDialog(MainActivity.this).show();
            noConnectionDialog(MainActivity.this).show();
        }
    }


    public boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if ((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting()))
                return true;
            else
                return false;
        } else
            return false;
    }

    public PromptDialog noConnectionDialog(Context context) {
        PromptDialog promptDialog = new PromptDialog(context);
        promptDialog.setDialogType(PromptDialog.DIALOG_TYPE_WRONG)
                .setAnimationEnable(true)
                .setTitleText("Ga ada koneksi nih!")
                .setContentText("Yuk konekin dulu ke internet. Pencet tombol Ok untuk keluar.")
                .setCancelable(false);
        promptDialog.setPositiveListener("Ok", new PromptDialog.OnPositiveListener() {
            @Override
            public void onClick(PromptDialog dialog) {
                finish();
            }
        });
        return promptDialog;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.btnsetting) {
            Intent intent2 = new
                    Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent2);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

