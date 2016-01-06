package com.example.sam.blutoothsocketreceiver;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
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
        tmp = null;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
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
                    text("Trying to connect...");
                    Log.e("serverSocket", "is null");
                }
                //otherwise accept the connection and print out 'accepting'
                System.out.println("accepting...");
                text("accepting connection...");
                System.out.println();
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
                /*try {
                    //THIS IS THE ERROR LINE
                    mmServerSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("Test_Connection", UUID.fromString(uuid));
                } catch (IOException ioe) {
                    new AlertDialog.Builder(context)
                            .setTitle("Bluetooth Error")
                            .setMessage("Serious bluetooth error due to ServerSocket failure.  Get an App Programmer and show him this message")
                            .setNeutralButton("Dismiss", null);
                    return;
                }
                */
            }
        }
    }

    public void text(final String change_text) {
        changing = (TextView)context.findViewById(R.id.text);
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                changing.setText(change_text);
            }
        });
    }

}


