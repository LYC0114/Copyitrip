package cn.itrip.auth.service;

import cn.itrip.beans.pojo.ItripUser;
import cn.itrip.beans.vo.ItripTokenVO;
import cn.itrip.common.Constants;
import cn.itrip.common.EmptyUtils;
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
    //生成token
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
            redisAPI.set(token,Constants.TOKEN_EXPIRE*60*60*1000,userJson);
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
//验证token有效性
    @Override
    public Boolean validatetoken(String token, String agent) throws Exception {
        //token判空
        if(EmptyUtils.isEmpty(token)){
            //未携带token信息
            throw new AuthException("未携带token信息");
        }
        /*判断是否为同一个客户
        * token：PC-3066014faOb10792e4a762-23-20170531133947-4f6496*/
        String[] tokenArr = token.split("-");
        if(!tokenArr.equals(MD5.getMd5(agent,6))){
            //不是原来的客户端
            throw new AuthException("不是同一个客户端未登录");
        }
        String gentimeStr = tokenArr[3];
        LocalDateTime genTime = LocalDateTime.parse(gentimeStr, DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        long time= genTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
        //判断token是否超时
        if(System.currentTimeMillis()-time>Constants.TOKEN_EXPIRE*60*60*1000){
            //token过期未登录状态；
            throw new AuthException("token过期");
        }
        return true;
    }
//删除token
    @Override
    public void deltoken(String token) throws Exception {
        redisAPI.delete(token);
    }

    @Override
    public String relodeToken(String token,String agent)throws Exception {
        String userJson = redisAPI.get(token);
        ItripUser itripUser = JSON.parseObject(userJson, ItripUser.class);

        String s = token.split("-")[3];
        LocalDateTime time = LocalDateTime.parse(s, DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        long genTime = time.toInstant(ZoneOffset.of("+8")).toEpochMilli();
        if(System.currentTimeMillis()-genTime<Constants.TOKEN_PROTECT_TIME*60*60*1000){
            throw new AuthException("保护期不允许置换");
        }
        String newToken = this.ganerateToken(agent, itripUser);
        this.saveToken(newToken,itripUser);
        redisAPI.set("token",2*60,userJson);
        return newToken;
    }
}
