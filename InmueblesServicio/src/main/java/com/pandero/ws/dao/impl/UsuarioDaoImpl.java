package com.pandero.ws.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import com.pandero.ws.bean.Usuario;
import com.pandero.ws.dao.UsuarioDao;

@Repository
public class UsuarioDaoImpl implements UsuarioDao {

	private static final Logger LOG = LoggerFactory.getLogger(PedidoDaoImpl.class);
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	@Qualifier("dataSource")
	public void setDataSource(DataSource dataSource)	{
	    this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public Usuario obtenerCorreoUsuarioCelula(String usuarioId) throws Exception{
		Usuario usuario =  null;

		SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate);
		call.withCatalogName("dbo");
		call.withProcedureName("USP_LOG_Inmb_ObtenerDatosUsuarioCelula");
		call.withoutProcedureColumnMetaDataAccess();
		call.addDeclaredParameter(new SqlParameter("@UsuarioID", Types.INTEGER));
		call.returningResultSet("p_recordset", new UsuarioMapper());
		SqlParameterSource sqlParameterSource = new MapSqlParameterSource().addValue("@UsuarioID", usuarioId);

		Map<String, Object> mapResultado = call.execute(sqlParameterSource);
		System.out.println("mapResultado:: "+mapResultado);
		List resultado = (List) mapResultado.get("p_recordset");
		if (resultado != null && resultado.size() > 0) {
			usuario = ((List<Usuario>)resultado).get(0);
		}

		return usuario;
	}

	private static final class UsuarioMapper implements RowMapper<Usuario>{
		public Usuario mapRow(ResultSet rs, int rowNum) throws SQLException {			
			Usuario e = new Usuario();	
			e.setUsuarioNombre(rs.getString("UsuarioNombre"));
			e.setCelulaNombre(rs.getString("CelulaDescripcion"));
			e.setCelulaCorreo(rs.getString("CelulaCorreo"));
			e.setEmpleadoCorreo(rs.getString("EmpleadoCorreo"));
			return e;		    
			}
		}
	
}
