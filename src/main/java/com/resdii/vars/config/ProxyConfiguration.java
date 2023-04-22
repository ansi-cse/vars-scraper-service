package com.resdii.vars.config;

import feign.Client;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;
import java.net.Proxy;

//@Configuration
public class ProxyConfiguration{

    private final String proxyHost = "127.0.0.1";
    private final int proxyPort = 8888;

//    @Bean
    public Client feignClient() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort)))
                .build();
        return new feign.okhttp.OkHttpClient(okHttpClient);
    }
}