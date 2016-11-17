package com.ahew.entando.tools.system.manager.common;

import java.io.IOException;

import com.ahew.entando.tools.system.dao.common.DBConnectionParams;
import com.ahew.entando.tools.system.dao.common.DaoFactory;

public class ManagerFactory {
	
	public ManagerFactory() throws IOException {
		DBConnectionParams params = new DBConnectionParams();
		this.initDaoFactory(params);
	}
	
	public ManagerFactory(DBConnectionParams params) {
		this.initDaoFactory(params);
	}
	
	private void initDaoFactory(DBConnectionParams params) {
		this.setDaoFactory(new DaoFactory(params));
		this.setDaoFactory1(new DaoFactory(params, SOURCE_1));
		this.setDaoFactory2(new DaoFactory(params, SOURCE_2));
	}
	
	public <T extends AbstractManager> T createManager(Class<T> clazz) throws Exception {
		return this.createManager(clazz, 0);
	}
	
	public <T extends AbstractManager> T createManager(Class<T> clazz, int source) throws Exception {
		T manager = clazz.newInstance();
		manager.init(this.getDaoFactory(source));
		return manager;
	}
	
	public DaoFactory getDaoFactory(int source) {
		DaoFactory daoFactory = null;
		switch (source) {
		case SOURCE_1:
			daoFactory = this.getDaoFactory1();
			break;
		case SOURCE_2:
			daoFactory = this.getDaoFactory2();
			break;
		}
		if (daoFactory==null) {
			daoFactory = this.getDaoFactory();
		}
		return daoFactory;
	}
	
	public DaoFactory getDaoFactory() {
		return daoFactory;
	}
	public void setDaoFactory(DaoFactory daoFactory) {
		this.daoFactory = daoFactory;
	}
	
	public DaoFactory getDaoFactory1() {
		return daoFactory1;
	}
	public void setDaoFactory1(DaoFactory daoFactory1) {
		this.daoFactory1 = daoFactory1;
	}
	
	public DaoFactory getDaoFactory2() {
		return daoFactory2;
	}
	public void setDaoFactory2(DaoFactory daoFactory2) {
		this.daoFactory2 = daoFactory2;
	}
	
	private DaoFactory daoFactory;
	
	private DaoFactory daoFactory1;
	private DaoFactory daoFactory2;
	
	public static final int SOURCE_1 = 1;
	public static final int SOURCE_2 = 2;
	public static final int LOCAL = SOURCE_1;
	public static final int REMOTE = SOURCE_2;
	
}
