/* UDOO Power Menu
 * Copyright (C) 2016 UDOO
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.udoo.powermenu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;

public class MainActivity extends Activity {
    private static final String SHUTDOWN = "svc power shutdown";
    private static final String REBOOT_CMD = "svc power reboot";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showPowerDialog();
    }

    private void showPowerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        String negativeText = getString(android.R.string.cancel);
        builder.setCancelable(false);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });

        AlertDialog dialog = builder.create();
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.dialog_layout, null);
        Button reboot = (Button) view.findViewById(R.id.reboot);
        Button shutdown = (Button) view.findViewById(R.id.shutdown);

        reboot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExecuteCommandLine(REBOOT_CMD);
            }
        });

        shutdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExecuteCommandLine(SHUTDOWN);
            }
        });

        dialog.setView(view);

        dialog.show();
    }

    public static void ExecuteCommandLine(final String commandLine) {
        Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Process process = Runtime.getRuntime().exec(commandLine);

                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    StringBuffer output = new StringBuffer();
                    char[] buffer = new char[4096];
                    int read;

                    while ((read = reader.read(buffer)) > 0) {
                        output.append(buffer, 0, read);
                    }

                    reader.close();

                    process.waitFor();

                    Log.d("executeCommandLine", output.toString());

                } catch (IOException e) {
                    throw new RuntimeException("Unable to execute '" + commandLine + "'", e);
                } catch (InterruptedException e) {
                    throw new RuntimeException("Unable to execute '" + commandLine + "'", e);
                }

            }
        });
    }
}
