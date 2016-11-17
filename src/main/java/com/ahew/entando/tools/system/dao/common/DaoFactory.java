package com.ahew.entando.tools.system.dao.common;

import javax.sql.DataSource;

public class DaoFactory {
	
	public DaoFactory(DBConnectionParams params) {
		this.setPortDataSource(this.createPortDataSource(params, 0));
		this.setServDataSource(this.createServDataSource(params, 0));
	}
	
	public DaoFactory(DBConnectionParams params, int source) {
		this.setPortDataSource(this.createPortDataSource(params, source));
		this.setServDataSource(this.createServDataSource(params, source));
	}
	
	public DataSource createPortDataSource(DBConnectionParams params, int source) {
		DataSource dataSource = DBUtils.createDataSource(params.getPortDriverClass(source), params.getPortUrl(source), 
				params.getPortUsername(source), params.getPortPassword(source), params.getPortValidationQuery(source));
		return dataSource;
	}
	
	public DataSource createServDataSource(DBConnectionParams params, int source) {
		DataSource dataSource = DBUtils.createDataSource(params.getServDriverClass(source), params.getServUrl(source), 
				params.getServUsername(source), params.getServPassword(source), params.getServValidationQuery(source));
		return dataSource;
	}
	
	public <T extends AbstractDAO> T createDAO(Class<T> clazz, boolean portDB) throws Exception {
		T dao = clazz.newInstance();
		dao.setDataSource(portDB ? this.getPortDataSource() : this.getServDataSource());
		return dao;
	}
	
	protected DataSource getPortDataSource() {
		return portDataSource;
	}
	public void setPortDataSource(DataSource portDataSource) {
		this.portDataSource = portDataSource;
	}
	
	protected DataSource getServDataSource() {
		return servDataSource;
	}
	public void setServDataSource(DataSource servDataSource) {
		this.servDataSource = servDataSource;
	}
	
	private DataSource portDataSource;
	private DataSource servDataSource;
	
}
