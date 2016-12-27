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
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import com.pandero.ws.bean.Contrato;
import com.pandero.ws.bean.PedidoInversionSAF;
import com.pandero.ws.bean.PersonaSAF;
import com.pandero.ws.bean.ResultadoBean;
import com.pandero.ws.dao.PedidoDao;


@Repository
public class PedidoDaoImpl implements PedidoDao {

	private static final Logger LOG = LoggerFactory.getLogger(PedidoDaoImpl.class);
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	@Qualifier("dataSource")
	public void setDataSource(DataSource dataSource)	{
	    this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	 
	@Override
	public ResultadoBean crearPedidoSAF(String nroContrato, String usuarioId) throws Exception {
		ResultadoBean resultado = null;
		SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate);
		call.withProcedureName("dbo.USP_LOG_Inmb_RegistrarNuevoPedido");
		call.withoutProcedureColumnMetaDataAccess();	
				
		call.addDeclaredParameter(new SqlParameter("@ContratoNumero", Types.VARCHAR));
		call.addDeclaredParameter(new SqlParameter("@UsuarioID", Types.INTEGER));		
		call.addDeclaredParameter(new SqlOutParameter("@NumeroPedido", Types.VARCHAR));
		call.addDeclaredParameter(new SqlOutParameter("@MensajeError", Types.VARCHAR));
				
		MapSqlParameterSource parameters = new MapSqlParameterSource();		
        parameters.addValue("@ContratoNumero", nroContrato);
		parameters.addValue("@UsuarioID", usuarioId);
				
		Map resultadoSP = call.execute(parameters);
		String mensajeError = resultadoSP.get("@MensajeError")!=null?(String)resultadoSP.get("@MensajeError"):"";
		String numeroPedido = resultadoSP.get("@NumeroPedido")!=null?(String)resultadoSP.get("@NumeroPedido"):"";
		System.out.println("RESULTADOS:: "+numeroPedido+" - "+mensajeError);
		
		resultado = new ResultadoBean();		
		resultado.setMensajeError(mensajeError);
		resultado.setResultado(numeroPedido);
		
		return resultado;
	}

	@Override
	public ResultadoBean eliminarPedidoSAF(String nroPedido, String usuarioId) throws Exception {
		ResultadoBean resultado = null;
		SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate);
		call.withProcedureName("dbo.USP_LOG_Inmb_EliminarPedido");
		call.withoutProcedureColumnMetaDataAccess();	
		
		call.addDeclaredParameter(new SqlParameter("@NumeroPedido", Types.VARCHAR));
		call.addDeclaredParameter(new SqlParameter("@UsuarioID", Types.INTEGER));			
		call.addDeclaredParameter(new SqlOutParameter("@MensajeError", Types.VARCHAR));
		
		MapSqlParameterSource parameters = new MapSqlParameterSource();		
        parameters.addValue("@NumeroPedido", nroPedido);
		parameters.addValue("@UsuarioID", usuarioId);
				
		Map resultadoSP = call.execute(parameters);
		String mensajeError = resultadoSP.get("@MensajeError")!=null?(String)resultadoSP.get("@MensajeError"):"";
		System.out.println("RESULTADOS:: "+nroPedido+" - "+mensajeError);
		
		resultado = new ResultadoBean();		
		resultado.setMensajeError(mensajeError);
		
		return resultado;
	}

	@Override
	public void agregarContratoPedidoSAF(String nroPedido, String nroContrato,
			String usuarioId) {
		SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate);
		call.withProcedureName("dbo.USP_LOG_Inmb_AgregarPedidoContrato");
		call.withoutProcedureColumnMetaDataAccess();	
		
		call.addDeclaredParameter(new SqlParameter("@PedidoNumero", Types.VARCHAR));
		call.addDeclaredParameter(new SqlOutParameter("@ContratoNumero", Types.VARCHAR));
		call.addDeclaredParameter(new SqlParameter("@ContratoNumero", Types.VARCHAR));
		call.addDeclaredParameter(new SqlParameter("@UsuarioID", Types.INTEGER));				
		call.addDeclaredParameter(new SqlOutParameter("@MensajeError", Types.VARCHAR));
		
		MapSqlParameterSource parameters = new MapSqlParameterSource();	
		parameters.addValue("@PedidoNumero", nroPedido);
        parameters.addValue("@ContratoNumero", nroContrato);		
		parameters.addValue("@UsuarioID", usuarioId);
				
		call.execute(parameters);		
	}

	@Override
	public void eliminarContratoPedidoSAF(String nroPedido, String nroContrato,
			String usuarioId) {
		SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate);
		call.withProcedureName("dbo.USP_LOG_Inmb_EliminarPedidoContrato");
		call.withoutProcedureColumnMetaDataAccess();	
		
		call.addDeclaredParameter(new SqlParameter("@PedidoNumero", Types.VARCHAR));
		call.addDeclaredParameter(new SqlParameter("@ContratoNumero", Types.VARCHAR));
		call.addDeclaredParameter(new SqlParameter("@UsuarioID", Types.INTEGER));
		
		System.out.println("usuarioId:: "+usuarioId);
		MapSqlParameterSource parameters = new MapSqlParameterSource();	
		parameters.addValue("@PedidoNumero", nroPedido);
        parameters.addValue("@ContratoNumero", nroContrato);
		parameters.addValue("@UsuarioID", usuarioId);
				
		call.execute(parameters);
	}
	

	@Override
	public void agregarPedidoInversionSAF(PedidoInversionSAF pedidoInversionSAF) throws Exception {
		SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate);
		call.withProcedureName("dbo.USP_LOG_Inmb_AgregarPedidoInversion");
		call.withoutProcedureColumnMetaDataAccess();	
		
		call.addDeclaredParameter(new SqlParameter("@PedidoNumero", Types.VARCHAR));
		call.addDeclaredParameter(new SqlParameter("@ProveedorID", Types.VARCHAR));
		call.addDeclaredParameter(new SqlParameter("@InversionNumero", Types.VARCHAR));
		call.addDeclaredParameter(new SqlParameter("@TipoInversionID", Types.VARCHAR));
		call.addDeclaredParameter(new SqlParameter("@ConfirmarID", Types.VARCHAR));
		call.addDeclaredParameter(new SqlParameter("@UsuarioID", Types.INTEGER));				
//		call.addDeclaredParameter(new SqlOutParameter("@MensajeError", Types.VARCHAR));
		
		MapSqlParameterSource parameters = new MapSqlParameterSource();	
		parameters.addValue("@PedidoNumero", pedidoInversionSAF.getNroPedido());
        parameters.addValue("@ProveedorID", pedidoInversionSAF.getProveedorID());		
		parameters.addValue("@InversionNumero", pedidoInversionSAF.getPedidoInversionNumero());
		parameters.addValue("@TipoInversionID", pedidoInversionSAF.getPedidoTipoInversionID());
		parameters.addValue("@ConfirmarID", "1");		
		parameters.addValue("@UsuarioID", pedidoInversionSAF.getUsuarioIDCreacion());
		
		call.execute(parameters);	
	}

	@Override
	public void eliminarPedidoInversionSAF(String nroInversion, String usuarioId) throws Exception {
		SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate);
		call.withProcedureName("dbo.USP_LOG_Inmb_EliminarPedidoInversion");
		call.withoutProcedureColumnMetaDataAccess();	
		
		call.addDeclaredParameter(new SqlParameter("@InversionNumero", Types.VARCHAR));
		call.addDeclaredParameter(new SqlParameter("@UsuarioID", Types.INTEGER));
		
		MapSqlParameterSource parameters = new MapSqlParameterSource();	
		parameters.addValue("@InversionNumero", nroInversion);
		parameters.addValue("@UsuarioID", usuarioId);
				
		call.execute(parameters);
	}

	@Override
	public List<Contrato> obtenerContratosxPedidoSAF(String numeroPedido) throws Exception {
		List<Contrato> listContratos = null;

		SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate);
		call.withCatalogName("dbo");
		call.withProcedureName("USP_LOG_Inmb_ObtenerContratosPorPedido");
		call.withoutProcedureColumnMetaDataAccess();
		call.addDeclaredParameter(new SqlParameter("@NumeroPedido", Types.VARCHAR));
		call.returningResultSet("contratos", new ContratoPedidoMapper());
		SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
		.addValue("@NumeroPedido", numeroPedido);
		Map<String, Object> mapResultado = call.execute(sqlParameterSource);
		
		List resultado = (List) mapResultado.get("contratos");
		if (resultado != null && resultado.size() > 0) {
			listContratos = (List<Contrato>) resultado;
		}

		return listContratos;
	}
	
	private static final class ContratoPedidoMapper implements RowMapper<Contrato>{
		public Contrato mapRow(ResultSet rs, int rowNum) throws SQLException {			
			Contrato p = new Contrato();
			p.setContratoId(rs.getInt("ContratoID"));
			p.setNroContrato(rs.getString("ContratoNumero"));
			p.setPedidoId(rs.getInt("PedidoID"));
			p.setNroPedido(rs.getString("NumeroPedido"));
			p.setMontoCertificado(rs.getDouble("MontoCertificado"));
			
			return p;		    
		}
	}

	@Override
	public PedidoInversionSAF obtenerPedidoInversionSAF(String nroInversion)
			throws Exception {
		PedidoInversionSAF pedidoInversion = null;

		SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate);
		call.withCatalogName("dbo");
		call.withProcedureName("USP_LOG_Inmb_ObtenerInversionPedido");
		call.withoutProcedureColumnMetaDataAccess();
		call.addDeclaredParameter(new SqlParameter("@NumeroInversion", Types.VARCHAR));
		call.returningResultSet("inversion", new InversionPedidoMapper());
		SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
		.addValue("@NumeroInversion", nroInversion);
		Map<String, Object> mapResultado = call.execute(sqlParameterSource);
		
		List resultado = (List) mapResultado.get("inversion");
		if (resultado != null && resultado.size() > 0) {
			List<PedidoInversionSAF> listPedidoInversion = (List<PedidoInversionSAF>) resultado;
			pedidoInversion=listPedidoInversion.get(0);
		}

		return pedidoInversion;
	}
	
	private static final class InversionPedidoMapper implements RowMapper<PedidoInversionSAF>{
		public PedidoInversionSAF mapRow(ResultSet rs, int rowNum) throws SQLException {			
			PedidoInversionSAF p = new PedidoInversionSAF();
			p.setPedidoID(rs.getString("PedidoID"));
			p.setNroPedido(rs.getString("NumeroPedido"));
			p.setPedidoInversionID(rs.getString("PedidoInversionID"));
			p.setPedidoInversionNumero(rs.getString("PedidoInversionNumero"));
			p.setProveedorID(rs.getString("ProveedorID"));
			p.setPedidoTipoInversionID(rs.getString("PedidoTipoInversionID"));
			
			return p;		    
		}
	}

}
