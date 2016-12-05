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

import com.pandero.ws.bean.ContratoSAF;
import com.pandero.ws.bean.ResultadoBean;
import com.pandero.ws.dao.ContratoDao;

@Repository
public class ContratoDaoImpl implements ContratoDao {

	private static final Logger LOG = LoggerFactory.getLogger(ContratoDaoImpl.class);
	private JdbcTemplate jdbcTemplate;
		
	@Autowired
	@Qualifier("dataSource")
	public void setDataSource(DataSource dataSource)	{
	    this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public ContratoSAF obtenerContratoSAF(String nroContrato) throws Exception {
			String query = 	"select ContratoNumero, SituacionContratoID, PersonaID, ContratoID "+
					"from FOC_Contrato where ContratoNumero='"+nroContrato+"'";
							
		List<ContratoSAF> listaContratos = this.jdbcTemplate.query(query, new ContratoMapper());
		ContratoSAF contrato = null;	
		if(listaContratos!=null && listaContratos.size()>0){	
			contrato = listaContratos.get(0);
			System.out.println("listaContratos:: "+listaContratos.size());
		}		
		return contrato;
	}
	
	private static final class ContratoMapper implements RowMapper<ContratoSAF>{
		public ContratoSAF mapRow(ResultSet rs, int rowNum) throws SQLException {			
			ContratoSAF e = new ContratoSAF();	
			e.setNroContrato(rs.getString("ContratoNumero"));
			e.setSituacionContrato(rs.getString("SituacionContratoID"));
			e.setAsociadoId(rs.getInt("PersonaID"));
			e.setContratoId(rs.getInt("ContratoID"));
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
	public Double obtenerDiferenciaPrecioPorContrato(Integer contratoId) throws Exception {
		LOG.info("###obtenerDiferenciaPrecioPorContrato : "+contratoId);
		ResultadoBean resultado = null;
		SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate);
		call.withCatalogName("dbo");
		call.withProcedureName("USP_FOC_ObtenerDiferenciaPrecioPorContrato");
		call.withoutProcedureColumnMetaDataAccess();	
		
		call.addDeclaredParameter(new SqlParameter("@contratoid", Types.INTEGER));
		call.addDeclaredParameter(new SqlOutParameter("@diferenciaPrecio", Types.DECIMAL));
		
		MapSqlParameterSource parameters = new MapSqlParameterSource();		
        parameters.addValue("@contratoid", contratoId);
				
		Map resultadoSP = call.execute(parameters);
		BigDecimal bdDiferenciaPrecio = resultadoSP.get("@diferenciaPrecio")!=null? 
				new BigDecimal(String.valueOf(resultadoSP.get("@diferenciaPrecio"))):new BigDecimal("0");
		LOG.info("###strDiferenciaPrecio:: "+bdDiferenciaPrecio);
		
		return bdDiferenciaPrecio.doubleValue();
	}

}
