package com.ahew.entando.tools.exec.i18n;

import java.util.Map;

import com.ahew.entando.tools.system.manager.I18nManager;
import com.ahew.entando.tools.system.manager.common.ManagerFactory;
import com.ahew.entando.tools.system.model.i18n.I18nLabel;
import com.ahew.entando.tools.system.model.i18n.I18nRecord;
import com.ahew.entando.tools.system.model.i18n.ILabelSource;
import com.ahew.entando.tools.system.utils.FileUtils;
import com.ahew.entando.tools.system.utils.i18n.DBLabelSource;
import com.ahew.entando.tools.system.utils.i18n.I18nHelper;
import com.ahew.entando.tools.system.utils.model.ObjComparison;

public class I18nComparator {
	
	public static void main(String[] args) throws Exception {
		I18nComparator executor = new I18nComparator();
		ManagerFactory factory = new ManagerFactory();
		executor.execute(factory);
	}
	
	public void execute(ManagerFactory factory) throws Exception {
		I18nHelper i18nHelper = new I18nHelper();
		I18nManager localI18nManager = factory.createManager(I18nManager.class, ManagerFactory.LOCAL);
		I18nManager remoteI18nManager = factory.createManager(I18nManager.class, ManagerFactory.REMOTE);
		String destFolder = "/tmp/labels/";
//		String localFile = folder + "i18n_local.csv";
//		String remoteFile = folder + "i18n_remote.csv";
		ILabelSource localSource = new DBLabelSource(localI18nManager);
		ILabelSource remoteSource = new DBLabelSource(remoteI18nManager);
//		ILabelSource localSource = new CsvLabelSource(i18nHelper, localFile);
//		ILabelSource remoteSource = new CsvLabelSource(i18nHelper, remoteFile);
		
		System.out.println("Comparazione dati - INIZIO");
		Map<String, I18nLabel> localLabels = localSource.getLabels();
		Map<String, I18nLabel> remoteLabels = remoteSource.getLabels();
		
//		ObjComparison<I18nLabel> comparison = i18nHelper.compareLabels(localLabels, remoteLabels);
//		FileUtils.writeToFile("/tmp/out.txt", out -> i18nHelper.printComparison(comparison, out));
		
		i18nHelper.saveLabelsToCsv(localLabels.values(), destFolder + "i18n_local.csv");
		i18nHelper.saveLabelsToCsv(remoteLabels.values(), destFolder + "i18n_remote.csv");
		ObjComparison<I18nRecord> comparison = i18nHelper.compareLabelRecords(localLabels, remoteLabels);
		
		FileUtils.writeToFile(destFolder + "labels_diff_it.txt", out -> i18nHelper.printComparison(comparison, out, record -> "it".equals(record.getLangCode())));
		FileUtils.writeToFile(destFolder + "labels_diff_en.txt", out -> i18nHelper.printComparison(comparison, out, record -> "en".equals(record.getLangCode())));
		FileUtils.writeToFile(destFolder + "labels_diff.txt", out -> i18nHelper.printComparison(comparison, out, null));
		
		ObjComparison<I18nRecord> invertedComparison = i18nHelper.compareLabelRecords(remoteLabels, localLabels);
		
		FileUtils.writeToFile(destFolder + "inv_labels_diff_it.txt", out -> i18nHelper.printComparison(invertedComparison, out, record -> "it".equals(record.getLangCode())));
		FileUtils.writeToFile(destFolder + "inv_labels_diff_en.txt", out -> i18nHelper.printComparison(invertedComparison, out, record -> "en".equals(record.getLangCode())));
		FileUtils.writeToFile(destFolder + "inv_labels_diff.txt", out -> i18nHelper.printComparison(invertedComparison, out, null));
		
		System.out.println("Comparazione dati - FINE");
	}
	
}
