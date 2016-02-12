package com.example.sam.blutoothsocketreceiver;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by sam on 1/3/16.
 */
public class accept_loop extends Thread {
    BluetoothServerSocket tmp;
    BluetoothServerSocket mmServerSocket;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket socket;
    String uuid;
    Activity context;
    TextView changing;

    public accept_loop(Activity context){

        this.context = context;
    }
    public void run() {
        //continously checking for connection
        tmp = null;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Log.wtf("Bluetooth Error", "Device Not Configured With Bluetooth");
            toasts("Device Not Configured With Bluetooth");
            return;
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Log.e("Bluetooth Error", "Bluetooth Not Enabled");
            toasts("Bluetooth Not Enabled");
            return;
        }
        uuid = "f8212682-9a34-11e5-8994-feff819cdc9f";
        try {
            tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("Test_Connection", UUID.fromString(uuid));
            mmServerSocket = tmp;

        } catch (IOException e) {
            System.out.println("Failed to accept");
        }

        while (true) {
            try {
                if (mmServerSocket.equals(null)) {
                    System.out.println("Trying to connect...");
                    Log.e("serverSocket", "is null");
                }
                //otherwise accept the connection and print out 'accepting'
                System.out.println("accepting...");
                //socket now calls accept() which returns bluetooth socket
                System.out.println("before .accept");
                socket = mmServerSocket.accept();
                System.out.println("after .accept");
                AcceptThread acceptThread = new AcceptThread(context, socket);
                acceptThread.start();
                System.out.println("after .start");

            } catch (IOException IOE) {
                Log.e("Bluetooth Error", "Failed to open socket");
                Toast.makeText(context, "Failed to open socket", Toast.LENGTH_SHORT).show();
                Log.e("IOE exceptipon", IOE.getMessage());

            }
        }
    }


    public void text(final String change_text) {
        changing = (TextView) context.findViewById(R.id.text);
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                changing.setText(change_text);
            }
        });
    }

    public void toasts(final String message) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });


    }
}