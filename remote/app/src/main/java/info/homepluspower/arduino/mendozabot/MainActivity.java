/*
 * Copyright 2015 Keith Mendoza Sr
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

package info.homepluspower.arduino.mendozabot;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Button;
import android.widget.SeekBar;
import android.util.Log;
import android.widget.TextView;
import android.widget.RadioButton;

import java.io.IOException;
import java.net.Socket;
import java.net.InetSocketAddress;

import 	android.os.AsyncTask;

public class MainActivity extends Activity {

    private final static String LogTag = MainActivity.class.getName();
    private Button speedUpBtn, stopBtn, speedDnBtn;
    private SeekBar steerBar;
    private EditText robotIPText;
    private TextView statusText;
    private Switch activateSwitch;
    private RadioButton forwardBtn, reverseBtn;
    private Socket socket = null;
    private char dir;

    private void sendMessage(char msg)
    {
        try
        {
            if(socket != null && socket.isConnected())
                socket.getOutputStream().write((int)msg);
        }
        catch(IOException e)
        {
            Log.e(LogTag, "Failed to send message. Cause: " + e.getMessage());
            setControlEnabled(false);
            activateSwitch.setChecked(false);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(LogTag, "onCreate called");

        setContentView(R.layout.activity_main);

        robotIPText = (EditText) findViewById(R.id.robotIPText);
        statusText = (TextView) findViewById(R.id.statusText);
        activateSwitch = (Switch) findViewById(R.id.activateSwitch);

        speedUpBtn = (Button) findViewById(R.id.speedUpBtn);
        stopBtn = (Button) findViewById(R.id.stopBtn);
        speedDnBtn = (Button) findViewById(R.id.speedDnBtn);
        forwardBtn = (RadioButton) findViewById(R.id.forwardBtn);
        reverseBtn = (RadioButton) findViewById(R.id.reverseBtn);

        steerBar = (SeekBar) findViewById(R.id.steerBar);
        steerBar.setEnabled(false);
        steerBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private int lastDir;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d(LogTag, "Progress changed to " + Integer.toString(progress));
                if(seekBar.getProgress() < 255 && lastDir >= 0) {
                    sendMessage('j');
                    lastDir = -1;
                }
                else if(seekBar.getProgress() > 255 && lastDir <= 0) {
                    sendMessage('k');
                    lastDir = 1;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                lastDir = 0;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d(LogTag, "onStopTrackingTouch");
                seekBar.setProgress(255);
                sendMessage(dir);
            }
        });

    }

    private void setControlEnabled(boolean enabled)
    {
        speedUpBtn.setEnabled(enabled);
        stopBtn.setEnabled(enabled);
        speedDnBtn.setEnabled(enabled);
        steerBar.setEnabled(enabled);

        forwardBtn.setEnabled(enabled);
        forwardBtn.setChecked(false);

        reverseBtn.setEnabled(enabled);
        reverseBtn.setChecked(false);

        robotIPText.setEnabled(!enabled);
    }

    private class ConnectToRobot extends AsyncTask<String, Void, Boolean>
    {
       @Override
        protected Boolean doInBackground(String... params) {
            if(params.length != 1) {
                Log.e(LogTag, "Invalid number of parameters given");
                return false;
            }

            try
            {
                if(socket == null || socket.isClosed())
                    socket = new Socket();

                socket.connect(new InetSocketAddress(params[0], 8888));
            }
            catch(IOException e)
            {
                Log.e(LogTag, "Error connecting. Cause: " + e.getMessage());
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(!aBoolean) {
                statusText.setText("Error connecting to the robot");
                setControlEnabled(false);
                activateSwitch.setChecked(false);
                socket = null;
            }
            else {
                statusText.setText("Connected to robot");
                setControlEnabled(true);
            }
        }
    }

    //Handle "Connect" being switched
    public void onActivateClicked(View v)
    {
        if(v != activateSwitch)
        {
            Log.e(LogTag, "Got event from unknown switch");
            return;
        }

        if(activateSwitch.isChecked())
        {
            String ip = robotIPText.getText().toString();

            if(!ip.matches("(\\d{1,3}\\.){3}\\d{1,3}"))
            {
                statusText.setText("Value is not a valid IP format");
                activateSwitch.setChecked(false);
                return;
            }

            Log.d(LogTag, "Value acceptable");
            new ConnectToRobot().execute(ip);
        }
        else
        {
            if(socket != null && socket.isConnected())
            {
                try
                {
                    socket.close();
                }
                catch(IOException e)
                {
                    Log.e(LogTag, "Exception encountered while closing connection. Cause: " + e.getMessage());
                }
            }

            statusText.setText(getText(R.string.NotConnectedStatus));
            setControlEnabled(false);
        }
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
        if(socket != null && socket.isConnected())
        {
            try
            {
                socket.close();
            }
            catch(IOException e)
            {
                Log.e(LogTag, "Error disconnecting. Cause: " + e.getMessage());
            }
        }
    }

    public void buttonClick(View v)
    {
        switch (v.getId()) {
            case R.id.forwardBtn:
                dir = 'i';
                sendMessage(dir);
                break;
            case R.id.reverseBtn:
                dir = 'm';
                sendMessage(dir);
                break;
            case R.id.stopBtn:
                dir = 's';
                sendMessage(dir);
                break;
            case R.id.speedUpBtn:
                sendMessage('u');
                break;
            case R.id.speedDnBtn:
                sendMessage('d');
                break;
        }
    }
}
