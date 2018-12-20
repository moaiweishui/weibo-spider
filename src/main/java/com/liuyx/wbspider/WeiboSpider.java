package com.liuyx.wbspider;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.liuyx.wbspider.login.Account;
import com.liuyx.wbspider.login.SimulationLogin;
import com.liuyx.wbspider.model.Follower;
import com.liuyx.wbspider.parser.FollowPageParser;
import com.liuyx.wbspider.parser.FollowPageParserImpl;
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

import static com.liuyx.wbspider.util.HttpUtils.httpGet;

public class WeiboSpider {
    public static void run() {
        CookieStore loginCookieStore = SimulationLogin.getCookies("local");

        String uid = "";
//        String html = httpGet("https://weibo.com/" + uid + "/follow?rightmod=1&wvr=6", loginCookieStore);
//        System.out.println(html);

        String url = "https://weibo.com/p//myfollow?t=1&cfs=&Pl_Official_RelationMyfollow__95_page=2#Pl_Official_RelationMyfollow__95";
        String html = httpGet(url, loginCookieStore);
        FollowPageParser followPageParser = new FollowPageParserImpl();
        List<Follower> followers = followPageParser.parse(html);

//        System.out.println(followers.size());

        followers.forEach(System.out::println);

    }

    public static void main(String[] args) throws ScriptException, NoSuchMethodException {
        run();
//        String testStr = "<div class=\"markup_choose\"></div>\r\n </li>";
//        testStr = testStr.replaceAll("[\\r|\\n]", "");
//        System.out.println(testStr);
    }

}
