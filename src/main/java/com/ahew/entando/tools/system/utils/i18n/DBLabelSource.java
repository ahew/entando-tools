package com.ahew.entando.tools.system.utils.i18n;

import java.util.Map;

import com.ahew.entando.tools.system.manager.I18nManager;
import com.ahew.entando.tools.system.model.i18n.I18nLabel;
import com.ahew.entando.tools.system.model.i18n.ILabelSource;

public class DBLabelSource implements ILabelSource {
	
	public DBLabelSource(I18nManager i18nManager) {
		this.i18nManager = i18nManager;
	}
	
	@Override
	public Map<String, I18nLabel> getLabels() throws Exception {
		Map<String, I18nLabel> labels = this.i18nManager.loadLabelsMap();
		return labels;
	}
	
	private I18nManager i18nManager;
	
}
