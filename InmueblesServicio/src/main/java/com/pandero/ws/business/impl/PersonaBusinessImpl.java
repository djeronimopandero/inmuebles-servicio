package com.pandero.ws.business.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pandero.ws.bean.Pedido;
import com.pandero.ws.bean.PersonaSAF;
import com.pandero.ws.business.PersonaBusiness;
import com.pandero.ws.dao.PersonaDao;
import com.pandero.ws.service.PedidoService;
import com.pandero.ws.util.ServiceRestTemplate;

@Component
public class PersonaBusinessImpl implements PersonaBusiness{
	
	private static final Logger LOG = LoggerFactory.getLogger(PersonaBusinessImpl.class);
	
	@Autowired
	PedidoService pedidoService;
	
	@Autowired
	PersonaDao personaDAO;
	
	@Override
	public PersonaSAF obtenerPersonaSAF(String pedidoId) throws Exception {
		LOG.info("###obtenerPersonaSAF pedidoId:"+pedidoId);
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		pedidoService.setTokenCaspio(tokenCaspio);
		
		PersonaSAF personaSAF=null;
		if(null!=pedidoId){
			
			Pedido pedidoCaspio= pedidoService.obtenerPedidoCaspioPorId(pedidoId);
			if(null!=pedidoCaspio){
				if(null!=pedidoCaspio.getAsociadoId()){
					personaSAF = personaDAO.obtenerPersonaSAF(String.valueOf(pedidoCaspio.getAsociadoId()));
				}
			}
			
		}
		return personaSAF;
	}

}
