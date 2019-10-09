package person.wangchen11.activate.aspect;

import com.smart.framework.AuthException;
import com.smart.framework.DataContext;
import com.smart.framework.annotation.Aspect;
import com.smart.framework.annotation.Bean;
import com.smart.framework.annotation.Order;
import com.smart.framework.base.BaseAspect;

import person.wangchen11.activate.entity.User;

import java.lang.reflect.Method;

@Bean
@Aspect(pkg = "com.smart.sample.action")
@Order(0)
public class AuthAspect extends BaseAspect {

    @Override
    public boolean intercept(Class<?> cls, Method method, Object[] params) throws Exception {
        String className = cls.getSimpleName();
        String methodName = method.getName();
        return !(
            className.equals("UserAction") &&
                (methodName.equals("login") || methodName.equals("logout")
            )
        );
    }

    @Override
    public void before(Class<?> cls, Method method, Object[] params) throws Exception {
        User user = DataContext.Session.get("user");
        if (user == null) {
            throw new AuthException();
        }
    }
}
