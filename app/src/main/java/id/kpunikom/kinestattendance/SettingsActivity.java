package id.kpunikom.kinestattendance;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Fragment fragment = new SettingsMenu();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        if(savedInstanceState == null){
            fragmentTransaction.add(R.id.relative_layout, fragment, "settings_fragment");
            fragmentTransaction.commit();
        }else{
            fragment = getFragmentManager().findFragmentByTag("settings_fragment");
        }
    }

    public static class SettingsMenu extends PreferenceFragment{
        @Override
        public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_menu);
        }
    }
}

