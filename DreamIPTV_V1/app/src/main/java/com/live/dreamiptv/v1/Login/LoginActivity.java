package com.live.dreamiptv.v1.Login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.live.dreamiptv.v1.Home.HomeActivity;
import com.live.dreamiptv.v1.Network.AppController;
import com.live.dreamiptv.v1.R;
import com.live.dreamiptv.v1.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private Boolean mSaveLogin;
    private EditText mUserName;
    private EditText mPassWord;
    private Button mLoginBtn;
    private CheckBox mSaveLoginCheckBox;
    private SharedPreferences.Editor mLoginPrefsEditor;
    private static final String SAVE_LOGIN_TEXT = "mSaveLogin";
    private static final String LOGIN_PREFS_TEXT = "loginPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mUserName = (EditText) findViewById(R.id.userName);
        mPassWord = (EditText) findViewById(R.id.userPass);
        mSaveLoginCheckBox = (CheckBox) findViewById(R.id.checkConfirm);
        SharedPreferences loginPreferences = getSharedPreferences(LOGIN_PREFS_TEXT, MODE_PRIVATE);
        mLoginPrefsEditor = loginPreferences.edit();
        mSaveLogin = loginPreferences.getBoolean(SAVE_LOGIN_TEXT, false);
        if (mSaveLogin) {
            /*mUserName.setText(loginPreferences.getString("username", ""));
            mPassWord.setText(loginPreferences.getString("password", ""));
            mSaveLoginCheckBox.setChecked(true);*/
            openHomeActivity();
        }

        mLoginBtn = (Button) findViewById(R.id.buttonLogin);
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                String user = mUserName.getText().toString();
                String pass = mPassWord.getText().toString();
                final String finalString = Utilities.URL + Utilities.USERNAME_TEXT + Utilities.EQUAL_SIGN + user + Utilities.AND_SIGN + Utilities.PASSWORD_TEXT + Utilities.EQUAL_SIGN + pass;
                Log.i("link", finalString);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, finalString, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject object = response.getJSONObject(Utilities.USER_INFO_TEXT);
                            int loginAuthentication = object.getInt(Utilities.AUTH_TEXT);
                            if (loginAuthentication == 0) {
                                Utilities.showShortToast(LoginActivity.this, "Username or Password Error");
                            } else {
                                String user = object.getString(Utilities.USERNAME_TEXT);
                                String pass = object.getString(Utilities.PASSWORD_TEXT);
                                String usernameInput = mUserName.getText().toString();
                                String passwordInput = mPassWord.getText().toString();
                                if (usernameInput.equals(user) && (passwordInput.equals(pass) && (loginAuthentication == 1)) && v == mLoginBtn) {
                                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(mUserName.getWindowToken(), 0);
                                    if (mSaveLoginCheckBox.isChecked()) {
                                        mLoginPrefsEditor.putBoolean(SAVE_LOGIN_TEXT, true);
                                        mLoginPrefsEditor.putString(Utilities.USERNAME_TEXT, usernameInput);
                                        mLoginPrefsEditor.putString(Utilities.PASSWORD_TEXT, passwordInput);
                                        mLoginPrefsEditor.apply();
                                    } else {
                                        mLoginPrefsEditor.clear();
                                        mLoginPrefsEditor.apply();
                                    }
                                    SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = pref.edit();
                                    editor.putString(Utilities.USERNAME_TEXT, usernameInput);
                                    editor.putString(Utilities.PASSWORD_TEXT, passwordInput);
                                    editor.apply();
                                    Utilities.showShortToast(LoginActivity.this, "Congratulations..Login is Successful");
                                    openHomeActivity();
                                } else {
//                                Utilities.showShortToast(LoginActivity.this, "auth = 0 \n User Does not exit");
                                    Utilities.showShortToast(LoginActivity.this, "Username or Password Error");
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Utilities.showShortToast(LoginActivity.this, Utilities.ON_RESPONSE_ERROR_TEXT);
                    }
                });
                AppController.getInstances().addToRequestQueue(jsonObjectRequest);
            }
        });
    }

    private void openHomeActivity() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}