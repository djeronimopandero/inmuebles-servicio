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
	public Contrato obtenerContrato(String nroContrato) throws Exception {
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
}
