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

import com.pandero.ws.business.GarantiaBusiness;
import com.pandero.ws.util.Constantes;

@Controller
@RequestMapping("/garantia")
public class GarantiaController {

private static final Logger LOG = LoggerFactory.getLogger(GarantiaController.class);
	
	@Autowired
	GarantiaBusiness garantiaBusiness;
	
	@RequestMapping(value = "/crearGarantiaSAF", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody   
	public Map<String, Object> crearGarantiaSAF(@RequestBody Map<String, Object> params) {
		LOG.info("###crearGarantiaSAF params:"+params);
		Map<String, Object> response = new HashMap<String, Object>();
		String result="", detail="";
		try{
			String pedidoCaspioId = String.valueOf(params.get("pedidoCaspioId"));
			String partidaRegistral = String.valueOf(params.get("partidaRegistral"));
			String fichaConstitucion = String.valueOf(params.get("fichaConstitucion"));
			String fechaConstitucion = String.valueOf(params.get("fechaConstitucion"));
			String uso = String.valueOf(params.get("uso"));
			String usuarioId = String.valueOf(params.get("usuarioId"));
			LOG.info("datos: "+pedidoCaspioId+" - "+partidaRegistral+" - "+fichaConstitucion+" "+
					fechaConstitucion+" - "+usuarioId);
			
			result = garantiaBusiness.crearGarantiaSAF(pedidoCaspioId, partidaRegistral, fichaConstitucion, fechaConstitucion, uso, usuarioId);
					
		}catch(Exception e){
			LOG.error("Error pedido/crearGarantiaSAF:: ",e);
			e.printStackTrace();
			result="0";
			detail=e.getMessage();
		}			
		response.put("result",result);
		response.put("detail",detail);
		System.out.println("RESPONSE: " +  response);	
		
		return response;
	}
	
	@RequestMapping(value = "/registrarSeguro", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody   
	public Map<String, Object> registrarSeguro(@RequestBody Map<String, Object> params) {
		LOG.info("###editarGarantiaSAF params:"+params);
		Map<String, Object> response = new HashMap<String, Object>();
		String result="", detail="";
		try{			
			response = garantiaBusiness.registrarSeguro(params);
			result = Constantes.Service.RESULTADO_EXITOSO;
			
		}catch(Exception e){
			LOG.error("Error pedido/editarGarantiaSAF:: ",e);
			e.printStackTrace();
			result=Constantes.Service.RESULTADO_ERROR_INESPERADO;
			detail=e.getMessage();
		}			
		response.put("result",result);
		response.put("detail",detail);
		System.out.println("RESPONSE: " +  response);	
		
		return response;
	}
	
	@RequestMapping(value = "/generarSeguro", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody   
	public Map<String, Object> generarSeguro(@RequestBody Map<String, Object> params) {
		LOG.info("###editarGarantiaSAF params:"+params);
		Map<String, Object> response = new HashMap<String, Object>();
		String result="", detail="";
		try{			
			response = garantiaBusiness.generarSeguro(params);
			result = Constantes.Service.RESULTADO_EXITOSO;
			
		}catch(Exception e){
			LOG.error("Error pedido/editarGarantiaSAF:: ",e);
			e.printStackTrace();
			result=Constantes.Service.RESULTADO_ERROR_INESPERADO;
			detail=e.getMessage();
		}			
		response.put("result",result);
		response.put("detail",detail);
		System.out.println("RESPONSE: " +  response);	
		
		return response;
	}
	
	@RequestMapping(value = "/anularSeguro", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody   
	public Map<String, Object> anularSeguro(@RequestBody Map<String, Object> params) {
		LOG.info("###editarGarantiaSAF params:"+params);
		Map<String, Object> response = new HashMap<String, Object>();
		String result="", detail="";
		try{			
			response = garantiaBusiness.anularSeguro(params);
			result = Constantes.Service.RESULTADO_EXITOSO;
			response.put("mensaje", "El seguro se anuló satisfactoriamente");
			
		}catch(Exception e){
			LOG.error("Error pedido/editarGarantiaSAF:: ",e);
			e.printStackTrace();
			result=Constantes.Service.RESULTADO_ERROR_INESPERADO;
			detail=e.getMessage();
		}			
		response.put("result",result);
		response.put("detail",detail);
		System.out.println("RESPONSE: " +  response);	
		
		return response;
	}
	
	@RequestMapping(value = "/editarGarantiaSAF", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody   
	public Map<String, Object> editarGarantiaSAF(@RequestBody Map<String, Object> params) {
		LOG.info("###editarGarantiaSAF params:"+params);
		Map<String, Object> response = new HashMap<String, Object>();
		String result="", detail="";
		try{
			String garantiaId = String.valueOf(params.get("garantiaId"));
			String partidaRegistral = String.valueOf(params.get("partidaRegistral"));
			String fichaConstitucion = String.valueOf(params.get("fichaConstitucion"));
			String fechaConstitucion = String.valueOf(params.get("fechaConstitucion"));
			String uso = String.valueOf(params.get("uso"));
			String montoPrima = String.valueOf(params.get("montoPrima"));			
			String modalidad = String.valueOf(params.get("modalidad"));
			String usuarioId = String.valueOf(params.get("usuarioId"));
			String nroContrato = String.valueOf(params.get("contratoNro"));
			System.out.println("modalidad="+modalidad+" - montoPrima="+montoPrima);
			
			garantiaBusiness.editarGarantiaSAF(garantiaId, partidaRegistral, fichaConstitucion, 
					fechaConstitucion, montoPrima, modalidad, uso, usuarioId, nroContrato);
			result = Constantes.Service.RESULTADO_EXITOSO;
			
		}catch(Exception e){
			LOG.error("Error pedido/editarGarantiaSAF:: ",e);
			e.printStackTrace();
			result=Constantes.Service.RESULTADO_ERROR_INESPERADO;
			detail=e.getMessage();
		}			
		response.put("result",result);
		response.put("detail",detail);
		System.out.println("RESPONSE: " +  response);	
		
		return response;
	}
	
	@RequestMapping(value = "/eliminarGarantia", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody   
	public Map<String, Object> eliminarGarantia(@RequestBody Map<String, Object> params) {
		LOG.info("###eliminarGarantia params:"+params);
		Map<String, Object> response = new HashMap<String, Object>();
		String result="", detail="";
		try{
			String garantiaId = String.valueOf(params.get("garantiaId"));
			String usuarioId = String.valueOf(params.get("usuarioId"));
			
			result = garantiaBusiness.eliminarGarantia(garantiaId, usuarioId);
			if(result.equals("")){
				result = Constantes.Service.RESULTADO_EXITOSO;
			}
		}catch(Exception e){
			LOG.error("Error pedido/eliminarGarantia:: ",e);
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
