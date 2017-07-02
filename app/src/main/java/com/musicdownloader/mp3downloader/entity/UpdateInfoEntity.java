package com.musicdownloader.mp3downloader.entity;

import java.util.ArrayList;

/**
 * Created by Hanh Nguyen on 6/30/2017.
 */

public class UpdateInfoEntity {
    /*"banner_ads":"",
    "interstitial_ads":"",
    "native_large_ads":"",
    "update":"true",
    "update_title":"Update de",
    "update_message":"Update nao",
    "update_cancelable":"true",
    "update_link":"https://play.google.com/store/apps/details?id=com.nexusupdate.android",
    "update_ok":"OK",
    "update_cancel":"Later"
    "yt":"true",
    "guide":"1. Click on the BROWSER FACEBOOK button at the bottom.\n2. Login to your Facebook account.\n3. Navigate to the page where the video you want to download is located.\n4. Click to the floating download button at the bottom right corner of screen.",
    "browse":"Browse Facebook"
    "country": ["VN","US"]
    */

    public String banner_ads;
    public String interstitial_ads;
    public String native_large_ads;
    public boolean update;
    public String update_title;
    public String update_message;
    public boolean update_cancelable;
    public String update_link;
    public String update_ok;
    public String update_cancel;
    public boolean yt;
    public String guide;
    public String browse;
    public ArrayList<String> country;
}
