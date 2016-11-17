package com.ahew.entando.tools.exec.i18n;

import com.ahew.entando.tools.system.manager.I18nManager;
import com.ahew.entando.tools.system.manager.common.ManagerFactory;

public class I18nExtractor {
	
	public static void main(String[] args) throws Exception {
		I18nExtractor executor = new I18nExtractor();
		ManagerFactory factory = new ManagerFactory();
		executor.execute(factory);
	}
	
	public void execute(ManagerFactory factory) throws Exception {
		I18nManager i18nManager = factory.createManager(I18nManager.class, ManagerFactory.LOCAL);
//		I18nManager i18nManager = factory.createManager(I18nManager.class, ManagerFactory.REMOTE);
		String filePath = "/tmp/i18n.csv";
		System.out.println("Estrazione dati verso file " + filePath + " - INIZIO");
		i18nManager.saveLabelsToCsv(filePath);
		System.out.println("Estrazione dati verso file " + filePath + " - FINE");
	}
	
}
