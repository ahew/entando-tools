package com.ahew.entando.tools.system.utils.i18n;

import java.util.Map;

import com.ahew.entando.tools.system.model.i18n.I18nLabel;
import com.ahew.entando.tools.system.model.i18n.ILabelSource;

public class CsvLabelSource implements ILabelSource {
	
	public CsvLabelSource(I18nHelper i18nHelper, String filePath) {
		this.i18nHelper = i18nHelper;
		this.filePath = filePath;
	}
	
	@Override
	public Map<String, I18nLabel> getLabels() throws Exception {
		Map<String, I18nLabel> labels = this.i18nHelper.extractLabelsFromCsv(this.filePath);
		return labels;
	}
	
	private I18nHelper i18nHelper;
	private String filePath;
	
}
