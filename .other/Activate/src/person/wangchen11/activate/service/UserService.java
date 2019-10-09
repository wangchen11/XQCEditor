package person.wangchen11.activate.service;

import java.util.Map;

import person.wangchen11.activate.entity.User;

public interface UserService {

    User login(Map<String, Object> fieldMap);
}
