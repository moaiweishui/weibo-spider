package com.liuyx.wbspider.parser;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.liuyx.wbspider.model.Follower;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.reflect.Type;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FollowPageParserImpl implements FollowPageParser {
    private static Pattern FM_VIEW_PATTERN = Pattern.compile("FM.view\\((.*)\\)");

    @Override
    public List<Follower> parse(String pageTextContent) {
        Document page = Jsoup.parse(pageTextContent);
        List<Map<String, Object>> fmViewList = new ArrayList<>();
        for(Element element : page.getElementsByTag("script")){
            Matcher fmViewMatcher = FM_VIEW_PATTERN.matcher(element.toString());
            if(fmViewMatcher.find()){
                Gson gson = new Gson();
                Type type = new TypeToken<Map<String, Object>>(){}.getType();
                fmViewList.add(gson.fromJson(fmViewMatcher.group(1), type));
            }
        }
        Document followHtmlSegment = null;
        for(Map<String, Object> fmView : fmViewList){
            if(fmView.containsKey("domid") && fmView.get("domid").equals("Pl_Official_RelationMyfollow__95")){
                followHtmlSegment = Jsoup.parse((String)fmView.get("html"));
            }
        }

        List<Follower> followers = new ArrayList<>();
        if(followHtmlSegment != null){
            for(Element member : followHtmlSegment.getElementsByClass("member_li")){
                Element titleElement = null;
                Element textElement = null;
                Element infoFromElement = null;
                try{
                    titleElement = member.getElementsByClass("title").first()
                            .getElementsByClass("S_txt1").first();
                    textElement = member.getElementsByClass("text").first();
                    infoFromElement = member.getElementsByClass("info_from").first()
                            .getElementsByClass("S_link2").first();
                } catch (Exception e){
                    e.printStackTrace();
                }

                if(titleElement != null){
                    String uid = titleElement.attr("usercard");
                    String nickname = titleElement.attr("title");
                    String href = titleElement.attr("href");

                    String introduction = textElement != null ? textElement.text() : null;
                    String from = infoFromElement != null ? infoFromElement.text() : null;

                    if(nickname != null && uid != null){
                        followers.add(new Follower(uid, nickname, introduction, from, href));
                    }
                }
            }
        }

        return followers;
    }

    @Override
    public Long getPageCount(String pageTextContent) {
        Document page = Jsoup.parse(pageTextContent);
        System.out.println(page.toString());
        return null;
    }

//    public static String isReplaceStr(String str_content) {
//        String r = "";
//        Pattern p = Pattern.compile("\\s*|\t|\r|\n");
//        Matcher m = p.matcher(str_content);
//        r = m.replaceAll("");
//        return r;
//    }

}
