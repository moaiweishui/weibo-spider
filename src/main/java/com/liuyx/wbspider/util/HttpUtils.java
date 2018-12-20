package com.liuyx.wbspider.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class HttpUtils {

    public static String httpGet(String url, CookieStore cookieStore) {
        String result = null;
        CloseableHttpClient client = HttpClientBuilder.create()
                .setDefaultCookieStore(cookieStore)
                .build();
        try{
            HttpGet get = new HttpGet(url);
            HttpResponse response = client.execute(get);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                result = EntityUtils.toString(entity, "utf-8");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

//    private static String dump(HttpEntity entity) throws IOException {
//        BufferedReader br = new BufferedReader(new InputStreamReader(
//                entity.getContent(), "utf8"));
//        StringBuilder content = new StringBuilder();
//        try{
//            String line = null;
//            while((line = br.readLine()) != null){//使用readLine方法，一次读一行
//                content.append(line);
//            }
//            br.close();
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//        return content.toString();
//    }
}
