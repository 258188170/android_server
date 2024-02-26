package com.card.myapplication;

import rxhttp.wrapper.annotation.DefaultDomain;
import rxhttp.wrapper.annotation.Domain;

public class Url {
    @DefaultDomain //通过该注解设置默认域名
    public static String BASE_URL = "http://127.0.0.1:9988";
    
    // name 参数在这会生成 setDomainToGoogleIfAbsent方法，可随意指定名称
    // className 参数在这会生成RxGoogleHttp类，可随意指定名称
//    @Domain(name = "Google", className = "Google")
//    public static String GOOGLE = "https://www.google.com";
}
