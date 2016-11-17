package com.ahew.entando.tools.system.model.i18n;

import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class I18nLabel implements Comparable<I18nLabel> {
	
	public int compareTo(I18nLabel label) {
		int comparison = this.keyCode.compareTo(label.getKeyCode());
		return comparison;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof I18nLabel) {
			I18nLabel label = (I18nLabel) obj;
			boolean result = false;
			if (this.keyCode.equals(label.getKeyCode())) {
				Map<String, String> labelValues = label.getValues();
				if (this.getValues().size() == labelValues.size()) {
					result = true;
					for (Entry<String, String> entry : this.getValues().entrySet()) {
						if (!entry.getValue().equals(labelValues.get(entry.getKey()))) {
							result = false;
							break;
						}
					}
				}
			}
			return result;
		} else {
			return super.equals(obj);
		}
	}
	
	public String getKeyCode() {
		return keyCode;
	}
	public void setKeyCode(String keyCode) {
		this.keyCode = keyCode;
	}
	
	public Map<String, String> getValues() {
		return values;
	}
	public void setValues(Map<String, String> values) {
		this.values = values;
	}
	public void addValue(String langCode, String value) {
		if (this.values==null) {
			this.values = new TreeMap<String, String>();
		}
		this.values.put(langCode, value);
	}
	public String getValue(String langCode) {
		return this.values.get(langCode);
	}
	
	private String keyCode;
	private Map<String, String> values = new TreeMap<String, String>(new Comparator<String>() {
		public int compare(String s1, String s2) {
			return s2.compareTo(s1);
		};
	});
	
}
