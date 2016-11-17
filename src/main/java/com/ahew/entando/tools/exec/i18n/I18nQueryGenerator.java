package com.ahew.entando.tools.exec.i18n;

import java.util.Map;

import com.ahew.entando.tools.system.manager.I18nManager;
import com.ahew.entando.tools.system.manager.common.ManagerFactory;
import com.ahew.entando.tools.system.model.i18n.I18nLabel;
import com.ahew.entando.tools.system.model.i18n.ILabelSource;
import com.ahew.entando.tools.system.utils.FileUtils;
import com.ahew.entando.tools.system.utils.i18n.DBLabelSource;
import com.ahew.entando.tools.system.utils.i18n.I18nHelper;

public class I18nQueryGenerator {
	
	public static void main(String[] args) throws Exception {
		I18nQueryGenerator executor = new I18nQueryGenerator();
		ManagerFactory factory = new ManagerFactory();
		executor.execute(factory);
	}
	
	public void execute(ManagerFactory factory) throws Exception {
		String folder = "/tmp/";
		String source = folder + "i18n_source.csv";
//		String source = folder + "i18n_dest.csv";
		String dest = "/tmp/labels_extracted.txt";
		I18nHelper i18nHelper = new I18nHelper();
		I18nManager i18nManager = factory.createManager(I18nManager.class, ManagerFactory.LOCAL);
//		I18nManager i18nManager = factory.createManager(I18nManager.class, ManagerFactory.REMOTE);
//		ILabelSource labelSource = new CsvLabelSource(i18nHelper, source);
		ILabelSource labelSource = new DBLabelSource(i18nManager);
		Map<String, I18nLabel> labels = labelSource.getLabels();
		
		FileUtils.writeToFile(dest, out -> {
			labels.values().stream().forEach(label -> {
				try {
					i18nHelper.appendInsertQueries(label, ";", out);
//					out.append("\n");
				} catch (Exception e) {
					throw new RuntimeException("Error building label queries", e);
				}
			});
		});
	}
	
}
