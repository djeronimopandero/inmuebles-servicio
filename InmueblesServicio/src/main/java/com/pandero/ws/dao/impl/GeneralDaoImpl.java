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

import com.pandero.ws.dao.GeneralDao;

@Repository
public class GeneralDaoImpl implements GeneralDao{
	
	private static final Logger LOG = LoggerFactory.getLogger(GeneralDaoImpl.class);
	private JdbcTemplate jdbcTemplate;
		
	@Autowired
	@Qualifier("dataSource")
	public void setDataSource(DataSource dataSource)	{
	    this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	
	@Override
	public Double obtenerTipoCambio(String tipoCambio, String fecha) throws Exception {
		LOG.info("###obtenerTipoCambio tipoCambio:"+tipoCambio+",fecha:"+fecha);
		SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate);
		call.withCatalogName("dbo");
		call.withProcedureName("UFC_ObtieneTipoCambio_RegistroComprobante");
		call.withoutProcedureColumnMetaDataAccess();	
		
		call.addDeclaredParameter(new SqlParameter("@TipoCambio", Types.VARCHAR));
		call.addDeclaredParameter(new SqlParameter("@Fecha", Types.VARCHAR));
		call.addDeclaredParameter(new SqlOutParameter("@Monto", Types.DECIMAL));
		
		MapSqlParameterSource parameters = new MapSqlParameterSource();		
        parameters.addValue("@TipoCambio", tipoCambio);
        parameters.addValue("@Fecha", fecha);
				
		Map resultadoSP = call.execute(parameters);
//		BigDecimal bdDiferenciaPrecio = resultadoSP.get("@tipoCambio")!=null? 
//				new BigDecimal(String.valueOf(resultadoSP.get("@tipoCambio"))):new BigDecimal("0");
		Double dblDiferenciaPrecio = resultadoSP.get("@Monto")!=null? 
				Double.parseDouble(String.valueOf(resultadoSP.get("@Monto"))):0.00;
		LOG.info("###dblDiferenciaPrecio:: "+dblDiferenciaPrecio);
		
		return dblDiferenciaPrecio;
	}

}
