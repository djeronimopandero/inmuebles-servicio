package com.pandero.ws.business.impl;

import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pandero.ws.bean.Inversion;
import com.pandero.ws.bean.Pedido;
import com.pandero.ws.bean.PersonaSAF;
import com.pandero.ws.business.PersonaBusiness;
import com.pandero.ws.dao.LiquidacionDao;
import com.pandero.ws.dao.PersonaDao;
import com.pandero.ws.service.PedidoService;
import com.pandero.ws.service.PersonaService;
import com.pandero.ws.util.Constantes;
import com.pandero.ws.util.JsonUtil;
import com.pandero.ws.util.ServiceRestTemplate;

@Component
public class PersonaBusinessImpl implements PersonaBusiness{
	
	private static final Logger LOG = LoggerFactory.getLogger(PersonaBusinessImpl.class);
	
	@Autowired
	PedidoService pedidoService;
	
	@Autowired
	PersonaDao personaDAO;

	@Autowired
	PersonaService personaService;
	
	@Autowired
	LiquidacionDao liquidacionDao;
	
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
	
	

	@Override
	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> obtenerDatosCorrespondencia(String pedidoId) throws Exception{
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		personaService.setTokenCaspio(tokenCaspio);
		Map<String,Object> viewData = personaService.obtenerViewDescargaSeguroCaspio(pedidoId);
		List<Map<String,Object>> result = (List<Map<String,Object>>)viewData.get("Result");
		for(Map<String,Object> current:result){
			Map<String,Object> tmp = new HashMap<String,Object>();
			for(Map.Entry<String, Object> entry : current.entrySet()){
				if("FECINIVIG".equals(entry.getKey()) || "FECFINVIG".equals(entry.getKey())){
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");  
					Date d = sdf.parse(entry.getValue().toString());
					sdf.applyPattern("dd/MM/yyyy");
					entry.setValue(sdf.format(d));
				}				
			}
			tmp = obtenerDatosCorrespondeciaSAF(current.get("idPersona").toString());
			if(tmp!=null)
				current.putAll(tmp);
			current.remove("PK_ID");
			current.remove("idGarantia");
			current.remove("Garantia_pedidoId");
			current.remove("Seguro_modalidad");
			if("".equals(String.valueOf(current.get("NOMBRE")))){
				current.put("NOMBRE", current.get("Persona_RazonSocial"));
			}
			current.remove("Persona_RazonSocial");
			current.remove("idPersona");
		}
		
		return  result;
		
	}
	
	@SuppressWarnings("unchecked")
	private Map<String,Object> obtenerDatosCorrespondeciaSAF(String personaId){
		Map<String,String> parameters = new HashMap<String,String>();
		parameters.put("personaId", personaId);
		Map<String,Object> datosCorrespondencia = liquidacionDao.executeProcedure(parameters, "USP_PER_obtenerDatosCorrespondencia");
		List<Map<String,Object>> data = (List<Map<String,Object>>)datosCorrespondencia.get("#result-set-1");
		return data.size()>0?data.get(0):null;
	}

}
