package py.una.pol.iin.pwb.mybatis;

import java.io.IOException;
import java.io.Reader;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class MyBatisUtil {
	private final static String RESOURCE = "py/una/pol/iin/pwb/mybatis/configuration.xml";
	private static SqlSessionFactory sqlMapper = null;
	
	public static SqlSession getSession() throws Exception
	{
	
		if (sqlMapper == null) { createSessionFactory(); }
		return sqlMapper.openSession();
	
	}
	
	private static void createSessionFactory() throws IOException
	{
		Reader reader = Resources.getResourceAsReader(RESOURCE);
		sqlMapper = new SqlSessionFactoryBuilder().build(reader);
	}
}
