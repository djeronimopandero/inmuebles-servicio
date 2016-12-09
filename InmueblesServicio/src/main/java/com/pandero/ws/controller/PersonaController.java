package com.pandero.ws.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pandero.ws.bean.PersonaSAF;
import com.pandero.ws.bean.ResultadoBean;
import com.pandero.ws.business.PersonaBusiness;
import com.pandero.ws.dao.PersonaDAO;

@Controller
@RequestMapping("/persona")
public class PersonaController {
	
	private static final Logger LOG = LoggerFactory.getLogger(PersonaController.class);
	
	@Autowired
	PersonaBusiness personaBusiness;
	
	@Autowired
	PersonaDAO personaDAO;
	
	@RequestMapping(value = "obtenerPorPedidoId/{pedidoId}", method = RequestMethod.GET)
	public @ResponseBody ResultadoBean getPersonaPorPedidoId(@PathVariable(value="pedidoId") Integer pedidoId){
		LOG.info("###ContratoController.getPersonaPorId pedidoId:"+pedidoId);
		ResultadoBean resultadoBean = null;
		if(null!=pedidoId){
			try {
				resultadoBean = new ResultadoBean();
				PersonaSAF personaSAF=personaBusiness.obtenerPersonaSAF(String.valueOf(pedidoId));
				resultadoBean.setResultado(personaSAF);
			} catch (Exception e) {
				resultadoBean = new ResultadoBean();
				resultadoBean.setResultado("Ocurrio un error al actualizar la diferencia de precio");
				LOG.error("###getDiferenciaPrecioPorContrato:",e);
			}
		}
		return resultadoBean;
	}
	
	@RequestMapping(value = "obtenerPorPersonaId/{personaId}", method = RequestMethod.GET)
	public @ResponseBody ResultadoBean getPersonaPorId(@PathVariable(value="personaId") Integer personaId){
		LOG.info("###ContratoController.getPersonaPorId personaId:"+personaId);
		ResultadoBean resultadoBean = null;
		if(null!=personaId){
			try {
				resultadoBean = new ResultadoBean();
				PersonaSAF personaSAF=personaDAO.obtenerPersonaSAF(String.valueOf(personaId));
				resultadoBean.setResultado(personaSAF);
			} catch (Exception e) {
				resultadoBean = new ResultadoBean();
				resultadoBean.setResultado("Ocurrio un error al actualizar la diferencia de precio");
				LOG.error("###getDiferenciaPrecioPorContrato:",e);
			}
		}
		return resultadoBean;
	}
	
	
}
