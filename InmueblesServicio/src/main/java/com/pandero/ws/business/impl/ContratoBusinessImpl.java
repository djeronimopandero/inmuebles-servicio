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
import com.pandero.ws.service.PersonaService;
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

	@Override
	public ResultadoBean sincronizarContratosyAsociadosSafACaspio() {
		LOGGER.info("###sincronizarContratosyAsociadosSafACaspio execute "
				+ new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(new Date()));

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
						// if exists --> update : situacion, fecha de
						// adjudicacion
						contratoService.actualizarSituacionContratoCaspio(contratoSAF.getNroContrato(),
								String.valueOf(contratoSAF.getSituacionContratoID()),
								contratoSAF.getSituacionContrato(), contratoSAF.getFechaAdjudicacion());
					} else {
						// else --> insert todos los datos
						ContratoSAF contratoCaspioReg = new ContratoSAF();
						contratoCaspioReg.setNroContrato(contratoSAF.getNroContrato());
						contratoCaspioReg.setFechaVenta(contratoSAF.getFechaVenta());
						contratoCaspioReg.setMontoCertificado(contratoSAF.getMontoCertificado());
						contratoCaspioReg.setMontoDisponible(contratoSAF.getMontoDisponible());
						contratoCaspioReg.setAsociadoId(contratoSAF.getAsociadoId());
						contratoCaspioReg.setSituacionContratoCASPIO(contratoSAF.getSituacionContratoCASPIO());
						contratoCaspioReg.setDiferenciaPrecio(contratoSAF.getDiferenciaPrecio());
						contratoCaspioReg.setDiferenciaPrecioDisponible(contratoSAF.getDiferenciaPrecioDisponible());
						contratoCaspioReg.setOtrosIngresos(contratoSAF.getOtrosIngresos());
						contratoCaspioReg.setOtrosDisponibles(contratoSAF.getOtrosDisponibles());
						contratoCaspioReg.setTotalDisponible(contratoSAF.getTotalDisponible());
						contratoCaspioReg.setEstado(contratoSAF.getEstado());
						contratoCaspioReg.setFechaAdjudicacion(contratoSAF.getFechaAdjudicacion());
						contratoCaspioReg.setSituacionContrato(contratoSAF.getSituacionContrato());
						String success = contratoService.crearContratoCaspio(contratoCaspioReg);
					}

					// 2.1.-Verificar existencia del asociado
					// La llave con el saf es el tipo de documento y el numero
					// de documento

					PersonaSAF personaSAF = personaDao
							.obtenerAsociadosxContratoSAF(String.valueOf(contratoSAF.getPersonaId()));

					UtilEnum.TIPO_DOCUMENTO tipoDoc;

					tipoDoc = UtilEnum.TIPO_DOCUMENTO
							.obtenerTipoDocumentoByCodigo(null != personaSAF.getTipoDocumentoID()
									? Integer.parseInt(personaSAF.getTipoDocumentoID()) : 4);

					PersonaSAF personaParam = new PersonaSAF();
					personaParam.setTipoDocumentoID(String.valueOf(tipoDoc.getCodigoCaspio()));
					personaParam.setPersonaCodigoDocumento(personaSAF.getPersonaCodigoDocumento());
					PersonaCaspio personaCaspio = personaService.obtenerPersonaCaspio(personaParam);

					if (null == personaCaspio
							|| (personaCaspio.getTipoDocumento() == null && personaCaspio.getNroDocumento() == null)) {
						// if not exists --> insert todos los campos
						PersonaSAF personaNuevaCaspio = new PersonaSAF();
						personaNuevaCaspio.setTipoDocumentoID(personaSAF.getTipoDocumentoID());
						personaNuevaCaspio.setPersonaCodigoDocumento(personaSAF.getPersonaCodigoDocumento());
						personaNuevaCaspio.setNombre(personaSAF.getNombre());
						personaNuevaCaspio.setApellidoPaterno(personaSAF.getApellidoPaterno());
						personaNuevaCaspio.setApellidoMaterno(personaSAF.getApellidoMaterno());
						personaNuevaCaspio.setRazonSocial(personaSAF.getRazonSocial());
						personaNuevaCaspio.setTipoPersona(personaSAF.getTipoPersona());
						personaNuevaCaspio.setNombreCompleto(personaSAF.getNombreCompleto());
						personaService.crearPersonaCaspio(personaNuevaCaspio);
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

}
