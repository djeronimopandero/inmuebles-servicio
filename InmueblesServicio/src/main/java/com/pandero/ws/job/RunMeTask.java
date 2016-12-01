package com.pandero.ws.job;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pandero.ws.bean.Contrato;
import com.pandero.ws.bean.ContratoSAF;
import com.pandero.ws.bean.PersonaCaspio;
import com.pandero.ws.bean.PersonaSAF;
import com.pandero.ws.dao.impl.ContratoDaoImpl;
import com.pandero.ws.dao.impl.PersonaDaoImpl;
import com.pandero.ws.service.impl.ContratoServiceImpl;
import com.pandero.ws.service.impl.PersonaServiceImpl;
import com.pandero.ws.util.ServiceRestTemplate;
import com.pandero.ws.util.UtilEnum;

//@Component
public class RunMeTask {
	
	private static Logger LOGGER = LoggerFactory.getLogger(RunMeTask.class);
		
	public void sincronizarContratos() {
		LOGGER.info("###sincronizarPedidos execute "+new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(new Date()) );
		
		try {
			ApplicationContextProvider appContext = new ApplicationContextProvider();
			ContratoDaoImpl contratoDaoImpl = appContext.getApplicationContext().getBean("contratoDaoImpl", ContratoDaoImpl.class);
			PersonaDaoImpl personaDaoImpl = appContext.getApplicationContext().getBean("personaDaoImpl", PersonaDaoImpl.class);
			ContratoServiceImpl contratoServiceImpl = appContext.getApplicationContext().getBean("contratoServiceImpl", ContratoServiceImpl.class);
			PersonaServiceImpl personaServiceImpl = appContext.getApplicationContext().getBean("personaServiceImpl", PersonaServiceImpl.class);
			String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
			contratoServiceImpl.setTokenCaspio(tokenCaspio);
			personaServiceImpl.setTokenCaspio(tokenCaspio);
			
			// 1.-Obtener contratos con movimientos a la fecha actual del SAF
			List<ContratoSAF> listContratosSAF = null;

			listContratosSAF = contratoDaoImpl.getListContratoAlDia();

			int countContratos = 0;
			if (null != listContratosSAF) {
				countContratos = listContratosSAF.size();
				for (ContratoSAF contratoSAF : listContratosSAF) {
					// 2.-Verificar existencia del contrato en CASPIO
					Contrato contratoCaspio = contratoServiceImpl.obtenerContratoCaspio(contratoSAF.getNroContrato());

					if (null != contratoCaspio) {
						// if exists --> update : situacion, fecha de
						// adjudicacion
						contratoServiceImpl.actualizarSituacionContratoCaspio(contratoSAF.getNroContrato(),
								String.valueOf(contratoSAF.getSituacionContratoID()),
								contratoSAF.getSituacionContrato(), contratoSAF.getFechaAdjudicacion());
					} else {
						
						// 2.1.-Verificar existencia del asociado
						// La llave con el saf es el tipo de documento y el numero
						// de documento

						PersonaSAF personaSAF = personaDaoImpl.obtenerPersonaSAF(String.valueOf(contratoSAF.getPersonaId()));

						UtilEnum.TIPO_DOCUMENTO tipoDoc;

						tipoDoc = UtilEnum.TIPO_DOCUMENTO
								.obtenerTipoDocumentoByCodigo(null != personaSAF.getTipoDocumentoID()
										? Integer.parseInt(personaSAF.getTipoDocumentoID()) : 4);

						PersonaCaspio personaCaspio = personaServiceImpl.obtenerPersonaCaspio(String.valueOf(tipoDoc.getCodigoCaspio()),personaSAF.getPersonaCodigoDocumento());
						
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
							personaServiceImpl.crearPersonaCaspio(personaNuevaCaspio);
						}
												
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
						String success = contratoServiceImpl.crearContratoCaspio(contratoCaspioReg);
					}


				}

			} 
		} catch (Exception e) {
			LOGGER.error("###Sincronizacion manual de contratos y asociados:", e);
		}
		
	}

}
