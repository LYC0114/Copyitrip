package cn.itrip.auth.service;

import cn.itrip.beans.pojo.ItripUser;
import cn.itrip.common.MD5;
import cn.itrip.common.RedisAPI;
import cn.itrip.mapper.itripUser.ItripUserMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    @Resource
     private RedisAPI redisAPI;
    @Resource
    private SmsService smsService;
    @Resource
    private ItripUserMapper itripUserMapper;
    //根据userCode查询用户数据
    @Override
    public ItripUser getItripUserByUserCode(String userCode)throws Exception {
        Map<String, Object> param= new HashMap<String, Object>();
        List<ItripUser> List = itripUserMapper.getItripUserListByMap(param);
        if(List.size()==0){
            return null;
        }
        return List.get(0);
    }

    @Override
    public void itriptxCreateItripUser(ItripUser itripUser) throws Exception {
        //写入数据库
        itripUser.setActivated(0);
        itripUser.setUserPassword(MD5.getMd5(itripUser.getUserPassword(),32));
        itripUserMapper.insertItripUser(itripUser);
        //发送短信验证码给用户
        int code=MD5.getRandomCode();
        int expire=1;
        smsService.send(itripUser.getUserCode(),"1",new String[]{String.valueOf(code),String.valueOf(expire)});
        //缓存短信验证码
        redisAPI.set("activation"+itripUser.getUserCode(),expire*60,String.valueOf(code));
    }
}
