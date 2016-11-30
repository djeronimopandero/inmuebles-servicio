package com.pandero.ws.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pandero.ws.bean.ResultadoBean;
import com.pandero.ws.business.ContratoBusiness;

@Controller
@RequestMapping("/contrato")
public class ContratoController {

	private static final Logger LOG = LoggerFactory.getLogger(ContratoController.class);
	
	@Autowired
	ContratoBusiness contratoBusiness;
	
	@RequestMapping(value = "actualizarCaspio", method = RequestMethod.GET)
	public @ResponseBody ResultadoBean sincronizarContratosyAsociadosSafACaspio() {
		LOG.info("###ContratoController.sincronizarContratosyAsociadosSafACaspio ");
		
		ResultadoBean resultadoBean=null;
		try{
			
			resultadoBean = contratoBusiness.sincronizarContratosyAsociadosSafACaspio();
			
		}catch(Exception e){
			LOG.error("###getContratosPorDocumento:",e);
		}
		return resultadoBean;
	}
	
}
