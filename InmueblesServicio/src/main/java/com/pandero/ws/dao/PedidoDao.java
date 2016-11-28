package com.pandero.ws.dao;

import java.util.List;

import com.pandero.ws.bean.Asociado;
import com.pandero.ws.bean.ResultadoBean;

public interface PedidoDao {

	public ResultadoBean crearPedidoSAF(String nroContrato, String usuarioId) throws Exception;	
	public ResultadoBean eliminarPedidoSAF(String nroPedido, String usuarioId) throws Exception;
	
	public void agregarContratoPedidoSAF(String nroPedido, String nroContrato, String usuarioId) throws Exception;
	public void eliminarContratoPedidoSAF(String nroPedido, String nroContrato, String usuarioId) throws Exception;
	
	public List<Asociado> obtenerAsociadosxContratoSAF(String nroContrato) throws Exception;
}
