package com.ahew.entando.tools.system.manager.common;

import com.ahew.entando.tools.system.dao.common.DaoFactory;

public abstract class AbstractManager {
	
	public abstract void init(DaoFactory factory) throws Exception;
	
}
