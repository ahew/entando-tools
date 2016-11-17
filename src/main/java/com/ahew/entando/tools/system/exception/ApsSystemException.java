package com.ahew.entando.tools.system.exception;

public class ApsSystemException extends Exception {
	/**
	 * Costruttore con solo messaggio
	 * @param message Il messaggio associato all'eccezione
	 */
	public ApsSystemException(String message){
		super(message);
	}
	
	/**
	 * Costruttore con messaggio e causa (precedente eccezione).
	 * @param message Il messaggio associato all'eccezione
	 * @param cause L'eccezione che ha causato l'eccezione originale 
	 */
	public ApsSystemException(String message, Throwable cause){
		super(message, cause);
	}
}