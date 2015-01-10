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

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Button;
import android.widget.SeekBar;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends Activity {

    private final static String LogTag = MainActivity.class.getName();
    private Button forwardBtn, stopBtn, reverseBtn;
    private SeekBar steerBar;
    private EditText robotIPText;
    private TextView statusText;
    private Switch activateSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(LogTag, "onCreate called");

        setContentView(R.layout.activity_main);

        robotIPText = (EditText) findViewById(R.id.robotIPText);
        statusText = (TextView) findViewById(R.id.statusText);
        activateSwitch = (Switch) findViewById(R.id.activateSwitch);

        forwardBtn = (Button) findViewById(R.id.forwardBtn);
        stopBtn = (Button) findViewById(R.id.stopBtn);
        reverseBtn = (Button) findViewById(R.id.reverseBtn);

        steerBar = (SeekBar) findViewById(R.id.steerBar);
        steerBar.setEnabled(false);
        steerBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d(LogTag, "Progress changed to " + Integer.toString(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.d(LogTag, "onStartTrackingTouch");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d(LogTag, "onStopTrackingTouch");
                seekBar.setProgress(255);
            }
        });

    }

    private void setControlEnabled(boolean enabled)
    {
        forwardBtn.setEnabled(enabled);
        stopBtn.setEnabled(enabled);
        reverseBtn.setEnabled(enabled);
        steerBar.setEnabled(enabled);

        robotIPText.setEnabled(enabled);
    }

    //Handle "Connect" being switched
    public void onActivateClicked(View v)
    {
        Switch s = (Switch)v;
        if(s.isChecked())
            setControlEnabled(true);
        else
            setControlEnabled(false);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Log.d(LogTag, "onResume");

        //Check if WIFI exists
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm.getActiveNetworkInfo().getType() != ConnectivityManager.TYPE_WIFI)
        {
            statusText.setText("Not on WIFI connection");
            robotIPText.setEnabled(false);
            activateSwitch.setEnabled(false);
            setControlEnabled(false);
        }
        else
        {
            statusText.setText(getString(R.string.NotConnectedStatus));
            robotIPText.setEnabled(true);
            activateSwitch.setEnabled(true);
            activateSwitch.setChecked(false);
            setControlEnabled(false);
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        Log.d(LogTag, "onPause");
    }
}
