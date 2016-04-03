package com.example.sam.blutoothsocketreceiver;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Set;
import java.util.UUID;

/**
 * Created by sam on 4/3/16.
 */
public class scoutrank extends Thread {
    Activity context;
    protected static BluetoothDevice device = null;
    protected static final Object deviceLock = new Object();
    String scoutName;
    private static final String uuid = "f8212682-9a34-11e5-8994-feff819cdc9f";
    String data;

    public scoutrank(Activity context, String scoutName, String data) {
        this.context = context;
        this.scoutName = scoutName;
        this.data = data;
    }
    public static boolean initBluetooth(final Activity context, String scoutName) {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) {
            Log.wtf("Bluetooth Error", "Device Not Configured With Bluetooth");
            toastText("Device Not Configured With Bluetooth", Toast.LENGTH_LONG, context);
            return false;
        }
        if (!adapter.isEnabled()) {
            Log.e("Bluetooth Error", "Bluetooth Not Enabled");
            toastText("Bluetooth Not Enabled", Toast.LENGTH_LONG, context);
            return false;
        }
        Set<BluetoothDevice> devices = adapter.getBondedDevices();
        if (devices.size() < 1) {
            Log.e("Bluetooth Error", "No Paired Devices");
            toastText("No Paired Devices", Toast.LENGTH_LONG, context);
            return false;
        }
        adapter.cancelDiscovery();
        for (BluetoothDevice tmpDevice : devices) {
            if (tmpDevice.getName().equals(scoutName)) {
                synchronized (deviceLock) {
                    device = tmpDevice;
                }
                return true;
            }
        }
        Log.e("Bluetooth Error", "No Paired Device With Name: \"" + scoutName + "\"");
        toastText("No Paired Device With Name: \"" + scoutName + "\"", Toast.LENGTH_LONG, context);
        return false;
    }

    public void run() {
        if(!initBluetooth(context, scoutName)) {
            return;
        }

        //we try the entire process of sending three times.  If it repeatedly fails, we exit
        int counter = 0;
        while (true) {
            PrintWriter out = null;
            BufferedReader in;
            BluetoothSocket socket = null;
            try {
                //first open connection
                synchronized (deviceLock) {
                    socket = device.createRfcommSocketToServiceRecord(UUID.fromString(uuid));
                }
                Log.i("Socket Info", "Attempting To Start Connection...");
                socket.connect();
                Log.i("Socket info", "Connection Successful!  Getting Ready To Send Data...");
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException ioe) {
                //if it fails, close stuff and start over
                try {
                    if (out != null) {
                        out.close();
                    }
                    if (socket != null) {
                        socket.close();
                    }
                } catch (IOException ioe2) {
                    Log.e("Socket Error", "Failed To End Socket");
                    toastText("Failed To Close Connection To Super", Toast.LENGTH_LONG, context);
                    return;
                }
                if (counter == 2) { //TODO
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(context)
                                    .setTitle("Repeated Connection Failure")
                                    .setMessage("Please resend this data when successful data transfer is made.")
                                    .setNeutralButton("Dismiss", null)
                                    .show();
                        }
                    });
                    return;
                }
                counter++;
                continue;
            }

            Log.i("Communications Info", "Starting To Communicate");

            try {
                //we print the length of the data before we print the data so the super can identify corrupted data
                out.println(data.length());
                out.print(data);
                //we print '\0' at end of data to signify the end
                out.println("\0");
                out.flush();
                if (out.checkError()) {
                    throw new IOException();
                }
                int ackCode = Integer.parseInt(in.readLine());
                //super will send 0 if the data sizes match up, 1 if they don't
                if (ackCode == 1) {
                    throw new IOException();
                } else if (ackCode == 2) {
                    //data is invalid JSON, notify user and return
                    Log.e("Communications Error", "Data not in valid format");
                    Toast.makeText(context, "Data not in valid format", Toast.LENGTH_LONG).show();
                    return;
                }
            } catch (IOException ioe) {
                try {
                    out.close();
                    in.close();
                    socket.close();
                } catch (IOException ioe2) {
                    Log.e("Socket Error", "Failed To End Socket");
                    toastText("Failed To Close Connection To Super", Toast.LENGTH_LONG, context);
                    return;
                }
                if (counter == 2) { //TODO
                    Log.e("Communications Error", "Repeated Data Send Failure");
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(context)
                                    .setTitle("Repeated Data Send Failure")
                                    .setMessage("Please resend this data when successful data transfer is made.")
                                    .setNeutralButton("Dismiss", null)
                                    .show();
                        }
                    });
                    return;
                }
                counter++;
                continue;
            }

            //we succeeded, close stuff and leave
            try {
                in.close();
                out.close();
                socket.close();
            } catch (IOException ioe) {
                Log.e("Socket Error", "Failed To End Socket");
                toastText("Failed To Close Connection To Super", Toast.LENGTH_LONG, context);
            }
            break;
        }

        Log.i("Communications Info", "Done");
        toastText("Data Send Success", Toast.LENGTH_LONG, context);

    }
    protected static void toastText(final String text, final int duration, final Activity context) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, text, duration).show();
            }
        });
    }
}
