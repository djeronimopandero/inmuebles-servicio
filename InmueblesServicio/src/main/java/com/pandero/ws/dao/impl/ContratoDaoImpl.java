package com.pandero.ws.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import com.pandero.ws.bean.Contrato;
import com.pandero.ws.bean.ContratoSAF;
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
	public Contrato obtenerContratoSAF(String nroContrato) throws Exception {
			String query = 	"select ContratoNumero, SituacionContratoID, PersonaID "+
					"from FOC_Contrato where ContratoNumero='"+nroContrato+"'";
							
		List<Contrato> listaContratos = this.jdbcTemplate.query(query, new ContratoMapper());
		Contrato contrato = null;	
		if(listaContratos!=null && listaContratos.size()>0){	
			contrato = listaContratos.get(0);
			System.out.println("listaContratos:: "+listaContratos.size());
		}		
		return contrato;
	}
	
	private static final class ContratoMapper implements RowMapper<Contrato>{
		public Contrato mapRow(ResultSet rs, int rowNum) throws SQLException {			
			Contrato e = new Contrato();	
			e.setNroContrato(rs.getString("ContratoNumero"));
			e.setSituacionContrato(rs.getString("SituacionContratoID"));
			e.setAsociadoId(rs.getInt("PersonaID"));
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

}
