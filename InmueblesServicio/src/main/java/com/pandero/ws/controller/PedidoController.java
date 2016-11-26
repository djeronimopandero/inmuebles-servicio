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
import com.pandero.ws.service.MailService;
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
	
	
	@RequestMapping(value = "/agregarContratoPedidoSAF", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody   
	public Map<String, Object> agregarContratoPedidoSAF(@RequestBody Map<String, Object> params) {
		System.out.println("EN METODO agregarContratoPedidoSAF");
		System.out.println("REQUEST: " +  params);		
		Map<String, Object> response = new HashMap<String, Object>();
		String result="", error="";
		try{
			String pedidoId = String.valueOf(params.get("pedidoId"));
			String nroContrato = String.valueOf(params.get("nroContrato"));
			String usuarioId = String.valueOf(params.get("usuarioId"));
			
			pedidoDao.agregarContratoPedidoSAF(pedidoId,nroContrato,usuarioId);
			
		}catch(Exception e){
			LOG.error("Error pedido/agregarContratoPedidoSAF:: ",e);
			error = "Se produjo un error en la base de datos";
			e.printStackTrace();
		}
			
		response.put("result",result);
		response.put("error",error);
		System.out.println("RESPONSE: " +  response);	
		
		return response;
	}
	
	
	public Map<String, Object> eliminarContratoPedidoSAF(@RequestBody Map<String, Object> params) {
		System.out.println("EN METODO eliminarContratoPedidoSAF");
		System.out.println("REQUEST: " +  params);		
		Map<String, Object> response = new HashMap<String, Object>();
		String result="", error="";
		try{
			String pedidoId = String.valueOf(params.get("pedidoId"));
			String contratoId = String.valueOf(params.get("contratoId"));
			String usuarioId = String.valueOf(params.get("usuarioId"));
			
			pedidoDao.agregarContratoPedidoSAF(pedidoId,contratoId,usuarioId);
			
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
