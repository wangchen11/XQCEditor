package person.wangchen11.activate.servlet;

import javax.servlet.http.HttpServlet;

import person.wangchen11.activate.service.ActivateService;
import person.wangchen11.activate.service.impl.ActivateServiceImpl;

public class BaseHttpServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static ActivateService activateService = new ActivateServiceImpl();
	
	public ActivateService getActivateService() {
		return activateService;
	}
	
}
