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
			String montoPrima = String.valueOf(params.get("montoPrima"));
			String usuarioId = String.valueOf(params.get("usuarioId"));
			LOG.info("datos: "+pedidoCaspioId+" - "+partidaRegistral+" - "+fichaConstitucion+" "+
					fechaConstitucion+" - "+montoPrima+" - "+usuarioId);
			
			result = garantiaBusiness.crearGarantiaSAF(pedidoCaspioId, partidaRegistral, fichaConstitucion, fechaConstitucion, montoPrima, usuarioId);
					
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
			String montoPrima = String.valueOf(params.get("montoPrima"));
			String usuarioId = String.valueOf(params.get("usuarioId"));
			
			result = garantiaBusiness.editarGarantiaSAF(garantiaId, partidaRegistral, fichaConstitucion, 
					fechaConstitucion, montoPrima, usuarioId);
					
		}catch(Exception e){
			LOG.error("Error pedido/editarGarantiaSAF:: ",e);
			e.printStackTrace();
			result="0";
			detail=e.getMessage();
		}			
		response.put("result",result);
		response.put("detail",detail);
		System.out.println("RESPONSE: " +  response);	
		
		return response;
	}
}
