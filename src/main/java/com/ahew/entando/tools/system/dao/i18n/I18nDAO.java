package com.ahew.entando.tools.system.dao.i18n;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.ahew.entando.tools.system.dao.common.AbstractDAO;
import com.ahew.entando.tools.system.model.i18n.I18nLabel;
import com.ahew.entando.tools.system.utils.ApsSystemUtils;

public class I18nDAO extends AbstractDAO {
	
	public Map<String, I18nLabel> loadLabelsMap() {
		Map<String, I18nLabel> labels = new TreeMap<String, I18nLabel>();
		Connection conn = null;
		Statement stat = null;
		ResultSet res = null;
		try {
			conn = this.getConnection();
			stat = conn.createStatement();
			res = stat.executeQuery(LOAD_ALL_LABELS);
			I18nLabel lastLabel = null;
			while (res.next()) {
				String keyCode = res.getString(1);
				String langCode = res.getString(2);
				String value = res.getString(3);
				if (lastLabel==null || !lastLabel.getKeyCode().equals(keyCode)) {
					lastLabel = new I18nLabel();
					lastLabel.setKeyCode(keyCode);
					labels.put(keyCode, lastLabel);
				}
				lastLabel.addValue(langCode, value);
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "loadLabels", "Error loading labels");
			throw new RuntimeException("Error loading labels", t);
		} finally {
			closeDaoResources(res, stat, conn);
		}
		return labels;
	}
	
	public List<I18nLabel> loadLabels() {
		List<I18nLabel> labels = new ArrayList<I18nLabel>();
		Connection conn = null;
		Statement stat = null;
		ResultSet res = null;
		try {
			conn = this.getConnection();
			stat = conn.createStatement();
			res = stat.executeQuery(LOAD_ALL_LABELS);
			I18nLabel lastLabel = null;
			while (res.next()) {
				String keyCode = res.getString(1);
				String langCode = res.getString(2);
				String value = res.getString(3);
				if (lastLabel==null || !lastLabel.getKeyCode().equals(keyCode)) {
					lastLabel = new I18nLabel();
					lastLabel.setKeyCode(keyCode);
					labels.add(lastLabel);
				}
				lastLabel.addValue(langCode, value);
			}
			Collections.sort(labels);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "loadLabels", "Error loading labels");
			throw new RuntimeException("Error loading labels", t);
		} finally {
			closeDaoResources(res, stat, conn);
		}
		return labels;
	}
	
	private final String LOAD_ALL_LABELS = "SELECT keycode, langcode, stringvalue FROM localstrings ORDER BY keycode, langcode";
	
}
