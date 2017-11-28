package com.example.akash.mindspacequiz;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import static com.example.akash.mindspacequiz.AddOrEditActivity.serveradd;

public class ExportResult extends AppCompatActivity implements View.OnClickListener {

    Button dateBtn, exportBtn;
    Spinner subjectsSpinner;
    ArrayList<String> data;
    ArrayAdapter<String> adapter;
    boolean dateSet = false;
    String quizDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.export);

        //ActionBar bar = getActionBar();
        //bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#5f9ea0")));

        subjectsSpinner = (Spinner) findViewById(R.id.spinner1);
        dateBtn = (Button) findViewById(R.id.button1);
        exportBtn = (Button) findViewById(R.id.button2);

        data = new ArrayList<String>();
        GetSubjects gs = new GetSubjects();
        gs.execute();

        adapter = new ArrayAdapter<String>(this, R.layout.row, data);
        subjectsSpinner.setAdapter(adapter);

        dateBtn.setOnClickListener(this);
        exportBtn.setOnClickListener(this);

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
                System.out.println(sb.toString());

                return sb.toString();

            } catch (Exception e) {
                return new String(
                        "Something went wrong!\nPlease try again later.");
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

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

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.button2:

                if (dateSet) {

                    Intent i1 = new Intent(this, ViewResult.class);
                    i1.putExtra("subject", subjectsSpinner.getSelectedItem().toString());
                    i1.putExtra("quizDay", quizDay);
                    startActivity(i1);
                } else {

                }
                break;
            case R.id.button1:



                DatePickerDialog dateDialog = new DatePickerDialog(this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker arg0, int arg1,
                                                  int arg2, int arg3) {

                                dateSet = true;

                                if (arg3<10) {
                                    quizDay = arg1+"-"+(arg2+1)+ "-" + "0"+arg3  ;
                                    dateBtn.setText(arg1+"-"+(arg2+1)+ "-" + "0"+arg3);
                                } else {
                                    quizDay = arg1+"-"+(arg2+1)+ "-" +arg3;
                                    dateBtn.setText(arg1+"-"+(arg2+1)+ "-"+arg3);
                                }

                            }
                        }, 2017, 10, 17);
                dateDialog.show();

                break;
        }
    }
}
