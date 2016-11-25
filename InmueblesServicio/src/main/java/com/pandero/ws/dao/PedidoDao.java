package com.pandero.ws.dao;

import java.util.List;

import com.pandero.ws.bean.Asociado;
import com.pandero.ws.bean.ResultadoBean;

public interface PedidoDao {

	public ResultadoBean crearPedidoSAF(String nroContrato, String usuarioId) throws Exception;	
	public ResultadoBean eliminarPedidoSAF(String pedidoId, String usuarioId) throws Exception;
	public String actualizarEstadoPedidoSAF(String nroContrato, String usuarioId) throws Exception;
	
	public void agregarContratoPedidoSAF(String pedidoId, String nroContrato, String usuarioId) throws Exception;
	public void eliminarContratoPedidoSAF(String pedidoId, String contratoId, String usuarioId) throws Exception;
	
	public List<Asociado> obtenerAsociadosxContrato(String nroContrato) throws Exception;
}
