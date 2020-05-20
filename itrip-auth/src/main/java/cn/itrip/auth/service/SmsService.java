package cn.itrip.auth.service;

public interface SmsService {
    void send(String to, String templateId,String[]dates)throws Exception;
}
