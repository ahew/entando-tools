package com.ahew.entando.tools.system.dao.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.ahew.entando.tools.system.manager.common.ManagerFactory;
import com.ahew.entando.tools.system.utils.FileUtils;

public class DBConnectionParams {
	
	public DBConnectionParams() throws IOException {
		Properties properties = new Properties();
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("systemParams.properties");
		properties.load(in);
		FileUtils.closeResource(in);
		this.properties = properties;
	}
	
	public String getPortDriverClass() {
		return this.getPortDriverClass(0);
	}
	public String getPortUrl() {
		return this.getPortUrl(0);
	}
	public String getPortUsername() {
		return this.getPortUsername(0);
	}
	public String getPortPassword() {
		return this.getPortPassword(0);
	}
	public String getPortValidationQuery() {
		return this.getPortValidationQuery(0);
	}
	
	public String getServDriverClass() {
		return this.getServDriverClass(0);
	}
	public String getServUrl() {
		return this.getServUrl(0);
	}
	public String getServUsername() {
		return this.getServUsername(0);
	}
	public String getServPassword() {
		return this.getServPassword(0);
	}
	public String getServValidationQuery() {
		return this.getServValidationQuery(0);
	}
	
	public String getPortDriverClass(int source) {
		return this.getPortProperty(source, "driverClassName");
	}
	public String getPortUrl(int source) {
		return this.getPortProperty(source, "url");
	}
	public String getPortUsername(int source) {
		return this.getPortProperty(source, "username");
	}
	public String getPortPassword(int source) {
		return this.getPortProperty(source, "password");
	}
	public String getPortValidationQuery(int source) {
		return this.getPortProperty(source, "validationQuery");
	}
	
	public String getServDriverClass(int source) {
		return this.getServProperty(source, "driverClassName");
	}
	public String getServUrl(int source) {
		return this.getServProperty(source, "url");
	}
	public String getServUsername(int source) {
		return this.getServProperty(source, "username");
	}
	public String getServPassword(int source) {
		return this.getServProperty(source, "password");
	}
	public String getServValidationQuery(int source) {
		return this.getServProperty(source, "validationQuery");
	}
	
	private String getPortProperty(int source, String suffix) {
		return this.getProperty(source, true, suffix);
	}
	
	private String getServProperty(int source, String suffix) {
		return this.getProperty(source, false, suffix);
	}
	
	private String getProperty(int source, boolean port, String suffix) {
		String paramName = this.getDbParamName(source, port, suffix);
		return this.properties.getProperty(paramName);
	}
	
	private String getDbParamName(int source, boolean port, String suffix) {
		StringBuilder paramName = new StringBuilder("jdbc.");
		switch (source) {
		case ManagerFactory.SOURCE_1:
		case ManagerFactory.SOURCE_2:
			paramName.append("source").append(source).append(".");
			break;
		}
		paramName.append(port ? "portDb." : "servdb.");
		paramName.append(suffix);
		return paramName.toString();
	}
	
	private Properties properties;
	
}