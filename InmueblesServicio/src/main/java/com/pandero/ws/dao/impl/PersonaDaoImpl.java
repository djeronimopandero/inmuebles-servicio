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
import com.pandero.ws.util.Util;

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
		LOG.info("###PersonaDaoImpl.obtenerAsociadosxContratoSAF personaID:"+personaID);		
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
			p.setTipoDocumentoID(null!=rs.getString("TipoDocumentoID")?rs.getString("TipoDocumentoID"):"");
			p.setPersonaCodigoDocumento(null!=rs.getString("PersonaCodigoDocumento")?rs.getString("PersonaCodigoDocumento"):"");
			p.setNombre(null!=rs.getString("PersonaNombre")?rs.getString("PersonaNombre"):"");
			p.setApellidoPaterno(null!=rs.getString("PersonaApellidoPaterno")?rs.getString("PersonaApellidoPaterno"):"");
			p.setApellidoMaterno(null!=rs.getString("PersonaApellidoMaterno")?rs.getString("PersonaApellidoMaterno"):"");
			p.setRazonSocial(null!=rs.getString("PersonaRazonSocial")?rs.getString("PersonaRazonSocial"):"");
			p.setTipoPersona(null!=rs.getString("PersonaTipoID")?rs.getString("PersonaTipoID"):"");
			p.setNombreCompleto(null!=rs.getString("PersonaNombreCompleto")?rs.getString("PersonaNombreCompleto"):"");
			p.setPersonaID(rs.getInt("PersonaID"));
			p.setPersonaRelacionadaID(rs.getInt("PersonaRelacionadaID"));
			return p;		    
		}
	}
	
	private static final class PersonaPorDocMapper implements RowMapper<PersonaSAF>{
		public PersonaSAF mapRow(ResultSet rs, int rowNum) throws SQLException {			
			PersonaSAF p = new PersonaSAF();
			p.setNombreCompleto(null!=rs.getString("PersonaNombreCompleto")?rs.getString("PersonaNombreCompleto"):"");
			p.setPersonaID(rs.getInt("PersonaID"));
			p.setTipoDocumentoID(rs.getString("TipoDocumentoID"));
			p.setPersonaCodigoDocumento(rs.getString("PersonaCodigoDocumento"));
			return p;		    
		}
	}

	@Override
	public PersonaSAF obtenerProveedorSAF(Integer proveedorId, String tipoProveedor, Integer personaID, 
			String tipoDocumento, String nroDocumento) throws Exception {
		List<PersonaSAF> listPersonas = null;
		LOG.info("obtenerProveedorSAF:::  proveedorId="+proveedorId+", tipoProveedor="+tipoProveedor+", personaID="+personaID
				+", tipoDocumento="+tipoDocumento+", nroDocumento="+nroDocumento);

		SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate);
		call.withCatalogName("dbo");
		call.withProcedureName("USP_PER_ObtenerProveedor");
		call.withoutProcedureColumnMetaDataAccess();
		call.addDeclaredParameter(new SqlParameter("@ProveedorID", Types.INTEGER));
		call.addDeclaredParameter(new SqlParameter("@TipoProveedorID", Types.INTEGER));
		call.addDeclaredParameter(new SqlParameter("@TipoDocumento", Types.INTEGER));
		call.addDeclaredParameter(new SqlParameter("@NroDocumento", Types.VARCHAR));
		call.addDeclaredParameter(new SqlParameter("@PersonaID", Types.INTEGER));
		call.returningResultSet("proveedor", new ProveedorMapper());
		SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
		.addValue("@ProveedorID", proveedorId==null?0:proveedorId)
		.addValue("@TipoProveedorID", Integer.parseInt(Util.esVacio(tipoProveedor)?"0":tipoProveedor))
		.addValue("@PersonaID", personaID==null?0:personaID)
		.addValue("@TipoDocumento", Integer.parseInt(Util.esVacio(tipoDocumento)?"0":tipoDocumento))
		.addValue("@NroDocumento", nroDocumento);
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
		if(personaSAF!=null){
			LOG.info("registrarProveedorSAF:::  tipoProveedor="+personaSAF.getTipoProveedor()+", personaID="+personaSAF.getPersonaID()
					+", tipoDocumento="+personaSAF.getTipoDocumentoID()+", nroDocumento="+personaSAF.getPersonaCodigoDocumento());
		}

		SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate);
		call.withCatalogName("dbo");
		call.withProcedureName("USP_PER_RegistrarProveedor");
		call.withoutProcedureColumnMetaDataAccess();
		call.addDeclaredParameter(new SqlParameter("@TipoProveedor", Types.VARCHAR));	
		call.addDeclaredParameter(new SqlParameter("@PersonaID", Types.VARCHAR));
		call.addDeclaredParameter(new SqlParameter("@TipoDocumento", Types.VARCHAR));
		call.addDeclaredParameter(new SqlParameter("@NroDocumento", Types.VARCHAR));
		call.addDeclaredParameter(new SqlParameter("@PersonaNombre", Types.VARCHAR));
		call.addDeclaredParameter(new SqlParameter("@PersonaApellidoPaterno", Types.VARCHAR));
		call.addDeclaredParameter(new SqlParameter("@PersonaApellidoMaterno", Types.VARCHAR));
		call.addDeclaredParameter(new SqlParameter("@PersonaRazonSocial", Types.VARCHAR));			
		call.returningResultSet("proveedor", new ProveedorMapper());
		SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
		.addValue("@TipoProveedor", personaSAF.getTipoProveedor())
		.addValue("@PersonaID", personaSAF.getPersonaID())
		.addValue("@TipoDocumento", personaSAF.getTipoDocumentoID())
		.addValue("@NroDocumento", personaSAF.getPersonaCodigoDocumento())
		.addValue("@PersonaNombre", personaSAF.getNombre())
		.addValue("@PersonaApellidoPaterno", personaSAF.getApellidoPaterno())
		.addValue("@PersonaApellidoMaterno", personaSAF.getApellidoMaterno())
		.addValue("@PersonaRazonSocial", personaSAF.getRazonSocial());		
		Map<String, Object> mapResultado = call.execute(sqlParameterSource);
		
		PersonaSAF persona=null;
		List resultado = (List) mapResultado.get("proveedor");
		if (resultado != null && resultado.size() > 0) {
			listPersonas = (List<PersonaSAF>) resultado;
			persona = listPersonas.get(0);
		}

		return persona;
	}

	/**
	 * @method	: obtenerPersonaPorDoc
	 * @date	: 26 de dic. de 2016
	 * @time	: 10:57:01 a. m.
	 * @author	: Arly Fernandez.
	 * @descripcion : Obtener persona de la tabla PER_Persona por tipo y numero de documento	
	 */
	@Override
	public PersonaSAF obtenerPersonaPorDoc(Integer tipoDoc, String nroDoc) throws Exception {
		List<PersonaSAF> listPersonas = null;

		SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate);
		call.withCatalogName("dbo");
		call.withProcedureName("USP_PER_ObtenerProveedorPorDocumento");
		call.withoutProcedureColumnMetaDataAccess();
		call.addDeclaredParameter(new SqlParameter("@TipoDocumento", Types.INTEGER));	
		call.addDeclaredParameter(new SqlParameter("@NroDocumento", Types.VARCHAR));
		call.returningResultSet("persona", new PersonaPorDocMapper());
		SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
		.addValue("@TipoDocumento", tipoDoc)
		.addValue("@NroDocumento", nroDoc);
		Map<String, Object> mapResultado = call.execute(sqlParameterSource);
		
		PersonaSAF persona=null;
		List resultado = (List) mapResultado.get("persona");
		if (resultado != null && resultado.size() > 0) {
			listPersonas = (List<PersonaSAF>) resultado;
			persona = listPersonas.get(0);
		}

		return persona;
	}

}
