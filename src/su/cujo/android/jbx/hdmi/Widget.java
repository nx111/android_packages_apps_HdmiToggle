/*
 * Copyright (C) 2014 Alexandr Dvorin Licensed under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package su.cujo.android.jbx.hdmi;

import android.app.PendingIntent;
import android.appwidget.AppWidgetProvider;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.ComponentName;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.Exception;

public class Widget extends AppWidgetProvider {

    public static String TAG = "HDMI_Toggle";
    public static String ACTION_TOGGLE = "su.cujo.android.jbx.hdmi.Widget.ACTION_TOGGLE";
    
    private static final int modeOff = 0;
    private static final int modeOn = 1;
    private static final int modeGet = 2;
    private static final int modeToggle = 3;

    static String ctl = "/sys/kernel/hdmi/hdmi_active";
    static int state = -1;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
       updateHdmiState(context, modeOff);
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION_TOGGLE)) {
            updateHdmiState(context, modeToggle);
        } else {
            super.onReceive(context, intent);
        }
    }

    @Override
    public void onDisabled(Context context){
        if (state==0)
            updateHdmiState(context, modeOn);
    }

    private void updateHdmiState(Context context, int toggle)
    {
        try {
            BufferedReader in = new BufferedReader(new FileReader(ctl));
            BufferedWriter out = new BufferedWriter(new FileWriter(ctl, true));
            String line;
            in.mark(80);
            line = in.readLine();
            if (line != null)
                state = Integer.parseInt(line);

            if (toggle == modeOff && line != null  && state != 0 )
                out.write("0");
            else if (toggle == modeOn && line != null  && state != 1 ){
                out.write("1");
            }              
            else if (toggle == modeToggle && line != null && state == 0){
                out.write("1");
            }
            else if (toggle == modeToggle && line != null && state == 0){
                out.write("0");
            }
            out.close();
            in.reset();
            line = in.readLine();
            in.close();

            state = Integer.parseInt(line);
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
            remoteViews.setImageViewResource(R.id.icon, state == 1 ? R.drawable.icon_on : R.drawable.icon);
            Intent active = new Intent(context, Widget.class);
            active.setAction(ACTION_TOGGLE);
            PendingIntent actionPendingIntent = PendingIntent.getBroadcast(context, 0, active, 0);
            remoteViews.setOnClickPendingIntent(R.id.icon, actionPendingIntent);
            ComponentName cn = new ComponentName(context, Widget.class);
            AppWidgetManager.getInstance(context).updateAppWidget(cn, remoteViews);

//            Toast.makeText(context, "HDMI is " + (state == 1 ? "on" : "off"), Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "hdmi_active=" + state);
    }

}
