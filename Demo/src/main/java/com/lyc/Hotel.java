package com.lyc;

import org.apache.solr.client.solrj.beans.Field;
//创建hotelbean
public class Hotel {
    //@Field进行注解装配。
    @Field
    private String id;
    //如果属性与数据库中的字段名称不相同可以在注解中指明字段属性
    @Field("hotelName")
    private String Name;
    @Field
    private String address;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Hotel{" +
                "id='" + id + '\'' +
                ", Name='" + Name + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}