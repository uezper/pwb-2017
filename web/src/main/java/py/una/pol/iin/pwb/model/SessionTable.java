package py.una.pol.iin.pwb.model;

import java.util.HashMap;

import py.una.pol.iin.pwb.bean.ICarritoVentaBean;

public class SessionTable {
	
	private static HashMap<String, ICarritoVentaBean> sessionTable = new HashMap<String, ICarritoVentaBean>();

	public static HashMap<String, ICarritoVentaBean> getSessionTable() {
		return sessionTable;
	}

	public static void setSessionTable(HashMap<String, ICarritoVentaBean> sessionTable) {
		SessionTable.sessionTable = sessionTable;
	}
	
	public static void addSession(String sessionKey, ICarritoVentaBean carrito)
	{
		sessionTable.put(sessionKey, carrito);
	}
	
	public static ICarritoVentaBean getSession(String sessionKey)
	{
		return sessionTable.get(sessionKey);
	}
	
}
