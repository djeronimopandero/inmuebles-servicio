package com.pandero.ws.dao;

import java.util.List;

import com.pandero.ws.bean.Contrato;
import com.pandero.ws.bean.Inversion;
import com.pandero.ws.bean.PedidoInversionSAF;
import com.pandero.ws.bean.ResultadoBean;

public interface PedidoDao {

	public ResultadoBean crearPedidoSAF(String nroContrato, String usuarioId) throws Exception;	
	public ResultadoBean eliminarPedidoSAF(String nroPedido, String usuarioId) throws Exception;
	
	public void agregarContratoPedidoSAF(String nroPedido, String nroContrato, String usuarioId) throws Exception;
	public void eliminarContratoPedidoSAF(String nroPedido, String nroContrato, String usuarioId) throws Exception;
		
	public void agregarPedidoInversionSAF(PedidoInversionSAF pedidoInversionSAF) throws Exception; 
	public void eliminarPedidoInversionSAF(String nroInversion, String usuarioId) throws Exception;
	public void registrarInmuebleInversionSAF(Inversion inversion, String usuarioId) throws Exception;
	
	public List<Contrato> obtenerContratosxPedidoSAF(String numeroPedido) throws Exception;
	public PedidoInversionSAF obtenerPedidoInversionSAF(String nroInversion) throws Exception;
}
