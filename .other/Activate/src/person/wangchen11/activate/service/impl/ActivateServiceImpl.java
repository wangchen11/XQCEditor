package person.wangchen11.activate.service.impl;

import java.util.Map;

import com.smart.framework.DataSet;

import person.wangchen11.activate.entity.Activate;
import person.wangchen11.activate.service.ActivateService;

public class ActivateServiceImpl implements ActivateService {

	@Override
	public boolean activate(Map<String, Object> fieldMap) {
		System.out.println("fieldMap:"+fieldMap);
		try {
			if(isActivated(fieldMap)) {
				return true;
			}
			System.out.println("activate success!");
			return DataSet.insert(Activate.class, fieldMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean isActivated(Map<String, Object> fieldMap) {
		Activate activate = DataSet.select(Activate.class, "user_id = ?", fieldMap.get("user_id"));
		if(activate != null) {
			System.out.println("already activated!");
			return true;
		}
		return false;
	}

}
