package person.wangchen11.activate.service;

import java.util.Map;

public interface ActivateService {
	public boolean activate(Map<String, Object> fieldMap);
	public boolean isActivated(Map<String, Object> fieldMap);
}
