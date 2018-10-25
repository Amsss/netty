package com.zhuzz.rpc;

/**
 * @description:
 * @author: zhuzz
 * @date: 2018-10-25 19:44
 */
public class HelloServiceImpl implements HelloService{

    @Override
    public String hello(String msg) {
        return msg != null ? msg + " -----> I am fine." : "I am fine.";
    }
}
