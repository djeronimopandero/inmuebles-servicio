package com.pandero.ws.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;


 /**
  * Proyecto: InmueblesServicio
  * @date	: 26 de nov. de 2016
  * @time	: 1:01:20 p. m.
  * @author	: Arly Fernandez.
 */
public class DaoDefinition<T> extends BeanPropertyRowMapper<T>{
	public DaoDefinition(Class<T> mappedClass){
		super(mappedClass);
	}
	
	 /**
	  * @param columna
	  * @param rs
	  * @return	: boolean
	  * @date	: 26 de nov. de 2016
	  * @time	: 1:01:30 p. m.
	  * @author	: Arly Fernandez.
	  * @descripcion : Verifica si la columna enviada esta en el ResultSet enviado tras una consulta	
	 */
	public boolean findColumnaEnResultSet(String columna,ResultSet rs){
		try{
			return rs.findColumn(columna) > 0 ? true : false;
		}catch(SQLException ex){
			return false;
		}
	}
}
