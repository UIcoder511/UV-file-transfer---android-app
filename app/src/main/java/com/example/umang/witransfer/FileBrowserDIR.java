

package com.example.umang.witransfer;




import java.io.File;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import java.io.IOException;

import java.util.ArrayList;

import android.Manifest;

import android.os.Bundle;
import android.app.Activity;

import android.os.Environment;
import android.provider.Settings;
import android.util.Log;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import java.util.Properties;




public class FileBrowserDIR extends Activity {

    private  String root;
    private  String currentPath;

    private ArrayList<String> targets;
    private ArrayList<String> paths;


    private File targetFile;



    @Override
    public void onCreate(Bundle savedInstanceState) {

        if (!Settings.System.canWrite(this)) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE}, 2909);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_browserdir);


        root = "/";
        currentPath = root;

        targets = null;
        paths = null;

        targetFile = null;



        showDir(currentPath);
    }








        public void selectDirectory(View view) throws IOException
        {

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


        returnTarget();
    }


    public void setCurrentPathText(String message)
    {
        TextView fileTransferStatusText = (TextView) findViewById(R.id.current_path);
        fileTransferStatusText.setText(message);
    }


    private void showDir(String targetDirectory){

        setCurrentPathText("Current Directory: " + currentPath);

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
        }

        for(File target: directoryContents)
        {
            paths.add(target.getPath());

            if(target.isDirectory())
            {
                targets.add(target.getName() + "/");
            }
            else
            {
                targets.add(target.getName());

            }

        }

        ListView fileBrowserListView = (ListView) findViewById(R.id.file_browser_listview);

        ArrayAdapter<String> directoryData = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, targets);
        fileBrowserListView.setAdapter(directoryData);




        fileBrowserListView.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View view, int pos,long id) {

                File f = new File(paths.get(pos));

                if(f.isFile())
                {
                    targetFile = f;

                    Log.d("DD","Select File...");


                }
                else
                {
                    //f must be a dir
                    if(f.canRead())
                    {
                        currentPath = paths.get(pos);
                        showDir(paths.get(pos));
                    }

                }


            }

        });

    }

    public void returnTarget()
    {


       finish();


    }








    @Override
    protected void onDestroy()
    {


        super.onDestroy();

    }

}







