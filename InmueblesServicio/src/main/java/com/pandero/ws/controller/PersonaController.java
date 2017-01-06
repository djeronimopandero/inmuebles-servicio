package com.pandero.ws.controller;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pandero.ws.bean.Inversion;
import com.pandero.ws.bean.PersonaSAF;
import com.pandero.ws.bean.ResultadoBean;
import com.pandero.ws.business.PersonaBusiness;
import com.pandero.ws.dao.PersonaDao;
import com.pandero.ws.service.InversionService;
import com.pandero.ws.util.ServiceRestTemplate;
import com.pandero.ws.util.UtilEnum;

@Controller
@RequestMapping("/persona")
public class PersonaController {
	
	private static final Logger LOG = LoggerFactory.getLogger(PersonaController.class);
	
	@Autowired
	PersonaBusiness personaBusiness;
	
	@Autowired
	PersonaDao personaDAO;
	
	@Autowired
	InversionService inversionService;
	
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
				resultadoBean.setResultado("Ocurrio un error al obtener persona por pedido id");
				LOG.error("###getPersonaPorPedidoId:",e);
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
				resultadoBean.setResultado("Ocurrio un error al obtener persona por id");
				LOG.error("###getPersonaPorId:",e);
			}
		}
		return resultadoBean;
	}
	
	@RequestMapping(value = "obtenerPersonaPorDocumento/{tipoDoc}/{nroDoc}", method = RequestMethod.GET)
	public @ResponseBody ResultadoBean getPersonaPorDocumento(@PathVariable(value="tipoDoc") Integer tipoDoc,@PathVariable(value="nroDoc") String nroDoc){
		LOG.info("###ContratoController.getPersonaPorDocumento tipoDoc:"+tipoDoc+", nroDoc:"+nroDoc);
		ResultadoBean resultadoBean = null;
		if(null!=tipoDoc && null!=nroDoc){
			try {
				resultadoBean = new ResultadoBean();
				
				UtilEnum.TIPO_DOCUMENTO tipoDocEnum= UtilEnum.TIPO_DOCUMENTO.obtenerTipoDocumentoByCodigoCaspio(tipoDoc);
				
				PersonaSAF personaSAF=personaDAO.obtenerPersonaPorDoc(tipoDocEnum.getCodigo(), nroDoc);
				
				if(null!=personaSAF){
					resultadoBean.setEstado(UtilEnum.ESTADO_OPERACION.EXITO.getCodigo());
					
					UtilEnum.TIPO_DOCUMENTO tipoDocumentoID = UtilEnum.TIPO_DOCUMENTO
							.obtenerTipoDocumentoByCodigo(!StringUtils.isEmpty(personaSAF.getTipoDocumentoID())
									? Integer.parseInt(personaSAF.getTipoDocumentoID()) : 4);
					
					personaSAF.setTipoDocumentoID(String.valueOf(tipoDocumentoID.getCodigoCaspio()));
					resultadoBean.setResultado(personaSAF);
				}else{
					resultadoBean.setEstado(UtilEnum.ESTADO_OPERACION.ERROR.getCodigo());
					resultadoBean.setResultado("La persona no existe");
				}
				
			} catch (Exception e) {
				resultadoBean = new ResultadoBean();
				resultadoBean.setResultado("Ocurrio un error al obtener persona por documento");
				resultadoBean.setEstado(UtilEnum.ESTADO_OPERACION.EXCEPTION.getCodigo());
				LOG.error("###getPersonaPorDocumento:",e);
			}
		}
		return resultadoBean;
	}
	
	@RequestMapping(value = "crearProveedor", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody   
	public ResultadoBean crearProveedor(@RequestBody Map<String, Object> params) {
		LOG.info("###crearProveedor params:"+params);
		ResultadoBean response = new ResultadoBean();
		try{
			if(null!=params.get("tipoProveedor") && 
			   null!=params.get("tipoDocumento") && null!=params.get("nroDocumento")&&
			   null!=params.get("personaNombre") && null!=params.get("personaApellidoPaterno") && 
			   null!=params.get("personaApellidoMaterno") && null!=params.get("personaRazonSocial")
			){
				PersonaSAF personaSAF=new PersonaSAF();
				personaSAF.setTipoProveedor(String.valueOf(params.get("tipoProveedor")));
				personaSAF.setPersonaID(null);
				
				
				UtilEnum.TIPO_DOCUMENTO tipoDocEnum = UtilEnum.TIPO_DOCUMENTO.obtenerTipoDocumentoByCodigoCaspio(Integer.parseInt(String.valueOf(params.get("tipoDocumento"))));
				personaSAF.setTipoDocumentoID(String.valueOf(tipoDocEnum.getCodigo()));
				
				personaSAF.setPersonaCodigoDocumento(String.valueOf(params.get("nroDocumento")));
				personaSAF.setNombre(String.valueOf(params.get("personaNombre")));
				personaSAF.setApellidoPaterno(String.valueOf(params.get("personaApellidoPaterno")));
				personaSAF.setApellidoMaterno(String.valueOf(params.get("personaApellidoMaterno")));
				personaSAF.setRazonSocial(String.valueOf(params.get("personaRazonSocial")));
				personaSAF = personaDAO.registrarProveedorSAF(personaSAF);
				response.setEstado(UtilEnum.ESTADO_OPERACION.EXITO.getCodigo());
				response.setResultado(personaSAF.getPersonaID());
			}else{
				response.setEstado(UtilEnum.ESTADO_OPERACION.ERROR.getCodigo());
				response.setResultado("Existen nulos en los parametros");
			}
		}catch(Exception e){
			LOG.error("###Error:",e);
			response.setEstado(UtilEnum.ESTADO_OPERACION.EXCEPTION.getCodigo());
			response.setResultado("Ocurrio un error al registrar el proveedor");
		}
		return response;
	}
	
	@RequestMapping(value = "obtenerPersonaPorDocumento/{inversionId}", method = RequestMethod.GET)
	public @ResponseBody ResultadoBean getPersonaPorInversionId(@PathVariable(value="inversionId") String inversionId){
		LOG.info("###ContratoController.getPersonaPorInversionId inversionId:"+inversionId);
		ResultadoBean resultadoBean = null;
		if(null!=inversionId){
			try {
				String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
				inversionService.setTokenCaspio(tokenCaspio);
				
				resultadoBean = new ResultadoBean();
				
				Inversion inversion = inversionService.obtenerInversionCaspioPorId(inversionId);
				
				UtilEnum.TIPO_DOCUMENTO tipoDocEnum= UtilEnum.TIPO_DOCUMENTO.obtenerTipoDocumentoByCodigoCaspio(Integer.parseInt(inversion.getPropietarioTipoDocId()));
				
				PersonaSAF personaSAF=personaDAO.obtenerPersonaPorDoc(tipoDocEnum.getCodigo(), inversion.getPropietarioNroDoc());
				
				if(null!=personaSAF){
					tipoDocEnum = UtilEnum.TIPO_DOCUMENTO.obtenerTipoDocumentoByCodigo(Integer.parseInt(personaSAF.getTipoDocumentoID()));
					personaSAF.setTipoDocumentoID(String.valueOf(tipoDocEnum.getCodigoCaspio()));
					
					resultadoBean.setEstado(UtilEnum.ESTADO_OPERACION.EXITO.getCodigo());
					resultadoBean.setResultado(personaSAF);
				}else{
					resultadoBean.setEstado(UtilEnum.ESTADO_OPERACION.ERROR.getCodigo());
					resultadoBean.setResultado("La persona no existe");
				}
				
			} catch (Exception e) {
				resultadoBean = new ResultadoBean();
				resultadoBean.setResultado("Ocurrio un error al obtener persona por inversion");
				resultadoBean.setEstado(UtilEnum.ESTADO_OPERACION.EXCEPTION.getCodigo());
				LOG.error("###getPersonaPorInversionId:",e);
			}
		}
		return resultadoBean;
	}
	
}
