package person.wangchen11.activate.servlet;

import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.smart.framework.util.WebUtil;

import person.wangchen11.util.CaseUtil;

@WebServlet(name="activate",urlPatterns= {"/activate"},loadOnStartup=1 )
public class ActivateServlet extends BaseHttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Format format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Map<String, String> map = WebUtil.getRequestParamMap(req);
		map.put("activate_time", format.format(new Date()) );
		boolean ret = getActivateService().activate( CaseUtil.caseToMapObject(map) );
		if(ret) {
			resp.getOutputStream().write("success".getBytes());
		} else {
			resp.getOutputStream().write("failed".getBytes());
		} 
	}
}
