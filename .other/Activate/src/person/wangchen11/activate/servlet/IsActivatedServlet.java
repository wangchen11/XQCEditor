package person.wangchen11.activate.servlet;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.smart.framework.util.WebUtil;

import person.wangchen11.util.CaseUtil;

@WebServlet(name="isactivated",urlPatterns= {"/isactivated"},loadOnStartup=1 )
public class IsActivatedServlet extends BaseHttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Map<String, String> map = WebUtil.getRequestParamMap(req);
		boolean ret = getActivateService().isActivated( CaseUtil.caseToMapObject(map) );
		resp.getOutputStream().write((""+ret).getBytes());
	}
}
