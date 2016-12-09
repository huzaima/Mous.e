package com.huzaima.mouse;

import java.net.Socket;

/**
 * Created by Huzaima Khan on 05-Dec-16.
 */

public class SocketHandler {

    private static Socket[] socket = new Socket[5];


    // [0] => CursorSocket
    // [1] => LeftClickSocket
    // [2] => rightClickSocket
    // [3] => SwipeSocket
    // [4] => calibrationSocket

    public static synchronized Socket getCursorSocket() {
        return socket[0];
    }

    public static synchronized void setCursorSocket(Socket socket) {
        SocketHandler.socket[0] = socket;
    }

    public static synchronized Socket getLeftClickSocket() {
        return socket[1];
    }

    public static synchronized void setLeftClickSocket(Socket socket) {
        SocketHandler.socket[1] = socket;
    }

    public static synchronized Socket getRightClickSocket() {
        return socket[2];
    }

    public static synchronized void setRightClickSocket(Socket socket) {
        SocketHandler.socket[2] = socket;
    }

    public static synchronized Socket getSwipeSocket() {
        return socket[3];
    }

    public static synchronized void setSwipeSocket(Socket socket) {
        SocketHandler.socket[3] = socket;
    }

    public static synchronized Socket getCalibrationSocket() {
        return socket[4];
    }

    public static synchronized void setCalibrationSocket(Socket socket) {
        SocketHandler.socket[4] = socket;
    }

}