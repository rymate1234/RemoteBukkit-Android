package net.rymate.remote;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {

    private EditText host;
    private EditText user;
    private EditText port;
    private EditText pass;
    private CheckBox remember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        host = (EditText) findViewById(R.id.textHostname);
        port = (EditText) findViewById(R.id.textPort);
        user = (EditText) findViewById(R.id.textUsername);
        pass = (EditText) findViewById(R.id.editText2);
        remember = (CheckBox) findViewById(R.id.checkBox);

        remember.setChecked(LoginDetailsUtils.getRemembered(this));

        if (remember.isChecked()) {
            host.setText(LoginDetailsUtils.getFromPrefs(this, LoginDetailsUtils.HOSTNAME, ""));
            port.setText(LoginDetailsUtils.getFromPrefs(this, LoginDetailsUtils.PORT, ""));
            user.setText(LoginDetailsUtils.getFromPrefs(this, LoginDetailsUtils.USERNAME, ""));
            pass.setText(LoginDetailsUtils.getFromPrefs(this, LoginDetailsUtils.PASSWORD, ""));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    public void clickLogin(View v) {
        if (this.host.getText().toString().isEmpty()) {
            makeToast("Please enter your server hostname");
        } else if (this.port.getText().toString().isEmpty()) {
            makeToast("Please enter your server port");
        } else if (this.user.getText().toString().isEmpty()) {
            makeToast("Please enter your username");
        } else if (this.pass.getText().toString().isEmpty()) {
            makeToast("Please enter your password");
        } else {
            if (remember.isChecked()) {
                LoginDetailsUtils.setRemembered(this, true);
                LoginDetailsUtils.saveStringToPrefs(this, LoginDetailsUtils.HOSTNAME, this.host.getText().toString());
                LoginDetailsUtils.saveStringToPrefs(this, LoginDetailsUtils.PORT, this.port.getText().toString());
                LoginDetailsUtils.saveStringToPrefs(this, LoginDetailsUtils.USERNAME, this.user.getText().toString());
                LoginDetailsUtils.saveStringToPrefs(this, LoginDetailsUtils.PASSWORD, this.pass.getText().toString());

            } else {
                LoginDetailsUtils.setRemembered(this, false);
            }

            //new ServerConnectionThread(host.getText().toString(), port.getText().toString(), user.getText().toString(), pass.getText().toString(), this).start();

            Intent intent = new Intent(this, ConsoleActivity.class);
            intent.putExtra(LoginDetailsUtils.HOSTNAME, host.getText().toString());
            intent.putExtra(LoginDetailsUtils.PORT, Integer.parseInt(port.getText().toString()));
            intent.putExtra(LoginDetailsUtils.USERNAME, user.getText().toString());
            intent.putExtra(LoginDetailsUtils.PASSWORD, pass.getText().toString());
            startActivity(intent);

        }
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

    public void makeToast(String s) {
        Context context = getApplicationContext();
        CharSequence text = s;

        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

    }

}
