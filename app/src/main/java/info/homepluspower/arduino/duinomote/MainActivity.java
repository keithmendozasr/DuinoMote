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
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Button;
import android.widget.SeekBar;
import android.util.Log;

public class MainActivity extends Activity {
    private final static String LogTag = MainActivity.class.getName();
    private Button speedUpBtn, stopBtn, speedDownBtn;
    private SeekBar steerBar;
    private EditText robotIPText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        speedUpBtn      = (Button) findViewById(R.id.speedUpBtn);
        stopBtn         = (Button) findViewById(R.id.stopBtn);
        speedDownBtn    = (Button) findViewById(R.id.speedDownBtn);

        steerBar        = (SeekBar) findViewById(R.id.steerBar);
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

        robotIPText     = (EditText) findViewById(R.id.robotIPText);
    }

    private void setControlVisibility(boolean visible)
    {
        int visibility = (visible ? View.VISIBLE : View.INVISIBLE);

        speedUpBtn.setVisibility(visibility);
        stopBtn.setVisibility(visibility);
        speedDownBtn.setVisibility(visibility);
        steerBar.setVisibility(visibility);

        robotIPText.setEnabled(!visible);
    }

    //Handle "Connect" being switched
    public void onActivateClicked(View v)
    {
        Switch s = (Switch)v;
        if(s.isChecked())
            setControlVisibility(true);
        else
            setControlVisibility(false);
    }
}
