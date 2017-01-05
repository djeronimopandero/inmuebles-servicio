package com.pandero.ws.controller;

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
import com.pandero.ws.business.GeneralBusiness;
import com.pandero.ws.util.UtilEnum;

@Controller
@RequestMapping("general")
public class GeneralController {

	private static final Logger LOG = LoggerFactory.getLogger(GeneralController.class);
	
	@Autowired
	GeneralBusiness generalBusiness;
	
	@RequestMapping(value = "convertirDolares", method = RequestMethod.POST)
	public @ResponseBody ResultadoBean convertirDolares(@RequestBody Map<String, Object> params){
		LOG.info("###GeneralController.convertirSoles :"+params);
		ResultadoBean resultadoBean = null;
		Double montoDolares=0.00;
		if(null!=params.get("montoSoles") && null!=params.get("fechaEmision")){
			try {
				Double montoSoles=Double.parseDouble(params.get("montoSoles").toString());
				String fechaEmision=String.valueOf(params.get("fechaEmision"));
				
				resultadoBean = new ResultadoBean();
				montoDolares = generalBusiness.convertirDolares(montoSoles,fechaEmision);
				resultadoBean.setEstado(UtilEnum.ESTADO_OPERACION.EXITO.getCodigo());
				resultadoBean.setResultado(montoDolares);

			} catch (Exception e) {
				resultadoBean = new ResultadoBean();
				resultadoBean.setEstado(UtilEnum.ESTADO_OPERACION.EXCEPTION.getCodigo());
				resultadoBean.setResultado("Ocurrio un error al convertir a dolares");
				LOG.error("###convertirSoles:",e);
			}
		}else{
			LOG.error("###convertirSoles: parametros nulos");
			resultadoBean = new ResultadoBean();
			resultadoBean.setResultado("Parametros nulos");
		}
		return resultadoBean;
	}
	
	@RequestMapping(value = "convertirSoles", method = RequestMethod.POST)
	public @ResponseBody ResultadoBean convertirSoles(@RequestBody Map<String, Object> params){
		LOG.info("###GeneralController.convertirSoles params:"+params);
		ResultadoBean resultadoBean = null;
		Double montoSoles = 0.00;
		if(null!=params.get("montoDolares") && null!=params.get("fechaEmision")){
			
			Double montoDolares=Double.parseDouble(params.get("montoDolares").toString());
			String fechaEmision=String.valueOf(params.get("fechaEmision"));
			
			try {
				resultadoBean = new ResultadoBean();
				montoSoles = generalBusiness.convertirSoles(montoDolares,fechaEmision);
				resultadoBean.setEstado(UtilEnum.ESTADO_OPERACION.EXITO.getCodigo());
				resultadoBean.setResultado(montoSoles);
				
			} catch (Exception e) {
				resultadoBean = new ResultadoBean();
				resultadoBean.setEstado(UtilEnum.ESTADO_OPERACION.EXCEPTION.getCodigo());
				resultadoBean.setResultado("Ocurrio un error al convertir el monto "+montoDolares);
				LOG.error("###convertirSoles:",e);
			}
		}else{
			LOG.error("###Parametros vacios");
			resultadoBean = new ResultadoBean();
			resultadoBean.setResultado("Parametros vacios");
		}
		return resultadoBean;
	}
	
}
