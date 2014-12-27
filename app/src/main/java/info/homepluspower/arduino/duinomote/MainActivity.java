/*
 * Copyright 2014 Keith Mendoza Sr
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package info.homepluspower.arduino.duinomote;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;

public class MainActivity extends ActionBarActivity implements SeekBar.OnSeekBarChangeListener {
    private final static String LogTag = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ((SeekBar)findViewById(R.id.leftSlider)).setOnSeekBarChangeListener(this);
        ((SeekBar)findViewById(R.id.rightSlider)).setOnSeekBarChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch(seekBar.getId())
        {
            case R.id.leftSlider:
                Log.v(LogTag, "Left slider moving to " + Integer.toString(progress));
                break;
            case R.id.rightSlider:
                Log.v(LogTag, "Right slider moving to " + Integer.toString(progress));
                break;
            default:
                Log.w(LogTag, "Got progress from " + Integer.toString(seekBar.getId()));
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        //do nothing
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        //do nothing
    }
}
