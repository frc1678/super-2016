package com.example.sam.blutoothsocketreceiver;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
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
                //socket now calls accept() which returns bluetooth socket
                socket = mmServerSocket.accept();
                new AcceptThread(context, socket).start();

            } catch (IOException IOE) {
                Log.e("Bluetooth Error", "Failed to open socket");
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

