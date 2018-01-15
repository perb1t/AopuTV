package com.hzrobot.aoputv.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.hzrobot.aoputv.model.event.RobotCommand;
import com.hzrobot.aoputv.utils.log.LogUtil;
import com.hzrobot.aoputv.utils.net.NetWorkUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by shijiwei on 2018/1/5.
 *
 * @VERSION 1.0
 */

public class CommandServer extends Service {

    private static final String TAG = "CommandServer";
    private static final String SUFFIX = "\r\n";
    /* tcp server port */
    private final int TCP_PORT = 6666;
    /* */
    private final int CHECK_TCP_SERVER_INTERVAL = 5 * 1000;
    /* The monitoring service is opened */
    private boolean isStartServer = false;
    private boolean TCP_SERVER_ENABLE = true;

    private ServerSocket mServer;
    private HashMap<String, Socket> mClientSet = new HashMap<>();

    private ExecutorService mServerPolice = Executors.newCachedThreadPool();

    private Handler mServerLooper = new Handler();
    private Runnable mServerStateRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isStartServer && TCP_SERVER_ENABLE) {
                startServer();
            }
            mServerLooper.postDelayed(this, CHECK_TCP_SERVER_INTERVAL);
        }
    };


    /**
     * send heart beat to client
     *
     * @param out
     * @param msg
     * @throws IOException
     */
    private Handler mHeartBeatHandler = new Handler();

    class HeartBeatRunnable implements Runnable {

        private WeakReference<Socket> wr;
        private String host = "";

        public HeartBeatRunnable(Socket client) {
            wr = new WeakReference<Socket>(client);
        }

        @Override
        public void run() {
            Socket client = wr.get();
            OutputStream out = null;
            try {

                if (client != null && !client.isClosed() && client.isConnected() && !client.isOutputShutdown() && !client.isInputShutdown()) {
                    out = client.getOutputStream();
                    out.write("heartbeat".getBytes());
                    out.flush();

                    mHeartBeatHandler.postDelayed(this, 5 * 1000);
                }

            } catch (IOException e) {
                try {
                    if (out != null) {
                        out.close();
                        client.close();
                        mClientSet.remove(host);
                        LogUtil.e(TAG, "======= client host: " + host + " leave =====");
                    }
                } catch (IOException e1) {
                    e.printStackTrace();
                }
            } finally {
//                LogUtil.e(TAG, "============= send heart beat =================");
            }
        }
    }


    /* TCP client Runnable */
    class ClientRunnable implements Runnable {

        private WeakReference<Socket> wr;
        private String host = "";

        public ClientRunnable(Socket client) {
            this.wr = new WeakReference<Socket>(client);
        }

        @Override
        public void run() {
            Socket client = wr.get();
            InputStream in = null;
            try {
                if (client != null) {
                    host = client.getInetAddress().getHostName();
                    in = client.getInputStream();
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = in.read(buffer)) != -1 && client != null && !client.isClosed() && client.isConnected() && !client.isOutputShutdown() && !client.isInputShutdown()) {
                        String msg = new String(buffer, 0, len);
                        String[] commandSet = msg.split(SUFFIX);
                        if (commandSet != null && commandSet.length != 0) {
                            RobotCommand cmd = JSON.toJavaObject((JSON) JSON.parse(commandSet[commandSet.length - 1]), RobotCommand.class);
                            EventBus.getDefault().post(cmd);
                        }

                        LogUtil.e(TAG, " Msg :  " + msg);
                    }
                }
            } catch (IOException e) {
            } finally {

                try {
                    if (in != null) {
                        in.close();
                        client.close();
                        mClientSet.remove(host);
                        LogUtil.e(TAG, "======= client host: " + host + " leave =====");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mServerLooper.removeCallbacks(mServerStateRunnable);
        mServerLooper.postDelayed(mServerStateRunnable, 0);
    }

    @Override
    public void onDestroy() {
        release();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 开启TCP服务
     */
    private void startServer() {

        mServerPolice.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    release();
                    mServer = new ServerSocket(TCP_PORT);
                    isStartServer = true;
                    TCP_SERVER_ENABLE = true;
                    LogUtil.e(TAG, "===== waiting for client : server host " + NetWorkUtil.getHost() + " ======");
                    while (TCP_SERVER_ENABLE && isStartServer) {
                        Socket client = mServer.accept();
                        String host = client.getInetAddress().getHostName();
                        mClientSet.put(host, client);
                        LogUtil.e(TAG, "======= client host: " + host + " enter ========");
                        mServerPolice.execute(new ClientRunnable(client));
                        mHeartBeatHandler.post(new HeartBeatRunnable(client));
                    }
                } catch (IOException e) {
                    isStartServer = false;
                }
            }
        });

    }

    /**
     * 释放TCP资源
     */
    private void release() {
        try {
            if (mServer != null) {
                Iterator<String> iterator = mClientSet.keySet().iterator();
                while (iterator.hasNext()) {
                    String host = iterator.next();
                    Socket client = mClientSet.get(host);
                    if (client != null) {
                        client.close();
                        client = null;
                    }
                    iterator.remove();
                }
                mClientSet.clear();
                mServer.close();
                mServer = null;
                isStartServer = false;
                TCP_SERVER_ENABLE = false;

            }
        } catch (IOException e) {
        } finally {
            LogUtil.e(TAG, "===== tcp server close ======");
        }
    }

}
