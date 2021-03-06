package com.liuyx.wbspider.fetcher;

import com.liuyx.wbspider.login.SimulationLogin;
import com.liuyx.wbspider.model.Follower;
import com.liuyx.wbspider.parser.FollowPageParser;
import com.liuyx.wbspider.parser.FollowPageParserImpl;
import org.apache.http.client.CookieStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static com.liuyx.wbspider.util.HttpUtils.httpGet;

public class FollowerFetcher {
    private static final String SELF_FOLLOW_PAGE_URL_TEMPLATE = "https://weibo.com/p/%s/myfollow?t=1&cfs=&Pl_Official_RelationMyfollow__95_page=%d#Pl_Official_RelationMyfollow__95";

    private static Logger logger = LoggerFactory.getLogger(FollowerFetcher.class);

    public List<Follower> fetchSelfFollowers(String id){
        List<Follower> followers = new ArrayList<>();
        List<Follower> followersBuffer;

        FollowPageParser followPageParser = new FollowPageParserImpl();
        CookieStore loginCookieStore = SimulationLogin.getCookies("local");
        Long pageIndex = 1L;
        boolean doFetch = true;
        while(doFetch){
            logger.info("Fetch page {} ...", pageIndex);
            String fetchUrl = String.format(SELF_FOLLOW_PAGE_URL_TEMPLATE, id, pageIndex);
            followersBuffer = followPageParser.parse(httpGet(fetchUrl, loginCookieStore));
            if(followersBuffer.isEmpty()){
                logger.info("Done");
                doFetch = false;
            }else{
                followers.addAll(followersBuffer);
                pageIndex += 1;
            }
        }

        return followers;
    }
}
