package com.liuyx.wbspider.parser;

import com.liuyx.wbspider.model.Follower;

import java.util.List;

public interface FollowPageParser {
    List<Follower> parse(String pageTextContent);

    Long getPageCount(String pageTextContent);
}
