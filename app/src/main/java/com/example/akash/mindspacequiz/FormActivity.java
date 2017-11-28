package com.example.akash.mindspacequiz;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import static com.example.akash.mindspacequiz.AddOrEditActivity.serveradd;

public class FormActivity extends AppCompatActivity implements View.OnClickListener {

    EditText nameBox, numberBox;
    Spinner dropdown;
    Button submitBtn;
    TextView formTitle;
    AlertDialog.Builder builder;
    AlertDialog alert;
    static String name, number, subject;
    String passwordSetByAdmin = "";
    ArrayList<String> data;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.form);

        //ActionBar bar = getActionBar();
        //bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#5f9ea0")));

        formTitle = (TextView) findViewById(R.id.textView1);
        nameBox = (EditText) findViewById(R.id.editText1);
        numberBox = (EditText) findViewById(R.id.editText2);
        dropdown = (Spinner) findViewById(R.id.spinner1);
        submitBtn = (Button) findViewById(R.id.button1);
        submitBtn.setOnClickListener(this);

        data = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
        dropdown.setAdapter(adapter);

        FetchPassword fp = new FetchPassword();
        fp.execute();

        GetSubjects gs = new GetSubjects();
        gs.execute();
    }

    @Override
    public void onClick(View v) {

        FormActivity.name = nameBox.getText().toString();
        FormActivity.number = numberBox.getText().toString();
        FormActivity.subject = dropdown.getSelectedItem().toString();

        if (!name.equals("")) {
            if (!number.equals("") && number.length() == 10) {

                AlertDialog.Builder a_builder = new AlertDialog.Builder(this);
                a_builder.setTitle("Authentication");

                View dialogBox = getLayoutInflater().inflate(
                        R.layout.custom_dialog, null);

                a_builder.setView(dialogBox);

                final EditText box = (EditText) dialogBox.findViewById(R.id.editText2);

                a_builder.setPositiveButton("Login",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {

                                if (box.getText().toString().equals(passwordSetByAdmin)) {
                                    arg0.dismiss();
                                    Intent i = new Intent(FormActivity.this, ExpandableListAppActivity.class);
                                    startActivity(i);
                                } else {
                                    Toast.makeText(FormActivity.this, "Login failed!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                a_builder.setNegativeButton("Back",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                arg0.dismiss();
                            }
                        });

                AlertDialog alert = a_builder.create();
                alert.show();

            } else {
                Toast.makeText(this, "Enter Number", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Enter Name", Toast.LENGTH_SHORT).show();
        }
    }

    class FetchPassword extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... arg0) {

            try {

                String link = "http://"+serveradd+"/codes/authenticator.php";

                URL url = new URL(link);
                URLConnection conn = url.openConnection();

                conn.setDoOutput(true);

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    break;
                }
                passwordSetByAdmin = sb.toString();
                return sb.toString();
            } catch (Exception e) {
                return new String("Something went wrong!\nPlease try again later.");
            }
        }
    }

  class GetSubjects extends AsyncTask<Void, Void, String> {

        String subjects;

        @Override
        protected String doInBackground(Void... arg0) {

            try {

                String link = "http://"+serveradd+"/codes/getSubjects.php";

                URL url = new URL(link);
                URLConnection conn = url.openConnection();

                conn.setDoOutput(true);

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                    break;
                }
                subjects = sb.toString();
                Log.i("doinform",sb.toString());
                return sb.toString();
            } catch (Exception e) {
                return new String(
                        "Something went wrong!\nPlease try again later.");
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.i("onpostForm",result);
            try {
                JSONObject obj = new JSONObject(subjects);
                JSONArray array = obj.getJSONArray("result");
                JSONObject innerObj;

                for (int i = 0; i < array.length(); i++) {
                    innerObj = new JSONObject(array.get(i).toString());
                    data.add(innerObj.getString("name"));
                }
                adapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
