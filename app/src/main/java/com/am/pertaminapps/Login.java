package com.am.pertaminapps;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.am.pertaminapps.AppController.AppController;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editorPreferences;

    private EditText etUsername, etPassword;
    private TextView tvNotifikasi;
    private Button btnLogin;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sharedPreferences = getSharedPreferences(getResources().getString(R.string.sharedpreferences), 0);
        editorPreferences = sharedPreferences.edit();
        url = getResources().getString(R.string.api_endpoint).concat(getResources().getString(R.string.api_login));
        tvNotifikasi = (TextView)findViewById(R.id.tv_notifikasi);
        etUsername = (EditText)findViewById(R.id.et_username);
        etPassword = (EditText)findViewById(R.id.et_password);
        btnLogin = (Button)findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doLogin(etUsername.getText().toString(),etPassword.getText().toString());
            }
        });
    }

    private void doLogin(String username,String password) {
        String tag_login = "request_login";
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading . . . ");
        pDialog.show();
        final Map<String, String> params = new HashMap<String, String>();
        params.put("username", username);
        params.put("password", password);
        JSONObject parameter = new JSONObject(params);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, parameter,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    pDialog.dismiss();
                    checkResponse(response);
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    pDialog.dismiss();
                    tvNotifikasi.setVisibility(View.VISIBLE);
                    tvNotifikasi.setText("Error : " + error.getMessage());
                    tvNotifikasi.setBackgroundColor(getResources().getColor(R.color.backgroundDanger));
                }
            });
        AppController.getInstance().addToRequestQueue(jsonObjectRequest, tag_login);
    }

    public void checkResponse(JSONObject response){
        tvNotifikasi.setVisibility(View.VISIBLE);
        try{
            tvNotifikasi.setText(response.getString("message"));
            if(response.getString("severity").equals("success")){
                tvNotifikasi.setBackgroundColor(getResources().getColor(R.color.backgroundSuccess));
                Intent intent = null;
                JSONArray jsonArray = response.getJSONArray("data");
                JSONObject objectUser = jsonArray.getJSONObject(0);
                editorPreferences.putString("pref_user_id",objectUser.getString("id"));
                editorPreferences.putString("pref_user_username",objectUser.getString("username"));
                editorPreferences.putString("pref_user_password",objectUser.getString("password"));
                editorPreferences.putString("pref_user_full_name",objectUser.getString("user_full_name"));
                editorPreferences.putString("pref_user_dob",objectUser.getString("dob"));
                editorPreferences.commit();
                if(objectUser.getString("user_type").equals("1")){
                    intent = new Intent(Login.this,DealerAdmin.class);
                }else
                if(objectUser.getString("user_type").equals("2")){
                    intent = new Intent(Login.this,UserCostumer.class);
                }
                startActivity(intent);
                finish();
            }else
            if(response.getString("severity").equals("warning")){
                tvNotifikasi.setBackgroundColor(getResources().getColor(R.color.backgroundWarning));
            }else
            if(response.getString("severity").equals("danger")){
                tvNotifikasi.setBackgroundColor(getResources().getColor(R.color.backgroundDanger));
            }
        }catch (JSONException e){
            tvNotifikasi.setBackgroundColor(getResources().getColor(R.color.backgroundDanger));
            tvNotifikasi.setText(e.getMessage().toString());
        }
    }

    @Override
    public  void onBackPressed(){
        super.onBackPressed();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}
