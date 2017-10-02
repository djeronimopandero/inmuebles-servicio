package com.pandero.ws.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.pandero.ws.bean.ComprobanteCaspio;
import com.pandero.ws.bean.Garantia;
import com.pandero.ws.bean.Inversion;
import com.pandero.ws.bean.InversionRequisito;
import com.pandero.ws.service.InversionService;
import com.pandero.ws.util.Constantes;
import com.pandero.ws.util.JsonUtil;
import com.pandero.ws.util.ServiceRestTemplate;
import com.pandero.ws.util.UtilEnum;

@Service
public class InversionServiceImpl implements InversionService {

	private static final Logger LOG = LoggerFactory.getLogger(InversionServiceImpl.class);

	@Autowired
	@Qualifier("restTemplate")
	private RestTemplate restTemplate;

	@Value("${url.service.table.pedidoInversion}")
	private String tablePedidoInversionURL;
	
	@Value("${url.service.view.tablaDetalleInversion}")
	private String viewTablaDetalleInversionURL;
	
	@Value("${url.service.table.inversionRequisito}")
	private String tableInversionRequisitoURL;
	
	@Value("${url.service.table.comprobante}")
	private String tableComprobanteURL;
	
	@Value("${url.service.table.pedido}")
	private String tablePedidoURL;
	
	@Value("${url.service.table.liquidacionDesembolso}")
	private String tableLiquidacionDesembolso;
	
	String tokenCaspio = "";
	public void setTokenCaspio(String token){
		tokenCaspio = token;
	}

	public Inversion obtenerInversionCaspioPorId(String inversionId) throws Exception {
		Inversion inversion = null;
		String serviceWhere = "{\"where\":\"InversionId=" + inversionId + "\"}";		
		String obtenerInversionesxPedidoURL = viewTablaDetalleInversionURL+Constantes.Service.URL_WHERE;
		
        Object jsonResult=ServiceRestTemplate.getForObject(restTemplate,tokenCaspio,obtenerInversionesxPedidoURL,Object.class,null,serviceWhere);
     	String response = JsonUtil.toJson(jsonResult);	     	
        if(response!=null && !response.isEmpty()){
	        Map<String, Object> responseMap = JsonUtil.jsonToMap(response);
	        if(responseMap!=null){
	        	Object jsonResponse = responseMap.get("Result");
	        	if(jsonResponse!=null){        		
	        		List mapInversiones = JsonUtil.fromJson(JsonUtil.toJson(jsonResponse), ArrayList.class);
	        		if(mapInversiones!=null && mapInversiones.size()>0){
	        			for(Object bean : mapInversiones){
	        				String beanString = JsonUtil.toJson(bean);
	        				inversion =  JsonUtil.fromJson(beanString, Inversion.class);			
	        			}
	        		}        		
	        	}
	        }
	    }

		return inversion;
	}
	
	public Inversion obtenerInversionCaspioPorNro(String nroInversion) throws Exception {
		Inversion inversion = null;
		String serviceWhere = "{\"where\":\"NroInversion='" + nroInversion + "'\"}";		
		String obtenerInversionesxPedidoURL = viewTablaDetalleInversionURL+Constantes.Service.URL_WHERE;
		
        Object jsonResult=ServiceRestTemplate.getForObject(restTemplate,tokenCaspio,obtenerInversionesxPedidoURL,Object.class,null,serviceWhere);
     	String response = JsonUtil.toJson(jsonResult);	     	
        if(response!=null && !response.isEmpty()){
	        Map<String, Object> responseMap = JsonUtil.jsonToMap(response);
	        if(responseMap!=null){
	        	Object jsonResponse = responseMap.get("Result");
	        	if(jsonResponse!=null){        		
	        		List mapInversiones = JsonUtil.fromJson(JsonUtil.toJson(jsonResponse), ArrayList.class);
	        		if(mapInversiones!=null && mapInversiones.size()>0){
	        			for(Object bean : mapInversiones){
	        				String beanString = JsonUtil.toJson(bean);
	        				inversion =  JsonUtil.fromJson(beanString, Inversion.class);			
	        			}
	        		}        		
	        	}
	        }
	    }

		return inversion;
	}

	@Override
	public String actualizarEstadoInversionCaspio(String inversionId,
			String estadoInversion) throws Exception {
		Map<String, String> request = new HashMap<String, String>();
		request.put("Estado", estadoInversion);		
		
		String serviceWhere = "{\"where\":\"InversionId='" + inversionId + "'\"}";	
		String actualizarPedidoURL = tablePedidoInversionURL+Constantes.Service.URL_WHERE;
		
        ServiceRestTemplate.putForObject(restTemplate,tokenCaspio,actualizarPedidoURL,Object.class,request,serviceWhere);	
		return null;
	}
	
	public String actualizarEstadoInversionCaspioPorNro(String nroInversion,
			String estadoInversion) throws Exception {
		Map<String, String> request = new HashMap<String, String>();
		request.put("Estado", estadoInversion);		
		
		String serviceWhere = "{\"where\":\"NroInversion='" + nroInversion + "'\"}";	
		String actualizarPedidoURL = tablePedidoInversionURL+Constantes.Service.URL_WHERE;
		
        ServiceRestTemplate.putForObject(restTemplate,tokenCaspio,actualizarPedidoURL,Object.class,request,serviceWhere);	
		return null;
	}
	
	public String actualizarEstadoInversionLiquidadoPorNro(String inversionId, String nroLiquidacion,
			String estadoInversion) throws Exception {
		Map<String, String> request = new HashMap<String, String>();
		request.put("Estado", estadoInversion);		
		request.put("NroLiquidacion", nroLiquidacion);		
		
		String serviceWhere = "{\"where\":\"NroInversion='" + inversionId + "'\"}";	
		String actualizarPedidoURL = tablePedidoInversionURL+Constantes.Service.URL_WHERE;
		
        ServiceRestTemplate.putForObject(restTemplate,tokenCaspio,actualizarPedidoURL,Object.class,request,serviceWhere);	
		return null;
	}

	public String actualizarSituacionConfirmadoInversionCaspio(String inversionId, String situacionConfirmado) throws Exception {
		Map<String, String> request = new HashMap<String, String>();
		request.put("Confirmado", situacionConfirmado);		
		
		String serviceWhere = "{\"where\":\"InversionId='" + inversionId + "'\"}";	
		String actualizarPedidoURL = tablePedidoInversionURL+Constantes.Service.URL_WHERE;
		
        ServiceRestTemplate.putForObject(restTemplate,tokenCaspio,actualizarPedidoURL,Object.class,request,serviceWhere);	
		return null;
	}
		
	public String actualizarIndicadorInmuebleHipotecadoInversionCaspio(String inversionId, 
			String indicadorInmuebleHipotecado) throws Exception{
		Map<String, String> request = new HashMap<String, String>();
		request.put("InmuebleInversionHipotecado", indicadorInmuebleHipotecado);		
		
		String serviceWhere = "{\"where\":\"InversionId='" + inversionId + "'\"}";	
		String actualizarPedidoURL = tablePedidoInversionURL+Constantes.Service.URL_WHERE;
		
        ServiceRestTemplate.putForObject(restTemplate,tokenCaspio,actualizarPedidoURL,Object.class,request,serviceWhere);	
		return null;
	}
	
	@Override
	public List<InversionRequisito> obtenerRequisitosPorInversion(String inversionId) throws Exception {
		List<InversionRequisito> listaRequisitos = null;	
		String serviceWhere = "{\"where\":\"InversionId=" + inversionId + "\"}";	
		String obtenerRequisitosxInversionURL = tableInversionRequisitoURL+Constantes.Service.URL_WHERE;
		
        Object jsonResult=ServiceRestTemplate.getForObject(restTemplate,tokenCaspio,obtenerRequisitosxInversionURL,Object.class,null,serviceWhere);
     	String response = JsonUtil.toJson(jsonResult);	     	
        if(response!=null && !response.isEmpty()){
        Map<String, Object> responseMap = JsonUtil.jsonToMap(response);
	        if(responseMap!=null){
	        	Object jsonResponse = responseMap.get("Result");
	        	if(jsonResponse!=null){        		
	        		List mapRequisitos = JsonUtil.fromJson(JsonUtil.toJson(jsonResponse), ArrayList.class);
	        		if(mapRequisitos!=null && mapRequisitos.size()>0){
	        			listaRequisitos = new ArrayList<InversionRequisito>();
	        			for(Object bean : mapRequisitos){
	        				String beanString = JsonUtil.toJson(bean);
	        				InversionRequisito inversionRequisito =  JsonUtil.fromJson(beanString, InversionRequisito.class);
	        				listaRequisitos.add(inversionRequisito);
	        			}
	        			System.out.println("listaRequisitos:: "+listaRequisitos.size());
	        		}        		
	        	}
	        }
        }
        
		return listaRequisitos;
	}
	
	@Override
	public String actualizarEstadoInversionRequisitoCaspio(String inversionId,String estadoInversionReq) throws Exception {
		Map<String, String> request = new HashMap<String, String>();
		request.put("EstadoRequisito", estadoInversionReq);	
		request.put("Observacion", "");	
		
		String serviceWhere = "{\"where\":\"InversionId='" + inversionId + "'\"}";	
		String actualizarInversionRequisitoURL = tableInversionRequisitoURL+Constantes.Service.URL_WHERE;
		
        ServiceRestTemplate.putForObject(restTemplate,tokenCaspio,actualizarInversionRequisitoURL,Object.class,request,serviceWhere);	
		return null;
	}

	@Override
	public String crearRequisitoInversion(String inversionId, String requisitoId)
			throws Exception {
		Map<String, String> request = new HashMap<String, String>();
		request.put("InversionId", inversionId);
		request.put("RequisitoId", requisitoId);		
		request.put("EstadoRequisito", Constantes.DocumentoRequisito.ESTADO_REQUISITO_PENDIENTE);
        ServiceRestTemplate.postForObject(restTemplate,tokenCaspio,tableInversionRequisitoURL,Object.class,request,null);	
		
		return null;
	}

	@Override
	public List<Inversion> listarPedidoInversionPorPedidoId(String pedidoId) throws Exception {
		LOG.info("###listarPedidoInversionPorPedidoId pedidoId:"+pedidoId);
		
		List<Inversion> listaInversiones = null;	
		String serviceWhere = "{\"where\":\"PedidoId=" + pedidoId + " and Estado!='ANULADO'\"}";	
		String obtenerRequisitosxInversionURL = tablePedidoInversionURL+Constantes.Service.URL_WHERE;
		
        Object jsonResult=ServiceRestTemplate.getForObject(restTemplate,tokenCaspio,obtenerRequisitosxInversionURL,Object.class,null,serviceWhere);
     	String response = JsonUtil.toJson(jsonResult);	     	
        if(response!=null && !response.isEmpty()){
        Map<String, Object> responseMap = JsonUtil.jsonToMap(response);
	        if(responseMap!=null){
	        	Object jsonResponse = responseMap.get("Result");
	        	if(jsonResponse!=null){        		
	        		List mapRequisitos = JsonUtil.fromJson(JsonUtil.toJson(jsonResponse), ArrayList.class);
	        		if(mapRequisitos!=null && mapRequisitos.size()>0){
	        			listaInversiones = new ArrayList<Inversion>();
	        			for(Object bean : mapRequisitos){
	        				String beanString = JsonUtil.toJson(bean);
	        				Inversion inversion =  JsonUtil.fromJson(beanString, Inversion.class);
	        				listaInversiones.add(inversion);
	        			}
	        			System.out.println("listaPedidoInversion:: "+listaInversiones.size());
	        		}        		
	        	}
	        }
        }
        
		return listaInversiones;
	}

	@Override
	public List<ComprobanteCaspio> getComprobantes(Integer inversionId, Integer nroArmada) throws Exception {
		List<ComprobanteCaspio> listaComprobantes = null;	
		String serviceWhere = "{\"where\":\"InversionId='" + inversionId + "' and NroArmada='"+nroArmada+"'\" , \"order by\" : \"FechaCreacion\"}";	
		String obtenerRequisitosxInversionURL = tableComprobanteURL+Constantes.Service.URL_WHERE;
		
        Object jsonResult=ServiceRestTemplate.getForObject(restTemplate,tokenCaspio,obtenerRequisitosxInversionURL,Object.class,null,serviceWhere);
     	String response = JsonUtil.toJson(jsonResult);	     	
        if(response!=null && !response.isEmpty()){
        Map<String, Object> responseMap = JsonUtil.jsonToMap(response);
	        if(responseMap!=null){
	        	Object jsonResponse = responseMap.get("Result");
	        	if(jsonResponse!=null){        		
	        		List mapRequisitos = JsonUtil.fromJson(JsonUtil.toJson(jsonResponse), ArrayList.class);
	        		if(mapRequisitos!=null && mapRequisitos.size()>0){
	        			listaComprobantes = new ArrayList<ComprobanteCaspio>();
	        			for(Object bean : mapRequisitos){
	        				String beanString = JsonUtil.toJson(bean);
	        				ComprobanteCaspio comprobante =  JsonUtil.fromJson(beanString, ComprobanteCaspio.class);
	        				listaComprobantes.add(comprobante);
	        			}
	        			System.out.println("listaComprobantes:: "+listaComprobantes.size());
	        		}        		
	        	}
	        }
        }
        
		return listaComprobantes;
	}
	
	@Override
	public String actualizarComprobanteEnvioCartaContabilidad(String inversionId,String nroArmada,String fechaEnvio,String usuarioEnvio, String estado) throws Exception {
		LOG.info("##InversionServiceImpl.actualizarComprobanteEnvioCartaContabilidad inversionId:"+inversionId+", nroArmada:"+nroArmada+", fechaEnvio"+fechaEnvio+", estado:"+estado);
		
		Map<String, String> request = new HashMap<String, String>();
		request.put("EnvioContabilidadFecha", fechaEnvio);	
		request.put("EnvioContabilidadUsuario", usuarioEnvio);	
		request.put("EstadoContabilidad", estado);	
		
		String serviceWhere = "{\"where\":\"InversionId='" + inversionId + "' and NroArmada='"+nroArmada+"'\"}";
		
		String actualizarComprobanteRequisitoURL = tableComprobanteURL+Constantes.Service.URL_WHERE;
		
        ServiceRestTemplate.putForObject(restTemplate,tokenCaspio,actualizarComprobanteRequisitoURL,Object.class,request,serviceWhere);	
		return null;
	}

	@Override
	public String recepcionarCargoContabilidad(String inversionId,
			String nroArmada, String fechaRecepcion, String usuarioRecepcion) throws Exception {		
		Map<String, String> request = new HashMap<String, String>();
		request.put("RecepContabilidadFecha", fechaRecepcion);	
		request.put("RecepContabilidadUsuario", usuarioRecepcion);	
		request.put("EstadoContabilidad", UtilEnum.ESTADO_COMPROBANTE.RECIBIDO.getTexto());	
		
		String serviceWhere = "{\"where\":\"InversionId='" + inversionId + "' and NroArmada='"+nroArmada+"'\"}";
		
		String actualizarComprobanteRequisitoURL = tableComprobanteURL+Constantes.Service.URL_WHERE;
		
        ServiceRestTemplate.putForObject(restTemplate,tokenCaspio,actualizarComprobanteRequisitoURL,Object.class,request,serviceWhere);
		return null;
	}

	@Override
	public String envioCargoContabilidadActualizSaldo(String inversionId,
			String fechaEnvio, String usuarioEnvio) throws Exception {
		Map<String, String> request = new HashMap<String, String>();
		request.put("EnvioContabilidadFecha", fechaEnvio);
		request.put("EnvioContabilidadUsuario", usuarioEnvio);
		
		String serviceWhere = "{\"where\":\"InversionId='" + inversionId + "'\"}";	
		String actualizarPedidoURL = tablePedidoInversionURL+Constantes.Service.URL_WHERE;
		
        ServiceRestTemplate.putForObject(restTemplate,tokenCaspio,actualizarPedidoURL,Object.class,request,serviceWhere);
		return null;
	}

	@Override
	public String recepcionarCargoContabilidadActualizSaldo(String inversionId,
			String fechaRecepcion, String usuarioRecepcion) throws Exception {
		Map<String, String> request = new HashMap<String, String>();
		request.put("RecepContabilidadFecha", fechaRecepcion);
		request.put("RecepContabilidadUsuario", usuarioRecepcion);
		
		String serviceWhere = "{\"where\":\"InversionId='" + inversionId + "'\"}";	
		String actualizarPedidoURL = tablePedidoInversionURL+Constantes.Service.URL_WHERE;
		
        ServiceRestTemplate.putForObject(restTemplate,tokenCaspio,actualizarPedidoURL,Object.class,request,serviceWhere);
		return null;
	}
	
	@Override
	public String actualizarInversionGarantiaHipotecado(Garantia garantiaCaspio) throws Exception {
		Map<String, String> request = new HashMap<String, String>();
		request.put("InmuebleInversionHipotecado", "0");
		
		String serviceWhere = "{\"where\":\"PartidaRegistral='"+garantiaCaspio.getPartidaRegistral()+"' and pedidoId=" + garantiaCaspio.getPedidoID() + "\"}";	
		String actualizarGarantiaURL = tablePedidoInversionURL+Constantes.Service.URL_WHERE;
		
        ServiceRestTemplate.putForObject(restTemplate,tokenCaspio,actualizarGarantiaURL,Object.class,request,serviceWhere);	
		return null;
	}

	@Override
	public String anularRecepcionCargoContabilidad(String inversionId,
			String nroArmada, String usuario) throws Exception {
		Map<String, String> request = new HashMap<String, String>();
		request.put("RecepContabilidadFecha", null);	
		request.put("RecepContabilidadUsuario", null);	
		request.put("EstadoContabilidad", UtilEnum.ESTADO_COMPROBANTE.ENVIADO.getTexto());	
		
		String serviceWhere = "{\"where\":\"InversionId='" + inversionId + "' and NroArmada='"+nroArmada+"'\"}";
		
		String actualizarComprobanteRequisitoURL = tableComprobanteURL+Constantes.Service.URL_WHERE;
		
        ServiceRestTemplate.putForObject(restTemplate,tokenCaspio,actualizarComprobanteRequisitoURL,Object.class,request,serviceWhere);
		return null;
	}
	
	public void actualizarTablaCaspio(Map<String,Object> body, String tableURL, String where) throws Exception{			
		tableURL = tableURL+Constantes.Service.URL_WHERE;	
        ServiceRestTemplate.putForObject(restTemplate,tokenCaspio,tableURL,Object.class,body,where);
	}
	
	public List<Map<String,Object>> obtenerTablaCaspio(String tableURL, String where) throws Exception{			
		tableURL = tableURL+Constantes.Service.URL_WHERE;	
		Map<String,Object> mapResult=(Map<String,Object>)ServiceRestTemplate.getForObject(restTemplate,tokenCaspio,tableURL,Object.class,null,where);
		return (List<Map<String,Object>>)mapResult.get("Result");
	}
	
	@Override
	public Map<String,Object> confirmacionEntrega(Map<String,Object> params)throws Exception{
		Map<String,Object> resultMap = new HashMap<String,Object>();
		SimpleDateFormat sdf = new SimpleDateFormat(Constantes.FORMATO_DATE_NORMAL);
		Date today = new Date();
		//actualizamos la inversion a entregado
		String serviceWhere = "{\"where\":\"NroInversion='"+params.get("NroInversion")+"'\"}";
		Map<String,Object> body = new HashMap<String, Object>();
		body.put("Estado", "ENTREGADO");
		body.put("FechaEntrega", sdf.format(today));
		actualizarTablaCaspio(body,tablePedidoInversionURL,serviceWhere);
		body.remove("FechaEntrega");
		serviceWhere = "{\"where\":\"InversionId='"+params.get("InversionId")+"'\"}";
		actualizarTablaCaspio(body,tableLiquidacionDesembolso,serviceWhere);
        
        //obtemos las inversiones por pedido		
        serviceWhere = "{\"where\":\"PedidoId="+params.get("pedidoId")+"\"}";		
        List<Map<String,Object>> mapInversionesPedido = obtenerTablaCaspio(tablePedidoInversionURL,serviceWhere);
        
		//obtenemos las inversiones entregadas
		serviceWhere = "{\"where\":\"PedidoId="+params.get("pedidoId")+" and Estado='ENTREGADO'\"}";
		List<Map<String,Object>> mapInversionesPedidoEntregado = obtenerTablaCaspio(tablePedidoInversionURL,serviceWhere);
		
		//verificamos que todas las inversiones esten entregadas
		if(mapInversionesPedido.size()>0 && mapInversionesPedidoEntregado.size()>0 && mapInversionesPedido.size()==mapInversionesPedidoEntregado.size()){
			serviceWhere = "{\"where\":\"PedidoId="+params.get("pedidoId")+"\"}";			
			body = new HashMap<String, Object>();
			body.put("Estado", "CERRADO");
			actualizarTablaCaspio(body,tablePedidoURL,serviceWhere);			
		}
		resultMap.put("mensaje", "OK");
		return resultMap;
	}
	
	public Integer actualizarInmuebleInversionHipotecado(String nroInversion,String check) throws Exception {
		Map<String, String> request = new HashMap<String, String>();
		request.put("InmuebleInversionHipotecado", check);		
		
		String serviceWhere = "{\"where\":\"NroInversion='" + nroInversion + "'\"}";	
		String actualizarPedidoURL = tablePedidoInversionURL+Constantes.Service.URL_WHERE;
		
        ServiceRestTemplate.putForObject(restTemplate,tokenCaspio,actualizarPedidoURL,Object.class,request,serviceWhere);	
		return UtilEnum.ESTADO_OPERACION.EXITO.getCodigo();
	}
	
}
