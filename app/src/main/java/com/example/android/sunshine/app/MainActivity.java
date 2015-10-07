/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.sunshine.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity {

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    // In MainActivity create a mLocation variable to store our current known location.
    private String mLocation;

    private static final String FORECASTFRAGMENT_TAG = "FORECASTFRAGMENT_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Always call the superclass first
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState == null) {
            // si no hay una instancia previamente destruida
            // getSupportFragmentManager (): Porque es una subclase de FragmentActivity. Clase base para soporte de Fragments y Loaders
            getSupportFragmentManager().beginTransaction()
                    // aniado el fragment al estado de la activity
                    // agregaremos el fragment en R.id.container
                    // el fragmento a agregar es de tipo ForecastFragment
                    // tag para ser utilizado mediante findFragmentByTag(String).
                    .add(R.id.container, new ForecastFragment(), FORECASTFRAGMENT_TAG)
                            // commitea varon
                    .commit();
        }

        // Initialize this variable in onCreate to be whatever is currently stored in settings.
        mLocation = Utility.getPreferredLocation(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        if (id == R.id.action_map) {
            openPreferredLocationInMap();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openPreferredLocationInMap() {
        String location = Utility.getPreferredLocation(this);

        // Using the URI scheme for showing a location found on a map.  This super-handy
        // intent can is detailed in the "Common Intents" page of Android's developer site:
        // http://developer.android.com/guide/components/intents-common.html#Maps
        Uri geoLocation = Uri.parse("geo:0,0?").buildUpon()
                .appendQueryParameter("q", location)
                .build();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d(LOG_TAG, "Couldn't call " + location + ", no receiving apps installed!");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!Utility.getPreferredLocation(this).equals(mLocation)) {
            ForecastFragment ff = (ForecastFragment) getSupportFragmentManager().findFragmentByTag(FORECASTFRAGMENT_TAG);
            ff.onLocationChanged();
            mLocation = Utility.getPreferredLocation(this);
        }
    }
}
