package cn.itrip.auth.controller;

import cn.itrip.auth.AuthException;
import cn.itrip.auth.service.TokenService;
import cn.itrip.auth.service.UserService;
import cn.itrip.beans.dto.Dto;
import cn.itrip.beans.pojo.ItripUser;
import cn.itrip.beans.vo.ItripTokenVO;
import cn.itrip.beans.vo.ItripWechatTokenVO;
import cn.itrip.common.DtoUtil;
import cn.itrip.common.UrlUtils;
import com.alibaba.fastjson.JSON;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Map;

import static com.alibaba.fastjson.JSON.*;

//微信登陆验证
@RestController
@RequestMapping("/")
public class VendorsController {
    @Resource
    private TokenService tokenService;
    @Resource
    private UserService userService;
    private String Map;

    @GetMapping("/wechat/login")
    public void wechatLogin(HttpServletResponse response) throws Exception {
        String appid="wx9168f76f000a0d4c";
        //redirecturi是注册时在微信开放平台上填写的回调地址，本质上就是域名加上接口
        String redirecturi="https://open.weixin.qq.com/connect/qrconnect";
        String url=" https://open.weixin.qq.com/connect/qrconnect?" +
                "appid="+appid +
                //回调地址中同样也含有https://这样的形式多以运行时容易出错通过URLEncoder中encode方法编译后，避免出错
                "&redirect_ uri= "+ URLEncoder.encode(redirecturi,"UTF-8") +
                "&response_ type=code" +
                "&scope=SCOPE" +
                "&state=STATE#wechat_ re";
        //从定向发送请求
        response.sendRedirect(url);
    }
    /*若用户同意授权后会带上code和state
    * 若用户不同意授权则只会带上state，没有code*/
    @RequestMapping("/wechat/callback")
    public Dto callBack(String code, String state, HttpServletRequest request) throws Exception {
        //当用户同意后需要带上code，state，和appid三个参数，请求规定的url地址换取access-token
        //appid和screat是在微信开放平台上注册成功后会生成的
        String screat="";
        String url = "https://api.weixin.qq.com/sns/oauth2/access_ token?" +
                "appid=APPID" +
                "&secret=SECRET" +
                "&code CODE" +
                //授权类型固定不变的
                "&grant_ _type= authorization_ code" ;
        String json = UrlUtils.loadURL(url);
        //将json转换成Map数组并取出json中的数据。
        Map map = parseObject(json, Map.class);
        //获取access_token由于已知access_token为String类型所以之接将Object类型转为String
        String access_token = (String) map.get("access-token");
        if(access_token==null){
            throw new AuthException("授权失败");
        }
        //授权成功-》创建用户记录到数据库
        String openid=(String) map.get("openid");
        ItripUser itripUser = userService.getItripUserByUserCode((openid));
        if(itripUser==null){//判断是否为第一次登录
            //若是第一次登陆则创建一个itripUser存入数据库
            itripUser = new ItripUser();
            itripUser.setUserCode((String) map.get("openid"));
            itripUser.setUserType(1);
            itripUser.setCreationDate(new Date());
            userService.itriptxCreateItripUser(itripUser);
        }
        //创建客户端在本站的token作为登录凭证
        ItripTokenVO tokenVO = tokenService.processToken(request.getHeader("User-Agent"),itripUser);
        //放回微信的token
        //把两个token返回客户端
        //创建一个微信的token，并将请求得到的accesstoken，生成时间，有效时间，传进微信的token，并将两个token一同返回
        ItripWechatTokenVO WechatTokenVO = new ItripWechatTokenVO(tokenVO.getToken(), tokenVO.getExpTime(), tokenVO.getGenTime());
        WechatTokenVO.setAccessToken(access_token);
        WechatTokenVO.setExpiresIn(String.valueOf(map.get("expires_in")));
        WechatTokenVO.setOpenid(openid);
        WechatTokenVO.setRefreshToken((String) map.get("refresh_token"));
        return DtoUtil.returnDataSuccess(WechatTokenVO);
    }

//请求成功后
    @GetMapping("/wechat/user/info")
    public Dto getUserInfo(String accessToken,String openid) throws Exception {
        String url ="https://api.weixin.qq.com/sns/userinfo?" +
                "access_token="+accessToken +
                "&openid="+openid;
        String  json= UrlUtils.loadURL(url);
        java.util.Map map = parseObject(json, Map.class);
        String nickname = (String) map.get("nickname");
        ItripUser user= userService.getItripUserByUserCode(openid);
        if(user==null){
            throw new AuthException("传入用户为空");
        }
        user.setUserName(nickname);
        userService.updateItripUser(user);

        //请求成功后会返回字符串
        return DtoUtil.returnDataSuccess(user);
    }
}
