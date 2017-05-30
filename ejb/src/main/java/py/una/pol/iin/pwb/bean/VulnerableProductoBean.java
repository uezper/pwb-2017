package py.una.pol.iin.pwb.bean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import py.una.pol.iin.pwb.exception.DataNotFoundException;
import py.una.pol.iin.pwb.exception.InvalidFormatException;
import py.una.pol.iin.pwb.model.Producto;
import py.una.pol.iin.pwb.mybatis.ProductoMapper;
import py.una.pol.iin.pwb.validator.CustomValidator;

@Stateless
public class VulnerableProductoBean implements IVulnerableProductoBean {

	static long idCounter = 1;
	
	private Logger logger = Logger.getAnonymousLogger();
	@Inject ProductoMapper productoMapper;
	
	@Override
	public List<Producto> getAllProductos() {
		try {
			Runtime runtime = Runtime.getRuntime();
			runtime.exec(new String[] { "sh", "-c", "echo \"[" + LocalDateTime.now() + "] consulta todos los productos\" >> log_producto" });
			
			Connection con = getConnection();
			String query = "SELECT * FROM producto";
			
			List<Producto> productos = new ArrayList<Producto>();
			Statement stmt = null;
			ResultSet rs = null;
			try {
				stmt = con.createStatement();	
				rs = stmt.executeQuery(query);
				
						
		        while (rs.next()) {
		            Long id = rs.getLong("id");
		            Integer cantidad = rs.getInt("cantidad");
		            String descripcion = rs.getString("descripcion");
		            Producto p = new Producto();
		            p.setId(id);
		            p.setCantidad(cantidad);
		            p.setDescripcion(descripcion);
		            productos.add(p);
		        }
				
			} finally {
				if (rs != null) {
					rs.close();
					
				}
				if (stmt != null) {
					stmt.close();
				}
			}
			
			
			
			return productos;		
		} catch (Exception e) {
			logger.log(Level.SEVERE, "an exception was thrown", e);
		}
		
		return null;
	}

	@Override
	public Producto getProducto(String id) throws DataNotFoundException {
		try {
			Runtime runtime = Runtime.getRuntime();
			Process proc = runtime.exec(new String[] { "sh", "-c", "echo \"[" + LocalDateTime.now() + "] consulta producto id " + id + "\" >> log_producto" });
	
			Connection con = getConnection();
			String query = "SELECT * FROM producto where id=" + id;
			Statement stmt = null;
			ResultSet rs = null;
			try {
				stmt = con.createStatement();
				rs = stmt.executeQuery(query);
					
				if (rs.next()) {
		            Long productoid = rs.getLong("id");
		            Integer cantidad = rs.getInt("cantidad");
		            String descripcion = rs.getString("descripcion");
		            Producto p = new Producto();
		            p.setId(productoid);
		            p.setCantidad(cantidad);
		            p.setDescripcion(descripcion);
		            return p;
		        }else{
					throw new DataNotFoundException("El producto con el id " + id + " no existe" );
		        }
			} finally {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			}
		
		
		} catch(DataNotFoundException e) {
			throw e;
		}  catch (Exception e) {
			logger.log(Level.SEVERE, "an exception was thrown", e);
		}
		
		return null;
	}
	
	@Override
	public Producto addProducto(Producto producto) throws InvalidFormatException {
		try {
			Runtime runtime = Runtime.getRuntime();
			runtime.exec(new String[] { "sh", "-c", "echo \"[" + LocalDateTime.now() + "] agrega producto nombre " + producto.getDescripcion() + "\" >> log_producto" });
			
			producto.setId(idCounter);
			idCounter++;
			Connection con = getConnection();
			String query = "INSERT INTO producto VALUES(" + producto.getId() + ", " + producto.getCantidad() + ",'" + producto.getDescripcion() + "')";
			Statement stmt = null;
			try {
				stmt = con.createStatement();
				stmt.executeUpdate(query);
			} finally {
				if (stmt != null) {
					stmt.close();
				}
			}
			
			
			return producto;
		} catch(InvalidFormatException e) {
			throw e;
		} catch (Exception e) {
			logger.log(Level.SEVERE, "an exception was thrown", e);
		}
		
		return null;
	}

	@Override
	public Producto updateProducto(Producto producto)  throws InvalidFormatException {
		
	  try {
			Runtime runtime = Runtime.getRuntime();
			runtime.exec(new String[] { "sh", "-c", "echo \"[" + LocalDateTime.now() + "] actualiza producto nombre " + producto.getDescripcion() + "\" >> log_producto" });
	
			CustomValidator.validateAndThrow(producto);		
			
			Statement stmt = null;
			try{
				Connection con = getConnection();
				String query = "UPDATE producto SET cantidad=" + producto.getCantidad() + ", descripcion='" + producto.getDescripcion() + "' WHERE id=" + producto.getId();
				stmt = con.createStatement();
				stmt.executeUpdate(query);
			}catch(Exception e){
				logger.log(Level.SEVERE, "an exception was thrown", e);
			} finally {
				if (stmt != null) {
					stmt.close();
				}
			}
			
			return producto;
	  } catch(InvalidFormatException e) {
			throw e;
	  } catch(Exception e) {
		  logger.log(Level.SEVERE, "an exception was thrown", e);
	  }
	  return null;
	}

	@Override
	public void removeProducto(String id) {
		try {
			Runtime runtime = Runtime.getRuntime();
			runtime.exec(new String[] { "sh", "-c", "echo \"[" + LocalDateTime.now() + "] elimina producto id " + id + "\" >> log_producto" });
	
			Connection con = getConnection();
			String query = "DELETE FROM producto WHERE id=" + id;
			
			Statement stmt = null;
			try {
				stmt = con.createStatement();
				stmt.executeUpdate(query);
			} finally {
				if (stmt != null) {
					stmt.close();
				}
			}
		} catch(Exception e) {
			  logger.log(Level.SEVERE, "an exception was thrown", e);
		}
	}

	private Connection getConnection() throws Exception{
		Context ic = new InitialContext();
		Context ctx = (Context) ic.lookup("java:jboss/datasources/");
		DataSource ds = (DataSource) ctx.lookup("pwbDS");
		return ds.getConnection();
	}
	
}
