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

import com.pandero.ws.bean.PersonaSAF;
import com.pandero.ws.dao.PersonaDao;

@Repository
public class PersonaDaoImpl implements PersonaDao {

	private static final Logger LOG = LoggerFactory.getLogger(PersonaDaoImpl.class);
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	@Qualifier("dataSource")
	public void setDataSource(DataSource dataSource)	{
	    this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public PersonaSAF obtenerPersonaSAF(String personaID) throws Exception {
		LOG.info("###PersonaDaoImpl.obtenerAsociadosxContratoSAF");		
		List<PersonaSAF> listPersonas = null;

		SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate);
		call.withCatalogName("dbo");
		call.withProcedureName("USP_PER_ObtenerPersonaPorID");
		call.withoutProcedureColumnMetaDataAccess();
		call.addDeclaredParameter(new SqlParameter("@personaID", Types.INTEGER));
		call.returningResultSet("persona", new PersonaMapper());
		SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
		.addValue("@personaID", Integer.parseInt(personaID));
		Map<String, Object> mapResultado = call.execute(sqlParameterSource);
		
		PersonaSAF persona=null;
		List resultado = (List) mapResultado.get("persona");
		if (resultado != null && resultado.size() > 0) {
			listPersonas = (List<PersonaSAF>) resultado;
			persona = listPersonas.get(0);
		}

		return persona;
	}
	
	private static final class PersonaMapper implements RowMapper<PersonaSAF>{
		public PersonaSAF mapRow(ResultSet rs, int rowNum) throws SQLException {			
			PersonaSAF p = new PersonaSAF();
			p.setTipoDocumentoID(rs.getString("TipoDocumentoID"));
			p.setPersonaCodigoDocumento(rs.getString("PersonaCodigoDocumento"));
			p.setNombre(rs.getString("PersonaNombre"));
			p.setApellidoPaterno(rs.getString("PersonaApellidoPaterno"));
			p.setApellidoMaterno(rs.getString("PersonaApellidoMaterno"));
			p.setRazonSocial(rs.getString("PersonaRazonSocial"));
			p.setTipoPersona(rs.getString("PersonaTipoID"));
			p.setNombreCompleto(rs.getString("PersonaNombreCompleto"));
			p.setPersonaID(rs.getInt("PersonaID"));
			p.setPersonaRelacionadaID(rs.getInt("PersonaRelacionadaID"));
			
			return p;		    
		}
	}

	@Override
	public PersonaSAF obtenerProveedorSAF(String tipoDocumento, String nroDocumento, Integer personaId) throws Exception {
		List<PersonaSAF> listPersonas = null;

		SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate);
		call.withCatalogName("dbo");
		call.withProcedureName("USP_PER_ObtenerProveedorPorDocumento");
		call.withoutProcedureColumnMetaDataAccess();
		call.addDeclaredParameter(new SqlParameter("@TipoDocumento", Types.INTEGER));
		call.addDeclaredParameter(new SqlParameter("@NroDocumento", Types.VARCHAR));
		call.addDeclaredParameter(new SqlParameter("@PersonaID", Types.VARCHAR));
		call.returningResultSet("proveedor", new ProveedorMapper());
		SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
		.addValue("@TipoDocumento", Integer.parseInt(tipoDocumento))
		.addValue("@NroDocumento", nroDocumento)
		.addValue("@PersonaID", personaId==null?"":String.valueOf(personaId.intValue()));
		Map<String, Object> mapResultado = call.execute(sqlParameterSource);
		
		PersonaSAF persona=null;
		List resultado = (List) mapResultado.get("proveedor");
		if (resultado != null && resultado.size() > 0) {
			listPersonas = (List<PersonaSAF>) resultado;
			persona = listPersonas.get(0);
		}

		return persona;
	}
	
	private static final class ProveedorMapper implements RowMapper<PersonaSAF>{
		public PersonaSAF mapRow(ResultSet rs, int rowNum) throws SQLException {			
			PersonaSAF p = new PersonaSAF();
			p.setTipoDocumentoID(rs.getString("TipoDocumentoID"));
			p.setPersonaCodigoDocumento(rs.getString("NroDocumento"));
			p.setNombreCompleto(rs.getString("PersonaNombreCompleto"));
			p.setPersonaID(rs.getInt("PersonaID"));
			p.setProveedorID(rs.getInt("ProveedorID"));
			
			return p;		    
		}
	}

	@Override
	public PersonaSAF registrarProveedorSAF(PersonaSAF personaSAF) throws Exception {
		List<PersonaSAF> listPersonas = null;

		SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate);
		call.withCatalogName("dbo");
		call.withProcedureName("USP_PER_RegistrarProveedor");
		call.withoutProcedureColumnMetaDataAccess();
		call.addDeclaredParameter(new SqlParameter("@TipoDocumento", Types.VARCHAR));
		call.addDeclaredParameter(new SqlParameter("@NroDocumento", Types.VARCHAR));
		call.addDeclaredParameter(new SqlParameter("@PersonaNombre", Types.VARCHAR));
		call.addDeclaredParameter(new SqlParameter("@PersonaApellidoPaterno", Types.VARCHAR));
		call.addDeclaredParameter(new SqlParameter("@PersonaApellidoMaterno", Types.VARCHAR));
		call.addDeclaredParameter(new SqlParameter("@PersonaRazonSocial", Types.VARCHAR));	
		call.addDeclaredParameter(new SqlParameter("@TipoProveedor", Types.VARCHAR));	
		call.addDeclaredParameter(new SqlParameter("@PersonaID", Types.VARCHAR));
		call.returningResultSet("proveedor", new ProveedorMapper());
		SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
		.addValue("@TipoDocumento", personaSAF.getTipoDocumentoID())
		.addValue("@NroDocumento", personaSAF.getPersonaCodigoDocumento())
		.addValue("@PersonaNombre", personaSAF.getNombre())
		.addValue("@PersonaApellidoPaterno", personaSAF.getApellidoPaterno())
		.addValue("@PersonaApellidoMaterno", personaSAF.getApellidoMaterno())
		.addValue("@PersonaRazonSocial", personaSAF.getRazonSocial())
		.addValue("@TipoProveedor", personaSAF.getTipoProveedor())
		.addValue("@PersonaID", personaSAF.getPersonaID()==null?"":String.valueOf(personaSAF.getPersonaID().intValue()));
		Map<String, Object> mapResultado = call.execute(sqlParameterSource);
		
		PersonaSAF persona=null;
		List resultado = (List) mapResultado.get("proveedor");
		if (resultado != null && resultado.size() > 0) {
			listPersonas = (List<PersonaSAF>) resultado;
			persona = listPersonas.get(0);
		}

		return persona;
	}

}
