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

public class WeiboSpider {
    public static void run() {
        List<Account> accounts = Account.genAccountFromText(new File(System.getProperty("user.dir") + "\\src\\main\\resources\\login.txt"));
        HttpClientContext loginHttpClientContext = SimulationLogin.login(accounts.get(0).getAccount(), accounts.get(0).getPassword());
        CookieStore loginCookieStore = loginHttpClientContext.getCookieStore();

        String uid = "";
        String html = httpGet("https://weibo.com/" + uid + "/follow?rightmod=1&wvr=6", loginCookieStore);
        Document doc = Jsoup.parse(html);
        for(Element element : doc.getElementsByTag("script")){
            if(element.html().contains("\"domid\":\"Pl_Official_RelationMyfollow")){

                System.out.println(element.html());

//                String htmlJson = element.toString().substring(16, element.toString().length() - 10);
//                Gson gson = new Gson();
//                Type type = new TypeToken<Map<String, String>>() {}.getType();
//                Map<String, String> map2 = gson.fromJson(htmlJson, type);
//                System.out.println(map2.get("html"));
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
    }

}
