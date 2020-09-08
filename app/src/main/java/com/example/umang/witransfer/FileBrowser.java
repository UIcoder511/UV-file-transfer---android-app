

package com.example.umang.witransfer;



import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.ArrayList;
import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import java.util.List;
import java.util.Properties;


import android.widget.Toast;

public class FileBrowser extends Activity{

    private  String root;
    private  String currentPath;

    private ArrayList<String> targets;
    private ArrayList<String> paths;



    private File targetFile;

    public final int port = 46032;
    ServerSocket welcomeSocket;
    Socket serverSocket;
    Socket clientSocket=new Socket();

    Server  s=new Server();
    conToServer  c=new conToServer();


    ProgressDialog pro;
    NotificationManager notificationManager;
    Notification.Builder notificationBuilder;



    Button button1;


    Integer notificationID = 100;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        pro=new ProgressDialog(FileBrowser.this);
        pro.setIndeterminate(false);
        pro.setMax(100);
        pro.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        button1 = (Button) findViewById(R.id.b);

        notificationBuilder = new Notification.Builder(getApplicationContext());
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        //Set notification information:

        notificationBuilder.setOngoing(true)
                .setContentTitle("Notification Content Title")
                .setContentText("Notification Content Text")
                .setSmallIcon(R.mipmap.myicon)
                .setProgress(100, 0, false);



        if (!Settings.System.canWrite(this)) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE}, 2909);
        }
        super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_file_browser);

        root = "/";
        currentPath = root;

        targets = null;
        paths = null;

        targetFile = null;


        if(SocketHandler.Loc==true)
        {
          if(SocketHandler.tapper==true)
          {
              try {
                  Thread.sleep(2000);
              } catch (InterruptedException e) {
                  e.printStackTrace();
              }
              c.start();

          }
          else
          {

              s.start();

          }
        }

        showDir(currentPath);
    }



    public void DIR(View v) {

        PopupMenu popup = new PopupMenu(FileBrowser.this, v);

        //Inflating the Popup using xml file
        popup.getMenuInflater()
                .inflate(R.menu.main_menu, popup.getMenu());
        final Intent i = new Intent(v.getContext(),FileBrowserDIR.class);
        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item)
            {
                int id = item.getItemId();
                switch (id) {
                    case R.id.item1:
                    {
                        Toast.makeText(getApplicationContext(), "Path : "+SocketHandler.getPath().getAbsolutePath(), Toast.LENGTH_LONG).show();
                        return true;
                    }

                    case R.id.item2:
                    {
                        Toast.makeText(getApplicationContext(), "Select new path", Toast.LENGTH_LONG).show();

                        SocketHandler.Loc=false;
                        SocketHandler.change=true;
                        startActivity(i);
                        return true;
                    }

                    default: return false;

                }

            }
        });

        popup.show();
    }






        public void selectDirectory(View view) throws IOException {

        File f = new File(currentPath);
        targetFile = f;
        SocketHandler.setPath(f);

        Log.d("path",currentPath);
        Properties p=new Properties();
        File f1 = new File(Environment.getExternalStorageDirectory() + "/WiTransfer"+ "/LOC.properties");
        FileOutputStream fileOut = null;

        try {
            fileOut = new FileOutputStream(f1);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        p.setProperty("Path",currentPath);
        p.store(fileOut, "Properties");
        fileOut.close();


        //Return target File to activity
        returnTarget();
    }



    private void showDir(String targetDirectory) {
        //rowItems = new ArrayList<ViewHandler>();

        RecyclerView itemsList;
        ItemsListAdapter adapter;


        final ArrayList<ViewHandler> mDataList = new ArrayList<>();



        itemsList = (RecyclerView) findViewById(R.id.recycler_view);
        itemsList.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        itemsList.setLayoutManager(mLayoutManager);


        targets = new ArrayList<String>();
        paths = new ArrayList<String>();

        File f = new File(targetDirectory);
        File[] directoryContents = f.listFiles();


        if (!targetDirectory.equals(root))

        {
            targets.add(root);
            paths.add(root);

            targets.add("../");
            paths.add(f.getParent());

            mDataList.add(new ViewHandler("/",R.drawable.rt,
                    -1));
            mDataList.add(new ViewHandler("../",R.drawable.bk,-1));
        }
        int cc = 0,size=directoryContents.length;


        Log.d("VVV", String.valueOf(size));


        for (File target : directoryContents)
        {
            ViewHandler data;
            if(target.isDirectory())
            {
                data = new ViewHandler(target.getName(),R.drawable.fd,-1);
            }
            else
            {

                long fsize=(target.length());
                if (target.toString().endsWith(".jpg") || target.toString().endsWith(".jpeg") || target.toString().endsWith(".png"))
                    {
                       data = new ViewHandler(target.getName(),R.drawable.im,fsize);
                     }
                     else if (target.toString().endsWith(".3gp") || target.toString().endsWith(".mp4") || target.toString().endsWith(".mkv") || target.toString().endsWith(".avi"))
                     {
                        data = new ViewHandler(target.getName(),R.drawable.vd,fsize);
                       }
                       else
                           {
                             data = new ViewHandler(target.getName(),R.drawable.download,fsize);
                           }
            }


            mDataList.add(data);

            paths.add(target.getPath());
            if (target.isDirectory()) {
                targets.add(target.getName() + "/");
            } else {
                targets.add(target.getName());

            }

        }

        adapter = new ItemsListAdapter(this,mDataList, new CustomItemClickListener()
        {
            @Override
            public void onItemClick(View v, int position)
            {


                File f = new File(paths.get(position));
                double fs=(f.length())/(1024.0 * 1024.0);
                DecimalFormat dec = new DecimalFormat("0.00");
                Log.d("TAG", f.getName() +"  |  "+ position+"  "+dec.format(fs).concat(" MB"));
                if(f.isFile())
                {
                    targetFile = f;

                    SocketHandler.setFile(f);

                    SocketHandler.sender=true;

                    Sen sr2=new Sen(serverSocket,clientSocket,FileBrowser.this);
                    sr2.execute();







                    Log.d("DD","Select File...");


                }
                else
                {
                    //f must be a dir
                    if(f.canRead())
                    {
                        currentPath = paths.get(position);
                        showDir(paths.get(position));
                    }

                }



            }
        });
        itemsList.setAdapter(adapter);
    }







    public void returnTarget()
    {
       if(SocketHandler.Loc==false)
       {

           {
               Log.d("S","SSSSS");
               Intent returnIntent = new Intent();
               returnIntent.putExtra("file", targetFile);
               setResult(RESULT_OK, returnIntent);
               finish();
           }

       }



    }




    class Server extends Thread {


        @Override
        public void run() {


            try {

                welcomeSocket = new ServerSocket(7950);
                Log.d("S1", "Server started...");

                serverSocket = welcomeSocket.accept();
                Log.d("S2", "Server accepted...");

                Reci rc2=new Reci(serverSocket,clientSocket);
                rc2.start();
                    }

             catch (IOException e) {


                e.printStackTrace();
            }

        }










        }







    class conToServer extends  Thread
    {

        @Override
        public void run()
        {

                try
                {
                    Log.d("C","Client..."+SocketHandler.getTargetIP());
                    clientSocket.bind(null);
                    clientSocket.connect(new InetSocketAddress(SocketHandler.getTargetIP(),7950));

                    Log.d("DD","After Client...");

                    SocketHandler.setClientSocket(clientSocket);


                    Reci rc2=new Reci(serverSocket,clientSocket);
                    rc2.start();

                }

                catch (IOException e)
                {
                    e.printStackTrace();
                }


         }







    }

    class Sen extends AsyncTask<String,Integer,String> {
        private Socket ssocket, csocket;
        private Context context;
        private PowerManager.WakeLock mWakeLock;

        Sen(Socket ss, Socket cc, Context c) {
            ssocket = ss;
            csocket = cc;
            context = c;
        }

        @Override
        protected String doInBackground(String... params) {

            if (SocketHandler.tapper == true) {

                Log.d("DD", "No tapper Sending...");


                boolean stop = false;
                {

                    try {


                        Log.d("DD", "ack Recieve...");


                        OutputStream os = csocket.getOutputStream();
                        DataOutputStream DOS = new DataOutputStream(os);

                        String fname = SocketHandler.getFile().getName();
                        DOS.writeUTF(fname);
                        os.flush();

                        long fsize = SocketHandler.getFile().length();

                        DOS.writeLong(fsize);
                        os.flush();

                        long countt = 0;


                        byte[] buffer = new byte[1024 * 8];

                        FileInputStream fis = new FileInputStream(SocketHandler.getFile());
                        BufferedInputStream bis = new BufferedInputStream(fis);
                        long total = 0;



                        while (true) {

                            int bytesRead = bis.read(buffer, 0, buffer.length);
                            total = total + bytesRead;



                            publishProgress((int) (total * 100 / fsize));
                            Log.d("DD", String.valueOf(bytesRead));
                            if (bytesRead == -1) {
                                break;
                            }

                            os.write(buffer, 0, bytesRead);
                            os.flush();

                        }


                        Log.d("DD", "Sent...");
                        SocketHandler.sender = false;

                        DOS.flush();

                        fis.close();
                        bis.close();


                    } catch (Exception e) {

                        e.printStackTrace();
                    }
                }

            } else {
                Log.d("DD", "tapper Sending...");



                {

                    try {


                        Log.d("DD", "ack Recieve...");


                        OutputStream os = ssocket.getOutputStream();
                        DataOutputStream DOS = new DataOutputStream(os);

                        String fname = SocketHandler.getFile().getName();
                        DOS.writeUTF(fname);
                        os.flush();

                        long fsize = SocketHandler.getFile().length();

                        DOS.writeLong(fsize);
                        os.flush();


                        byte[] buffer = new byte[1024 * 8];

                        FileInputStream fis = new FileInputStream(SocketHandler.getFile());
                        BufferedInputStream bis = new BufferedInputStream(fis);
                        long total = 0;


                        while (true) {


                            int bytesRead = bis.read(buffer, 0, buffer.length);
                            total = total + bytesRead;

                            publishProgress((int) (total * 100 / fsize));
                            //pro.setProgress((int) (total * 100 / fsize));
                            Log.d("DD", String.valueOf(bytesRead));
                            if (bytesRead == -1) {
                                break;
                            }

                            os.write(buffer, 0, bytesRead);
                            os.flush();

                        }


                        Log.d("DD", "Sent...");
                        SocketHandler.sender = false;

                        DOS.flush();

                        fis.close();
                        bis.close();


                    } catch (Exception e) {

                        e.printStackTrace();
                    }
                }

            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            pro.setTitle("Sending...");
            pro.setMessage(SocketHandler.getFile().getName());
            pro.setCanceledOnTouchOutside(false);
            pro.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            pro.setIndeterminate(false);
            pro.setMax(100);
            pro.setProgress(progress[0]);
        }


        @Override
        protected void onPostExecute(String result) {

            pro.dismiss();


        }
    }


    class Reci extends Thread
    {
        private Socket ssocket,csocket;

        Reci(Socket ss,Socket cc)
        {
           ssocket=ss;
           csocket=cc;
        }

        @Override
        public void run()
        {

            try
              {

                if(SocketHandler.tapper==true && SocketHandler.sender!=true )
                {

                    Recieve(csocket);

                }
                else
                {

                    Recieve(ssocket);

                }
            }
            catch (Exception e){}

            }





        public void Recieve(Socket socket)
        {
            Log.d("DD","RRRR...");


             DataInputStream DIS=null;
                try {

                    Log.d("aaa","before Dis..");
                    InputStream is = socket.getInputStream();
                    DIS = new DataInputStream(is);


                    while (true)
                    {
                        if(DIS.available()>0)
                        {
                            Log.d("aaa",">0");
                            break;
                        }

                    }

                    Log.d("DD","A...");
                                      String savedAs = DIS.readUTF();
                    Log.d("DD","B...");

                                      Long size=DIS.readLong();
                                      int count=0;


                    Log.d("DD","C..");
                    Properties p=new Properties();
                                      File file = new File(SocketHandler.getPath(), savedAs);
                    Log.d("DD","D...");
                                      byte[] buffer2 = new byte[1024*8];

                    Log.d("DD","E...");


                                      FileOutputStream fos = new FileOutputStream(file);
                                      BufferedOutputStream bos = new BufferedOutputStream(fos);

                    Log.d("DD","F...");
                    Log.d("DD",String.valueOf(size));

                    notificationBuilder
                            .setContentTitle("Recieveing...")
                            .setContentText(savedAs);

                    Notification notification = notificationBuilder.build();
                    notificationManager.notify(notificationID, notification);


                    int bytesRead;

                                      while(true)
                                      {

                                          bytesRead = is.read(buffer2, 0, buffer2.length);
                                          count=count+bytesRead;

                                          Log.d("DD", String.valueOf(bytesRead));
                                          Log.d("DD", String.valueOf(count));

                                          notificationBuilder.setProgress(100, (int)(count * 100 / size), false);
                                          notification = notificationBuilder.build();
                                          notificationManager.notify(notificationID, notification);

                                         // if(bytesRead==-1)
                                           //   break;
                                          Log.d("DD", "start");//1544811 1544811
                                                                      bos.write(buffer2,0,bytesRead);
                                          Log.d("ED", "end");
                                                                      bos.flush();

                                                                      if(count==size)
                                                                          break;//1635986
                                      }

                    notificationManager.cancel(notificationID);

                    Log.d("DD","Recieved...");

                    Reci rc2=new Reci(serverSocket,clientSocket);
                    rc2.start();

                    Thread.currentThread().interrupt();
                   // return;


                } catch (Exception e)
                {

                    e.printStackTrace();

                    Log.d("aaaa","Exception");

                }

            }


        }




    @Override
    protected void onDestroy() {


        super.onDestroy();





    }

}







