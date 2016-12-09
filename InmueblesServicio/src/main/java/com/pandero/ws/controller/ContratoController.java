package com.pandero.ws.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pandero.ws.bean.ResultadoBean;
import com.pandero.ws.business.ContratoBusiness;
import com.pandero.ws.dao.ContratoDao;

@Controller
@RequestMapping("/contrato")
public class ContratoController {

	private static final Logger LOG = LoggerFactory.getLogger(ContratoController.class);
	
	@Autowired
	ContratoBusiness contratoBusiness;
	
	@Autowired
	ContratoDao contratoDao;
	
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
	
	@RequestMapping(value = "obtenerDiferenciaPrecio/{pedidoId}", method = RequestMethod.GET)
	public @ResponseBody ResultadoBean getDiferenciaPrecioPorContrato(@PathVariable(value="pedidoId") Integer pedidoId){
		LOG.info("###ContratoController.getDiferenciaPrecioPorContrato pedidoId:"+pedidoId);
		Map<String, Object> response = new HashMap<String, Object>();
		ResultadoBean resultadoBean = null;
		if(null!=pedidoId){
			try {
				resultadoBean=contratoBusiness.actualizarDiferenciaPrecioContratos(pedidoId);
			} catch (Exception e) {
				resultadoBean = new ResultadoBean();
				resultadoBean.setResultado("Ocurrio un error al actualizar la diferencia de precio");
				LOG.error("###getDiferenciaPrecioPorContrato:",e);
			}
		}
		return resultadoBean;
	}
	
}
