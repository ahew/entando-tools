package com.ahew.entando.tools.system.model.i18n;


public class I18nRecord implements Comparable<I18nRecord> {
	
	public I18nRecord() {
	}
	
	public I18nRecord(String keyCode, String langCode, String value) {
		this.setKeyCode(keyCode);
		this.setLangCode(langCode);
		this.setValue(value);
	}
	
	public int compareTo(I18nRecord label) {
		int comparison = this.getKeyCode().compareTo(label.getKeyCode());
		if (comparison == 0) {
			comparison = label.getLangCode().compareTo(this.getLangCode());// Ordine inverso
		}
		return comparison;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof I18nRecord) {
			I18nRecord label = (I18nRecord) obj;
			boolean result = false;
			if (this.getKeyCode().equals(label.getKeyCode()) && 
					this.getLangCode().equals(label.getLangCode()) && 
					this.getValue().equals(label.getValue())) {
				result = true;
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
	
	public String getLangCode() {
		return langCode;
	}
	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}
	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	private String keyCode;
	private String langCode;
	private String value;
	
}
