package net.rymate.remote;

import android.content.Context;
import android.widget.Toast;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;


public class ServerConnectionThread extends Thread {
    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private static ConsoleActivity activity;
    private Socket connectionSocket;
    private SocketForwardThread forwardThread;

    public ServerConnectionThread(String host, int port, String username, String password, ConsoleActivity a) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.activity = a;
    }

    public void run() {
        try {
            connectionSocket = new Socket(host, port);

            Runtime.getRuntime().addShutdownHook(new Thread("Shutdown Thread") {
                public void run() {
                    try {
                        connectionSocket.close();
                    } catch (IOException ex) {
                    }
                }
            });

            final PrintStream out = new PrintStream(connectionSocket.getOutputStream());

            forwardThread = null;
            try {
                forwardThread = new SocketForwardThread(activity, connectionSocket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            forwardThread.setDaemon(true);
            forwardThread.start();


            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    try {
                        connectionSocket.close();
                    } catch (IOException ex) {
                    }
                }
            });

            out.println(username);
            out.println(password);
            out.println("");

            activity.setOutputStream(out);

            try {
                Thread.sleep(500L);
            } catch (InterruptedException ex) {

            }
        } catch (final Exception ex) {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    Context context = activity.getBaseContext();
                    CharSequence text = "Failed to connect to server:\n\n" + ex.getMessage();

                    int duration = Toast.LENGTH_LONG;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            });
        }
    }

    public void closeSocket() {
        try {
            forwardThread.kill();
            forwardThread.interrupt();
            connectionSocket.shutdownInput();
            connectionSocket.shutdownOutput();
            connectionSocket.close();
            this.interrupt();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
