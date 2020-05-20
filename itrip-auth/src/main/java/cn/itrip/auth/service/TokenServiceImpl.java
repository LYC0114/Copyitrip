package cn.itrip.auth.service;

import cn.itrip.beans.pojo.ItripUser;
import cn.itrip.beans.vo.ItripTokenVO;
import cn.itrip.common.Constants;
import cn.itrip.common.MD5;

import cn.itrip.common.RedisAPI;
import com.alibaba.fastjson.JSON;
import eu.bitwalker.useragentutils.DeviceType;
import eu.bitwalker.useragentutils.UserAgent;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class TokenServiceImpl implements TokenService {
    @Resource
    private RedisAPI redisAPI;
    @Override
    public String ganerateToken(String agent, ItripUser itripUser) throws Exception {
        //token:客户端标识-USERCODE-USERID-CREATIONDATE-RONDEM[6位]
        //token：PC-3066014faOb10792e4a762-23-20170531133947-4f6496

        //创建StringBuffer对象拼接token字符串
        StringBuffer sb = new StringBuffer();
        sb.append(Constants.TOKEN_PRIFIX);
        //为什么区分手机端和客户端，pc端需要自定义有效时间，移动端永久有效。
//        boolean boolent = UserAgentUtil.CheckAgent(agent);
        DeviceType deviceType = UserAgent.parseUserAgentString(agent).getOperatingSystem().getDeviceType();
        if(deviceType.getName().equals(DeviceType.MOBILE)){
            //移动端
            sb.append("MOBILE");
        }else{//PC端
            sb.append("PC");
        }
        sb.append("-");
        sb.append(MD5.getMd5(itripUser.getUserCode(),32));
        sb.append("-");
        sb.append(itripUser.getId());
        sb.append("-");
        sb.append(LocalDateTime.now(ZoneOffset.of("+8")).format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
        sb.append("-");
        sb.append(MD5.getMd5(agent,6));
        return sb.toString();
    }
//判断token是否为移动端
    @Override
    public void saveToken(String token,ItripUser itripUser) {
        String userJson = JSON.toJSONString(itripUser);
        if(token.startsWith(Constants.TOKEN_PRIFIX+"MOBILE")){//移动端
            redisAPI.set(token,userJson);
        }else{//PC端
            redisAPI.set(token,Constants.TOKEN_EXPIRE*60*60,userJson);
        }
    }
//处理token包括，生成-缓存-返回
    @Override
    public ItripTokenVO processToken(String agent, ItripUser user) throws Exception {
        //生成token数据
        String token = this.ganerateToken(agent, user);
        //缓存token数据
        this.saveToken(token,user);
        //向客户端返回token数据
        long expTIme=Constants.TOKEN_EXPIRE*60*60*1000;
        long genTime=System.currentTimeMillis();
        ItripTokenVO tokenVO = new ItripTokenVO(token,expTIme,genTime);
        return tokenVO;
    }
}
