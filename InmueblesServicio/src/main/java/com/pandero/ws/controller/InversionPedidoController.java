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

import com.pandero.ws.service.PedidoInversionService;
import com.pandero.ws.service.PedidoService;

@Controller
@RequestMapping("/inversionPedido")
public class InversionPedidoController {

	private static final Logger LOG = LoggerFactory.getLogger(InversionPedidoController.class);

	@Autowired
	PedidoInversionService pedidoInversionService;
	@Autowired
	PedidoService pedidoService;
	
	@RequestMapping(value = "/obtenerDatosInversion", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody   
	public Map<String, Object> obtenerDatosInversion(@RequestBody Map<String, Object> params) {
		System.out.println("EN METODO obtenerDatosInversion");
		System.out.println("REQUEST: " +  params);		
		Map<String, Object> response = new HashMap<String, Object>();
		String result="1", detail="";
		try{
			String inversionId = String.valueOf(params.get("inversionId"));
			pedidoInversionService.obtenerDatosInversion(inversionId);
			
		}catch(Exception e){
			LOG.error("Error pedidoInversion/obtenerDatosInversion:: ",e);
			e.printStackTrace();
			result="0";
		}
			
		response.put("result",result);
		response.put("detail",detail);
		System.out.println("RESPONSE: " +  response);	
		
		return response;
	}

}
