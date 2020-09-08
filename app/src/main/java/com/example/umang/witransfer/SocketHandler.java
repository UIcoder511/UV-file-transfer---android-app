package com.example.umang.witransfer;

import java.io.File;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

class SocketHandler
{

    private static int port;
    private static InetAddress targetIP;
    private static File path,file;
    static boolean Loc,tapper,change=false;
    static boolean sender=false,reciever=true;
    static boolean dir;

    public static synchronized InetAddress getTargetIP()
    {
        return targetIP;
    }
    public static synchronized int getPort()
    {
        return port;
    }
    public static synchronized File getPath()
    {
        return path;
    }
    public static synchronized File getFile()
    {
        return file;
    }

    public static void setDir(boolean dir) {
        SocketHandler.dir = dir;
    }

    public static boolean getDir(){return dir;}



    public static synchronized void setTargetIP(InetAddress IP)
    {
        SocketHandler.targetIP=IP;
    }
    public static synchronized void setPort(int p)
    {
        SocketHandler.port=p;
    }
    public static synchronized void setPath(File pp)
    {
        SocketHandler.path=pp;
    }
    public static synchronized void setFile(File pp)
    {
        SocketHandler.file=pp;
    }
}
