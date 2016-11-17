package com.ahew.entando.tools.system.model.i18n;

import java.util.Map;

public interface ILabelSource {
	
	public Map<String, I18nLabel> getLabels() throws Exception;
	
}
