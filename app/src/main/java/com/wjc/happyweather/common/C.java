package com.wjc.happyweather.common;

import com.wjc.happyweather.BuildConfig;
import com.wjc.happyweather.base.BaseApplication;

import java.io.File;

/**
 * Package_NAME : com.wjc.happyweather.common
 * File_NAME : C
 * Created by WJC on 2017/12/14 12:59
 * Describe : TODO
 */

public class C {
    public static final String API_TOKEN = BuildConfig.FirToken;
    public static final String KEY = BuildConfig.WeatherKey;// 和风天气 key

    public static final String MULTI_CHECK = "multi_check";

    public static final String ORM_NAME = "cities.db";

    public static final String UNKNOWN_CITY = "unknown city";

    public static final String NET_CACHE = BaseApplication.getAppCacheDir() + File.separator + "NetCache";
}
