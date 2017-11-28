package com.example.akash.mindspacequiz;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import static com.example.akash.mindspacequiz.AddOrEditActivity.serveradd;

public class ExpandableListAppActivity extends AppCompatActivity implements View.OnClickListener {

    DatabaseHelper helper;
    Button attempt;
    ConnectivityManager cm;
    NetworkInfo info;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mymain);

        //ActionBar bar = getActionBar();
        //bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#5f9ea0")));

        attempt = (Button) findViewById(R.id.button1);
        attempt.setOnClickListener(this);
        cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        helper = new DatabaseHelper(this, "quiz_database", null, 1000);
    }

    @Override
    public void onClick(View v) {
        info = cm.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            GetQuestions gq = new GetQuestions();
            gq.execute();
        } else {
            Toast.makeText(this,
                    "Please ensure you have internet connectivity",
                    Toast.LENGTH_SHORT).show();
        }
    }

    class GetQuestions extends AsyncTask<String, Void, String> {

        String myJSON, result;
        String tableName = "";

        @Override
        protected String doInBackground(String... params) {

            try {
                String link = "http://"+serveradd+"/codes/readQuestions.php";

                String data = URLEncoder.encode("subject", "UTF-8") + "="
                        + URLEncoder.encode(FormActivity.subject, "UTF-8");

                URL url = new URL(link);
                URLConnection conn = url.openConnection();

                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(
                        conn.getOutputStream());

                wr.write(data);
                wr.flush();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    break;
                }

                System.out.println("monitor: " + sb.toString());

                result = sb.toString();
                return sb.toString();
            } catch (Exception e) {
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            myJSON = result;
            System.out.println(myJSON);

            try {
                JSONObject obj = new JSONObject(myJSON);
                JSONArray array = obj.getJSONArray("result");
                JSONObject innerObj;

                helper.clearQuestions();

                for (int i = 0; i < array.length(); i++) {
                    innerObj = new JSONObject(array.get(i).toString());
                    helper.addQuestions(i,
                            innerObj.getString("question"),
                            innerObj.getString("optionA"),
                            innerObj.getString("optionB"),
                            innerObj.getString("optionC"),
                            innerObj.getString("optionD"),
                            innerObj.getString("correctAnswer"));
                }

                if (helper.getnumberOfQuestions() > 0) {

                    SimpleDateFormat time = new SimpleDateFormat("hh:mm:ss");
                    String startTime = time.format(new Date()).toString();

                    Intent i = new Intent(ExpandableListAppActivity.this,
                            QuizActivity.class);
                    i.putExtra("startTime", startTime);
                    startActivity(i);
                    finish();
                } else {

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
