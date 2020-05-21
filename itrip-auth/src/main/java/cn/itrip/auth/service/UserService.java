package cn.itrip.auth.service;

import cn.itrip.beans.pojo.ItripUser;

public interface UserService {
    ItripUser getItripUserByUserCode(String userCode) throws Exception;
   void itriptxCreateItripUser(ItripUser itripUser)throws Exception;
//验证手机短信接口
    Boolean itriptxValidateSmsCode(String userCode, String smscode) throws Exception;
//修改用户
    void updateItripUser(ItripUser user) throws Exception;
}
