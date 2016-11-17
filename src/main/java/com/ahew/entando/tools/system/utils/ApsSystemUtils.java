package com.ahew.entando.tools.system.utils;


public class ApsSystemUtils {
	
	/**
	 * Traccia una eccezione sul logger del contesto. Se il livello di soglia
	 * del logger è superiore a FINER, viene emesso solo un breve messaggio di
	 * livello SEVERE, altrimenti viene tracciato anche lo stack trace della
	 * eccezione (con il livello FINER).
	 * @param t L'eccezione da tracciare
	 * @param caller La classe chiamante, in cui si è verificato l'errore.
	 * @param methodName Il metodo in cui si è verificato l'errore.
	 * @param message Testo da includere nel tracciamento.
	 */
	public static void logThrowable(Throwable t, Object caller,
			String methodName, String message){
		String className = null;
		if(caller != null) {
			className = caller.getClass().getName();
		}
		// TODO Implementare
		System.out.println(message + " in " + className + "." + methodName + ": " + t.getMessage());
		t.printStackTrace();
	}
	
	/**
	 * Traccia una eccezione sul logger del contesto. Se il livello di soglia
	 * del logger è superiore a FINER, viene emesso solo un breve messaggio di
	 * livello SEVERE, altrimenti viene tracciato anche lo stack trace della
	 * eccezione (con il livello FINER).
	 * @param t L'eccezione da tracciare
	 * @param caller La classe chiamante, in cui si è verificato l'errore.
	 * @param methodName Il metodo in cui si è verificato l'errore.
	 */
	public static void logThrowable(Throwable t, Object caller, String methodName) {
		logThrowable(t, caller, methodName, "Exception");
	}
	
}