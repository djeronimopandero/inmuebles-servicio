package com.pandero.ws.business.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pandero.ws.bean.Pedido;
import com.pandero.ws.bean.PersonaSAF;
import com.pandero.ws.business.PersonaBusiness;
import com.pandero.ws.dao.LiquidacionDao;
import com.pandero.ws.dao.PersonaDao;
import com.pandero.ws.service.PedidoService;
import com.pandero.ws.service.PersonaService;
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
				current.put("CODPAISPREST", "");
				current.put("PAISPREST", "");
			}
			current.put("TIPOOPER", "GARANTIA");
			current.put("DESCRIPTIPOPLAN", "");
			current.put("CODPAISRIESGO", "189");
			current.put("PAISRIESGO", "PERÚ");
			
			if(current.get("PRIMAANUAL")!= null && !"".equals(String.valueOf(current.get("PRIMAANUAL")))){
				current.put("PRIMAANUAL", String.format(Locale.ROOT, "%.2f", Double.parseDouble(String.valueOf(current.get("PRIMAANUAL"))) * 12) );
			}else{
				current.put("PRIMAANUAL", "" );
				current.put("TASANETAANUAL", "");
			}
			
			current.remove("Persona_RazonSocial");
			current.remove("idPersona");
		}
		List<Map<String,Object>> resultFinal = new ArrayList<Map<String,Object>>();
		if(result!=null && !result.isEmpty()){
			for(Map<String,Object> current:result){
				Map<String,Object> tmpMap = new LinkedHashMap<String,Object>();
				tmpMap.put("NOMBRE", String.valueOf(current.get("NOMBRE")));
				tmpMap.put("APELL1", String.valueOf(current.get("APELL1")));
				tmpMap.put("APELL2", String.valueOf(current.get("APELL2")));
				tmpMap.put("TIPODOC", String.valueOf(current.get("TIPODOC")));
				tmpMap.put("NUMDOC", String.valueOf(current.get("NUMDOC")));
				tmpMap.put("FECNACIMIENTO", String.valueOf(current.get("FECNACIMIENTO")));
				tmpMap.put("SEXO", String.valueOf(current.get("SEXO")));
				tmpMap.put("ESTADOCIVIL", String.valueOf(current.get("ESTADOCIVIL")));
				tmpMap.put("CODPAISPREST", String.valueOf(current.get("CODPAISPREST")));
				tmpMap.put("PAISPREST", String.valueOf(current.get("PAISPREST")));
				tmpMap.put("CODDPTOPREST", String.valueOf(current.get("CODDPTOPREST")));
				tmpMap.put("DPTOPREST", String.valueOf(current.get("DPTOPREST")));
				tmpMap.put("CODPROVPREST", String.valueOf(current.get("CODPROVPREST")));
				tmpMap.put("PROVPREST", String.valueOf(current.get("PROVPREST")));
				tmpMap.put("CODDISTPREST", String.valueOf(current.get("CODDISTPREST")));
				tmpMap.put("DISTPREST", String.valueOf(current.get("DISTPREST")));
				tmpMap.put("DIRECPREST", String.valueOf(current.get("DIRECPREST")));
				tmpMap.put("TIPOOPER", String.valueOf(current.get("TIPOOPER")));
				tmpMap.put("NUMOPER", String.valueOf(current.get("NUMOPER")));
				tmpMap.put("FECINIVIG", String.valueOf(current.get("FECINIVIG")));
				tmpMap.put("FECFINVIG", String.valueOf(current.get("FECFINVIG")));
				tmpMap.put("SUMAASEG", String.valueOf(current.get("SUMAASEG")));
				tmpMap.put("CODTIPOPLAN", String.valueOf(current.get("CODTIPOPLAN")));
				tmpMap.put("DESCRIPTIPOPLAN", String.valueOf(current.get("DESCRIPTIPOPLAN")));
				tmpMap.put("CODPAISRIESGO", String.valueOf(current.get("CODPAISRIESGO")));
				tmpMap.put("PAISRIESGO", String.valueOf(current.get("PAISRIESGO")));
				tmpMap.put("CODDPTORIESGO", String.valueOf(current.get("CODDPTORIESGO")));
				
				tmpMap.put("DPTORIESGO", String.valueOf(current.get("DPTORIESGO")));
				tmpMap.put("CODPROVRIESGO", String.valueOf(current.get("CODPROVRIESGO")));
				tmpMap.put("PROVRIESGO", String.valueOf(current.get("PROVRIESGO")));
				tmpMap.put("CODDISTRIESGO", String.valueOf(current.get("CODDISTRIESGO")));
				tmpMap.put("DISTRIESGO", String.valueOf(current.get("DISTRIESGO")));
				tmpMap.put("DIRECRIESGO", String.valueOf(current.get("DIRECRIESGO")));
				tmpMap.put("CODCATCONSTRRIESGO", String.valueOf(current.get("CODCATCONSTRRIESGO")));
				tmpMap.put("CATCONSRIESGO", String.valueOf(current.get("CATCONSRIESGO")));
				tmpMap.put("TIPOEDIFRIESGO", String.valueOf(current.get("TIPOEDIFRIESGO")));
				tmpMap.put("NUMSOTCONSTRIESGO", String.valueOf(current.get("NUMSOTCONSTRIESGO")));
				tmpMap.put("NUMPISOCONSTRIESGO", String.valueOf(current.get("NUMPISOCONSTRIESGO")));
				tmpMap.put("AÑOCONSTRIESGO", String.valueOf(current.get("ANIOCONSTRIESGO")));
				tmpMap.put("USOSBS", String.valueOf(current.get("USOSBS")));
				tmpMap.put("MONEDA", String.valueOf(current.get("MONEDA")));
				tmpMap.put("PRIMA ANUAL", String.valueOf(current.get("PRIMAANUAL")));
				tmpMap.put("TASA NETA ANUAL", String.valueOf(current.get("TASANETAANUAL")));
				resultFinal.add(tmpMap);
			}
			
		}
		
		return  resultFinal;
		
	}
	
	@SuppressWarnings("unchecked")
	private Map<String,Object> obtenerDatosCorrespondeciaSAF(String personaId){
		Map<String,Object> parameters = new HashMap<String,Object>();
		parameters.put("personaId", personaId);
		Map<String,Object> datosCorrespondencia = liquidacionDao.executeProcedure(parameters, "USP_PER_obtenerDatosCorrespondencia");
		List<Map<String,Object>> data = (List<Map<String,Object>>)datosCorrespondencia.get("#result-set-1");
		return data.size()>0?data.get(0):null;
	}

}
