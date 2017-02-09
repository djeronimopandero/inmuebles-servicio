package com.pandero.ws.dao.impl;

import java.sql.Types;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import com.pandero.ws.bean.Garantia;
import com.pandero.ws.dao.GarantiaDao;
import com.pandero.ws.util.Util;

@Repository
public class GarantiaDaoImpl implements GarantiaDao {

	private static final Logger LOG = LoggerFactory.getLogger(GarantiaDaoImpl.class);
	private JdbcTemplate jdbcTemplate;
		
	@Autowired
	@Qualifier("dataSource")
	public void setDataSource(DataSource dataSource)	{
	    this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public String crearGarantiaSAF(Garantia garantia, String usuarioId) throws Exception {
		String garantiaSAFId = "";
		SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate);
		call.withProcedureName("dbo.USP_LOG_Inmb_AgregarGarantia");
		call.withoutProcedureColumnMetaDataAccess();	
				
		call.addDeclaredParameter(new SqlParameter("@PedidoNumero", Types.VARCHAR));
		call.addDeclaredParameter(new SqlParameter("@PartidaRegistral", Types.VARCHAR));		
		call.addDeclaredParameter(new SqlParameter("@FichaConstitucion", Types.VARCHAR));
		call.addDeclaredParameter(new SqlParameter("@FechaConstitucion", Types.VARCHAR));
		call.addDeclaredParameter(new SqlParameter("@UsoBien", Types.VARCHAR));
		call.addDeclaredParameter(new SqlParameter("@UsuarioID", Types.INTEGER));
		call.addDeclaredParameter(new SqlOutParameter("@GarantiaInmbID", Types.VARCHAR));
				
		MapSqlParameterSource parameters = new MapSqlParameterSource()
		.addValue("@PedidoNumero", garantia.getPedidoNumero())
		.addValue("@PartidaRegistral", garantia.getPartidaRegistral())
		.addValue("@FichaConstitucion", garantia.getFichaConstitucion())
		.addValue("@FechaConstitucion", garantia.getFechaConstitucion())
		.addValue("@UsoBien", garantia.getUsoBien())
		.addValue("@UsuarioID", Util.convertirCadenaAInt(usuarioId));     
				
		Map resultadoSP = call.execute(parameters);
		garantiaSAFId = resultadoSP.get("@GarantiaInmbID")!=null?(String)resultadoSP.get("@GarantiaInmbID"):"0";
		System.out.println("RESULTADOS:: "+garantiaSAFId);
		return garantiaSAFId;
	}

	
	
	@Override
	public String editarGarantiaSAF(Garantia garantia, String usuarioId) throws Exception {
		SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate);
		call.withProcedureName("dbo.USP_LOG_Inmb_EditarGarantia");
		call.withoutProcedureColumnMetaDataAccess();	
		
		call.addDeclaredParameter(new SqlParameter("@GarantiaInmbID", Types.VARCHAR));
		call.addDeclaredParameter(new SqlParameter("@PartidaRegistral", Types.VARCHAR));		
		call.addDeclaredParameter(new SqlParameter("@FichaConstitucion", Types.VARCHAR));
		call.addDeclaredParameter(new SqlParameter("@FechaConstitucion", Types.VARCHAR));
		call.addDeclaredParameter(new SqlParameter("@UsoBien", Types.VARCHAR));
		call.addDeclaredParameter(new SqlParameter("@MontoPrima", Types.VARCHAR));
		call.addDeclaredParameter(new SqlParameter("@Modalidad", Types.VARCHAR));
		call.addDeclaredParameter(new SqlParameter("@NroContrato", Types.INTEGER));
		call.addDeclaredParameter(new SqlParameter("@UsuarioID", Types.INTEGER));
		
		MapSqlParameterSource parameters = new MapSqlParameterSource();	
		parameters.addValue("@GarantiaInmbID", String.valueOf(garantia.getGarantiaSAFId().intValue()));
		parameters.addValue("@PartidaRegistral", garantia.getPartidaRegistral());
		parameters.addValue("@FichaConstitucion", garantia.getFichaConstitucion());
		parameters.addValue("@FechaConstitucion", garantia.getFechaConstitucion());
		parameters.addValue("@UsoBien", garantia.getUsoBien());
		parameters.addValue("@MontoPrima", garantia.getMontoPrima());
		parameters.addValue("@Modalidad", garantia.getModalidad());
		parameters.addValue("@NroContrato", Util.convertirCadenaAInt(garantia.getNroContrato()));
		parameters.addValue("@UsuarioID", Util.convertirCadenaAInt(usuarioId));
		
				
		System.out.println("garantia.getMontoPrima()>"+garantia.getMontoPrima()+"< >"+garantia.getUsoBien());
		System.out.println("garantia.getGarantiaSAFId()>"+String.valueOf(garantia.getGarantiaSAFId().intValue())+"<");
		call.execute(parameters);
		return null;
	}

	@Override
	public String eliminarGarantiaSAF(String garantiaSAFId, String usuarioId) throws Exception {
		SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate);
		call.withProcedureName("dbo.USP_LOG_Inmb_EliminarGarantia");
		call.withoutProcedureColumnMetaDataAccess();	
		
		call.addDeclaredParameter(new SqlParameter("@GarantiaInmbID", Types.VARCHAR));
		call.addDeclaredParameter(new SqlParameter("@UsuarioID", Types.INTEGER));
		
		MapSqlParameterSource parameters = new MapSqlParameterSource();	
		parameters.addValue("@GarantiaInmbID", garantiaSAFId);
		parameters.addValue("@UsuarioID", Util.convertirCadenaAInt(usuarioId));
				
		call.execute(parameters);
		return null;
	}
	
	
}
