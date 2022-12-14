package com.ppppppe.textplaceholder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class BaconRespond {

    private static String url = "https://baconipsum.com/api/?type=meat-and-filler&format=text&sentences=";

    public static String getSentences(int amount) throws IOException {

        URL obj;
        try {
            obj = new URL(url + amount);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Malformed URL ", e);
        }


        HttpURLConnection connection;
        BufferedReader in;
        try {
            connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestMethod("GET");
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        } catch (IOException e) {
            throw new ConnectException();
        }

        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }
}