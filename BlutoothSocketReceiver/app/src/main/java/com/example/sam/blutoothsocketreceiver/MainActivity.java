package com.example.sam.blutoothsocketreceiver;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.UUID;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;

public class MainActivity extends ActionBarActivity {
    BluetoothServerSocket tmp;
    BluetoothServerSocket mmServerSocket;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket socket;
    String text;
    String uuid;
    Activity context;
    String byteSize;
    String data;
    DataSnapshot snapshot;
    String key_value;
    TextView changing;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        accept_loop loop = new accept_loop();
        loop.start();
        Firebase.setAndroidContext(this);
        changing = (TextView)findViewById(R.id.text);
    }

    public class AcceptThread extends Thread {
        BluetoothSocket socket;
        public AcceptThread(BluetoothSocket socket) {
            this.socket = socket;
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
                        /*try {
                            File dir = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/MassStringText");
                            //can delete when doing the actual thing
                            file = new PrintWriter(new FileOutputStream(new File(dir, "Send-Data.txt")));
                        } catch (IOException IOE) {
                            Log.e("File error", "Failed to open File");
                            return;
                        }*/
                    try {
                        text = "";
                        //get the bytesize from the first line of the data
                        byteSize = reader.readLine();
                    } catch (IOException e) {
                        text("Failed to read data");
                        System.out.println("Failed to read Data");
                    }
                    int size = Integer.parseInt(byteSize);
                    while (socket != null) {
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
                            //text(Integer.toString(size));
                        }
                        //data = data.concat("asdf");
                        //if the actual byte size is different from the byte size received..
                        if (size != data.length()) {
                            //send error message to scout.
                            //0 = no error, 1 = ERROR!
                            out.println("1");
                            out.flush();
                            out.flush();
                            text("Error Message Sent " + "\n" + "Byte size: " + Integer.toString(size) + "\n"
                                    + "Length size: " + Integer.toString(data.length()));
                            Log.e("Error", "Error message sent");
                            //I the byte size of actual is equal to the byte size received
                        } else if (size == data.length()) {
                            //can delete when doing actual thing
                            //file.println(text);
                            System.out.println(text);
                            out.println("0");
                            out.flush();
                            context.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context, "Data trasnfer success", Toast.LENGTH_SHORT);
                                }
                            });
                            
                            Firebase myFirebaseRef = new Firebase("https://popping-torch-4659.firebaseio.com");
                            myFirebaseRef.child("Mass String Data").child("Byte Size").setValue(Integer.toString(size));
                            System.out.println("Firebase");

                                /*
                                key_value = snapshot.child("Mass String Data").getValue().toString();
                                if(key_value == null){
                                    System.out.println("Failed to send to FireBase");
                                    text("Failed to send to FireBase");
                                }
                                Log.e("success", "right byte size");
                                System.out.println("Sent to FireBase!");
                                text("Sent to Firebase");
                                */
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
                //you don't want to send the data infinite amount of times!
            }
        }
    }

    public class accept_loop extends Thread {
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
            while(true) {

                try{
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
                    new AcceptThread(socket).start();

                } catch (IOException IOE) {
                    Log.e("Bluetooth Error", "Failed to open socket");
                }
            }
        }
    }

    public void text(final String change_text){
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                changing.setText(change_text);
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}


