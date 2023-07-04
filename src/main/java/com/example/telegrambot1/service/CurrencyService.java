package com.example.telegrambot1.service;

import com.example.telegrambot1.model.CurrencyModel;
import com.fasterxml.jackson.databind.util.JSONPObject;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpRequest;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
public class CurrencyService {

    public static Double getCurrencyRate(String cur) throws IOException {
        CurrencyModel model = new CurrencyModel();
        JSONObject object = getJSON();
        try {
            object = object
                    .getJSONObject("Valute")
                    .getJSONObject(cur);
        } catch (JSONException e) {
            log.error("Неверный код валюты" + cur);
            return 0d;
        }

        model.setCur_name(object.getString("Name"));
        model.setCur_OfficialRate(object.getDouble("Value"));

        return model.getCur_OfficialRate();

    }

    private static JSONObject getJSON() throws IOException {
        URL url = new URL("https://www.cbr-xml-daily.ru/daily_json.js");
        Scanner scanner = new Scanner((InputStream) url.getContent());
        String result = "";

        while(scanner.hasNext()) {
            result += scanner.nextLine();
        }
        return new JSONObject(result);
    }

    public static List<CurrencyModel> getCurrencyNames() throws IOException {
        JSONObject object = getJSON().getJSONObject("Valute");
        List<CurrencyModel> rez = new ArrayList<>();
        Set<String> keySet = object.keySet();
        for (String key : keySet) {
            rez.add(CurrencyModel.builder()
                    .cur_Abbreviation(object.getJSONObject(key).getString("CharCode"))
                    .cur_name(object.getJSONObject(key).getString("Name"))
                    .cur_OfficialRate(object.getJSONObject(key).getDouble("Value"))
                    .build());
        }
        return rez;
    }
}
