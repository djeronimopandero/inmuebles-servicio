package com.pandero.ws.dao.impl;

import java.math.BigDecimal;
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
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import com.pandero.ws.bean.Asociado;
import com.pandero.ws.bean.ContratoSAF;
import com.pandero.ws.bean.PersonaSAF;
import com.pandero.ws.dao.ContratoDao;
import com.pandero.ws.dao.GenericDao;

@Repository
public class ContratoDaoImpl implements ContratoDao {

	private static final Logger LOG = LoggerFactory.getLogger(ContratoDaoImpl.class);
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	GenericDao genericDao;
		
	@Autowired
	@Qualifier("dataSource")
	public void setDataSource(DataSource dataSource)	{
	    this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public ContratoSAF obtenerContratoSAF(String nroContrato) throws Exception {
		LOG.info("###ContratoDaoImpl.obtenerContratoSAF");
		
		List<ContratoSAF> listContratos = null;

		SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate);
		call.withCatalogName("dbo");
		call.withProcedureName("USP_FOC_ObtenerContratoPorNumero");
		call.withoutProcedureColumnMetaDataAccess();
		call.addDeclaredParameter(new SqlParameter("@numeroContrato", Types.VARCHAR));
		call.returningResultSet("contrato", new ContratoMapper());
		SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
		.addValue("@numeroContrato", nroContrato);
		Map<String, Object> mapResultado = call.execute(sqlParameterSource);
		
		ContratoSAF contrato=null;
		List resultado = (List) mapResultado.get("contrato");
		if (resultado != null && resultado.size() > 0) {
			listContratos = (List<ContratoSAF>) resultado;
			contrato = listContratos.get(0);
		}

		return contrato;		
	}
	
	@Override
	public Map<String,Object> obtenerContratosEvaluacionCrediticia(Map<String,Object> params) throws Exception{
		return genericDao.executeProcedure(params, "USP_FOC_ObtenerContratosEvaluacionCrediticia");
	}
	
	public Map<String,Object> obtenerGarantiasEvaluacionCrediticia(Map<String,Object> params) throws Exception{
		return genericDao.executeProcedure(params, "USP_CRE_ConsultarCreditoGarantiasInmueble");
	}
	
	
	private static final class ContratoMapper implements RowMapper<ContratoSAF>{
		public ContratoSAF mapRow(ResultSet rs, int rowNum) throws SQLException {			
			ContratoSAF e = new ContratoSAF();	
			e.setNroContrato(rs.getString("ContratoNumero"));
			e.setSituacionContrato(rs.getString("SituacionContratoID"));
			e.setAsociadoId(rs.getInt("PersonaID"));
			e.setContratoId(rs.getInt("ContratoID"));
			e.setFuncionarioServicioyVentas(rs.getString("funcionarioServicioyVentas"));
			e.setCorreoCelula(rs.getString("correoCelula"));
			return e;		    
			}
		}
	
	
	private static final class ContratoAlDiaMapper implements RowMapper<ContratoSAF>{
		public ContratoSAF mapRow(ResultSet rs, int rowNum) throws SQLException {			
			ContratoSAF e = new ContratoSAF();
			e.setContratoId(rs.getInt("ContratoID"));
			e.setNroContrato(rs.getString("ContratoNumero"));
			e.setFechaVenta(rs.getString("ContratoFechaVenta"));
			e.setMontoCertificado(rs.getDouble("CertificadoValor"));
			e.setPedidoId(rs.getInt("PersonaID"));
			e.setSituacionContrato(rs.getString("SituacionContratoNombre"));
			e.setFechaAdjudicacion(rs.getString("FechaAdjudicacion"));
			e.setSituacionContratoID(rs.getInt("SituacionContratoID"));
			e.setPersonaId(rs.getInt("PersonaID"));
			e.setEsAdjudicado(rs.getInt("TipoAdjudicacion"));
			return e;		    
		}
	}
	
	@Override
	public List<ContratoSAF> getListContratoAlDia() throws Exception {		
		LOG.info("###ContratoDaoImpl.getListContratoAlDia");
		
		List<ContratoSAF> listContratos = null;

		SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate);
		call.withCatalogName("dbo");
		call.withProcedureName("USP_LOG_INMB_OBTENER_CONTRATOS_AL_DIA");
		call.withoutProcedureColumnMetaDataAccess();
		call.returningResultSet("contratos", new ContratoAlDiaMapper());
		SqlParameterSource sqlParameterSource = new MapSqlParameterSource();

		Map<String, Object> mapResultado = call.execute(sqlParameterSource);
		List resultado = (List) mapResultado.get("contratos");
		if (resultado != null && resultado.size() > 0) {
			listContratos = (List<ContratoSAF>) resultado;
		}

		return listContratos;
	}

	@Override
	public Double obtenerDiferenciaPrecioPorContrato(String nroContrato) throws Exception {
		LOG.info("###obtenerDiferenciaPrecioPorContrato : "+nroContrato);
		SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate);
		call.withCatalogName("dbo");
		call.withProcedureName("USP_FOC_ObtenerDiferenciaPrecioPorContrato");
		call.withoutProcedureColumnMetaDataAccess();	
		
		call.addDeclaredParameter(new SqlParameter("@nroContrato", Types.VARCHAR));
		call.addDeclaredParameter(new SqlOutParameter("@diferenciaPrecio", Types.DECIMAL));
		
		MapSqlParameterSource parameters = new MapSqlParameterSource();		
        parameters.addValue("@nroContrato", nroContrato);
				
		Map resultadoSP = call.execute(parameters);
		BigDecimal bdDiferenciaPrecio = resultadoSP.get("@diferenciaPrecio")!=null? 
				new BigDecimal(String.valueOf(resultadoSP.get("@diferenciaPrecio"))):new BigDecimal("0");
		LOG.info("###strDiferenciaPrecio:: "+bdDiferenciaPrecio);
		
		return bdDiferenciaPrecio.doubleValue();
	}

	public List<Asociado> obtenerAsociadosxContratoSAF(String nroContrato){
		List<Asociado> listAsociados = null;
		
		SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate);
		call.withCatalogName("dbo");
		call.withProcedureName("USP_LOG_Inmb_ObtenerAsociadosPorContrato");
		call.withoutProcedureColumnMetaDataAccess();
		call.addDeclaredParameter(new SqlParameter("@NumeroContrato", Types.VARCHAR));
		call.returningResultSet("asociados", new AsociadoMapper());
		SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
		.addValue("@NumeroContrato", nroContrato);
		Map<String, Object> mapResultado = call.execute(sqlParameterSource);
		
		PersonaSAF persona=null;
		List resultado = (List) mapResultado.get("asociados");
		if (resultado != null && resultado.size() > 0) {
			listAsociados = (List<Asociado>) resultado;
		}
		
		return listAsociados;
	}
		
	private static final class AsociadoMapper implements RowMapper<Asociado>{
		public Asociado mapRow(ResultSet rs, int rowNum) throws SQLException {			
			Asociado e = new Asociado();	
			e.setNombreCompleto(rs.getString("PersonaNombreCompleto"));
			e.setTipoDocumentoIdentidad(rs.getString("TipoDocumento"));
			e.setNroDocumentoIdentidad(rs.getString("PersonaCodigoDocumento"));
			e.setDireccion(rs.getString("PersonaDireccion"));
			return e;		    
			}
		}
}
