package com.ahew.entando.tools.system.dao.common;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

public class DBUtils {
	
	public static DataSource createDataSource(String driverClass, String url, String username, String password, String validationQuery) {
		BasicDataSource dataSource = new BasicDataSource();
		
		dataSource.setDriverClassName(driverClass);
		dataSource.setUsername(username);
		dataSource.setPassword(password);
		dataSource.setUrl(url);
		dataSource.setMaxActive(10);
		dataSource.setMaxIdle(5);
		dataSource.setInitialSize(5);
		dataSource.setValidationQuery(validationQuery);
		return dataSource;
	}
	
	public static String escapeStringValue(String value) {
		if (value!=null) {
			value = value.replaceAll("'", "''");
		}
		return value;
	}
	
}
