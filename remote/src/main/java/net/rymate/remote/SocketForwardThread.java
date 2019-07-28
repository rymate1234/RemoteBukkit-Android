package net.rymate.remote;

import android.content.Context;
import android.view.View;
import android.widget.ScrollView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SocketForwardThread extends Thread {
    private final ConsoleActivity gui;
    private final BufferedReader in;
    private boolean stopConnection;

    public SocketForwardThread(ConsoleActivity gui, InputStream in) {
        super("Socket Forward Thread");

        this.gui = gui;
        this.in = new BufferedReader(new InputStreamReader(in));
    }

    public void run() {
        try {
            while (!stopConnection) {
                final String input = this.in.readLine();
                if (input == null) {
                    makeToast("Connection closed.");
                    return;
                }

                gui.runOnUiThread(new Runnable() {
                    public void run() {
                        gui.appendText(filterColours(input) + "\n");
                        final ScrollView v = (ScrollView) gui.findViewById(R.id.scrollView);
                        v.post(new Runnable() {
                            @Override
                            public void run() {
                                v.fullScroll(View.FOCUS_DOWN);
                            }
                        });
                    }
                });
            }
            in.close();
        } catch (IOException ex) {
            makeToast("Connection to server lost:\n\n" + ex.getMessage());
        }
    }

    public void makeToast(final String s) {
        gui.runOnUiThread(new Runnable() {
            public void run() {
                Context context = gui.getApplicationContext();
                CharSequence text = s;

                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        });
    }

    public void kill() throws IOException {
        stopConnection = true;
    }

    public String filterColours(String s) {
        return s.replaceAll("\\x1B\\[([0-9]{1,3}((;[0-9]{1,3})*)?)?[m|K]", "");
    }
}
