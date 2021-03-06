package com.pandero.ws.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
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

import com.pandero.ws.bean.ConceptoLiquidacion;
import com.pandero.ws.bean.LiquidacionSAF;
import com.pandero.ws.bean.SolicitudAutorizacion;
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
	public List<LiquidacionSAF> obtenerLiquidacionesPorInversionSAF(String nroInversion) throws Exception{
		List<LiquidacionSAF> listaLiquidacion = null;
		
		SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate);
		call.withCatalogName("dbo");
		call.withProcedureName("USP_LOG_Inmb_ObtenerLiquidacionInversion"); // EN .net USP_LOG_Inmb_ObtenerLiquidacionInversionDetalle
		call.withoutProcedureColumnMetaDataAccess();
		call.addDeclaredParameter(new SqlParameter("@NumeroInversion", Types.VARCHAR));
		call.returningResultSet("liquidacion", new LiquidacionPorInversionMapper());
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
	public List<LiquidacionSAF> obtenerLiquidacionPorInversionArmada(
			String nroInversion, String nroArmada) throws Exception {
		List<LiquidacionSAF> listaLiquidacionArmada = null;
		
		List<LiquidacionSAF> listaLiquidacion=obtenerLiquidacionesPorInversionSAF(nroInversion);
		
		if(listaLiquidacion!=null && listaLiquidacion.size()>0){	
			listaLiquidacionArmada = new ArrayList<LiquidacionSAF>();
			for(LiquidacionSAF liquidacion : listaLiquidacion){
				if(liquidacion.getNroArmada()==Integer.parseInt(nroArmada)){
					listaLiquidacionArmada.add(liquidacion);
				}
			}
			if(listaLiquidacionArmada.size()==0) listaLiquidacionArmada=null;
		}
		
		return listaLiquidacionArmada;
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
		
		List resultado = (List) mapResultado.get("liquidacion");
		if (resultado != null && resultado.size() > 0) {
			listaLiquidacion = (List<LiquidacionSAF>) resultado;
		}
		
		return listaLiquidacion;
	}
		
	private static final class LiquidacionMapper implements RowMapper<LiquidacionSAF>{
		public LiquidacionSAF mapRow(ResultSet rs, int rowNum) throws SQLException {			
			LiquidacionSAF e = new LiquidacionSAF();	
			e.setPedidoID(rs.getInt("PedidoID"));
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
			e.setNroArmada(rs.getInt("NroArmada"));
			return e;		    
			}
		}

	private static final class LiquidacionPorInversionMapper implements RowMapper<LiquidacionSAF>{
		public LiquidacionSAF mapRow(ResultSet rs, int rowNum) throws SQLException {			
			LiquidacionSAF e = new LiquidacionSAF();	
			e.setPedidoID(rs.getInt("PedidoID"));
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
			e.setNroArmada(rs.getInt("NroArmada"));
			e.setLiquidacionPagoTesoreria(rs.getString("LiquidacionPagoTesoreria"));
			e.setLiquidacionFecha(rs.getString("Fecha"));
			e.setUsuarioIdCreacion(rs.getInt("UsuarioIDCreacion"));
			e.setUsuarioLogin(rs.getString("UsuarioNombre"));
			e.setNroReferenciaLiquidacionTesoreria(rs.getString("PagoTesoreriaNumeroReferencia"));
			return e;		    
			}
		}

	
	@Override
	public String obtenerCorrelativoLiquidacionSAF(String pedidoId) throws Exception{
		String nroCorrelativo = null;
		
		SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate);
		call.withProcedureName("dbo.USP_LOG_Inmb_ObtenerCorrelativoLiquidacion");
		call.withoutProcedureColumnMetaDataAccess();	
				
		call.addDeclaredParameter(new SqlParameter("@PedidoID", Types.INTEGER));		
		call.addDeclaredParameter(new SqlOutParameter("@LiquidacionNumero", Types.VARCHAR));
				
		MapSqlParameterSource parameters = new MapSqlParameterSource();		
        parameters.addValue("@PedidoID", pedidoId);
				
		Map resultadoSP = call.execute(parameters);
		nroCorrelativo = resultadoSP.get("@LiquidacionNumero")!=null?(String)resultadoSP.get("@LiquidacionNumero"):"";
		System.out.println("NRO CORRELATIVO:: "+nroCorrelativo);
		
		return nroCorrelativo;
	}
	
	@Override
	public String registrarLiquidacionInversionSAF(LiquidacionSAF liquidacionSAF, String usuarioId)
			throws Exception {
		SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate);
		call.withProcedureName("dbo.USP_LOG_Inmb_RegistrarLiquidacion");
		call.withoutProcedureColumnMetaDataAccess();	
				
		call.addDeclaredParameter(new SqlParameter("@PedidoID", Types.INTEGER));
		call.addDeclaredParameter(new SqlParameter("@PedidoInversion", Types.INTEGER));		
		call.addDeclaredParameter(new SqlParameter("@LiquidacionNumero", Types.VARCHAR));		
		call.addDeclaredParameter(new SqlParameter("@LiquidacionTipo", Types.VARCHAR));
		call.addDeclaredParameter(new SqlParameter("@ProveedorID", Types.INTEGER));		
		call.addDeclaredParameter(new SqlParameter("@LiquidacionTipoDocumento", Types.VARCHAR));		
		call.addDeclaredParameter(new SqlParameter("@ContratoID", Types.INTEGER));
		call.addDeclaredParameter(new SqlParameter("@MonedaID", Types.VARCHAR));		
		call.addDeclaredParameter(new SqlParameter("@LiquidacionImporte", Types.DECIMAL));		
		call.addDeclaredParameter(new SqlParameter("@LiquidacionOrigen", Types.VARCHAR));
		call.addDeclaredParameter(new SqlParameter("@LiquidacionDestino", Types.VARCHAR));
		call.addDeclaredParameter(new SqlParameter("@LiquidacionEstado", Types.VARCHAR));
		call.addDeclaredParameter(new SqlParameter("@UsuarioID", Types.VARCHAR));
		call.addDeclaredParameter(new SqlParameter("@NroArmada", Types.VARCHAR));
						
		MapSqlParameterSource parameters = new MapSqlParameterSource();		
        parameters.addValue("@PedidoID", liquidacionSAF.getPedidoID());
        parameters.addValue("@PedidoInversion", liquidacionSAF.getPedidoInversionID());
        parameters.addValue("@LiquidacionNumero", liquidacionSAF.getLiquidacionNumero());
        parameters.addValue("@LiquidacionTipo", liquidacionSAF.getLiquidacionTipo());
        parameters.addValue("@ProveedorID", liquidacionSAF.getProveedorID());
        parameters.addValue("@LiquidacionTipoDocumento", liquidacionSAF.getLiquidacionTipoDocumento());
        parameters.addValue("@ContratoID", liquidacionSAF.getContratoID());
        parameters.addValue("@MonedaID", liquidacionSAF.getMonedaID());
        parameters.addValue("@LiquidacionImporte", liquidacionSAF.getLiquidacionImporte());
        parameters.addValue("@LiquidacionOrigen", liquidacionSAF.getLiquidacionOrigen());
        parameters.addValue("@LiquidacionDestino", liquidacionSAF.getLiquidacionDestino());
        parameters.addValue("@LiquidacionEstado", liquidacionSAF.getLiquidacionEstado());
		parameters.addValue("@UsuarioID", usuarioId);
		parameters.addValue("@NroArmada", liquidacionSAF.getNroArmada());
		
		call.execute(parameters);
		
		return null;
	}

	@Override
	public String eliminarLiquidacionInversionSAF(String nroInversion,
			String nroArmada, String usuarioId) throws Exception {
		SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate);
		call.withProcedureName("dbo.USP_LOG_Inmb_EliminarLiquidacionInversion");
		call.withoutProcedureColumnMetaDataAccess();	
				
		call.addDeclaredParameter(new SqlParameter("@NumeroInversion", Types.VARCHAR));		
		call.addDeclaredParameter(new SqlParameter("@NroArmada", Types.VARCHAR));
		call.addDeclaredParameter(new SqlParameter("@UsuarioID", Types.INTEGER));
						
		MapSqlParameterSource parameters = new MapSqlParameterSource();		
        parameters.addValue("@NumeroInversion", nroInversion);		
		parameters.addValue("@NroArmada", nroArmada);
		parameters.addValue("@UsuarioID", usuarioId);
				
		call.execute(parameters);
		
		return null;
	}

	@Override
	public String confirmarLiquidacionInversion(String nroInversion, String nroArmada, String usuarioId)
			throws Exception {
		SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate);
		call.withProcedureName("dbo.USP_LOG_Inmb_ConfirmarLiquidacionInversion");
		call.withoutProcedureColumnMetaDataAccess();	
				
		call.addDeclaredParameter(new SqlParameter("@NumeroInversion", Types.VARCHAR));
		call.addDeclaredParameter(new SqlParameter("@NroArmada", Types.VARCHAR));
		call.addDeclaredParameter(new SqlParameter("@UsuarioID", Types.INTEGER));
						
		MapSqlParameterSource parameters = new MapSqlParameterSource();		
        parameters.addValue("@NumeroInversion", nroInversion);
        parameters.addValue("@NroArmada", nroArmada);
		parameters.addValue("@UsuarioID", usuarioId);
				
		call.execute(parameters);
		
		return null;
	}
	
	@Override
    public Map<String,Object> executeProcedure(Map<String,Object> parameters, String procedureName){
    	SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate);
    	call.withProcedureName(procedureName);
    	MapSqlParameterSource in = new MapSqlParameterSource();
    	for(Map.Entry<String, Object> parameter:parameters.entrySet()){
    		in.addValue(parameter.getKey(), parameter.getValue());
    	}
    	Map<String,Object> procedureResult = call.execute(in);
		return procedureResult;
    }
	
	
	
	

	@Override
	public List<ConceptoLiquidacion> obtenerConceptosLiquidacion(
			String nroInversion) throws Exception {
		System.out.println("USP_LOG_Inmb_MostratConceptos_ActaEntrega:: "+nroInversion);
		List<ConceptoLiquidacion> listaConceptos = null;
		
		SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate);
		call.withCatalogName("dbo");
		call.withProcedureName("USP_LOG_Inmb_MostratConceptos_ActaEntrega");
		call.withoutProcedureColumnMetaDataAccess();
		call.addDeclaredParameter(new SqlParameter("@NumeroInversion", Types.VARCHAR));
		call.returningResultSet("concepto", new ConceptoLiquidacionMapper());
		SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
		.addValue("@NumeroInversion", nroInversion);
		Map<String, Object> mapResultado = call.execute(sqlParameterSource);
		
		List resultado = (List) mapResultado.get("concepto");
		if (resultado != null && resultado.size() > 0) {
			listaConceptos = (List<ConceptoLiquidacion>) resultado;
		}
		
		return listaConceptos;
	}
		
	private static final class ConceptoLiquidacionMapper implements RowMapper<ConceptoLiquidacion>{
		public ConceptoLiquidacion mapRow(ResultSet rs, int rowNum) throws SQLException {			
			ConceptoLiquidacion e = new ConceptoLiquidacion();	
			e.setConceptoID(rs.getString("ConceptoID"));
			e.setConceptoNombre(rs.getString("ConceptoNombre"));
			e.setImporte(rs.getDouble("Importe"));
			e.setImportePagado(rs.getDouble("ImportePagado"));
			e.setTotalPendiente(rs.getDouble("TotalPendiente"));
			e.setSituacionID(rs.getString("SituacionID"));
			e.setSituacion(rs.getString("Situacion"));
			e.setContratoID(rs.getInt("ContratoID"));
			e.setContratoNumero(rs.getString("ContratoNumero"));
			e.setPedidoID(rs.getInt("PedidoID"));
			e.setPedidoInversionID(rs.getInt("VehiculoID"));
			return e;		    
			}
		}

	@Override
	public String eliminarConformidadInversion(String nroInversion, String nroArmada, String usuarioId) throws Exception {
		SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate);
		call.withProcedureName("dbo.USP_LOG_Inmb_EliminarConformidadLiquidacion");
		call.withoutProcedureColumnMetaDataAccess();	
				
		call.addDeclaredParameter(new SqlParameter("@NumeroInversion", Types.VARCHAR));
		call.addDeclaredParameter(new SqlParameter("@NroArmada", Types.VARCHAR));
		call.addDeclaredParameter(new SqlParameter("@UsuarioID", Types.INTEGER));
						
		MapSqlParameterSource parameters = new MapSqlParameterSource();		
        parameters.addValue("@NumeroInversion", nroInversion);
        parameters.addValue("@NroArmada", nroArmada);
		parameters.addValue("@UsuarioID", usuarioId);
				
		call.execute(parameters);
		
		return null;
	}

	@Override
	public List<SolicitudAutorizacion> obtenerSolicitudesAutorizacionPorInversion(
			String nroInversion) throws Exception {
		List<SolicitudAutorizacion> listaSolicitudes = null;
		
		SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate);
		call.withCatalogName("dbo");
		call.withProcedureName("USP_GEN_ObtenerSolicitudAutorizacionPorInversion");
		call.withoutProcedureColumnMetaDataAccess();
		call.addDeclaredParameter(new SqlParameter("@NroInversion", Types.VARCHAR));
		call.returningResultSet("solicitud", new SolicitudAutorizacionMapper());
		SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
		.addValue("@NroInversion", nroInversion);
		Map<String, Object> mapResultado = call.execute(sqlParameterSource);
		
		List resultado = (List) mapResultado.get("solicitud");
		if (resultado != null && resultado.size() > 0) {
			listaSolicitudes = (List<SolicitudAutorizacion>) resultado;
		}
		
		return listaSolicitudes;
	}

	private static final class SolicitudAutorizacionMapper implements RowMapper<SolicitudAutorizacion>{
		public SolicitudAutorizacion mapRow(ResultSet rs, int rowNum) throws SQLException {			
			SolicitudAutorizacion e = new SolicitudAutorizacion();	
			e.setSolicitudID(rs.getInt("SolicitudID"));
			e.setProcesoID(rs.getString("ProcesoID"));
			e.setPedidoInversionID(rs.getInt("CodigoID"));
			e.setNroArmada(rs.getString("SolicitudNumeroCuota"));
			e.setNroInversion(rs.getString("PedidoInversionNumero"));
			e.setSolicitudEstadoID(rs.getString("SolicitudEstadoID"));
			return e;		    
			}
		}
}
