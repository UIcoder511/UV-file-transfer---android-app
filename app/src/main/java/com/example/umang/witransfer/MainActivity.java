package com.example.umang.witransfer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;

import android.os.Environment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.io.IOException;
import java.io.InputStream;

import java.net.InetAddress;

import java.util.ArrayList;
import java.util.Properties;

public class MainActivity extends Activity {
    public final int fileRequestID = 55;
    public final int port = 8080;


    boolean peertapped = false;

    private IntentFilter wifiServerReceiverIntentFilter;
    Intent i, j, set;

    private WifiP2pManager wifiManager;
    private WifiP2pManager.Channel wifichannel;
    private BroadcastReceiver wifiServerReceiver;

    private WifiManager wifionoff;

    private WifiP2pDevice targetDevice;
    private WifiP2pInfo wifiInfo;



    public File downloadTarget;

    public InetAddress targetIP;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        wifiManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        wifichannel = wifiManager.initialize(this, getMainLooper(), null);
        wifiServerReceiver = new WiFiServerBroadcastReceiver(wifiManager, wifichannel, this);

        wifiServerReceiverIntentFilter = new IntentFilter();

        wifiServerReceiverIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        wifiServerReceiverIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        wifiServerReceiverIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        wifiServerReceiverIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        registerReceiver(wifiServerReceiver, wifiServerReceiverIntentFilter);


        wifionoff = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        SocketHandler.setPort(port);

        wifionoff.setWifiEnabled(true);


        wifiManager.discoverPeers(wifichannel, null);
    }


    public void finale() throws IOException {
        targetIP = wifiInfo.groupOwnerAddress;
        SocketHandler.setTargetIP(targetIP);


        Properties p=new Properties();
        File f = new File(Environment.getExternalStorageDirectory() + "/WiTransfer");

        if(!f.isDirectory())
        {
            f.mkdirs();
            File file = new File(f, "LOC.properties");
            FileOutputStream fileOut = new FileOutputStream(file);
            p.setProperty("Path","S");

            p.store(fileOut, "Properties");
            fileOut.close();
        }


            File f1 = new File(Environment.getExternalStorageDirectory() + "/WiTransfer"+ "/LOC.properties");
            InputStream is=new FileInputStream(f1);
            p.load(is);
            Log.d("Pro",p.getProperty("Path"));
            Log.d("S","SSS");
            if(p.getProperty("Path").equals("S"))
            {

                if (peertapped == true)
                {




                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                  //  Toast.makeText(getApplicationContext(), "Samsung"+"\n"+targetIP.toString(), Toast.LENGTH_SHORT).show();



                    try {

                        set = new Intent(this, FileBrowser.class);

                        startActivityForResult(set, fileRequestID);

                       // Toast.makeText(getApplicationContext(), "set.. ", Toast.LENGTH_SHORT).show();


                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();

                    }

                } else
                    {

                  //  Toast.makeText(getApplicationContext(), "MI..."+"\n"+targetIP.toString(), Toast.LENGTH_SHORT).show();


                    set = new Intent(this, FileBrowser.class);

                    startActivityForResult(set, fileRequestID);

                   // Toast.makeText(getApplicationContext(), "set... ", Toast.LENGTH_SHORT).show();

                }

            }

        else
        {

            SocketHandler.Loc=true;
            Log.d("S","SSSs");
            SocketHandler.setPath(new File(p.getProperty("Path")));
            i=new Intent(this,FileBrowser.class);
            startActivity(i);

        }
        //  Toast.makeText(getApplicationContext(),"Connected to FINAL",Toast.LENGTH_SHORT).show();


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

         {Log.d("S","ACS");
            //Fetch result
            File targetDir = (File) data.getExtras().get("file");

            if (targetDir.isDirectory()) {
                if (targetDir.canWrite()) {
                    downloadTarget = targetDir;

                    SocketHandler.setPath(targetDir);
                    SocketHandler.Loc=true;


                    Toast.makeText(getApplicationContext(), "Location set...", Toast.LENGTH_SHORT).show();

                    i=new Intent(this,FileBrowser.class);
                    startActivity(i);




                } else
                    {
                    Toast.makeText(getApplicationContext(), "You do not have permission to write to ", Toast.LENGTH_SHORT).show();

                    }

            }
            else
                {
                Toast.makeText(getApplicationContext(), "The selected file is not a directory. Please select a valid download directory.", Toast.LENGTH_SHORT).show();

            }

        }
    }


    public void displayPeers(final WifiP2pDeviceList peers) {
        //Dialog to show errors/status
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("WiFi Direct File Transfer");

        //Get list view
        ListView peerView = (ListView) findViewById(R.id.peers_listview);

        //Make array list
        ArrayList<String> peersStringArrayList = new ArrayList<String>();

        //Fill array list with strings of peer names
        for (WifiP2pDevice wd : peers.getDeviceList()) {
            peersStringArrayList.add(wd.deviceName);
        }

        //Set list view as clickable
        peerView.setClickable(true);

        //Make adapter to connect peer data to list view
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, peersStringArrayList.toArray());

        //Show peer data in listview
        peerView.setAdapter(arrayAdapter);



    //-----end--------
        peerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {

                //Get string from textview
                TextView tv = (TextView) view;

                WifiP2pDevice device = null;

                //Search all known peers for matching name
                for (WifiP2pDevice wd : peers.getDeviceList()) {
                    if (wd.deviceName.equals(tv.getText())) {
                        device = wd;

                        }

                }

                if (device != null) {
                    //Connect to selected peer
                    connectToPeer(device);

                } else
                    {


                }
            }
            // TODO Auto-generated method stub
        });


    }

    public void connectToPeer(final WifiP2pDevice wifiPeer) {
        peertapped = true;
        SocketHandler.tapper=true;
        this.targetDevice = wifiPeer;
        // Toast.makeText(getApplicationContext(),"Connected to "+"2....",Toast.LENGTH_SHORT).show();
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = wifiPeer.deviceAddress;


        wifiManager.connect(wifichannel, config, new WifiP2pManager.ActionListener() {
            public void onSuccess() {


            }

            public void onFailure(int reason) {


            }
        });
    }




        @Override
        protected void onResume () {
            super.onResume();
        }

        @Override
        protected void onPause () {
            super.onPause();

        }


        protected void onDestroy ()
        {
            super.onDestroy();



            try {
                 WiFiServerBroadcastReceiver.disconnect();
                unregisterReceiver(wifiServerReceiver);

            } catch (Exception e) {

            }
        }



    }




