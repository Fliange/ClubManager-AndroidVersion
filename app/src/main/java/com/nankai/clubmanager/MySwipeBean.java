package com.nankai.clubmanager;

/**
 * Created by Administrator on 2017/8/11.
 */

public class MySwipeBean {

    Integer id;
    String content;//名字
    String org;
    Integer count;
    String extra;
    boolean isTag;

    public MySwipeBean(Integer id,String content,String org,Integer count,String extra, boolean isTag) {
        this.id=id;
        this.content = content;
        this.org=org;
        this.count=count;
        this.extra=extra;
        this.isTag = isTag;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isTag() {
        return isTag;
    }

    public void setTag(boolean isTag) {
        this.isTag = isTag;
    }

}