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

import com.pandero.ws.bean.Asociado;
import com.pandero.ws.bean.LiquidacionSAF;
import com.pandero.ws.bean.PersonaSAF;
import com.pandero.ws.dao.LiquidacionDao;

@Repository
public class LiquidacionDaoImpl implements LiquidacionDao {

	private static final Logger LOG = LoggerFactory.getLogger(ContratoDaoImpl.class);
	private JdbcTemplate jdbcTemplate;
		
	@Autowired
	@Qualifier("dataSource")
	public void setDataSource(DataSource dataSource)	{
	    this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public List<LiquidacionSAF> obtenerLiquidacionPorInversionSAF(String nroInversion) throws Exception{
		List<LiquidacionSAF> listaLiquidacion = null;
		
		SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate);
		call.withCatalogName("dbo");
		call.withProcedureName("USP_LOG_Inmb_ObtenerLiquidacionInversion");
		call.withoutProcedureColumnMetaDataAccess();
		call.addDeclaredParameter(new SqlParameter("@NumeroInversion", Types.VARCHAR));
		call.returningResultSet("liquidacion", new LiquidacionMapper());
		SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
		.addValue("@NumeroInversion", nroInversion);
		Map<String, Object> mapResultado = call.execute(sqlParameterSource);
		
		List resultado = (List) mapResultado.get("liquidacion");
		if (resultado != null && resultado.size() > 0) {
			listaLiquidacion = (List<LiquidacionSAF>) resultado;
		}
		
		return listaLiquidacion;
	}
	
	@Override
	public List<LiquidacionSAF> obtenerLiquidacionPorPedidoSAF(String nroPedido) throws Exception{
		List<LiquidacionSAF> listaLiquidacion = null;
		
		SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate);
		call.withCatalogName("dbo");
		call.withProcedureName("USP_LOG_Inmb_ObtenerLiquidacionPedido");
		call.withoutProcedureColumnMetaDataAccess();
		call.addDeclaredParameter(new SqlParameter("@NumeroPedido", Types.VARCHAR));
		call.returningResultSet("liquidacion", new LiquidacionMapper());
		SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
		.addValue("@NumeroPedido", nroPedido);
		Map<String, Object> mapResultado = call.execute(sqlParameterSource);
		
		PersonaSAF persona=null;
		List resultado = (List) mapResultado.get("liquidacion");
		if (resultado != null && resultado.size() > 0) {
			listaLiquidacion = (List<LiquidacionSAF>) resultado;
		}
		
		return listaLiquidacion;
	}
		
	private static final class LiquidacionMapper implements RowMapper<LiquidacionSAF>{
		public LiquidacionSAF mapRow(ResultSet rs, int rowNum) throws SQLException {			
			LiquidacionSAF e = new LiquidacionSAF();	
			e.setPedidoInversionID(rs.getInt("PedidoInversionID"));
			e.setLiquidacionNumero(rs.getString("LiquidacionNumero"));
			e.setLiquidacionTipo(rs.getString("LiquidacionTipo"));
			e.setProveedorID(rs.getInt("ProveedorID"));
			e.setLiquidacionTipoDocumento(rs.getString("LiquidacionTipoDocumento"));
			e.setContratoID(rs.getInt("ContratoID"));
			e.setMonedaID(rs.getString("MonedaID"));
			e.setLiquidacionImporte(rs.getDouble("LiquidacionImporte"));
			e.setLiquidacionOrigen(rs.getString("LiquidacionOrigen"));
			e.setLiquidacionDestino(rs.getString("LiquidacionDestino"));
			e.setLiquidacionEstado(rs.getString("LiquidacionEstado"));
			return e;		    
			}
		}

	@Override
	public String registrarLiquidacionInversionSAF(LiquidacionSAF liquidacionSAF)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	
}
