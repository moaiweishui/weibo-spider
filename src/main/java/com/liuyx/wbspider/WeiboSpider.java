package com.liuyx.wbspider;

import com.liuyx.wbspider.fetcher.FollowerFetcher;
import com.liuyx.wbspider.model.Follower;

import javax.script.ScriptException;
import java.util.*;

import static com.liuyx.wbspider.exporter.FollowExporter.exportSelfFollowersToCSV;

public class WeiboSpider {
    public static void run() {


    }

    public static void main(String[] args) throws ScriptException, NoSuchMethodException {
        FollowerFetcher followerFetcher = new FollowerFetcher();
        List<Follower> followers = followerFetcher.fetchSelfFollowers("");
        followers.forEach(System.out::println);
        System.out.println(followers.size());
        exportSelfFollowersToCSV(followers, System.getProperty("user.dir") + "\\out\\");

    }

}
