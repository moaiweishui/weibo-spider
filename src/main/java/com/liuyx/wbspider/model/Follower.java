package com.liuyx.wbspider.model;

public class Follower {
    private String uid;
    private String nickname;
    private String introduction;
    private String from;
    private String href;

    public Follower() {
    }

    public Follower(String uid, String nickname, String introduction, String from) {
        this.uid = uid;
        this.nickname = nickname;
        this.introduction = introduction;
        this.from = from;
    }

    public Follower(String uid, String nickname, String introduction, String from, String href) {
        this.uid = uid;
        this.nickname = nickname;
        this.introduction = introduction;
        this.from = from;
        this.href = href;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    @Override
    public String toString() {
        return "Follower{" +
                "uid='" + uid + '\'' +
                ", nickname='" + nickname + '\'' +
                ", introduction='" + introduction + '\'' +
                ", from='" + from + '\'' +
                ", href='" + href + '\'' +
                '}';
    }
}
