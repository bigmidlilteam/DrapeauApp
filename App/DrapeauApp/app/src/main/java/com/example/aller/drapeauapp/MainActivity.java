package com.example.aller.drapeauapp;

import android.content.Intent;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import android.view.View;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;


import com.example.aller.drapeauapp.db.DBConnection;
import com.example.aller.drapeauapp.modele.Country;
import com.example.aller.drapeauapp.modele.Drapeau;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.picasso.Picasso;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import pl.droidsonroids.gif.GifImageButton;


public class MainActivity extends AppCompatActivity {


    // views
    private ProgressBar progressBar;
    private ImageView imageTouch;
    private GifImageButton gifImageButton;

    // Liste de Pays
    private List<Country> countryList;

    // DB
    private DBConnection connection;





/*
*###################################################################################################
####################################################################################################
--------------------------------------------METHODES------------------------------------------------
####################################################################################################
####################################################################################################
*/

    //Redefinition de la methode onCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ReplaceFont.replaceDefaultFont(this, "SERIF", "Champagne & Limousines.ttf");

        //Initialisation de la vue
        progressBar = (ProgressBar) findViewById(R.id.progressBarMain);

        gifImageButton = (GifImageButton) findViewById(R.id.gifImageView);

        //ImageView Touch clignotante.
        imageTouch = (ImageView) findViewById(R.id.imageViewTouch);
        imageTouch.setVisibility(View.INVISIBLE);
        clignoterTouchMe();

        //Conncetion a la base de donnees
        connection = new DBConnection(this);

        startWebservice();

    }

    public void loadFragmentActivity() {
        Intent intent = new Intent(MainActivity.this, FragmentsActivity.class);
        startActivity(intent);
    }

    private void startWebservice() {
        Webservice webservice = new Webservice();
        webservice.execute("http://www.geognos.com/api/en/countries/info/all.json");
        countryList = new ArrayList<>();
        int count = 0;
        try {
            countryList = webservice.get();
            for (Country c : countryList) {
                count++;
                Log.i("webservice", "doInBackground: " + count + " " + c.getName() + " " + c.getCode());
                // put country into db
                Drapeau drapeau = new Drapeau(c.getName(), c.getCode(), c.getFlagURL());
                connection.getDaoDrapeau().createIfNotExists(drapeau);
                // load image into cache
                Log.i("picasso", "onCreate: " + count + " " + Picasso.with(getBaseContext()).load(c.getFlagURL()).toString());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    //Methode pour faire clignoter l'image touchMe
    public void clignoterTouchMe() {
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fadein);
        fadeIn.reset();
        Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.fadeout);
        fadeOut.reset();
        ImageView iv = (ImageView) findViewById(R.id.imageViewTouch);
        iv.clearAnimation();
        iv.startAnimation(fadeIn);
        iv.startAnimation(fadeOut);
    }



    /*
    *###################################################################################################
    ####################################################################################################
    ----------------------------------PRIVATE CLASS WEB SERVICE-----------------------------------------
    ####################################################################################################
    ####################################################################################################
    */

    private class Webservice extends AsyncTask<String, String, List<Country>> {

        /*
            Here we get the JSON and convert it into a String, then we parse and retrieve informations we need
            to build a new Country object with Jackson lib. In the end this method return the list of all countries
            which were in JSON file.
        */
        @Override
        protected List<Country> doInBackground(String... params) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;
            List<Country> countryList = null;

            try {
                // Getting URL from parametres
                URL url = new URL(params[0]);
                // Establish a connection
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                // Get a stream from connection
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                // Convert stream into a String
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                // Here we have convert the JSON into a String
                String finalJson = buffer.toString();

                // Construct Object from JSON
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode actualObj = objectMapper.readTree(finalJson);
                // Get all country codes from JSON
                List<String> codeList = actualObj.findValuesAsText("iso2");
                countryList = new ArrayList<>();

                // Building the country List
                for (int i = 0; i < codeList.size(); i++) {
                    Country country = new Country();
                    String code = codeList.get(i);
                    String flagURLprefix = "http://www.geognos.com/api/en/countries/flag/";
                    String flagTypeExtension = ".png";

                    // Get name of a country with the code
                    JsonNode nameNode = actualObj.path("Results").path(code);
                    country.setName(nameNode.path("Name").asText());
                    country.setCode(code);
                    // Build the flag URL
                    country.setFlagURL(flagURLprefix + code + flagTypeExtension);

                    countryList.add(country);
                }

                return countryList;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            progressBar.setVisibility(View.VISIBLE);
            imageTouch.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onPostExecute(List<Country> s) {
            super.onPostExecute(s);

            progressBar.setVisibility(View.INVISIBLE);
            imageTouch.setVisibility(View.VISIBLE);

            gifImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loadFragmentActivity();
                }
            });
        }
    }

}//end.
