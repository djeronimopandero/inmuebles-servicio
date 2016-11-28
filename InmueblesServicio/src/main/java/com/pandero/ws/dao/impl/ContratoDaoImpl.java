package com.pandero.ws.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.pandero.ws.bean.Contrato;
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
	
	
	@Override
	public List<Contrato> getListContratoAlDia() throws Exception {
		LOG.info("###ContratoDaoImpl.getListContratoAlDia");
		
//		jdbcCall = new SimpleJdbcCall(jdbcTemplate);
//		jdbcCall.withCatalogName(ConstantesDAO.SCHEMA_NAME);
//		jdbcCall.withProcedureName(ConstantesDAO.USP_LOG_INMB_OBTENER_CONTRATOS_AL_DIA)
//		.withoutProcedureColumnMetaDataAccess();
	//	jdbcCall.returningResultSet("contratos", contratoDaoDefinition);
//		List<Contrato> list = jdbcCall.executeObject(List.class);
//		return list;
		return null;
	}

}
