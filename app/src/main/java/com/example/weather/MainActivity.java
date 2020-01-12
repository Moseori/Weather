package com.example.weather;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
//42

public class MainActivity extends AppCompatActivity {
    float current_temp = 0, wind_chill;
    int humid;
    private String[] url = {"http://www.weather.go.kr/weather/observation/currentweather.jsp", "http://aqicn.org/city/seoul/kr/"};
    TextView tv_temp, tv_wind_chill, tv_humid, tv_find_dust, tv_particle;
    Elements elements;

    int place =0;
    int[] particle = {0, 0};
    LinearLayout linearLayout;

    GradientDrawable gradientDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_temp = findViewById(R.id.tv_temp);
        tv_humid = findViewById(R.id.tv_humid);
        tv_wind_chill = findViewById(R.id.tv_wind_chill);
        tv_particle = findViewById(R.id.tv_particle);
        tv_find_dust = findViewById(R.id.tv_fine_dust);
        linearLayout = findViewById(R.id.layout_main);
        JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask();
        jsoupAsyncTask.execute();
        findViewById(R.id.tv_country).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask();
                jsoupAsyncTask.execute();

                show();
            }
        });





    }

    private class JsoupAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                Connection.Response execute = Jsoup.connect(url[0]).execute();
                Document doc = Jsoup.parse(execute.body());

                switch (place){
                    case 1:
                        elements = doc.getElementsByClass("table_develop3").get(0).getElementsByTag("tr").get(43).getAllElements().get(0).getElementsByTag("td");
                        break;
                    case 2:
                        elements = doc.getElementsByClass("table_develop3").get(0).getElementsByTag("tr").get(33).getAllElements().get(0).getElementsByTag("td");
                        break;

                }

                elements = doc.getElementsByClass("table_develop3").get(0).getElementsByTag("tr").get(43).getAllElements().get(0).getElementsByTag("td");

                Connection.Response p_execute = Jsoup.connect(url[1]).execute();
                Document p_doc = Jsoup.parse(p_execute.body());

                particle[0] = Integer.parseInt(p_doc.select("#aqiwgtvalue").text());
                particle[1] = Integer.parseInt(p_doc.select("#cur_pm10").text());
                Log.e("wow", "doInBackground: "+ p_doc.select("#aqiwgtvalue").text());

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.e("asd", "onPostExecute: asd" );
            if (elements != null) {
                Log.e("asd", "onPostExecute: asd "+elements.get(0) );
                current_temp = Float.parseFloat(elements.get(5).text());
                wind_chill = Float.parseFloat(elements.get(7).text());
                humid = Integer.parseInt(elements.get(10).text());
                Log.e("getResult", current_temp + " " + wind_chill + " " + humid);
                if (current_temp != 0) {
                    tv_temp.setText(current_temp + "℃");
                }
                if(wind_chill != 0){
                    tv_wind_chill.setText("체감온도 "+wind_chill+"");
                }
                if(humid !=0){
                    tv_humid.setText("습도 "+humid+"%");
                }
                tv_find_dust.setText(particle[1]+"");
                tv_particle.setText(particle[0]+"");

                //나쁨
                if (particle[0] >150){



                } else if (particle[0] > 80){  //보통

                    GradientDrawable g = new GradientDrawable(GradientDrawable.Orientation.TL_BR, new int[] {Color.BLUE,Color.GREEN});
                    g.setShape(GradientDrawable.RECTANGLE);
                    g.setCornerRadius(5);
                    linearLayout.setBackgroundDrawable(g);




                } else if (particle[0] > 30){ //양호




                }else{

                }
                if (particle[1] >75){ //나쁨

                } else if (particle[1] > 35){ //보통

                } else if (particle[1] > 15){ //양호

                }else{

                }
            }
        }
    }

    void show(){
        final List<String> ListItems = new ArrayList<>();
        ListItems.add("서울");
        ListItems.add("부산");
        ListItems.add("대구");
        ListItems.add("ㅁ몰");
        final CharSequence[] items =  ListItems.toArray(new String[ ListItems.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("AlertDialog Title");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int pos) {
                String selectedText = items[pos].toString();

                switch (selectedText){
                    case "서울":
                        place =1;
                        break;

                    case "부산":
                        place =2;
                        break;
                }

            }
        });
        builder.show();
    }

}

