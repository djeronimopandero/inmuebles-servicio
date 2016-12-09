package com.pandero.ws.business.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pandero.ws.bean.Contrato;
import com.pandero.ws.bean.ContratoSAF;
import com.pandero.ws.bean.PersonaCaspio;
import com.pandero.ws.bean.PersonaSAF;
import com.pandero.ws.bean.ResultadoBean;
import com.pandero.ws.business.ContratoBusiness;
import com.pandero.ws.dao.ContratoDao;
import com.pandero.ws.dao.PersonaDAO;
import com.pandero.ws.service.ContratoService;
import com.pandero.ws.service.PedidoService;
import com.pandero.ws.service.PersonaService;
import com.pandero.ws.util.ServiceRestTemplate;
import com.pandero.ws.util.UtilEnum;

@Service
public class ContratoBusinessImpl implements ContratoBusiness {

	private static Logger LOGGER = LoggerFactory.getLogger(ContratoBusinessImpl.class);

	@Autowired
	ContratoDao contratoDao;
	@Autowired
	PersonaDAO personaDao;
	@Autowired
	ContratoService contratoService;
	@Autowired
	PersonaService personaService;
	@Autowired
	PedidoService pedidoService;
	
	@Override
	public ResultadoBean sincronizarContratosyAsociadosSafACaspio() throws Exception {
		LOGGER.info("###sincronizarContratosyAsociadosSafACaspio execute "
				+ new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(new Date()));
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		contratoService.setTokenCaspio(tokenCaspio);
		personaService.setTokenCaspio(tokenCaspio);
		
		ResultadoBean resultadoBean = null;
		try {
			// 1.-Obtener contratos con movimientos a la fecha actual del SAF
			List<ContratoSAF> listContratosSAF = null;

			listContratosSAF = contratoDao.getListContratoAlDia();

			int countContratos = 0;
			if (null != listContratosSAF) {
				countContratos = listContratosSAF.size();
				for (ContratoSAF contratoSAF : listContratosSAF) {
					// 2.-Verificar existencia del contrato en CASPIO
					Contrato contratoCaspio = contratoService.obtenerContratoCaspio(contratoSAF.getNroContrato());

					if (null != contratoCaspio) {
						// if exists --> update : situacion, fecha de adjudicacion
						contratoService.actualizarSituacionContratoCaspio(contratoSAF.getNroContrato(),
								String.valueOf(contratoSAF.getSituacionContratoID()),
								contratoSAF.getSituacionContrato(), contratoSAF.getFechaAdjudicacion());
					} else {
						
							//Solo registar los contratos ADJUDICADOS
							if(UtilEnum.ADJUDICACION.SI.getCodigo() == contratoSAF.getEsAdjudicado()){
								LOGGER.info("###El contrato "+contratoSAF.getNroContrato()+" es adjudicado se procede a registrarlo");
								
								// 2.1.-Verificar existencia del asociado
								// La llave con el saf es el tipo de documento y el numero
								// de documento
		
								PersonaSAF personaSAF = personaDao.obtenerPersonaSAF(String.valueOf(contratoSAF.getPersonaId()));
		
								UtilEnum.TIPO_DOCUMENTO tipoDoc;
		
								tipoDoc = UtilEnum.TIPO_DOCUMENTO
										.obtenerTipoDocumentoByCodigo(null != personaSAF.getTipoDocumentoID()
												? Integer.parseInt(personaSAF.getTipoDocumentoID()) : 4);
		
								PersonaCaspio personaCaspio = personaService.obtenerPersonaCaspio(String.valueOf(tipoDoc.getCodigoCaspio()), personaSAF.getPersonaCodigoDocumento());
								
								if (null == personaCaspio
										|| (personaCaspio.getTipoDocumento() == null && personaCaspio.getNroDocumento() == null)) {
									// if not exists --> insert todos los campos
									PersonaSAF personaNuevaCaspio = new PersonaSAF();
									personaNuevaCaspio.setPersonaID(personaSAF.getPersonaID());
									personaNuevaCaspio.setTipoDocumentoID(String.valueOf(tipoDoc.getCodigoCaspio().intValue()));
									personaNuevaCaspio.setPersonaCodigoDocumento(personaSAF.getPersonaCodigoDocumento());
									personaNuevaCaspio.setNombre(personaSAF.getNombre());
									personaNuevaCaspio.setApellidoPaterno(personaSAF.getApellidoPaterno());
									personaNuevaCaspio.setApellidoMaterno(personaSAF.getApellidoMaterno());
									personaNuevaCaspio.setRazonSocial(personaSAF.getRazonSocial());
									personaNuevaCaspio.setTipoPersona(personaSAF.getTipoPersona());
									personaNuevaCaspio.setNombreCompleto(personaSAF.getNombreCompleto());
									personaService.crearPersonaCaspio(personaNuevaCaspio);
								}
								
		//						personaCaspio = personaService.obtenerPersonaCaspio(personaParam);
								
								// else --> insert todos los datos
								ContratoSAF contratoCaspioReg = new ContratoSAF();
								contratoCaspioReg.setContratoId(contratoSAF.getContratoId());
								contratoCaspioReg.setNroContrato(contratoSAF.getNroContrato());
								contratoCaspioReg.setFechaVenta(contratoSAF.getFechaVenta());
								contratoCaspioReg.setMontoCertificado(null!=contratoSAF.getMontoCertificado()?contratoSAF.getMontoCertificado():0);
								contratoCaspioReg.setMontoDisponible(null!=contratoSAF.getMontoDisponible()?contratoSAF.getMontoDisponible():0);
								contratoCaspioReg.setAsociadoId(Integer.parseInt(personaSAF.getPersonaID()));
								contratoCaspioReg.setSituacionContratoCASPIO(contratoSAF.getSituacionContratoCASPIO());
								contratoCaspioReg.setDiferenciaPrecio(null!=contratoSAF.getDiferenciaPrecio()?contratoSAF.getDiferenciaPrecio():0);
								contratoCaspioReg.setDiferenciaPrecioDisponible(null!=contratoSAF.getDiferenciaPrecioDisponible()?contratoSAF.getDiferenciaPrecioDisponible():0);
								contratoCaspioReg.setOtrosIngresos(null!=contratoSAF.getOtrosIngresos()?contratoSAF.getOtrosIngresos():0);
								contratoCaspioReg.setOtrosDisponibles(null!=contratoSAF.getOtrosDisponibles()?contratoSAF.getOtrosDisponibles():0);
								contratoCaspioReg.setTotalDisponible(null!=contratoSAF.getTotalDisponible()?contratoSAF.getTotalDisponible():0);
								contratoCaspioReg.setEstado(contratoSAF.getEstado());
								contratoCaspioReg.setFechaAdjudicacion(contratoSAF.getFechaAdjudicacion());
								contratoCaspioReg.setSituacionContrato(contratoSAF.getSituacionContrato());
								String success = contratoService.crearContratoCaspio(contratoCaspioReg);
						}else{
							LOGGER.info("###El contrato "+contratoSAF.getNroContrato()+" no esta adjudicado, no sera registrado en CASPIO");
						}
					}

				}

				resultadoBean = new ResultadoBean();
				resultadoBean.setResultado("Se procesaron " + countContratos + " contrato(s).");

			} else {
				// No existe contrato que sincronizar
				resultadoBean = new ResultadoBean();
				resultadoBean.setResultado("No se encuentran contratos");
			}
		} catch (Exception e) {
			LOGGER.error("###Sincronizacion manual de contratos y asociados:", e);
		}
		return resultadoBean;
	}

	@Override
	public ResultadoBean actualizarDiferenciaPrecioContratos(Integer pedidoId) throws Exception {
		LOGGER.info("###actualizarDiferenciaPrecioContratos pedidoId:"+pedidoId);
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		pedidoService.setTokenCaspio(tokenCaspio);
		contratoService.setTokenCaspio(tokenCaspio);
		ResultadoBean resultadoBean=null;
		if(null!=pedidoId){
			List<Contrato> listContrato= pedidoService.obtenerContratosxPedidoCaspio(String.valueOf(pedidoId));
			
			if(null!=listContrato){
				for(Contrato contrato:listContrato){
					ContratoSAF contratoSAF= contratoDao.obtenerContratoSAF(contrato.getNroContrato());
					Double dblDifPrecio=contratoDao.obtenerDiferenciaPrecioPorContrato(contratoSAF.getContratoId());
					if(null!=dblDifPrecio){
						if(dblDifPrecio>0){
							contratoService.actualizarDifPrecioContratoCaspio(contrato.getNroContrato(), dblDifPrecio);
						}
					}
				}
			}
			
		}else{
			resultadoBean = new ResultadoBean();
			resultadoBean.setResultado("Es necesario enviar el identificador de pedido");
		}
			
		return resultadoBean;
	}

}
