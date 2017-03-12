package py.una.pol.iin.pwb.rest;

import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import py.una.pol.iin.pwb.bean.ICarritoBean;
import py.una.pol.iin.pwb.decortator.CatchExceptions;
import py.una.pol.iin.pwb.exception.InvalidArgumentException;
import py.una.pol.iin.pwb.exception.InvalidFormatException;
import py.una.pol.iin.pwb.model.CarritoQuery;
import py.una.pol.iin.pwb.model.SessionIdentifierGenerator;
import py.una.pol.iin.pwb.model.SessionTable;

@Path("/carrito")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CarritoResource {

	@POST
	@Path("/agregar")
	@CatchExceptions
	public Response agregarProducto(@HeaderParam("session-key") String sessionKey, List<CarritoQuery> carritoQueries) throws InvalidArgumentException, InvalidFormatException
	{
		ICarritoBean cb = getSessionBean(sessionKey);		
		
		return Response
				.status(200)
				.entity(cb.agregarProductos(carritoQueries))
				.header("session-key", cb.getSessionKey()).build();
	}
	
	@POST	
	@Path("/quitar")
	@CatchExceptions
	public Response quitarProducto(@HeaderParam("session-key") String sessionKey, List<CarritoQuery> carritoQueries) throws InvalidArgumentException, InvalidFormatException
	{
		ICarritoBean cb = getSessionBean(sessionKey);		
		
		return Response
				.status(200)
				.entity(cb.quitarProductos(carritoQueries))
				.header("session-key", cb.getSessionKey()).build();
		
	}
	
	@GET
	@Path("/")
	@CatchExceptions
	public Response infoCarrito(@HeaderParam("session-key") String sessionKey)
	{
		ICarritoBean cb = getSessionBean(sessionKey);		
		
		return Response
				.status(200)
				.entity(cb.getCarrito())
				.header("session-key", cb.getSessionKey())
				.build();
		
	}
	
	public ICarritoBean getSessionBean(String sessionKey)
	{
		ICarritoBean carritoBean = null;
		
	    if (sessionKey != null)
	    {
	    	carritoBean = SessionTable.getSession(sessionKey);
	    	if (carritoBean != null)
	    	{
	    		System.out.println("El session key del cliente es: " + carritoBean.getSessionKey());
	    	}
	    }
		
		if (carritoBean == null)
		{			
					   
			try {
				
				String sk = SessionIdentifierGenerator.nextSessionId();
				System.out.println("El cliente aun no tiene un key, se debe generar:" + sk);
				
				Context ctx = new InitialContext();
				carritoBean = (ICarritoBean) ctx.lookup("global/pwb-ear/pwb-ejb/CarritoBean");		    	        			
				carritoBean.setSessionKey(sk);				
				SessionTable.addSession(sk, carritoBean);			
				
			} catch (NamingException e) {
				e.printStackTrace();
			}
		    
						
			
		}
		
		return carritoBean;
	}
	
}
