package cn.itrip.common;

/***
 * 常量类 放置一些常量
 */
public class Constants {

    //默认起始页
    public static final Integer DEFAULT_PAGE_NO = 1;
    //默认页大小
    public static final Integer DEFAULT_PAGE_SIZE = 10;

    //手机注册验证码前缀
    public static final String POHONE_SMS_ACTIVE_PREFIX="activation:";
    //token前缀
    public static final String TOKEN_PRIFIX="token:";
    //token非移动端的有效期
    public static final Integer TOKEN_EXPIRE = 2;
    //token保护期
    public static final Integer TOKEN_PROTECT_TIME = 1;//小时
}
