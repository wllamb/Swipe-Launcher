package comsitelandonlambvsu.google.httpssites.swipelauncher;


import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.widget.Toast;


public class SettingsActivity extends PreferenceActivity {


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.user_pref);
        Preference myPref = (Preference) findPreference("about");
        myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }


}
