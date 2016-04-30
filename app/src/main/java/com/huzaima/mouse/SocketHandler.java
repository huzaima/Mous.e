package com.huzaima.mouse;

import java.net.Socket;

/**
 * Created by Marib on 4/20/2016.
 */
public class SocketHandler {

    private static Socket socket;

    public static synchronized Socket getSocket(){
        return socket;
    }

    public static synchronized void setSocket(Socket socket){
        SocketHandler.socket = socket;
    }
}