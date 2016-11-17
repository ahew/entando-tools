package com.ahew.entando.tools.system.manager;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.ahew.entando.tools.system.dao.common.DaoFactory;
import com.ahew.entando.tools.system.dao.i18n.I18nDAO;
import com.ahew.entando.tools.system.manager.common.AbstractManager;
import com.ahew.entando.tools.system.model.i18n.I18nLabel;
import com.ahew.entando.tools.system.utils.i18n.I18nHelper;

public class I18nManager extends AbstractManager {
	
	@Override
	public void init(DaoFactory factory) throws Exception {
		I18nDAO dao = factory.createDAO(I18nDAO.class, true);
		this.setDao(dao);
	}
	
	public List<I18nLabel> loadLabels() {
		return this.getDao().loadLabels();
	}
	
	public Map<String, I18nLabel> loadLabelsMap() {
		return this.getDao().loadLabelsMap();
	}
	
	public void saveLabelsToCsv(String filePath) throws Exception {
		this.saveLabelsToCsv(new File(filePath));
	}
	
	public void saveLabelsToCsv(File file) throws Exception {
		this.getHelper().saveLabelsToCsv(this.getDao().loadLabels(), file);
	}
	
	public void saveLabelsToQuery(String filePath) throws Exception {
		this.saveLabelsToQuery(new File(filePath));
	}
	
	public void saveLabelsToQuery(File file) throws Exception {
		this.getHelper().saveLabelsToQuery(this.getDao().loadLabels(), file);
	}
	
	public I18nDAO getDao() {
		return dao;
	}
	public void setDao(I18nDAO dao) {
		this.dao = dao;
	}
	
	public I18nHelper getHelper() {
		if (this.i18nHelper==null) {
			this.i18nHelper = new I18nHelper();
		}
		return i18nHelper;
	}
	public void setHelper(I18nHelper i18nHelper) {
		this.i18nHelper = i18nHelper;
	}
	
	private I18nDAO dao;
	private I18nHelper i18nHelper;
	
}
