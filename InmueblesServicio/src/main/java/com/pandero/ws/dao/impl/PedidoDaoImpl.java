package com.pandero.ws.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
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
		call.addDeclaredParameter(new SqlParameter("@UsuarioID", Types.VARCHAR));		
		call.addDeclaredParameter(new SqlOutParameter("@NumeroPedido", Types.VARCHAR));
		call.addDeclaredParameter(new SqlOutParameter("@MensajeError", Types.VARCHAR));
				
		MapSqlParameterSource parameters = new MapSqlParameterSource();		
        parameters.addValue("@ContratoNumero", nroContrato);
		parameters.addValue("@PedidoFecha", null);
		parameters.addValue("@UsuarioID", usuarioId);
				
		Map resultadoSP = call.execute(parameters);
		String mensajeError = resultadoSP.get("@MensajeError")!=null?(String)resultadoSP.get("@MensajeError"):"";
		String NumeroPedido = resultadoSP.get("@NumeroPedido")!=null?(String)resultadoSP.get("@NumeroPedido"):"";
		System.out.println("RESULTADOS:: "+NumeroPedido+" - "+mensajeError);
		
		resultado = new ResultadoBean();		
		resultado.setMensajeError(mensajeError);
		resultado.setResultado(NumeroPedido);
		
		return resultado;
	}

	@Override
	public ResultadoBean eliminarPedidoSAF(String pedidoId, String usuarioId) throws Exception {
		ResultadoBean resultado = null;
		SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate);
		call.withProcedureName("dbo.USP_LOG_Inmb_EliminarPedido");
		call.withoutProcedureColumnMetaDataAccess();	
		
		call.addDeclaredParameter(new SqlParameter("@PedidoID", Types.INTEGER));
		call.addDeclaredParameter(new SqlParameter("@UsuarioID", Types.VARCHAR));			
		call.addDeclaredParameter(new SqlOutParameter("@MensajeError", Types.VARCHAR));
		
		MapSqlParameterSource parameters = new MapSqlParameterSource();		
        parameters.addValue("@PedidoID", pedidoId);
		parameters.addValue("@UsuarioID", usuarioId);
				
		Map resultadoSP = call.execute(parameters);
		String mensajeError = resultadoSP.get("@MensajeError")!=null?(String)resultadoSP.get("@MensajeError"):"";
		System.out.println("RESULTADOS:: "+pedidoId+" - "+mensajeError);
		
		resultado = new ResultadoBean();		
		resultado.setMensajeError(mensajeError);
		
		return resultado;
	}

	@Override
	public void agregarContratoPedidoSAF(String pedidoId, String nroContrato,
			String usuarioId) {
		SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate);
		call.withProcedureName("dbo.USP_LOG_AgregarPedidoContrato");
		call.withoutProcedureColumnMetaDataAccess();	
		
		call.addDeclaredParameter(new SqlParameter("@PedidoID", Types.INTEGER));
		call.addDeclaredParameter(new SqlOutParameter("@ContratoID", Types.INTEGER));
		call.addDeclaredParameter(new SqlParameter("@ContratoNumero", Types.VARCHAR));
		call.addDeclaredParameter(new SqlParameter("@UsuarioID", Types.VARCHAR));				
		call.addDeclaredParameter(new SqlParameter("@EvaluacionCrediticia", Types.BOOLEAN));
		
		MapSqlParameterSource parameters = new MapSqlParameterSource();	
		parameters.addValue("@PedidoID", pedidoId);
        parameters.addValue("@ContratoNumero", nroContrato);		
		parameters.addValue("@UsuarioID", usuarioId);
		parameters.addValue("@EvaluacionCrediticia", 0);
				
		call.execute(parameters);		
	}

	@Override
	public void eliminarContratoPedidoSAF(String pedidoId, String contratoId,
			String usuarioId) {
		SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate);
		call.withProcedureName("dbo.USP_LOG_EliminarPedidoContrato");
		call.withoutProcedureColumnMetaDataAccess();	
		
		call.addDeclaredParameter(new SqlParameter("@PedidoID", Types.INTEGER));
		call.addDeclaredParameter(new SqlParameter("@ContratoID", Types.INTEGER));
		call.addDeclaredParameter(new SqlParameter("@UsuarioID", Types.VARCHAR));
		
		MapSqlParameterSource parameters = new MapSqlParameterSource();	
		parameters.addValue("@PedidoID", pedidoId);
        parameters.addValue("@ContratoID", contratoId);		
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
				"where c.ContratoNumero="+nroContrato;
							
		List<Asociado> listAsociados = this.jdbcTemplate.query(query, new AsociadoMapper());
		
//		List<Asociado> listAsociados = new ArrayList<Asociado>();
//		Asociado asociado = new Asociado();
//		asociado.setNombreCompleto("LUIS ANGEL PEREZ VALDIVIEZO");
//		asociado.setTipoDocumentoIdentidad("DNI");
//		asociado.setNroDocumentoIdentidad("45776580");
//		asociado.setDireccion("AV TOMAS VALLE 1530 - LOS OLIVOS");
//		listAsociados.add(asociado);
		
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
}
