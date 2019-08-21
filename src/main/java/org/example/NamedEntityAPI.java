package org.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import json.GooRequestJSON;
import json.GooResponseJSON;
import net.arnx.jsonic.JSON;
import rikyu.Rikyu;
import rikyu.model.Sentence;

public class NamedEntityAPI {

	public static void main(String[] args) {
		Rikyu.init();
		Sentence sentence = Rikyu.analyze("プレゼンが不安です。");
		System.out.println(sentence.getPoint());
		sentence.getWordList().forEach(e->System.out.println(e.getReading() + ":" + e.getPoint() + ":" +e.getPos()));
	}

	static String analyzeNamedEntity(String sentence) throws Exception {

		String data = "apikey=DZZdKW0V7V635LrmVkDeGnKfn30VjNql&sentence=" + sentence;

		HttpURLConnection conn = null;

		URL url = new URL("https://api.a3rt.recruit-tech.co.jp/named_entity/v1/predict?" + data);
		conn = (HttpURLConnection) url.openConnection();
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setRequestMethod("GET");
		conn.setUseCaches(false);


		int rescode = conn.getResponseCode();
		if (rescode == HttpURLConnection.HTTP_OK) {
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
				StringBuilder buf = new StringBuilder();
				String line;

				while ((line = reader.readLine()) != null) {
					buf.append(line);
				}

				return buf.toString();
			}
		}
		else {
			return "";
		}

	}

	static String analyzeSingleKeywordByGoo(String sentence) {

		String keyword = "";

		try{
			keyword = NamedEntityAPI.analyzeByGoo(sentence);
		}catch(Exception e) {
			e.printStackTrace();
		}

		GooResponseJSON json = JSON.decode(keyword,GooResponseJSON.class);
		System.out.println(keyword);

		List<Map<String, Double>> keywords = json.getKeywords();

		String maxKey = "";
		Double maxScore = 0d;

		for(Map<String, Double> m:  keywords) {
			for(Map.Entry<String, Double> e : m.entrySet()) {
				if(e.getValue() > maxScore) {
					System.out.println(e.getKey() + ":" + e.getValue());
					maxScore = e.getValue();
					maxKey = e.getKey();
				}
			}
		}

		return maxKey;
	}

	static String analyzeByGoo(String sentence) throws Exception {

		HttpURLConnection conn = null;

		//URL url = new URL("https://labs.goo.ne.jp/api/morph");
		URL url = new URL("https://labs.goo.ne.jp/api/keyword");
		conn = (HttpURLConnection) url.openConnection();
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		conn.setUseCaches(false);
		conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");


		GooRequestJSON jsonObj = new GooRequestJSON();
		jsonObj.setApp_id("84b518082ade66aacf41a69f77b2263286aa4874da24f8985321f66899c2c70c");
		jsonObj.setBody(sentence);
		jsonObj.setTitle(sentence);


		//POST送信
        try (OutputStreamWriter dos = new OutputStreamWriter(conn.getOutputStream())) {
        	String json = JSON.encode(jsonObj);
        	System.out.println("req:" + json);
            dos.write(json);
        }

        conn.connect();


		int rescode = conn.getResponseCode();
		if (rescode == HttpURLConnection.HTTP_OK) {
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
				StringBuilder buf = new StringBuilder();
				String line;

				while ((line = reader.readLine()) != null) {
					buf.append(line);
				}

				return buf.toString();
			}
		}
		else {
			return "";
		}

	}
}
