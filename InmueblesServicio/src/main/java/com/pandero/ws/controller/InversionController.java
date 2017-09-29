package com.pandero.ws.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pandero.ws.bean.DetalleDiferenciaPrecio;
import com.pandero.ws.bean.Inversion;
import com.pandero.ws.bean.LiquidacionSAF;
import com.pandero.ws.bean.ResultadoBean;
import com.pandero.ws.business.InversionBusiness;
import com.pandero.ws.business.LiquidacionBusiness;
import com.pandero.ws.dao.LiquidacionDao;
import com.pandero.ws.service.InversionService;
import com.pandero.ws.util.Constantes;
import com.pandero.ws.util.JsonUtil;
import com.pandero.ws.util.ServiceRestTemplate;
import com.pandero.ws.util.UtilEnum;

@Controller
@RequestMapping("/inversionPedido")
public class InversionController {

	private static final Logger LOG = LoggerFactory.getLogger(InversionController.class);

	@Autowired
	InversionBusiness inversionBusiness;
	@Autowired
	LiquidacionBusiness liquidacionBusiness;
	@Autowired
	InversionService inversionService;
	@Autowired
	LiquidacionDao liquidacionDao;
	
	@RequestMapping(value = "obtenerInversion/{inversionId}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody   
	public ResultadoBean obtenerInversion(@PathVariable(value="inversionId") String inversionId) {
		LOG.info("###obtenerInversion inversionId:"+inversionId);
		ResultadoBean response = null;
		try{
			String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
			inversionService.setTokenCaspio(tokenCaspio);
			
			Inversion inversion = inversionService.obtenerInversionCaspioPorId(inversionId);	
			response = new ResultadoBean();
			response.setEstado(UtilEnum.ESTADO_OPERACION.EXITO.getCodigo());
			response.setResultado(inversion);
		}catch(Exception e){
			LOG.error("Error inversion/obtenerInversion:: ",e);
			response = new ResultadoBean();
			response.setEstado(UtilEnum.ESTADO_OPERACION.EXCEPTION.getCodigo());
		}
		return response;
	}
	
	@RequestMapping(value = "actualizarInversion/{nroInversion}/{montoInversion}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody   
	public ResultadoBean actualizarInversion(@PathVariable(value="nroInversion") String nroInversion,@PathVariable(value="montoInversion") Double montoInversion) {
		LOG.info("###obtenerInversion nroInversion:"+nroInversion);
		LOG.info("###obtenerInversion montoInversion:"+montoInversion);
		ResultadoBean response = null;
		try{
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("nroInversion", nroInversion);
			params.put("montoInversion", montoInversion);
			response=inversionBusiness.actualizarInversionMonto(params);
			
		}catch(Exception e){
			LOG.error("Error inversion/obtenerInversion:: ",e);
			response = new ResultadoBean();
			response.setEstado(UtilEnum.ESTADO_OPERACION.EXCEPTION.getCodigo());
		}
		return response;
	}
	
	@RequestMapping(value = "/confirmarInversion", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody   
	public Map<String, Object> confirmarInversion(@RequestBody Map<String, Object> params) {
		LOG.info("###confirmarInversion params:"+params);	
		Map<String, Object> response = new HashMap<String, Object>();
		String result="1", detail="";
		try{
			String inversionId = String.valueOf(params.get("inversionId"));
			String situacionConfirmado = String.valueOf(params.get("situacionConfirmado"));
			String usuarioId = String.valueOf(params.get("usuarioId"));
			result  = inversionBusiness.confirmarInversion(inversionId,situacionConfirmado,usuarioId);		
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
		LOG.info("###eliminarInversion params:"+params);		
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
		LOG.info("###registrarInversionRequisitos params:"+params);			
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
	
	@RequestMapping(value = "/anularVerificacion", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Map<String, Object> anularVerificacion(@RequestBody Map<String, Object> params) {
		LOG.info("###anularVerificacion params:"+params);
		Map<String, Object> response = new HashMap<String, Object>();
		String result="", detail="";
		try{			
			if(null!=params){
				if(null!=params.get("inversionId")){					
					String inversionId = String.valueOf(params.get("inversionId"));
					result = inversionBusiness.anularVerificacion(inversionId);
				}
			}			
		}catch(Exception e){
			LOG.error("Error inversion/anularVerificacion:: ",e);
			e.printStackTrace();
			result=Constantes.Service.RESULTADO_ERROR_INESPERADO;
			detail=e.getMessage();
		}
			
		response.put("result",result);
		response.put("detail",detail);
			
		return response;
	}
	
	@RequestMapping(value = "/generarCartaObservacion", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody   
	public Map<String, Object> generarCartaObservacion(@RequestBody Map<String, Object> params) {
		LOG.info("###generarCartaObservacion params:"+params);
		Map<String, Object> response = new HashMap<String, Object>();
		String result="", detail="";
		try{
			
			String usuarioId = String.valueOf(params.get("usuarioId"));
			String inversionId = String.valueOf(params.get("inversionId"));
			result = inversionBusiness.generarCartaObservacion(inversionId, usuarioId);
			
		}catch(Exception e){
			LOG.error("Error inversion/generarCartaObservacion:: ",e);
			e.printStackTrace();
			result=Constantes.Service.RESULTADO_ERROR_INESPERADO;
			detail=e.getMessage();
		}
			
		response.put("result",result);
		response.put("detail",detail);
		
		return response;
	}
	
	@RequestMapping(value = "/generarLiquidacionInversion", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Map<String, Object> generarLiquidacionInversion(@RequestBody Map<String, Object> params) {
		LOG.info("###generarLiquidacionInversion params:"+params);
		Map<String, Object> response = new HashMap<String, Object>();
		String result="1", detail="";
		try{
			String nroInversion = String.valueOf(params.get("nroInversion"));
			String nroArmada = String.valueOf(params.get("nroArmada"));
			String usuarioId = String.valueOf(params.get("usuarioId"));	
			result = liquidacionBusiness.generarLiquidacionPorInversion(nroInversion, nroArmada, usuarioId);
			if(result.equals("")){
				result = Constantes.Service.RESULTADO_EXITOSO;
			}
		}catch(Exception e){
			LOG.error("Error inversion/generarLiquidacionInversion:: ",e);
			e.printStackTrace();
			result=Constantes.Service.RESULTADO_ERROR_INESPERADO;
			detail=e.getMessage();
		}
			
		response.put("result",result);
		response.put("detail",detail);
		System.out.println("RESPONSE: " +  response);
					
		return response;
	}
	
	@RequestMapping(value = "/anularLiquidacionInversion", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Map<String, Object> anularLiquidacionInversion(@RequestBody Map<String, Object> params) {	
		LOG.info("###anularLiquidacionInversion params:"+params);
		Map<String, Object> response = new HashMap<String, Object>();
		String result="1", detail="";
		try{
			String nroInversion = String.valueOf(params.get("nroInversion"));
			String nroArmada = String.valueOf(params.get("nroArmada"));
			String usuarioId = String.valueOf(params.get("usuarioId"));	
			result = liquidacionBusiness.eliminarLiquidacionInversion(nroInversion, nroArmada, usuarioId);
			if(result.equals("")){
				result = Constantes.Service.RESULTADO_EXITOSO;
			}
		}catch(Exception e){
			LOG.error("Error inversion/generarLiquidacionInversion:: ",e);
			e.printStackTrace();
			result=Constantes.Service.RESULTADO_ERROR_INESPERADO;
			detail=e.getMessage();
		}
			
		response.put("result",result);
		response.put("detail",detail);
		System.out.println("RESPONSE: " +  response);
			
		return response;
	}
	
	@RequestMapping(value = "/actualizarEstadoInversion", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Map<String, Object> actualizarEstadoInversion(@RequestBody Map<String, Object> params) {	
		LOG.info("###actualizarEstadoInversion params:"+params);
		Map<String, Object> response = new HashMap<String, Object>();
		String result="1", detail="";
		try{
			String nroInversion = String.valueOf(params.get("nroInversion"));
			String estadoInversion = String.valueOf(params.get("estadoInversion"));
			result = inversionBusiness.actualizarEstadoInversionCaspioPorNro(nroInversion, estadoInversion);
			if(result.equals("")){
				result = Constantes.Service.RESULTADO_EXITOSO;
			}
		}catch(Exception e){
			LOG.error("Error inversion/actualizarEstadoInversion:: ",e);
			e.printStackTrace();
			result=Constantes.Service.RESULTADO_ERROR_INESPERADO;
			detail=e.getMessage();
		}
			
		response.put("result",result);
		response.put("detail",detail);
		System.out.println("RESPONSE: " +  response);
			
		return response;
	}
	
	@RequestMapping(value = "/confirmarLiquidacionInversion", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Map<String, Object> confirmarLiquidacionInversion(@RequestBody Map<String, Object> params) {	
		LOG.info("###confirmarLiquidacionInversion params:"+params);
		Map<String, Object> response = new HashMap<String, Object>();
		String result="1", detail="";
		try{
			String nroInversion = String.valueOf(params.get("nroInversion"));
			String nroArmada = String.valueOf(params.get("nroArmada"));
			String usuarioId = String.valueOf(params.get("usuarioId"));
			result = liquidacionBusiness.confirmarLiquidacionInversion(nroInversion, nroArmada, usuarioId);
			if(result.equals("")){
				result = Constantes.Service.RESULTADO_EXITOSO;
			}
		}catch(Exception e){
			LOG.error("Error inversion/confirmarLiquidacionInversion:: ",e);
			e.printStackTrace();
			result=Constantes.Service.RESULTADO_ERROR_INESPERADO;
			detail=e.getMessage();
		}
			
		response.put("result",result);
		response.put("detail",detail);
		System.out.println("RESPONSE: " +  response);
			
		return response;
	}

	@RequestMapping(value = "obtenerImporteComprobante/{inversionNumero}/{nroArmada}", method = RequestMethod.GET)
	public @ResponseBody ResultadoBean getImporteComprobantePath(@PathVariable(value="inversionNumero") String inversionNumero,@PathVariable(value="nroArmada") Integer nroArmada){
		LOG.info("###ContratoController.getImporteComprobante inversionNumero:"+inversionNumero+", nroArmada:"+nroArmada);
		ResultadoBean resultadoBean = null;
		//Obtener el id de inversion y con eso llamar a los comprbantes y por armada
		if(null!=inversionNumero && null!=nroArmada){
			try {
				resultadoBean=inversionBusiness.getImporteComprobante(inversionNumero,nroArmada);
			} catch (Exception e) {
				resultadoBean = new ResultadoBean();
				resultadoBean.setEstado(UtilEnum.ESTADO_OPERACION.EXCEPTION.getCodigo());
				resultadoBean.setResultado("Ocurrio un error al obtener el importe de comprobante");
				LOG.error("###obtenerImporteComprobante:",e);
			}
		}
		return resultadoBean;
	}
	
	@RequestMapping(value = "solicitudDesembolso/{inversionNumero}/{nroArmada}/{usuarioId}", method = RequestMethod.GET)
	public @ResponseBody String solicitudDesembolso(@PathVariable(value="inversionNumero") String inversionNumero,
			@PathVariable(value="nroArmada") String nroArmada, @PathVariable(value="usuarioId") String usuarioId){
		LOG.info("###ContratoController.solicitudDesembolso inversionNumero:"+inversionNumero);
		String resultadoBean = null;
		if(null!=inversionNumero ){
			try {
				resultadoBean=inversionBusiness.generarDocumentoDesembolso(inversionNumero,nroArmada,usuarioId);
			} catch (Exception e) {
				LOG.error(e.getMessage());
				return "Ocurrió un error al generar la solicitud de desembolso";
			}
		}
		return resultadoBean;
	}
	
	@RequestMapping(value = "obtenerMontosDifPrecioInversion/{nroInversion}", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> obtenerMontosDifPrecioInversion(@PathVariable(value="nroInversion") String nroInversion) {	
		LOG.info("###obtenerMontosDifPrecioInversion params: "+nroInversion);
		Map<String, Object> response = new HashMap<String, Object>();
		try{
			DetalleDiferenciaPrecio detalleDifPrecio = liquidacionBusiness.obtenerMontosDifPrecioInversion(nroInversion);
			if(detalleDifPrecio!=null){	
				response.put("result","1");
				response.put("diferenciaPrecio",detalleDifPrecio.getDiferenciaPrecio()==null?"0.00":detalleDifPrecio.getDiferenciaPrecio().replace(",", ""));
				response.put("importeFinanciado",detalleDifPrecio.getImporteFinanciado()==null?"0.00":detalleDifPrecio.getImporteFinanciado().replace(",", ""));
				response.put("saldoDiferencia",detalleDifPrecio.getSaldoDiferencia()==null?"0.00":detalleDifPrecio.getSaldoDiferencia().replace(",", ""));
				response.put("montoDifPrecioPagado",detalleDifPrecio.getMontoDifPrecioPagado()==null?"0.00":detalleDifPrecio.getMontoDifPrecioPagado().replace(",", ""));
				response.put("tipoInversion",detalleDifPrecio.getTipoInversion());
				response.put("montoDisponible",detalleDifPrecio.getMontoDisponible());
				response.put("montoInversion",detalleDifPrecio.getMontoInversion());
			}else{
				response.put("result","0");
			}
		}catch(Exception e){
			LOG.error("Error inversion/obtenerMontosDifPrecioInversion:: ",e);
			e.printStackTrace();
			response.put("result","0");
			response.put("detail",e.getMessage());
		}
			
		System.out.println("RESPONSE: " +  response);
			
		return response;
	}
		
	//Serice recibe parametro InversionId y retorne la url
	@RequestMapping(value = "obtenerUrlCancelarComprobante/{inversionId}", method = RequestMethod.GET)
	public @ResponseBody ResultadoBean getURLCancelarComprobante(@PathVariable(value="inversionId") String inversionId){
		LOG.info("###ContratoController.getURLCancelarComprobante inversionId:"+inversionId);
		
		ResultadoBean resultadoBean = null;
		//Obtener el id de inversion y con eso llamar a los comprbantes y por armada
		if(null!=inversionId){
			try {
				
				String locationHref = inversionBusiness.getURLCancelarComprobante(inversionId);
				
				resultadoBean = new ResultadoBean();
				resultadoBean.setEstado(UtilEnum.ESTADO_OPERACION.EXITO.getCodigo());
				resultadoBean.setResultado(locationHref);
				
			} catch (Exception e) {
				resultadoBean = new ResultadoBean();
				resultadoBean.setEstado(UtilEnum.ESTADO_OPERACION.EXCEPTION.getCodigo());
				resultadoBean.setResultado("Ocurrio un error al obtener la url de cancelar comprobante");
				LOG.error("###getURLCancelarComprobante:",e);
			}
		}else{
			resultadoBean = new ResultadoBean();
			resultadoBean.setEstado(UtilEnum.ESTADO_OPERACION.ERROR.getCodigo());
			resultadoBean.setResultado("Ocurrio un error, el InversionId es null");
		}
		return resultadoBean;
	}
	

	@RequestMapping(value = "enviarCargoContabilidad/{inversionId}/{nroArmada}/{usuario}/{usuarioId}", method = RequestMethod.GET)
	public @ResponseBody ResultadoBean enviarCargoContabilidad(@PathVariable(value="inversionId") String inversionId,
			@PathVariable(value="nroArmada") String nroArmada,@PathVariable(value="usuario") String usuario,@PathVariable(value="usuarioId") String usuarioId){
		LOG.info("###ContratoController.enviarCargoContabilidad inversionId:"+inversionId+", nroArmada:"+nroArmada+",usuario:"+usuario+",usuarioId:"+usuarioId);
		
		ResultadoBean resultadoBean = null;
		if(null!=inversionId && null!=nroArmada && null!=usuarioId){
			try {				
				resultadoBean = inversionBusiness.enviarCargoContabilidad(inversionId, nroArmada, usuario, usuarioId);				
			} catch (Exception e) {
				resultadoBean = new ResultadoBean();
				resultadoBean.setEstado(UtilEnum.ESTADO_OPERACION.EXCEPTION.getCodigo());
				resultadoBean.setResultado(Constantes.Service.RESULTADO_ERROR_INESPERADO);
				LOG.error("###enviarCartaContabilidad:",e);
			}
		}else{
			resultadoBean = new ResultadoBean();
			resultadoBean.setEstado(UtilEnum.ESTADO_OPERACION.ERROR.getCodigo());
			resultadoBean.setResultado(Constantes.Service.RESULTADO_ERROR_INESPERADO);
		}
		return resultadoBean;
	}
	
	@RequestMapping(value = "anularCargoContabilidad/{inversionId}/{nroArmada}/{usuario}", method = RequestMethod.GET)
	public @ResponseBody ResultadoBean anularCargoContabilidad(@PathVariable(value="inversionId") String inversionId,
			@PathVariable(value="nroArmada") String nroArmada,@PathVariable(value="usuario") String usuario){
		LOG.info("###ContratoController.anularCargoContabilidad inversionId:"+inversionId+", nroArmada:"+nroArmada+",usuario:"+usuario);
		
		ResultadoBean resultadoBean = null;
		try {				
			resultadoBean = inversionBusiness.anularCargoContabilidad(inversionId, nroArmada, usuario);				
		} catch (Exception e) {
			resultadoBean = new ResultadoBean();
			resultadoBean.setEstado(UtilEnum.ESTADO_OPERACION.EXCEPTION.getCodigo());
			resultadoBean.setResultado("Ocurrió un error al anular el envío de documentos");
			LOG.error("###enviarCargoContabilidad:",e);
		}
		return resultadoBean;
	}
		
	@RequestMapping(value = "/recepcionarCargoContabilidad", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Map<String, Object> recepcionarCargoContabilidad(@RequestBody Map<String, Object> params) {	
		LOG.info("###recepcionarCargoContabilidad params:"+params);
		Map<String, Object> response = new HashMap<String, Object>();
		String result="1", detail="";
		try{
			String inversionId = String.valueOf(params.get("inversionId"));
			String nroArmada = String.valueOf(params.get("nroArmada"));
			String fechaRecepcion = String.valueOf(params.get("fechaRecepcion"));
			String usuarioRecepcion = String.valueOf(params.get("usuarioRecepcion"));
			result = inversionBusiness.recepcionarCargoContabilidad(inversionId, nroArmada, fechaRecepcion, usuarioRecepcion);
			if(result.equals("")){
				result = Constantes.Service.RESULTADO_EXITOSO;
			}
		}catch(Exception e){
			LOG.error("Error inversion/recepcionarCargoContabilidad:: ",e);
			e.printStackTrace();
			result=Constantes.Service.RESULTADO_ERROR_INESPERADO;
			detail=e.getMessage();
		}			
		response.put("result",result);
		response.put("detail",detail);
		System.out.println("RESPONSE: " +  response);			
		return response;
	}
	
	@RequestMapping(value = "envioCargoContabilidadActualizSaldo/{inversionId}/{usuario}", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody ResultadoBean envioCargoContabilidadActualizSaldo(@PathVariable(value="inversionId") String inversionId,@PathVariable(value="usuario") String usuario) {	
		LOG.info("###envioCargoContabilidadActualizSaldo inversionId:"+inversionId+", usuario:"+usuario);
		ResultadoBean resultadoBean = null;
		try{
			String resultado = inversionBusiness.envioCargoContabilidadActualizSaldo(inversionId, usuario);
			if(resultado.equals("")){
				resultado = "Se enviaron los documentos a contabilidad.";
			}
			resultadoBean = new ResultadoBean();
			resultadoBean.setEstado(UtilEnum.ESTADO_OPERACION.EXITO.getCodigo());			
			resultadoBean.setResultado(resultado);
		}catch(Exception e){
			LOG.error("Error inversion/envioCargoContabilidadActualizSaldo:: ",e);
			resultadoBean = new ResultadoBean();
			resultadoBean.setEstado(UtilEnum.ESTADO_OPERACION.EXCEPTION.getCodigo());
			resultadoBean.setResultado(Constantes.Service.RESULTADO_ERROR_INESPERADO);
			LOG.error("###enviarCartaContabilidad:",e);
		}	
		return resultadoBean;
	}	
	
	@RequestMapping(value = "anularEnvioCargoContabilidadActualizSaldo/{inversionId}/{usuario}", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody ResultadoBean anularEnvioCargoContabilidadActualizSaldo(@PathVariable(value="inversionId") String inversionId,@PathVariable(value="usuario") String usuario) {	
		LOG.info("###envioCargoContabilidadActualizSaldo inversionId:"+inversionId+", usuario:"+usuario);
		ResultadoBean resultadoBean = null;
		try{
			String resultado = inversionBusiness.anularEnvioCargoContabilidadActualizSaldo(inversionId, usuario);
			resultadoBean = new ResultadoBean();
			resultadoBean.setEstado(UtilEnum.ESTADO_OPERACION.EXITO.getCodigo());
			if(resultado.equals("")){
				resultado="Se anuló el envio de documentos a contabilidad.";
			}
			resultadoBean.setResultado(resultado);
		}catch(Exception e){
			LOG.error("Error inversion/anularEnvioCargoContabilidadActualizSaldo:: ",e);
			resultadoBean = new ResultadoBean();
			resultadoBean.setEstado(UtilEnum.ESTADO_OPERACION.EXCEPTION.getCodigo());
			resultadoBean.setResultado(Constantes.Service.RESULTADO_ERROR_INESPERADO);
			LOG.error("###enviarCartaContabilidad:",e);
		}	
		return resultadoBean;
	}

	@RequestMapping(value = "/recepcionarCargoContabilidadActualizSaldo", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Map<String, Object> recepcionarCargoContabilidadActualizSaldo(@RequestBody Map<String, Object> params) {	
		LOG.info("###recepcionarCargoContabilidadActualizSaldo params:"+params);
		Map<String, Object> response = new HashMap<String, Object>();
		String result="1", detail="";
		try{
			String inversionId = String.valueOf(params.get("inversionId"));
			String fechaRecepcion = String.valueOf(params.get("fechaRecepcion"));
			String usuarioRecepcion = String.valueOf(params.get("usuarioRecepcion"));
			result = inversionBusiness.recepcionarCargoContabilidadActualizSaldo(inversionId, fechaRecepcion, usuarioRecepcion);
			if(result.equals("")){
				result = Constantes.Service.RESULTADO_EXITOSO;
			}
		}catch(Exception e){
			LOG.error("Error inversion/recepcionarCargoContabilidadActualizSaldo:: ",e);
			e.printStackTrace();
			result=Constantes.Service.RESULTADO_ERROR_INESPERADO;
			detail=e.getMessage();
		}			
		response.put("result",result);
		response.put("detail",detail);
		System.out.println("RESPONSE: " +  response);			
		return response;
	}	

	@RequestMapping(value = "verificarRegistrarFacturas/{inversionId}/{nroArmada}", method = RequestMethod.GET)
	public @ResponseBody ResultadoBean verificarRegistrarFacturas(
	@PathVariable(value="inversionId") String inversionId,@PathVariable(value="nroArmada") String nroArmada){
		LOG.info("###ContratoController.verificarRegistrarFacturas inversionId:"+inversionId+", nroArmada:"+nroArmada);
		
		ResultadoBean resultadoBean = null;
		if(null!=inversionId && null!=nroArmada){
			try {
				
				resultadoBean = inversionBusiness.verificarRegistrarFacturas(inversionId, nroArmada);
				
			} catch (Exception e) {
				resultadoBean = new ResultadoBean();
				resultadoBean.setEstado(UtilEnum.ESTADO_OPERACION.EXCEPTION.getCodigo());
				resultadoBean.setResultado("Ocurrio un error al verificar si es posible registrar facturas");
				LOG.error("###verificarRegistrarFacturas:",e);
			}
		}else{
			resultadoBean = new ResultadoBean();
			resultadoBean.setEstado(UtilEnum.ESTADO_OPERACION.ERROR.getCodigo());
			resultadoBean.setResultado("Ocurrio un error, parametro null");
		}
		return resultadoBean;
	}
	

	@RequestMapping(value = "grabarComprobante/{inversionId}/{nroArmada}/{usuarioId}", method = RequestMethod.GET)
	public @ResponseBody ResultadoBean grabarComprobantes(@PathVariable(value="inversionId") String inversionId,
			@PathVariable(value="nroArmada") String nroArmada,@PathVariable(value="usuarioId") String usuarioId){
		LOG.info("###ContratoController.grabarComprobantes inversionId:"+inversionId+", nroArmada:"+nroArmada+",usuarioId:"+usuarioId);
		
		ResultadoBean resultadoBean = null;
		if(null!=inversionId && null!=nroArmada && null!=usuarioId){
			try {
				
				resultadoBean = inversionBusiness.grabarComprobantes(inversionId, nroArmada, usuarioId);
				
			} catch (Exception e) {
				resultadoBean = new ResultadoBean();
				resultadoBean.setEstado(UtilEnum.ESTADO_OPERACION.EXCEPTION.getCodigo());
				resultadoBean.setResultado("Ocurrio un error al grabar los comprobantes para inversionId:"+inversionId);
				LOG.error("###grabarComprobantes:",e);
			}
		}else{
			resultadoBean = new ResultadoBean();
			resultadoBean.setEstado(UtilEnum.ESTADO_OPERACION.ERROR.getCodigo());
			resultadoBean.setResultado("Ocurrio un error, parametro null");
		}
		return resultadoBean;
	}

//	@RequestMapping(value = "/obtenerUltimaLiquidacionInversion", method = RequestMethod.POST, produces = "application/json")
//	@ResponseBody
//	public Map<String, Object> obtenerUltimaLiquidacionInversion(@RequestBody Map<String, Object> params) {
//		LOG.info("###obtenerUltimaLiquidacionInversion params:"+params);
//		Map<String, Object> response = new HashMap<String, Object>();
//		String result="", detail="";
//		try{
//			String nroInversion = String.valueOf(params.get("nroInversion"));
//			LiquidacionSAF liquidacionSAF = inversionBusiness.obtenerUltimaLiquidacionInversion(nroInversion);
//			if(liquidacionSAF!=null){
//				result = "1";
//				detail = JsonUtil.toJson(liquidacionSAF);
//			}
//		}catch(Exception e){
//			LOG.error("Error inversion/obtenerUltimaLiquidacionInversion:: ",e);
//			e.printStackTrace();
//			result=Constantes.Service.RESULTADO_ERROR_INESPERADO;
//			detail=e.getMessage();
//		}			
//		response.put("result",result);
//		response.put("detail",detail);
//		System.out.println("RESPONSE: " +  response);			
//		return response;
//	}	

	@RequestMapping(value = "/obtenerUltimaLiquidacionInversionPorId", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Map<String, Object> obtenerUltimaLiquidacionInversionPorId(@RequestBody Map<String, Object> params) {
		LOG.info("###obtenerUltimaLiquidacionInversionPorId params:"+params);
		Map<String, Object> response = new HashMap<String, Object>();
		String result="", detail="";
		try{
			String inversionId = String.valueOf(params.get("inversionId"));
			LiquidacionSAF liquidacionSAF = inversionBusiness.obtenerUltimaLiquidacionInversionPorId(inversionId);
			if(liquidacionSAF!=null){
				result = "1";
				detail = JsonUtil.toJson(liquidacionSAF);
			}
		}catch(Exception e){
			LOG.error("Error inversion/obtenerUltimaLiquidacionInversionPorId:: ",e);
			e.printStackTrace();
			result=Constantes.Service.RESULTADO_ERROR_INESPERADO;
			detail=e.getMessage();
		}			
		response.put("result",result);
		response.put("detail",detail);
		System.out.println("RESPONSE: " +  response);			
		return response;
	}	
	
	@RequestMapping(value = "/actualizarDesembolsoCaspio", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Map<String, Object> actualizarDesembolsoCaspio(@RequestBody Map<String, Object> params) {
		LOG.info("###actualizarDesembolsoCaspio params:"+params);
		Map<String, Object> response = new HashMap<String, Object>();
		String result="", detail="";
		try{
			String nroInversion = String.valueOf(params.get("nroInversion"));
			String nroArmada = String.valueOf(params.get("nroArmada"));
			String nroDesembolso = String.valueOf(params.get("nroDesembolso"));
			liquidacionBusiness.actualizarDesembolsoCaspio(nroInversion, nroArmada, nroDesembolso);
			result = Constantes.Service.RESULTADO_EXITOSO;
		}catch(Exception e){
			LOG.error("Error inversion/actualizarDesembolsoCaspio:: ",e);
			e.printStackTrace();
			result=Constantes.Service.RESULTADO_ERROR_INESPERADO;
			detail=e.getMessage();
		}			
		response.put("result",result);
		response.put("detail",detail);
		System.out.println("RESPONSE: " +  response);			
		return response;
	}	
	
	
	@RequestMapping(value = "obtenerSolicitudDesembolsoExcepcional/{inversionNumero}/{nroArmada}", method = RequestMethod.GET)
	public @ResponseBody List<LinkedHashMap<String,Object>> getResumenComprobante(@PathVariable(value="inversionNumero") String inversionNumero,
			@PathVariable(value="nroArmada") String nroArmada){
		
		List<LinkedHashMap<String,Object>> result = new ArrayList<LinkedHashMap<String,Object>>();
		LinkedHashMap<String,Object> element = new LinkedHashMap<String,Object>();
		
		if(null!=inversionNumero){
			try {
				List<LiquidacionSAF> liquidacionesSAF = liquidacionDao.obtenerLiquidacionPorInversionArmada(inversionNumero, nroArmada);
				if(liquidacionesSAF!=null && liquidacionesSAF.size()>0){
					LiquidacionSAF liquidacionSAF = new LiquidacionSAF();
					double liquidacionImporte = 0.00;
					for(LiquidacionSAF liquidacion : liquidacionesSAF){
						liquidacionSAF.setLiquidacionFecha(liquidacion.getLiquidacionFecha());
						liquidacionImporte+=liquidacion.getLiquidacionImporte();
					}
					liquidacionSAF.setLiquidacionImporte(liquidacionImporte);
					
//					if("3".equals(liquidacionSAF.getLiquidacionEstado())){
						element.put("fecha", liquidacionSAF.getLiquidacionFecha());
						element.put("importe", liquidacionSAF.getLiquidacionImporte());
						element.put("tipo", "DESEMBOLSO");
						result.add(element);					
						result.add(inversionBusiness.getComprobanteResumen(inversionNumero,Integer.parseInt(nroArmada)));						
//					}else{
//						element.put("message", "Operación Cancelada. El estado de la liquidación consultada es diferente a DESEMBOLSADO.");
//						result.add(element);	
//					}
				}else{
					element.put("message", "Operación Cancelada. La liquidación consultada NO EXISTE.");
					result.add(element);					
				}
				
				
			} catch (Exception e) {
				element = new LinkedHashMap<String,Object>();
				element.put("message", UtilEnum.ESTADO_OPERACION.ERROR.getCodigo());
				result.add(element);
				LOG.error("###obtenerImporteComprobante:",e);
			}
		}
		return result;
	}
	
	@RequestMapping(value = "validarImporteComprobantesNoExcedaInversion/{inversionId}/{nroArmada}/{importeIngresar}", method = RequestMethod.GET)
	public @ResponseBody ResultadoBean validarImporteComprobantesNoExcedaInversion(
			@PathVariable(value="inversionId") String inversionId,
			@PathVariable(value="nroArmada") String nroArmada,
			@PathVariable(value="importeIngresar") Double importeIngresar){
		LOG.info("###ContratoController.grabarComprobantes inversionId:"+inversionId+", nroArmada:"+nroArmada+", importeIngresar:"+importeIngresar);
		
		ResultadoBean resultadoBean = null;
		boolean resultado=false;
		if(null!=inversionId && null!=nroArmada && null!=importeIngresar){
			try {
				
				resultado = inversionBusiness.validarImporteComprobantesNoExcedaInversion(inversionId, Integer.parseInt(nroArmada),importeIngresar);
				resultadoBean = new ResultadoBean();
				resultadoBean.setEstado(UtilEnum.ESTADO_OPERACION.EXITO.getCodigo());
				resultadoBean.setResultado(resultado);
				
			} catch (Exception e) {
				resultadoBean = new ResultadoBean();
				resultadoBean.setEstado(UtilEnum.ESTADO_OPERACION.EXCEPTION.getCodigo());
				resultadoBean.setResultado("Ocurrio un error al validar el importe de comprobantes de la inversion:"+inversionId);
				LOG.error("###validarImporteComprobantesNoExcedaInversion:",e);
			}
		}else{
			resultadoBean = new ResultadoBean();
			resultadoBean.setEstado(UtilEnum.ESTADO_OPERACION.ERROR.getCodigo());
			resultadoBean.setResultado("Ocurrio un error, parametro null");
		}
		return resultadoBean;
	}
	
	@RequestMapping(value = "/elimiarConformidadLiquidacion", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Map<String, Object> elimiarConformidadLiquidacion(@RequestBody Map<String, Object> params) {	
		LOG.info("###elimiarConformidadLiquidacion params:"+params);
		Map<String, Object> response = new HashMap<String, Object>();
		String result="1", detail="";
		try{
			String nroInversion = String.valueOf(params.get("nroInversion"));
			String nroArmada = String.valueOf(params.get("nroArmada"));
			String usuarioId = String.valueOf(params.get("usuarioId"));
			result = liquidacionBusiness.eliminarConformidadLiquidacion(nroInversion, nroArmada, usuarioId);
			if(result.equals("")){
				result = Constantes.Service.RESULTADO_EXITOSO;
			}
		}catch(Exception e){
			LOG.error("Error inversion/confirmarLiquidacionInversion:: ",e);
			result=Constantes.Service.RESULTADO_ERROR_INESPERADO;
			detail=e.getMessage();
		}
			
		response.put("result",result);
		response.put("detail",detail);
		LOG.info("RESPONSE: " +  response);
			
		return response;
	}
	
	@RequestMapping(value = "/anularRecepcionCargoContabilidad", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Map<String, Object> anularRecepcionCargoContabilidad(@RequestBody Map<String, Object> params) {	
		LOG.info("###anularRecepcionCargoContabilidad params:"+params);
		Map<String, Object> response = new HashMap<String, Object>();
		String result="1", detail="";
		try{
			String inversionId = String.valueOf(params.get("inversionId"));
			String nroArmada = String.valueOf(params.get("nroArmada"));
			String usuarioId = String.valueOf(params.get("usuarioId"));
			result = inversionBusiness.anularRecepcionCargoContabilidad(inversionId, nroArmada, usuarioId);
			if(result.equals("")){
				result = Constantes.Service.RESULTADO_EXITOSO;
			}
		}catch(Exception e){
			LOG.error("Error inversion/anularRecepcionCargoContabilidad:: ",e);
			result=Constantes.Service.RESULTADO_ERROR_INESPERADO;
			detail=e.getMessage();
		}
			
		response.put("result",result);
		response.put("detail",detail);
		LOG.info("RESPONSE: " +  response);
			
		return response;
	}
	
	@RequestMapping(value = "/anularRecepcionCargoContabilidadActualizSaldo", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Map<String, Object> anularRecepcionCargoContabilidadActualizSaldo(@RequestBody Map<String, Object> params) {	
		LOG.info("###anularRecepcionCargoContabilidadActualizSaldo params:"+params);
		Map<String, Object> response = new HashMap<String, Object>();
		String result="1", detail="";
		try{
			String inversionId = String.valueOf(params.get("inversionId"));
			String usuarioId = String.valueOf(params.get("usuarioId"));
			result = inversionBusiness.anularRecepcionCargoContabilidadActualizSaldo(inversionId, usuarioId);
			if(result.equals("")){
				result = Constantes.Service.RESULTADO_EXITOSO;
			}
		}catch(Exception e){
			LOG.error("Error inversion/anularRecepcionCargoContabilidadActualizSaldo:: ",e);
			result=Constantes.Service.RESULTADO_ERROR_INESPERADO;
			detail=e.getMessage();
		}
			
		response.put("result",result);
		response.put("detail",detail);
		LOG.info("RESPONSE: " +  response);
			
		return response;
	}
	
	
	@RequestMapping(value = "obtenerInversionPorNro/{nroInversion}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody   
	public ResultadoBean obtenerInversionPorNumero(@PathVariable(value="nroInversion") String nroInversion) {
		LOG.info("###obtenerInversionPorNumero nroInversion:"+nroInversion);
		ResultadoBean response = null;
		try{
			String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
			inversionService.setTokenCaspio(tokenCaspio);
			
			Inversion inversion = inversionService.obtenerInversionCaspioPorNro(nroInversion);
			response = new ResultadoBean();
			response.setEstado(UtilEnum.ESTADO_OPERACION.EXITO.getCodigo());
			response.setResultado(inversion);
		}catch(Exception e){
			LOG.error("Error inversion/obtenerInversion:: ",e);
			response = new ResultadoBean();
			response.setEstado(UtilEnum.ESTADO_OPERACION.EXCEPTION.getCodigo());
		}
		return response;
	}
	
	
	@RequestMapping(value = "generarActaEntrega/{inversionNumero}/{usuarioId}", method = RequestMethod.GET)
	public @ResponseBody String generarActaEntrega(@PathVariable(value="inversionNumero") String inversionNumero,
			@PathVariable(value="usuarioId") String usuarioId){
		LOG.info("###ContratoController.generarActaEntrega inversionNumero:"+inversionNumero);
		String resultadoBean = null;
		if(null!=inversionNumero ){
			try {
				resultadoBean=inversionBusiness.generarActaEntrega(inversionNumero,usuarioId);
			} catch (Exception e) {
				LOG.error(e.getMessage());
				return "Ocurrió un error al generar el acta de entrega";
			}
		}
		return resultadoBean;
	}
	
	@RequestMapping(value = "/verificarDesembolso", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody   
	public Map<String, Object> verificarDesembolso(@RequestBody Map<String, Object> params) {
		LOG.info("###eliminarGarantia params:"+params);
		Map<String, Object> response = new HashMap<String, Object>();
		String result="", detail="";
		try{			
			response = inversionBusiness.getLiquidacionDesembolso(params);			
		}catch(Exception e){
			LOG.error("Error pedido/eliminarGarantia:: ",e);
			e.printStackTrace();
			result=Constantes.Service.RESULTADO_ERROR_INESPERADO;
			detail=e.getMessage();
		}			
		response.put("result",result);
		response.put("detail",detail);
		System.out.println("RESPONSE: " +  response);	
		
		return response;
	}
	
	@RequestMapping(value = "/verificarConfirmacionEntrega", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody   
	public Map<String, Object> verificarConfirmacionEntrega(@RequestBody Map<String, Object> params) {
		LOG.info("###eliminarGarantia params:"+params);
		Map<String, Object> response = new HashMap<String, Object>();
		String result="", detail="";
		try{			
			response = inversionBusiness.verificarConfirmacionEntrega(params);			
		}catch(Exception e){
			LOG.error("Error pedido/eliminarGarantia:: ",e);
			e.printStackTrace();
			result=Constantes.Service.RESULTADO_ERROR_INESPERADO;
			detail=e.getMessage();
		}			
		response.put("result",result);
		response.put("detail",detail);
		System.out.println("RESPONSE: " +  response);	
		
		return response;
	}
	
	@RequestMapping(value = "/confirmarEntrega", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody   
	public Map<String, Object> confirmarEntrega(@RequestBody Map<String, Object> params) {
		LOG.info("###eliminarGarantia params:"+params);
		Map<String, Object> response = new HashMap<String, Object>();
		String result="", detail="";
		try{			
			response = inversionBusiness.confirmacionEntrega(params);			
		}catch(Exception e){
			LOG.error("Error pedido/eliminarGarantia:: ",e);
			e.printStackTrace();
			result=Constantes.Service.RESULTADO_ERROR_INESPERADO;
			detail=e.getMessage();
		}			
		response.put("result",result);
		response.put("detail",detail);
		System.out.println("RESPONSE: " +  response);	
		
		return response;
	}
	
	 /**
	  * @param inversionId
	  * @param check
	  * @return	: ResultadoBean
	  * @date	: 29 de mar. de 2017
	  * @time	: 11:14:35 a. m.
	  * @author	: Arly Fernandez.
	  * @descripcion : Permite actualizar el check de inmueble como hipoteca de inversion, TRUE o FALSE	
	 */
	@RequestMapping(value = "/actualizarInmuebleInversionHipotecado/{inversionId}/{check}", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody ResultadoBean actualizarInmuebleInversionHipotecado(@PathVariable(value="inversionId") String inversionId,
			@PathVariable(value="check") String check) {	
		LOG.info("###actualizarInmuebleInversionHipotecado inversionId:"+inversionId+", check:"+check);
		ResultadoBean resultadoBean=null;
		try{
			resultadoBean= inversionBusiness.actualizarInmuebleInversionHipotecado(inversionId,check);
		}catch(Exception e){
			LOG.error("Error inversion/actualizarInmuebleInversionHipotecado:: ",e);
		}	
		return resultadoBean;
	}
	
	 /**
	  * @param nroInversion
	  * @param usuarioID
	  * @return	: ResultadoBean
	  * @date	: 30 de mar. de 2017
	  * @time	: 3:45:20 p. m.
	  * @author	: Arly Fernandez.
	  * @descripcion : Crear automaticamente el credito garantia al momento que se checkea en el registro del
	  * inmueble, inmueble hipotecado.
	 */
	@RequestMapping(value = "/crearCreditoGarantia/{nroInversion}/{usuarioID}", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody ResultadoBean crearCreditoGarantia(@PathVariable(value="nroInversion") String nroInversion,
			@PathVariable(value="usuarioID") String usuarioID) {
		LOG.info("###actualizarInmuebleInversionHipotecado nroInversion:"+nroInversion+", usuarioID:"+usuarioID);
		ResultadoBean resultadoBean = new ResultadoBean();
		try{
			if(null!=nroInversion && null!=usuarioID){
				resultadoBean= inversionBusiness.crearCreditoGarantia(nroInversion, usuarioID);
			}else{
				resultadoBean.setEstado(UtilEnum.ESTADO_OPERACION.ERROR.getCodigo());
				resultadoBean.setMensajeError("Falta especificar el parametro inversionId");
			}
		}catch(Exception e){
			LOG.error("Error inversion/crearCreditoGarantia:: ",e);
			resultadoBean.setEstado(UtilEnum.ESTADO_OPERACION.EXCEPTION.getCodigo());
			resultadoBean.setMensajeError("Error al crear el credito garantia");
		}	
		return resultadoBean;
	}
	
	 /**
	  * @param nroInversion
	  * @param usuarioID
	  * @return	: ResultadoBean
	  * @date	: 30 de mar. de 2017
	  * @time	: 3:44:48 p. m.
	  * @author	: Arly Fernandez.
	  * @descripcion : Eliminar el credito garantia creado automaticamente cuando se quita el check del registro
	  * de inversion, inmueble hipotecado
	 */
	@RequestMapping(value = "/eliminarCreditoGarantia", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody ResultadoBean eliminarCreditoGarantia(@PathVariable(value="nroInversion") String nroInversion,
			@PathVariable(value="usuarioID") String usuarioID) {
		LOG.info("###actualizarInmuebleInversionHipotecado nroInversion:"+nroInversion+", usuarioID:"+usuarioID);
		ResultadoBean resultadoBean = new ResultadoBean();
		try{
			if(null!=nroInversion && null!=usuarioID){
				resultadoBean= inversionBusiness.eliminarCreditoGarantia(nroInversion, usuarioID);
			}else{
				resultadoBean.setEstado(UtilEnum.ESTADO_OPERACION.ERROR.getCodigo());
				resultadoBean.setMensajeError("Falta especificar el parametro inversionId");
			}
		}catch(Exception e){
			LOG.error("Error inversion/crearCreditoGarantia:: ",e);
			resultadoBean.setEstado(UtilEnum.ESTADO_OPERACION.EXCEPTION.getCodigo());
			resultadoBean.setMensajeError("Error al crear el credito garantia");
		}	
		return resultadoBean;
	}
	
	
	@RequestMapping(value = "validarInversionesRetiroAdjudicacion", method = RequestMethod.GET)
	public @ResponseBody String validarInversionesRetiroAdjudicacion(@RequestParam(value="nroPedido") String nroPedido) {		
		try{			
			Map<String, Object> mapResult = new HashMap<String, Object>();
			return inversionBusiness.validarInversionesRetiroAdjudicacion(nroPedido);
		}catch(Exception e){
			LOG.error("###Exception:",e);
		}
		return null;
	}
	

	@RequestMapping(value = "/enviarCorreoConfirmacion/{inversionId}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Map<String, Object> enviarCorreoConfirmacionInmobiliaria(@PathVariable(value="inversionId") String inversionId) {
		LOG.info("###enviarCorreoConfirmacionInmobiliaria inversionId:"+inversionId);
		Map<String, Object> response = new HashMap<String, Object>();
		String result="", detail="";
		try{
			inversionBusiness.enviarCorreoVerificacion(inversionId);
		}catch(Exception e){
			LOG.error("Error inversion/enviarCorreoConfirmacion:: ",e);
			e.printStackTrace();
			result=Constantes.Service.RESULTADO_ERROR_INESPERADO;
			detail=e.getMessage();
		}			
		response.put("result",result);
		response.put("detail",detail);
		System.out.println("RESPONSE: " +  response);			
		return response;
	}	
	
	
}
