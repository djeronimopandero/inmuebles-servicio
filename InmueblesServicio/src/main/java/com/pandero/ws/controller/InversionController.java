package com.pandero.ws.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pandero.ws.bean.ObservacionInversion;
import com.pandero.ws.bean.Parametro;
import com.pandero.ws.bean.ResultadoBean;
import com.pandero.ws.business.InversionBusiness;
import com.pandero.ws.util.Constantes;
import com.pandero.ws.util.DocumentoUtil;

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
	
	@RequestMapping(value = "anularVerificacion", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody ResultadoBean anularVerificacion(@RequestBody Map<String, Object> params) {
		System.out.println("###anularVerificacion params:"+params);
		
		ResultadoBean response = null;
		try{
			
			if(null!=params){
				if(null!=params.get("inversionId")){
					
					String inversionId = String.valueOf(params.get("inversionId"));
					inversionBusiness.anularVerificacion(inversionId);
					
					response = new ResultadoBean();
					response.setResultado(Constantes.Service.RESULTADO_EXITOSO);
				}
			}
			
		}catch(Exception e){
			LOG.error("###Error ",e);
			response = new ResultadoBean();
			response.setMensajeError(Constantes.Service.RESULTADO_ERROR_INESPERADO);
		}
			
		return response;
	}
	
//	@RequestMapping(value = "/generarCartaObservacion", method = RequestMethod.POST, produces = "application/json")
//    @ResponseBody   
//	public Map<String, Object> generarCartaObservacion(@RequestBody Map<String, Object> params) {
//		System.out.println("EN METODO generarCartaObservacion");
//		System.out.println("REQUEST: " +  params);		
//		Map<String, Object> response = new HashMap<String, Object>();
//		String result="", detail="";
//		try{
//			String pedidoId = String.valueOf(params.get("pedidoId"));
//			String usuarioId = String.valueOf(params.get("usuarioId"));
//			
////			inversionBusiness.generarCartaObservacion(pedidoId, usuarioId);
//			result = Constantes.Service.RESULTADO_EXITOSO;
//			
//		}catch(Exception e){
//			LOG.error("Error pedido/generarCartaObservacion:: ",e);
//			e.printStackTrace();
//			result=Constantes.Service.RESULTADO_ERROR_INESPERADO;
//			detail=e.getMessage();
//		}
//			
//		response.put("result",result);
//		response.put("detail",detail);
//		System.out.println("RESPONSE: " +  response);	
//		
//		return response;
//	}
	
	/* Generacion de doc */
	@RequestMapping(value = "/generarCartaObservacion", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody   
	public Map<String, Object> generarCartaObservacion(@RequestBody Map<String, Object> params) {
		LOG.info("###generarCartaObservacion params:"+params);
		Map<String, Object> response = new HashMap<String, Object>();
		String result="";
		try{
			
			String usuarioId = String.valueOf(params.get("usuarioId"));
			String inversionId = String.valueOf(params.get("inversionId"));
			inversionBusiness.generarCartaObservacion(inversionId, usuarioId);
			
//			String fileDocx="D:/home/pandero/INMUEBLES/DOCUMENTS/TEMPLATE/gestion_inversion_inmobiliaria_desembolso/Carta-de-validacion-de-datos-inversion-inmobiliaria-TEMPLATE.docx";
//			String filePdf="D:/home/pandero/INMUEBLES/DOCUMENTS/GENERADOS/Carta-de-validacion-de-datos-inversion-inmobiliaria-COPIA.pdf";
//			String fileEditDocx="D:/home/pandero/INMUEBLES/DOCUMENTS/GENERADOS/Carta-de-validacion-de-datos-inversion-inmobiliaria-REEMPLAZADO.docx";
//			
//			DocumentoUtil documentoUtil=new DocumentoUtil();	
//			XWPFDocument docx= documentoUtil.openDocument(fileDocx);
//			
//			List<Parametro> listaParametros=new ArrayList<>();
//			Parametro parametro1=new Parametro("text_1", "Lima, 16 de agosto del 2016");
//			Parametro parametro2=new Parametro("text_2", "Arly Fernández");
//			Parametro parametro3=new Parametro("text_3", "CANCELACIÓN DE CRÉDITO HIPOTECARIO");
//			Parametro parametro4=new Parametro("text_4", "LOCAL COMERCIAL");
//			Parametro parametro5=new Parametro("text_5", "27,000.00");
//			Parametro parametro6=new Parametro("text_6", "Tabla");
//			Parametro parametro7=new Parametro("text_7", "FIORELLA FLORES PANDURO");
//			
//			
//			listaParametros.add(parametro1);
//			listaParametros.add(parametro2);
//			listaParametros.add(parametro3);
//			listaParametros.add(parametro4);
//			listaParametros.add(parametro5);
//			listaParametros.add(parametro6);
//			listaParametros.add(parametro7);
//			
//			List<ObservacionInversion> listObservacion=new ArrayList<>();
//			
//			XWPFDocument docxEdit=DocumentoUtil.replaceTextCartaValidacion(docx,listaParametros,listObservacion);
//			
//			documentoUtil.saveDocument(docxEdit, fileEditDocx);
//			
//			DocumentoUtil.convertDocxToPdf(fileEditDocx,filePdf);
			
			result = Constantes.Service.RESULTADO_EXITOSO;
			
		}catch(Exception e){
			LOG.error("Error pedido/generarCartaObservacion:: ",e);
			e.printStackTrace();
			result=Constantes.Service.RESULTADO_ERROR_INESPERADO;
		}
			
		response.put("result",result);
		
		return response;
	}
	/* Generacion de doc */
	
}
