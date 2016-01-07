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

    public class AcceptThread extends  Thread {
        String text;
        Activity context;
        String byteSize;
        String data;
        TextView changing;
        BluetoothSocket socket;

        public AcceptThread(Activity context, BluetoothSocket socket) {
            this.socket = socket;
            this.context = context;
        }
        public void run() {
            // If a connection was accepted
            if (socket != null) {
                //socket opened with connection
                // Do work to manage the connection (in a separate thread)
                try {
                    PrintWriter out;
                    out = new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter file = null;
                    try {
                        File dir = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/MassStringText");
                        //can delete when doing the actual thing
                        file = new PrintWriter(new FileOutputStream(new File(dir, "Send-Data.txt")));
                    } catch (IOException IOE) {
                        Log.e("File error", "Failed to open File");
                        return;
                    }
                    try {
                        text = "";
                        //get the bytesize from the first line of the data
                        byteSize = reader.readLine();
                    } catch (IOException e) {
                        text("Failed to read data");
                        System.out.println("Failed to read Data");
                    }
                    int size = Integer.parseInt(byteSize);
                    if (socket != null) {
                        data = "";
                        while (true) {
                            text = reader.readLine();
                            //If all the data is sent then break
                            if (text.equals("\0")) {
                                break;
                            }
                            //append data to the variable "data"
                            data = data.concat(text + "\n");
                            System.out.println(data);
                            file.println(text);
                            if (file.checkError()){
                                Toast.makeText(context, "Failed to write to file", Toast.LENGTH_SHORT).show();

                            }
                            Toast.makeText(context, "Sent to File", Toast.LENGTH_SHORT).show();
                            //text(Integer.toString(size));
                        }
                        //data = data.concat("asdf");
                        //if the actual byte size is different from the byte size received..
                        if (size != data.length()) {
                            //send error message to scout.
                            //0 = no error, 1 = ERROR!
                            out.println("1");
                            out.flush();
                            text("Error Message Sent " + "\n" + "Byte size: " + Integer.toString(size) + "\n"
                                    + "Length size: " + Integer.toString(data.length()));
                            Log.e("Error", "Error message sent");
                            //I the byte size of actual is equal to the byte size received
                        } else {
                            //can delete when doing actual thing
                            //file.println(text);
                            System.out.println(text);
                            out.println("0");
                            out.flush();
                            context.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context, "Data trasnfer success", Toast.LENGTH_SHORT).show();
                                }
                            });

                            Firebase myFirebaseRef = new Firebase("https://popping-torch-4659.firebaseio.com");
                            myFirebaseRef.child("Mass String Data").child("Byte Size").setValue(Integer.toString(size));
                            myFirebaseRef.child("Mass String Data").child("File name").setValue("Sent_Data.txt");
                            myFirebaseRef.child("Mass String Data").child("Time sent").setValue(new SimpleDateFormat("MM-dd-yyyy-H:mm:ss").format(new Date()));
                            System.out.println(" Data Sent to Firebase");
                            Toast.makeText(context, "Data sent to Firebase", Toast.LENGTH_SHORT).show();

                        }
                        System.out.println("end");
                        text("end");
                        return;
                    }
                    //file.close();
                    // socket.close();
                } catch (IOException e) {
                    System.out.println("Failed to handle data");
                    text("Failed to handle data");
                    Log.getStackTraceString(e);
                    return;
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

}


