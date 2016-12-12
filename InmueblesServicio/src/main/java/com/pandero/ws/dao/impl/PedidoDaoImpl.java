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
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import com.pandero.ws.bean.Asociado;
import com.pandero.ws.bean.PedidoInversionSAF;
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
	
	public List<Asociado> obtenerAsociadosxContratoSAF(String nroContrato){
		String query = 	"select PersonaNombreCompleto,ListaNombreCorto as TipoDocumento,PersonaCodigoDocumento, "+
				"(select DireccionDetalle+' - '+y.UbigeoNombre "+
				"from PER_Direccion x inner join GEN_Ubigeo y "+
				"on x.UbigeoID=UbigeoDepartamentoID+UbigeoProvinciaID+UbigeoDistritoID "+
				"where  DireccionTipoID='01' and PersonaID=a.PersonaID) as PersonaDireccion "+
				"from FOC_Asociado d inner join FOC_Contrato c "+
				"on d.ContratoID=c.ContratoID inner join PER_Persona a "+
				"on d.PersonaID=a.PersonaID inner join GEN_Lista b "+
				"on a.TipoDocumentoID=b.ListaID and ListaTipo='DOCUMENTO_IDENTIDAD' "+
				"where c.ContratoNumero='"+nroContrato+"'";
							
		List<Asociado> listAsociados = this.jdbcTemplate.query(query, new AsociadoMapper());
				
		if(listAsociados!=null ){			
			System.out.println("listAsociados:: "+listAsociados.size());
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

	@Override
	public void agregarPedidoInversionSAF(PedidoInversionSAF pedidoInversionSAF) throws Exception {
		SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate);
		call.withProcedureName("dbo.USP_LOG_Inmb_AgregarPedidoInversion");
		call.withoutProcedureColumnMetaDataAccess();	
		
		call.addDeclaredParameter(new SqlParameter("@PedidoNumero", Types.VARCHAR));
		call.addDeclaredParameter(new SqlOutParameter("@ProveedorID", Types.VARCHAR));
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
		
		call.addDeclaredParameter(new SqlParameter("@NumeroInversion", Types.VARCHAR));
		call.addDeclaredParameter(new SqlParameter("@UsuarioID", Types.INTEGER));
		
		MapSqlParameterSource parameters = new MapSqlParameterSource();	
		parameters.addValue("@NumeroInversion", nroInversion);
		parameters.addValue("@UsuarioID", usuarioId);
				
		call.execute(parameters);
	}

}
