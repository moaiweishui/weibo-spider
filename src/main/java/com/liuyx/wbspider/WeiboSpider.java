package com.liuyx.wbspider;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.liuyx.wbspider.login.Account;
import com.liuyx.wbspider.login.SimulationLogin;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import sun.misc.IOUtils;

import javax.script.ScriptException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WeiboSpider {
    public static void run() {
        CookieStore loginCookieStore = SimulationLogin.getCookies("local");

        String uid = "";
        String html = httpGet("https://weibo.com/" + uid + "/follow?rightmod=1&wvr=6", loginCookieStore);
//        System.out.println(html);
        Document doc = Jsoup.parse(html);
        for(Element element : doc.getElementsByTag("script")){
            if(element.html().contains("\"domid\":\"Pl_Official_RelationMyfollow")){
                Pattern pattern = Pattern.compile("FM.view\\(.*\"html\":\"(.*)\\}\\)");
                Matcher m = pattern.matcher(element.html());
                if(m.find()){
                    String htmlJson = m.group(1);
                    System.out.println(htmlJson);
//                    htmlJson = htmlJson.replaceAll("[\\\\t|\\\\n|\\\\r]", "");
                    htmlJson = htmlJson.replaceAll("\\\\", "");
//                    System.out.println(htmlJson);
                    Document followDoc = Jsoup.parse(htmlJson);
                    System.out.println(followDoc.toString());
                    for(Element member : followDoc.getElementsByClass("member_li")){
                        System.out.println(member.toString());
                        System.out.println("\n\n");
                    }
//                    Gson gson = new Gson();
//                    Type type = new TypeToken<Map<String, String>>() {}.getType();
//                    Map<String, String> map2 = gson.fromJson(htmlJson, type);
//                    System.out.println(map2.get("html"));
                }


            }
        }


    }

    private static String dump(HttpEntity entity) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(
                entity.getContent(), "utf8"));
        StringBuilder content = new StringBuilder();
        try{
            String line = null;
            while((line = br.readLine()) != null){//使用readLine方法，一次读一行
                content.append(line);
            }
            br.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return content.toString();
    }

    private static String httpGet(String url, CookieStore cookieStore) {
//        // 创建HttpClient上下文
//        HttpClientContext httpClientContext = HttpClientContext.create();
//        httpClientContext.setCookieStore(cookieStore);
//        HttpResponse response = null;
        String html = null;
        CloseableHttpClient client = HttpClientBuilder.create()
                .setDefaultCookieStore(cookieStore)
                .build();
        try{
            HttpGet get = new HttpGet(url);
            HttpResponse response = client.execute(get);
            html = dump(response.getEntity());
            get.abort();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return html;
    }

    public static void main(String[] args) throws ScriptException, NoSuchMethodException {
        run();
//        String testStr = "<div class=\"markup_choose\"></div>\r\n </li>";
//        testStr = testStr.replaceAll("[\\r|\\n]", "");
//        System.out.println(testStr);
    }

}
