package person.wangchen11.activate.service.impl;

import com.smart.framework.DataSet;
import com.smart.framework.annotation.Bean;
import com.smart.framework.base.BaseService;

import person.wangchen11.activate.entity.User;
import person.wangchen11.activate.service.UserService;

import java.util.Map;

@Bean
public class UserServiceImpl extends BaseService implements UserService {

    @Override
    public User login(Map<String, Object> fieldMap) {
        String username = String.valueOf(fieldMap.get("username"));
        String password = String.valueOf(fieldMap.get("password"));
        return DataSet.select(User.class, "username = ? and password = ?", username, password);
    }
}
