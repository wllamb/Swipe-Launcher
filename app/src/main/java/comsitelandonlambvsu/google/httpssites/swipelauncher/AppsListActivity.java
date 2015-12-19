package comsitelandonlambvsu.google.httpssites.swipelauncher;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;


public class AppsListActivity extends Activity {

    public EditText input;
    ArrayAdapter adapter;
    private ListView list;
    private GridView grid;
    ImageView settings;
    private PackageManager manager;
    private List<appDetail> apps;
    public boolean userPrefList;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps_list);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        setupVariables();
        loadApps();
        checkVariables();
        addClickListener();
        SharedPreferences.OnSharedPreferenceChangeListener spChanged = new
            SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    checkVariables();
                }
            };
        preferences.registerOnSharedPreferenceChangeListener(spChanged);
    }

    private void setupVariables()
    {
        settings = (ImageView)findViewById(R.id.imageView);
        input = (EditText)findViewById(R.id.editText);
        input.requestFocus();
        input.addTextChangedListener(searchTextWatcher);
        grid = (GridView)findViewById(R.id.gridView);
        list = (ListView)findViewById(R.id.appView);

    }

    private void checkVariables()
    {
        userPrefList = preferences.getBoolean("gridPreference",false);
        if(userPrefList)
        {
            list.setVisibility(View.INVISIBLE);
            grid.setVisibility(View.VISIBLE);
            loadGridView();
        } else {
            list.setVisibility(View.VISIBLE);
            grid.setVisibility(View.INVISIBLE);
            loadListView();
        }

    }


    private TextWatcher searchTextWatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // ignore
            if(userPrefList)
            {
                loadGridView();
                addClickListener();
            } else {
                loadListView();
                addClickListener();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            if(userPrefList)
            {
                filter(s.toString());
                loadGridView();
                addClickListener();
            } else {
                filter(s.toString());
                loadListView();
                addClickListener();
            }
        }
    };

    private void loadApps(){
        manager = getPackageManager();
        apps = new ArrayList<appDetail>();

        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> availableActivities = manager.queryIntentActivities(i, 0);
        for(ResolveInfo ri:availableActivities) {
            appDetail app = new appDetail();
            app.label = ri.loadLabel(manager);
            app.name = ri.activityInfo.packageName;
            app.icon = ri.activityInfo.loadIcon(manager);
            apps.add(app);
        }
        Collections.sort(apps, new Comparator<appDetail>() {
            @Override
            public int compare(appDetail appDetail, appDetail appDetail2) {
                return String.CASE_INSENSITIVE_ORDER.compare(
                        appDetail.label.toString(),
                        appDetail2.label.toString());
            }
        });
    }

    private void loadListView(){
        adapter = new ArrayAdapter<appDetail>(this,R.layout.list_item, apps) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if(convertView == null){
                    convertView = getLayoutInflater().inflate(R.layout.list_item, null);
                }
                ImageView appIcon = (ImageView)convertView.findViewById(R.id.item_app_icon);
                appIcon.setImageDrawable(apps.get(position).icon);
                TextView appLabel = (TextView)convertView.findViewById(R.id.item_app_label);
                appLabel.setText(apps.get(position).label);
                return convertView;
            }
        };
        list.setAdapter(adapter);
    }

    private void loadGridView(){

        adapter = new ArrayAdapter<appDetail>(this,R.layout.grid_item, apps) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if(convertView == null){
                    convertView = getLayoutInflater().inflate(R.layout.grid_item, null);
                }
                ImageView appIcon = (ImageView)convertView.findViewById(R.id.item_app_icon);
                appIcon.setImageDrawable(apps.get(position).icon);
                TextView appLabel = (TextView)convertView.findViewById(R.id.item_app_label);
                appLabel.setText(apps.get(position).label);
                return convertView;
            }
        };
        grid.setAdapter(adapter);
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        apps.clear();
        if (charText.length() == 0) {
            loadApps();
        }
        else
        {
            Intent i = new Intent(Intent.ACTION_MAIN, null);
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> availableActivities = manager.queryIntentActivities(i, 0);
            for(ResolveInfo ri:availableActivities){
                if(ri.loadLabel(manager).toString().toLowerCase(Locale.getDefault()).startsWith(charText)) {
                    appDetail app = new appDetail();
                    app.label = ri.loadLabel(manager);
                    app.name = ri.activityInfo.packageName;
                    app.icon = ri.activityInfo.loadIcon(manager);
                    apps.add(app);
                }
            }
            Collections.sort(apps, new Comparator<appDetail>() {
                @Override
                public int compare(appDetail appDetail, appDetail appDetail2) {
                    return String.CASE_INSENSITIVE_ORDER.compare(
                            appDetail.label.toString(),
                            appDetail2.label.toString());
                }
            });
        }
    }

    private void addClickListener(){
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos,
                                    long id) {
                Intent i = manager.getLaunchIntentForPackage(apps.get(pos).name.toString());
                AppsListActivity.this.startActivity(i);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(input.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                finish();
            }
        });
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos,
                                    long id) {
                Intent i = manager.getLaunchIntentForPackage(apps.get(pos).name.toString());
                AppsListActivity.this.startActivity(i);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(input.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                finish();
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(i);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_apps_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
