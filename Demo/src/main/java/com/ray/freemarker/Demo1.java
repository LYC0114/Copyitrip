package com.ray.freemarker;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * description:
 * Created by Ray on 2020-05-14
 */
public class Demo1 {
    public static void main(String[] args) throws IOException, TemplateException {
        //1.创建配置对象，配置上下文环境
        Configuration conf = new Configuration(Configuration.getVersion());
        //2.设置模板所在的位置（目录）
       conf.setDirectoryForTemplateLoading(new File(Thread.currentThread().getContextClassLoader().getResource("").getPath()));
        //3.获取模板对象
        Template template = conf.getTemplate("hello.ftl");
        //4.创建数据
        User user=new User();
        user.setName("jerry");
        user.setPassword("123");
        User user1=new User();
        user1.setName("jack");
        user1.setPassword("222");
        User user2=new User();
        user2.setName("rose");
        user2.setPassword("333");
        User user3=new User();
        user3.setName("");
        user3.setPassword("444");
        List<User> list = new ArrayList<User>();
        list.add(user);
        list.add(user1);
        list.add(user2);
        list.add(user3);
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("name", "tom123");
        data.put("user", user);
        data.put("users", list);
//        data.put("field","username");
        //5.合成-生成目标文档
        template.process(data,new FileWriter(new File("hello.html")));
    }
}
