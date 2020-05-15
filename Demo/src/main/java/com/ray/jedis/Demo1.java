package com.ray.jedis;

import redis.clients.jedis.Jedis;

/**
 * description:
 * Created by Ray on 2020-05-15
 */
public class Demo1 {
    public static void main(String[] args) {
        //创建jedis对象
        Jedis jedis = new Jedis("127.0.0.1",6379);
        //设置访问密码
        jedis.auth("123");
        //基础操作
//        jedis.set("hello", "redis");
        String name = jedis.get("name");//查不到返回null
        System.out.println("name="+name);
        Boolean exists = jedis.exists("name");
        System.out.println("exists="+exists);
        String ping = jedis.ping();
        System.out.println(ping);
        jedis.expire("name", 10);
        Long ttl = jedis.ttl("name");
        System.out.println(ttl);

    }

}
