package py.una.pol.iin.pwb.model;

import java.util.HashMap;

import py.una.pol.iin.pwb.bean.ICarritoBean;

public class SessionTable {
	
	private static HashMap<String, ICarritoBean> sessionTable = new HashMap<String, ICarritoBean>();

	public static HashMap<String, ICarritoBean> getSessionTable() {
		return sessionTable;
	}

	public static void setSessionTable(HashMap<String, ICarritoBean> sessionTable) {
		SessionTable.sessionTable = sessionTable;
	}
	
	public static void addSession(String sessionKey, ICarritoBean carrito)
	{
		sessionTable.put(sessionKey, carrito);
	}
	
	public static ICarritoBean getSession(String sessionKey)
	{
		return sessionTable.get(sessionKey);
	}
	
}
