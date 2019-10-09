package person.wangchen11.activate.action;

import com.smart.framework.DataContext;
import com.smart.framework.annotation.Bean;
import com.smart.framework.annotation.Inject;
import com.smart.framework.annotation.Request;
import com.smart.framework.base.BaseAction;
import com.smart.framework.bean.Result;

import person.wangchen11.activate.entity.User;
import person.wangchen11.activate.service.UserService;

import java.util.Map;

@Bean
public class UserAction extends BaseAction {

    @Inject
    private UserService userService;

    @Request("post:/login")
    public Result login(Map<String, Object> fieldMap) {
        User user = userService.login(fieldMap);
        if (user != null) {
            DataContext.Session.put("user", user);
            return new Result(true);
        } else {
            return new Result(false).error(ERROR_DATA);
        }
    }

    @Request("get:/logout")
    public Result logout() {
        DataContext.Session.removeAll();
        return new Result(true);
    }
}
