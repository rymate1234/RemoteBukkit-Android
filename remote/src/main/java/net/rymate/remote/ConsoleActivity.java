package net.rymate.remote;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.PrintStream;

public class ConsoleActivity extends Activity {

    private TextView consoleText;
    private String host;
    private int port;
    private String user;
    private String pass;
    ServerConnectionThread thread;
    private PrintStream outputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_console);

        host = getIntent().getExtras().getString(LoginDetailsUtils.HOSTNAME);
        port = getIntent().getExtras().getInt(LoginDetailsUtils.PORT);
        user = getIntent().getExtras().getString(LoginDetailsUtils.USERNAME);
        pass = getIntent().getExtras().getString(LoginDetailsUtils.PASSWORD);

        thread = new ServerConnectionThread(host, port, user, pass, this);

        consoleText = (TextView) findViewById(R.id.textView);

        final EditText editText = (EditText) findViewById(R.id.wow_much_console);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;

                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    outputStream.println(editText.getText().toString());
                    editText.setText("");
                    handled = true;
                }
                return handled;
            }
        });

        thread.start();

        appendText("Attempting to connect, please wait..." + "\n");
    }

    long lastPress;

    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastPress > 5000) {
            Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_LONG).show();
            lastPress = currentTime;
        } else {
            thread.closeSocket();
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.console, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void appendText(String s) {
        consoleText.append(s);
    }

    public TextView getTextView() {
        return consoleText;
    }

    public void setOutputStream(PrintStream outputStream) {
        this.outputStream = outputStream;
    }
}
