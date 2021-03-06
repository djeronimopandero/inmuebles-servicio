package com.pandero.ws.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pandero.ws.bean.Contrato;
import com.pandero.ws.bean.ResultadoBean;
import com.pandero.ws.business.LiquidacionBusiness;
import com.pandero.ws.business.PedidoBusiness;
import com.pandero.ws.dao.PedidoDao;
import com.pandero.ws.util.Constantes;
import com.pandero.ws.util.JsonUtil;
import com.pandero.ws.util.Util;

@Controller
@RequestMapping("/pedido")
public class PedidoController {

	private static final Logger LOG = LoggerFactory.getLogger(PedidoController.class);

	@Autowired
	PedidoDao pedidoDao;
	@Autowired
	PedidoBusiness pedidoBusiness;
	@Autowired
	LiquidacionBusiness liquidacionBusiness;
	
	@RequestMapping(value = "/crearPedido", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody   
	public Map<String, Object> crearPedido(@RequestBody Map<String, Object> params) {
		System.out.println("EN METODO crearPedido");
		System.out.println("REQUEST: " +  params);		
		Map<String, Object> response = new HashMap<String, Object>();
		String result="", detail="";
		try{
			String nroContrato = String.valueOf(params.get("nroContrato"));
			String usuarioId = String.valueOf(params.get("usuarioId"));
			
			ResultadoBean resultado = pedidoBusiness.registrarNuevoPedido(nroContrato, usuarioId);			
			detail = resultado.getResultado()==null?"":String.valueOf(resultado.getResultado());
			result = resultado.getMensajeError();
			if(Util.esVacio(result)){
				result = Constantes.Service.RESULTADO_EXITOSO;
			}			
		}catch(Exception e){
			LOG.error("Error pedido/crearPedido:: ",e);
			e.printStackTrace();
			result=Constantes.Service.RESULTADO_ERROR_INESPERADO;
			detail=e.getMessage();
		}			
		response.put("result",result);
		response.put("detail",detail);
		System.out.println("RESPONSE: " +  response);	
		
		return response;
	}
	
	@RequestMapping(value = "/contratoPedidoEnEvaluacionCrediticia", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody   
	public Map<String, Object> contratoPedidoEnEvaluacionCrediticia(@RequestBody Map<String, Object> params) {
		System.out.println("EN METODO contratoPedidoEnEvaluacionCrediticia");
		System.out.println("REQUEST: " +  params);		
		Map<String, Object> response = new HashMap<String, Object>();
		String result="", detail="";
		try{
			response = pedidoBusiness.contratoPedidoEnEvaluacionCrediticia(params);
			if(Util.esVacio(result)){
				result = Constantes.Service.RESULTADO_EXITOSO;
			}			
		}catch(Exception e){
			LOG.error("Error pedido/crearPedido:: ",e);
			e.printStackTrace();
			result=Constantes.Service.RESULTADO_ERROR_INESPERADO;
			detail=e.getMessage();
		}			
		response.put("result",result);
		response.put("detail",detail);
		System.out.println("RESPONSE: " +  response);	
		
		return response;
	}
	
	
	@RequestMapping(value = "/eliminarPedido", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody   
	public Map<String, Object> eliminarPedido(@RequestBody Map<String, Object> params) {
		System.out.println("EN METODO eliminarPedido");
		System.out.println("REQUEST: " +  params);		
		Map<String, Object> response = new HashMap<String, Object>();
		String result="", detail="";
		try{
			String pedidoId = String.valueOf(params.get("pedidoId"));
			String nroPedido = String.valueOf(params.get("nroPedido"));
			String usuarioId = String.valueOf(params.get("usuarioId"));
			
			ResultadoBean resultado = pedidoBusiness.eliminarPedido(pedidoId, nroPedido, usuarioId);
			result = resultado.getMensajeError();
			if(Util.esVacio(result)){
				result = Constantes.Service.RESULTADO_EXITOSO;
			}	
		}catch(Exception e){
			LOG.error("Error pedido/eliminarPedido:: ",e);
			e.printStackTrace();
			result=Constantes.Service.RESULTADO_ERROR_INESPERADO;
			detail=e.getMessage();
		}
			
		response.put("result",result);
		response.put("detail",detail);
		System.out.println("RESPONSE: " +  response);	
		
		return response;
	}
	
	
	@RequestMapping(value = "/agregarContratoPedido", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody   
	public Map<String, Object> agregarContratoPedido(@RequestBody Map<String, Object> params) {
		System.out.println("EN METODO agregarContratoPedido");
		System.out.println("REQUEST: " +  params);		
		Map<String, Object> response = new HashMap<String, Object>();
		String result="", detail="";
		try{
			String pedidoId = String.valueOf(params.get("pedidoId"));
			String nroContrato = String.valueOf(params.get("nroContrato"));
			String usuarioId = String.valueOf(params.get("usuarioId"));
			String idContrato = String.valueOf(params.get("idContrato"));
			String resultado = pedidoBusiness.agregarContratoPedido(pedidoId, nroContrato, usuarioId,idContrato);
			if("1".equals(resultado)){
				result = "Operacion Cancelada. El contrato se encuentra en una evaluacion crediticia.";
			}else{
				result = Constantes.Service.RESULTADO_EXITOSO;	
			}
			
			
		}catch(Exception e){
			LOG.error("Error pedido/agregarContratoPedido:: ",e);
			e.printStackTrace();
			result=Constantes.Service.RESULTADO_ERROR_INESPERADO;
			detail=e.getMessage();
		}
			
		response.put("result",result);
		response.put("detail",detail);
		System.out.println("RESPONSE: " +  response);	
		
		return response;
	}
	
	@RequestMapping(value = "/eliminarContratoPedido", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody   
	public Map<String, Object> eliminarContratoPedido(@RequestBody Map<String, Object> params) {
		System.out.println("EN METODO eliminarContratoPedido");
		System.out.println("REQUEST: " +  params);		
		Map<String, Object> response = new HashMap<String, Object>();
		String result="", detail="";
		try{
			String pedidoId = String.valueOf(params.get("pedidoId"));
			String nroContrato = String.valueOf(params.get("nroContrato"));
			String usuarioId = String.valueOf(params.get("usuarioId"));
			
			result = pedidoBusiness.eliminarContratoPedido(pedidoId, nroContrato, usuarioId);
			if(result.equals("")){
				result = Constantes.Service.RESULTADO_EXITOSO;
			}
		}catch(Exception e){
			LOG.error("Error pedido/eliminarContratoPedido:: ",e);
			e.printStackTrace();
			result=Constantes.Service.RESULTADO_ERROR_INESPERADO;
			detail=e.getMessage();
		}
			
		response.put("result",result);
		response.put("detail",detail);
		System.out.println("RESPONSE: " +  response);	
		
		return response;
	}
	
	@RequestMapping(value = "/generarOrdenIrrevocable", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody   
	public Map<String, Object> generarOrdenIrrevocable(@RequestBody Map<String, Object> params) {
		System.out.println("EN METODO generarOrdenIrrevocable");
		System.out.println("REQUEST: " +  params);		
		Map<String, Object> response = new HashMap<String, Object>();
		String result="", detail="";
		try{
			String pedidoId = String.valueOf(params.get("pedidoId"));
			String usuarioId = String.valueOf(params.get("usuarioId"));
			String pedidoNumero = String.valueOf(params.get("pedidoNumero"));
			result = pedidoBusiness.generarOrdenIrrevocablePedido(pedidoId, usuarioId, pedidoNumero);
			if(result.equals("")){
				result = Constantes.Service.RESULTADO_EXITOSO;
			}
		}catch(Exception e){
			LOG.error("Error pedido/generarOrdenIrrevocable:: ",e);
			e.printStackTrace();
			result=Constantes.Service.RESULTADO_ERROR_INESPERADO;
			detail=e.getMessage();
		}
			
		response.put("result",result);
		response.put("detail",detail);
		System.out.println("RESPONSE: " +  response);	
		
		return response;
	}
	
	@RequestMapping(value = "/obtenerContratosPedidoActualizado", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody   
	public Map<String, Object> obtenerContratosPedidoActualizado(@RequestBody Map<String, Object> params) {
		System.out.println("EN METODO obtenerContratosPedidoActualizado");
		System.out.println("REQUEST: " +  params);		
		Map<String, Object> response = new HashMap<String, Object>();
		String result="", detail="";
		try{
			String nroPedido = String.valueOf(params.get("nroPedido"));
			List<Contrato> contratosPedido = liquidacionBusiness.obtenerTablaContratosPedidoActualizado(nroPedido);
			if(contratosPedido!=null){
				detail = JsonUtil.toJson(contratosPedido);
			}
			if(result.equals("")){
				result = Constantes.Service.RESULTADO_EXITOSO;
			}
		}catch(Exception e){
			LOG.error("Error pedido/obtenerContratosPedidoActualizado:: ",e);
			e.printStackTrace();
			result=Constantes.Service.RESULTADO_ERROR_INESPERADO;
			detail=e.getMessage();
		}
			
		response.put("result",result);
		response.put("detail",detail);
		System.out.println("RESPONSE: " +  response);	
		
		return response;
	}
	
	@RequestMapping(value = "/actualizarTablaContratosPedido", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody   
	public Map<String, Object> actualizarTablaContratosPedido(@RequestBody Map<String, Object> params) {
		System.out.println("EN METODO actualizarTablaContratosPedido");
		System.out.println("REQUEST: " +  params);		
		Map<String, Object> response = new HashMap<String, Object>();
		String result="", detail="";
		try{
			String nroPedido = String.valueOf(params.get("nroPedido"));
			liquidacionBusiness.actualizarTablaContratosPedido(nroPedido);
			result = Constantes.Service.RESULTADO_EXITOSO;
		}catch(Exception e){
			LOG.error("Error pedido/actualizarTablaContratosPedido:: ",e);
			e.printStackTrace();
			result=Constantes.Service.RESULTADO_ERROR_INESPERADO;
			detail=e.getMessage();
		}			
		response.put("result",result);
		response.put("detail",detail);
		System.out.println("RESPONSE: " +  response);	
		
		return response;
	}
}
