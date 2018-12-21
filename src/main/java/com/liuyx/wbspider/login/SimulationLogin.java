package com.liuyx.wbspider.login;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.util.*;

public class SimulationLogin {
    private final static String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36";

    private static Map<String, String> preLogin(String usernameBase64){
        CloseableHttpClient client = HttpClientBuilder.create().build();
        String preLoginUrl = "http://login.sina.com.cn/sso/prelogin.php" +
                "?entry=weibo&callback=sinaSSOController.preloginCallBack" +
                "&su=" + usernameBase64 +
                "&rsakt=mod&client=ssologin.js(v1.4.5)&_=" + new Date().getTime();
//        System.out.println("pre login url: " + preLoginUrl);
        HttpGet get = new HttpGet(preLoginUrl);
        String preLoginResultsStr = null;
        try {
            get.addHeader("User-Agent", USER_AGENT);
            get.addHeader("Referer", "https://weibo.com/");
            HttpResponse response = client.execute(get);
            preLoginResultsStr = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return getParaFromResult(preLoginResultsStr);
    }

    /**
     * 从新浪返回的结果字符串中获得参数
     *
     * @param result
     * @return
     */
    private static HashMap<String, String> getParaFromResult(String result) {
        HashMap<String, String> hm = new HashMap<String, String>();
        result = result.substring(result.indexOf("{") + 1, result.indexOf("}"));
        String[] r = result.split(",");
        String[] temp;
        for (int i = 0; i < r.length; i++) {
            temp = r[i].split(":");
            for (int j = 0; j < 2; j++) {
                if (temp[j].contains("\""))
                    temp[j] = temp[j].substring(1, temp[j].length() - 1);
            }
            hm.put(temp[0], temp[1]);
        }
        return hm;
    }

    public static HttpClientContext login(String account, String passwd) {
        // 创建cookie store的本地实例
        CookieStore cookieStore = new BasicCookieStore();
        // 创建HttpClient上下文
        HttpClientContext httpClientContext = HttpClientContext.create();
        httpClientContext.setCookieStore(cookieStore);
        CloseableHttpClient client = HttpClientBuilder.create()
                .setDefaultCookieStore(cookieStore)
                .build();

        try {
            // prelogin
            String encryptedAccount = LoginInfoEncryption.encryptAccountWithBase64(account);
            Map<String, String> params = preLogin(encryptedAccount);

            /********登录操作*********/
            HttpPost post = new HttpPost("http://login.sina.com.cn/sso/login.php?client=ssologin.js(v1.4.19)");

            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36");
            post.setHeader("Origin", "http://weibo.com");
            post.setHeader("Referer", "http://weibo.com/");
            post.setHeader("Content-Type", "application/x-www-form-urlencoded");

            post.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            post.setHeader("Accept-Language", "zh-cn,zh;q=0.5");
            post.setHeader("Accept-Charset", "GB2312,utf-8;q=0.7,*;q=0.7");

            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("entry", "weibo"));
            nvps.add(new BasicNameValuePair("gateway", "1"));
            nvps.add(new BasicNameValuePair("from", ""));
            nvps.add(new BasicNameValuePair("savestate", "7"));
            nvps.add(new BasicNameValuePair("useticket", "1"));
            nvps.add(new BasicNameValuePair("vsnf", "1"));

            nvps.add(new BasicNameValuePair("su", encryptedAccount));
            nvps.add(new BasicNameValuePair("service", "miniblog"));
            nvps.add(new BasicNameValuePair("servertime", params.get("servertime")));
            nvps.add(new BasicNameValuePair("nonce", params.get("nonce")));
            nvps.add(new BasicNameValuePair("pwencode", "rsa2"));
            nvps.add(new BasicNameValuePair("rsakv", params.get("rsakv")));
            nvps.add(new BasicNameValuePair("sp",
                    LoginInfoEncryption.encryptPasswordWithRSA2(passwd, params.get("servertime"), params.get("nonce"), params.get("pubkey")))
            );
            nvps.add(new BasicNameValuePair("sr", "1920*1080"));
            nvps.add(new BasicNameValuePair("encoding", "UTF-8"));
            nvps.add(new BasicNameValuePair("prelt", "139"));
//            nvps.add(new BasicNameValuePair("pagerefer", "http://i.firefoxchina.cn/old/"));
            nvps.add(new BasicNameValuePair("returntype", "META"));
            nvps.add(new BasicNameValuePair("url",
                    "http://weibo.com/ajaxlogin.php?framelogin=1&callback=parent.sinaSSOController.feedBackUrlCallBack"));

            post.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));

            HttpResponse response = client.execute(post);

            String entity = EntityUtils.toString(response.getEntity());

            if (entity.replace("\\", "").contains("\"retcode\":0")) {
                String url = entity.substring(entity.indexOf("https://passport.weibo.com/wbsso/login?"), entity.indexOf("code=0")+6 );
//                System.out.println(url);

                String strScr = "";
                String nick = "暂无";     //昵称

                // 获取到实际url进行连接
                HttpGet getMethod = new HttpGet(url);
                response = client.execute(getMethod, httpClientContext);
                entity = EntityUtils.toString(response.getEntity());

                nick = entity.substring(entity.indexOf("displayname") + 14,
                        entity.lastIndexOf("userdomain") - 3).trim();

                url = entity.substring(entity.indexOf("userdomain") + 13,
                        entity.lastIndexOf("\""));
                getMethod = new HttpGet("http://weibo.com/"+url);
                response = client.execute(getMethod);
                entity = EntityUtils.toString(response.getEntity());

//                System.out.println(entity);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return httpClientContext;
    }

    public static boolean dumpCookiesToLocal(CookieStore cookieStore){
        // System.getProperty("user.dir") + "\\src\\main\\resources\\login.txt"
        Long timestamp = new Date().getTime();
        String filename = "cookies_" + timestamp.toString();
        File file = new File(System.getProperty("user.dir") + "\\src\\main\\resources\\" + filename);
        boolean success = true;
        try{
            if(!file.exists()){
                file.createNewFile();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(cookieStore);
            objectOutputStream.close();
        } catch (IOException e) {
            success = false;
            e.printStackTrace();
        }

        return success;
    }

    public static CookieStore recoverCookiesFromLocal(File file){
        CookieStore cookieStore = null;
        try{
            FileInputStream fileInputStream = new FileInputStream(file);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            cookieStore = (CookieStore)  objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return cookieStore;
    }

    public static CookieStore getCookies(String type){
        CookieStore cookieStore = null;
        if("remote".equals(type)){
            List<Account> accounts = Account.genAccountFromText(new File(System.getProperty("user.dir") + "\\src\\main\\resources\\login.txt"));
            HttpClientContext loginHttpClientContext = SimulationLogin.login(accounts.get(0).getAccount(), accounts.get(0).getPassword());
            cookieStore = loginHttpClientContext.getCookieStore();
            if(dumpCookiesToLocal(cookieStore)){
                System.out.println("Dump cookies to local succeed");
            }
        }else if("local".equals(type)){
            cookieStore = recoverCookiesFromLocal(new File(System.getProperty("user.dir") + "\\src\\main\\resources\\cookies_1545398815421"));
        }
        return cookieStore;
    }

}
