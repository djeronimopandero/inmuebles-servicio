package com.pandero.ws.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pandero.ws.bean.ResultadoBean;
import com.pandero.ws.business.PedidoBusiness;
import com.pandero.ws.dao.PedidoDao;
import com.pandero.ws.util.Constantes;

@Controller
@RequestMapping("/pedido")
public class PedidoController {

	private static final Logger LOG = LoggerFactory.getLogger(PedidoController.class);

	@Autowired
	PedidoDao pedidoDao;
	@Autowired
	PedidoBusiness pedidoBusiness;
	
	@RequestMapping(value = "/crearPedido", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody   
	public Map<String, Object> crearPedido(@RequestBody Map<String, Object> params) {
		System.out.println("EN METODO crearPedido");
		System.out.println("REQUEST: " +  params);		
		Map<String, Object> response = new HashMap<String, Object>();
		String result="", error="";
		try{
			String nroContrato = String.valueOf(params.get("nroContrato"));
			String usuarioId = String.valueOf(params.get("usuarioId"));
			
			ResultadoBean resultado = pedidoBusiness.registrarNuevoPedido(nroContrato, usuarioId);
			error = resultado.getMensajeError();
			result = resultado.getResultado()==null?"":String.valueOf(resultado.getResultado());
			
		}catch(Exception e){
			LOG.error("Error pedido/crearPedido:: ",e);
			error = Constantes.Service.RESULTADO_ERROR_INESPERADO;
			e.printStackTrace();
		}			
		response.put("result",result);
		response.put("error",error);
		System.out.println("RESPONSE: " +  response);	
		
		return response;
	}
	
	
	@RequestMapping(value = "/eliminarPedido", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody   
	public Map<String, Object> eliminarPedido(@RequestBody Map<String, Object> params) {
		System.out.println("EN METODO eliminarPedido");
		System.out.println("REQUEST: " +  params);		
		Map<String, Object> response = new HashMap<String, Object>();
		String result="", error="";
		try{
			String pedidoId = String.valueOf(params.get("pedidoId"));
			String usuarioId = String.valueOf(params.get("usuarioId"));
			
			ResultadoBean resultado = pedidoDao.eliminarPedidoSAF(pedidoId, usuarioId);
			error = resultado.getMensajeError();
//			result = resultado.getResultado()==null?"":String.valueOf((Integer)resultado.getResultado());
			
		}catch(Exception e){
			LOG.error("Error pedido/eliminarPedidoSAF:: ",e);
			error = "Se produjo un error en la base de datos";
			e.printStackTrace();
		}
			
		response.put("result",result);
		response.put("error",error);
		System.out.println("RESPONSE: " +  response);	
		
		return response;
	}
	
	
	@RequestMapping(value = "/agregarContratoPedido", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody   
	public Map<String, Object> agregarContratoPedido(@RequestBody Map<String, Object> params) {
		System.out.println("EN METODO agregarContratoPedido");
		System.out.println("REQUEST: " +  params);		
		Map<String, Object> response = new HashMap<String, Object>();
		String result="", error="";
		try{
			String pedidoId = String.valueOf(params.get("pedidoId"));
			String nroPedido = String.valueOf(params.get("nroPedido"));
			String nroContrato = String.valueOf(params.get("nroContrato"));
			String usuarioId = String.valueOf(params.get("usuarioId"));
			
			pedidoBusiness.agregarContratoPedido(pedidoId, nroPedido, nroContrato, usuarioId);
			
		}catch(Exception e){
			LOG.error("Error pedido/agregarContratoPedido:: ",e);
			error = "Se produjo un error en la base de datos";
			e.printStackTrace();
		}
			
		response.put("result",result);
		response.put("error",error);
		System.out.println("RESPONSE: " +  response);	
		
		return response;
	}
	
	
	public Map<String, Object> eliminarContratoPedido(@RequestBody Map<String, Object> params) {
		System.out.println("EN METODO eliminarContratoPedido");
		System.out.println("REQUEST: " +  params);		
		Map<String, Object> response = new HashMap<String, Object>();
		String result="", error="";
		try{
			String pedidoId = String.valueOf(params.get("pedidoId"));
			String nroPedido = String.valueOf(params.get("nroPedido"));
			String nroContrato = String.valueOf(params.get("nroContrato"));
			String usuarioId = String.valueOf(params.get("usuarioId"));
			
			pedidoBusiness.eliminarContratoPedido(pedidoId, nroPedido, nroContrato, usuarioId);
			
		}catch(Exception e){
			LOG.error("Error pedido/eliminarContratoPedidoSAF:: ",e);
			error = "Se produjo un error en la base de datos";
			e.printStackTrace();
		}
			
		response.put("result",result);
		response.put("error",error);
		System.out.println("RESPONSE: " +  response);	
		
		return response;
	}
	
	@RequestMapping(value = "/generarOrdenIrrevocable", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody   
	public Map<String, Object> generarOrdenIrrevocable(@RequestBody Map<String, Object> params) {
		System.out.println("EN METODO generarOrdenIrrevocable");
		System.out.println("REQUEST: " +  params);		
		Map<String, Object> response = new HashMap<String, Object>();
		String result="", error="";
		try{
			String pedidoId = String.valueOf(params.get("pedidoId"));			
			pedidoBusiness.generarOrdenIrrevocablePedido(pedidoId);
			response.put("result","El documento se genero correctamente");
			
		}catch(Exception e){
			LOG.error("Error pedido/crearPedidoSAF:: ",e);
			error = "Se produjo un error en la base de datos";
			e.printStackTrace();
		}
			
		response.put("result",result);
		response.put("error",error);
		System.out.println("RESPONSE: " +  response);	
		
		return response;
	}
}
