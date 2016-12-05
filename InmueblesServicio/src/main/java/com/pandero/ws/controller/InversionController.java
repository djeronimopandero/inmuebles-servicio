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

import com.pandero.ws.bean.Inversion;
import com.pandero.ws.business.InversionBusiness;
import com.pandero.ws.service.InversionService;
import com.pandero.ws.util.Constantes;
import com.pandero.ws.util.JsonUtil;
import com.pandero.ws.util.ServiceRestTemplate;

@Controller
@RequestMapping("/inversionPedido")
public class InversionController {

	private static final Logger LOG = LoggerFactory.getLogger(InversionController.class);

	@Autowired
	InversionBusiness inversionBusiness;
	
	@RequestMapping(value = "/obtenerInversion", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody   
	public Map<String, Object> obtenerInversion(@RequestBody Map<String, Object> params) {
		System.out.println("EN METODO obtenerDatosInversion");
		System.out.println("REQUEST: " +  params);		
		Map<String, Object> response = new HashMap<String, Object>();
		String result="", detail="";
		try{
//			String inversionId = String.valueOf(params.get("inversionId"));
//			Inversion inversion = inversionService.obtenerInversionCaspio(inversionId);		
//			if(inversion!=null){
//				detail = JsonUtil.toJson(inversion);
//			}
			result = Constantes.Service.RESULTADO_EXITOSO;
		}catch(Exception e){
			LOG.error("Error inversion/obtenerInversion:: ",e);
			e.printStackTrace();
			result=Constantes.Service.RESULTADO_ERROR_INESPERADO;
			detail=e.getMessage();
		}
			
		response.put("result",result);
		response.put("detail",detail);
		System.out.println("RESPONSE: " +  response);	
		
		return response;
	}
	
	@RequestMapping(value = "/confirmarInversion", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody   
	public Map<String, Object> confirmarInversion(@RequestBody Map<String, Object> params) {
		System.out.println("EN METODO confirmarInversion");
		System.out.println("REQUEST: " +  params);		
		Map<String, Object> response = new HashMap<String, Object>();
		String result="1", detail="";
		try{
			String inversionId = String.valueOf(params.get("inversionId"));
			String situacionConfirmado = String.valueOf(params.get("situacionConfirmado"));
			result  = inversionBusiness.confirmarInversion(inversionId,situacionConfirmado);		
		}catch(Exception e){
			LOG.error("Error inversion/confirmarInversion:: ",e);
			e.printStackTrace();
			result=Constantes.Service.RESULTADO_ERROR_INESPERADO;
			detail=e.getMessage();
		}
			
		response.put("result",result);
		response.put("detail",detail);
		System.out.println("RESPONSE: " +  response);	
		
		return response;
	}

	@RequestMapping(value = "/eliminarInversion", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody   
	public Map<String, Object> eliminarInversion(@RequestBody Map<String, Object> params) {
		System.out.println("EN METODO eliminarInversion");
		System.out.println("REQUEST: " +  params);		
		Map<String, Object> response = new HashMap<String, Object>();
		String result="1", detail="";
		try{
			String inversionId = String.valueOf(params.get("inversionId"));
			inversionBusiness.eliminarInversion(inversionId);
			result = Constantes.Service.RESULTADO_EXITOSO;
		}catch(Exception e){
			LOG.error("Error inversion/eliminarInversion:: ",e);
			e.printStackTrace();
			result=Constantes.Service.RESULTADO_ERROR_INESPERADO;
			detail=e.getMessage();
		}
			
		response.put("result",result);
		response.put("detail",detail);
		System.out.println("RESPONSE: " +  response);	
		
		return response;
	}
	
	@RequestMapping(value = "/registrarInversionRequisitos", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody   
	public Map<String, Object> registrarInversionRequisitos(@RequestBody Map<String, Object> params) {
		System.out.println("EN METODO registrarInversionRequisitos");
		System.out.println("REQUEST: " +  params);		
		Map<String, Object> response = new HashMap<String, Object>();
		String result="1", detail="";
		try{
			String inversionId = String.valueOf(params.get("inversionId"));
			inversionBusiness.registrarInversionRequisitos(inversionId);
			result = Constantes.Service.RESULTADO_EXITOSO;
		}catch(Exception e){
			LOG.error("Error inversion/registrarInversionRequisitos:: ",e);
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
