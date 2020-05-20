package cn.itrip.auth.service;

import cn.itrip.beans.pojo.ItripUser;

public interface UserService {
    ItripUser getItripUserByUserCode(String userCode) throws Exception;
   void itriptxCreateItripUser(ItripUser itripUser)throws Exception;
}
