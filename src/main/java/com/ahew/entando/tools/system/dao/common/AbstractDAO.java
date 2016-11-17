package com.ahew.entando.tools.system.dao.common;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import com.ahew.entando.tools.system.exception.ApsSystemException;
import com.ahew.entando.tools.system.utils.ApsSystemUtils;

public abstract class AbstractDAO implements Serializable {

	/**
	 * Traccia un'eccezione e rilancia una eccezione runtime 
	 * con il messaggio specificato. Da usare nel catch delle eccezioni.
	 * @param t L'eccezione occorsa.
	 * @param message Il messaggio per la nuova ecceione da rilanciare
	 * @param methodName Il nome del metodo in cui si e verificata l'eccezione 
	 *                   (non indispensabile, può essere null)
	 */
	@Deprecated
	protected void processDaoException(Throwable t, String message, String methodName) {
		ApsSystemUtils.logThrowable(t, this, methodName, message);
		throw new RuntimeException(message, t);
	}

	/**
	 * Restituisce una connessione SQL relativa al datasource.
	 * @return La connessione richiesta.
	 * @throws ApsSystemException In caso di errore in apertura di connessione.
	 */
	protected Connection getConnection() throws ApsSystemException {
		Connection conn = null;
		try {
			conn = this.getDataSource().getConnection();
		} catch (SQLException e) {
			ApsSystemUtils.logThrowable(e, this, "getConnection", "Error getting connection to the datasource");
			throw new ApsSystemException("Error getting connection to the datasource", e);
		}
		return conn;
	}
	
	/**
	 * Chiude in modo controllato un resultset, uno statement e la connessione, 
	 * senza rilanciare eccezioni. Da usare nel finally di gestione di
	 * una eccezione.
	 * @param res Il resultset da chiudere; può esser null
	 * @param stat Lo statement da chiudere; può esser null
	 * @param conn La connessione al db; può esser null
	 */
	protected void closeDaoResources(ResultSet res, Statement stat, Connection conn) {
		this.closeDaoResources(res, stat);
		this.closeConnection(conn);
	}

	/**
	 * Chiude in modo controllato un resultset e uno statement, 
	 * senza rilanciare eccezioni. Da usare nel finally di gestione di
	 * una eccezione.
	 * @param res Il resultset da chiudere; può esser null
	 * @param stat Lo statement da chiudere; può esser null
	 */
	protected void closeDaoResources(ResultSet res, Statement stat) {
		if (res != null) {
			try {
				res.close();
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "closeDaoResources", "Error while closing the resultset");
			}
		}
		if (stat != null) {
			try {
				stat.close();
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "closeDaoResources", "Error while closing the statement");
			}
		}
	}

	/**
	 * Esegue un rollback, senza rilanciare eccezioni. 
	 * Da usare nel blocco catch di gestione di una eccezione. 
	 * @param conn La connessione al db.
	 */
	protected void executeRollback(Connection conn) {
		try {
			if (conn != null) conn.rollback();
		} catch (SQLException e) {
			ApsSystemUtils.logThrowable(e, this, "executeRollback", "Error on connection rollback");
		}
	}

	/**
	 * Chiude in modo controllato una connessione, 
	 * senza rilanciare eccezioni. Da usare nel finally di gestione di
	 * una eccezione.
	 * @param conn La connessione al db; può esser null
	 */
	protected void closeConnection(Connection conn) {
		try {
			if (conn != null) conn.close();
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "closeConnection", "Error closing the connection");
		}
	}
	
	protected void executeQueryWithoutResultset(String query, Object... args) {
		Connection conn = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			this.executeQueryWithoutResultset(conn, query, args);
			conn.commit();
		} catch (Throwable t) {
			this.executeRollback(conn);
			ApsSystemUtils.logThrowable(t, this, "executeQueryWithoutResultset", "Error executing query");
			throw new RuntimeException("Error executing query", t);
		} finally {
			this.closeConnection(conn);
		}
	}
	
	protected void executeQueryWithoutResultset(Connection conn, String query, Object... args) {
		PreparedStatement stat = null;
    	try {
    		stat = conn.prepareStatement(query);
			for (int i = 0; i < args.length; i++) {
				Object object = args[i];
				stat.setObject(i+1, object);
			}
    		stat.executeUpdate();
    	} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "executeQueryWithoutResultset", "Error executing query");
			throw new RuntimeException("Error executing query", t);
    	} finally {
    		closeDaoResources(null, stat);
    	}
	}
	
	protected DataSource getDataSource() {
		return this._dataSource;
	}

	/**
	 * Setta il datasource relativo al db gestito dalla classe dao.
	 * @param dataSource Il datasorce da settare.
	 */
	public void setDataSource(DataSource dataSource) {
		this._dataSource = dataSource;
	}

	private DataSource _dataSource;

}