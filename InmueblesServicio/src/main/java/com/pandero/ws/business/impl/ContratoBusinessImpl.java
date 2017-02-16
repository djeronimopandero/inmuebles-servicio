package com.pandero.ws.business.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pandero.ws.bean.Contrato;
import com.pandero.ws.bean.ContratoSAF;
import com.pandero.ws.bean.DetalleDiferenciaPrecio;
import com.pandero.ws.bean.Inversion;
import com.pandero.ws.bean.LiquidacionSAF;
import com.pandero.ws.bean.Pedido;
import com.pandero.ws.bean.PedidoContrato;
import com.pandero.ws.bean.PersonaCaspio;
import com.pandero.ws.bean.PersonaSAF;
import com.pandero.ws.bean.ResultadoBean;
import com.pandero.ws.business.ContratoBusiness;
import com.pandero.ws.dao.ContratoDao;
import com.pandero.ws.dao.LiquidacionDao;
import com.pandero.ws.dao.PedidoDao;
import com.pandero.ws.dao.PersonaDao;
import com.pandero.ws.service.ContratoService;
import com.pandero.ws.service.InversionService;
import com.pandero.ws.service.PedidoService;
import com.pandero.ws.service.PersonaService;
import com.pandero.ws.util.Constantes;
import com.pandero.ws.util.ServiceRestTemplate;
import com.pandero.ws.util.Util;
import com.pandero.ws.util.UtilEnum;

@Service
public class ContratoBusinessImpl implements ContratoBusiness {

	private static Logger LOGGER = LoggerFactory.getLogger(ContratoBusinessImpl.class);

	@Autowired
	ContratoDao contratoDao;
	@Autowired
	PersonaDao personaDao;
	@Autowired
	ContratoService contratoService;
	@Autowired
	PersonaService personaService;
	@Autowired
	PedidoService pedidoService;
	@Autowired
	InversionService inversionService;
	@Autowired
	LiquidacionDao liquidacionDAO;
	@Autowired
	PedidoDao pedidoDao;
	
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
						
						// Verificar si es retiro de adjudicacion
						if(UtilEnum.ADJUDICACION.NO.getCodigo().intValue() == contratoSAF.getEsAdjudicado().intValue()){
							// Verificar si el contrato tiene un pedido
							PedidoContrato pedidoContrato = pedidoService.obtenerPedidoPorNroContrato(contratoSAF.getNroContrato());
							if(pedidoContrato!=null){
								// Si existe pedido, eliminarlo de SAF y Caspio
								String nroPedido="", pedidoCaspioId="";
								nroPedido = pedidoContrato.getNroPedido();
								pedidoCaspioId = String.valueOf(pedidoContrato.getPedidoId().intValue());
								
								// Eliminar pedido SAF
								pedidoDao.eliminarPedidoSAF(nroPedido, "1");
								
								// Caspio - cambiar estado pedido a anulado
								pedidoService.actualizarEstadoPedidoCaspio(nroPedido, Constantes.Pedido.ESTADO_ANULADO);
											
								// Caspio - desasociar los contratos del pedido
								List<Contrato> listaContratos = pedidoService.obtenerContratosxPedidoCaspio(pedidoCaspioId);
								if(listaContratos!=null && listaContratos.size()>0){
									for(Contrato contrato : listaContratos){
										contratoService.actualizarAsociacionContrato(contrato.getNroContrato(), Constantes.Contrato.ESTADO_NO_ASOCIADO);
									}
								}
								
								// Anular las inversiones
								List<Inversion> listaInversiones = pedidoService.obtenerInversionesxPedidoCaspio(pedidoCaspioId);
								if(listaInversiones!=null && listaInversiones.size()>0){
									for(Inversion inversion : listaInversiones){
										inversionService.actualizarEstadoInversionCaspio(String.valueOf(inversion.getInversionId().intValue()), Constantes.Inversion.ESTADO_ANULADO);
									}
								}
							}
						}
						
					} else {
						
							//Solo registar los contratos ADJUDICADOS
							if(UtilEnum.ADJUDICACION.SI.getCodigo() == contratoSAF.getEsAdjudicado()){
								LOGGER.info("###El contrato "+contratoSAF.getNroContrato()+" es adjudicado se procede a registrarlo");
								
								// 2.1.-Verificar existencia del asociado
								// La llave con el saf es el tipo de documento y el numero
								// de documento
		
								PersonaSAF personaSAF = personaDao.obtenerPersonaSAF(String.valueOf(contratoSAF.getPersonaId()));
								
								LOGGER.info("##personaSAF:"+personaSAF);
								
								UtilEnum.TIPO_DOCUMENTO tipoDoc;
		
								tipoDoc = UtilEnum.TIPO_DOCUMENTO
										.obtenerTipoDocumentoByCodigo(!StringUtils.isEmpty(personaSAF.getTipoDocumentoID())
												? Integer.parseInt(personaSAF.getTipoDocumentoID()) : 4);
		
								PersonaCaspio personaCaspio = personaService.obtenerPersonaCaspio(String.valueOf(tipoDoc.getCodigoCaspio()), personaSAF.getPersonaCodigoDocumento());
								
								if (null == personaCaspio
										|| (personaCaspio.getTipoDocumento() == null && personaCaspio.getNroDocumento() == null)) {
									LOGGER.info("###Por grabar a la persona en Caspio "+personaSAF.getNombreCompleto());
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
								contratoCaspioReg.setMontoDisponible(null!=contratoSAF.getMontoCertificado()?contratoSAF.getMontoCertificado():0);
								contratoCaspioReg.setAsociadoId(personaSAF.getPersonaID());
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
								LOGGER.info("##Se grabo el contrato en Caspio nro:"+contratoSAF.getNroContrato());
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
					//ContratoSAF contratoSAF= contratoDao.obtenerContratoSAF(contrato.getNroContrato());
					Double dblDifPrecio=contratoDao.obtenerDiferenciaPrecioPorContrato(contrato.getNroContrato());
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

	@Override
	public ResultadoBean getDetalleDiferenciaPrecio(Integer pedidoId) throws Exception {
		LOGGER.info("###getDetalleDiferenciaPrecio pedidoId:"+pedidoId);
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		pedidoService.setTokenCaspio(tokenCaspio);
		contratoService.setTokenCaspio(tokenCaspio);
		inversionService.setTokenCaspio(tokenCaspio);
		
		ResultadoBean resultadoBean=null;
		if(null!=pedidoId){			
			resultadoBean=new ResultadoBean();
			
			DetalleDiferenciaPrecio ddp = obtenerMontoDiferenciaPrecio(pedidoId);
			Double diferenciaPrecio = Double.parseDouble(ddp.getDiferenciaPrecio());
			System.out.println("diferenciaPrecioFinal::: "+diferenciaPrecio);
									
			if(diferenciaPrecio.doubleValue()<0){
				resultadoBean.setEstado(UtilEnum.ESTADO_OPERACION.EXITO.getCodigo());
				ddp.setDiferenciaPrecio(Util.getMontoFormateado(Double.parseDouble(ddp.getDiferenciaPrecio())));
				ddp.setImporteFinanciado(Util.getMontoFormateado(Double.parseDouble(ddp.getImporteFinanciado())));
				ddp.setSaldoDiferencia(Util.getMontoFormateado(Double.parseDouble(ddp.getSaldoDiferencia())));
				resultadoBean.setResultado(ddp);
				if(!verificarDiferenciaPrecio(String.valueOf(pedidoId))){
					resultadoBean.setEstado(3);
				}
			}else{
				resultadoBean.setEstado(UtilEnum.ESTADO_OPERACION.ERROR.getCodigo());
				resultadoBean.setMensajeError("No existe diferencia de precio, por lo tanto no se podrá registrar la cancelación");
			}			
		}
		return resultadoBean;
	}
	
	private boolean verificarDiferenciaPrecio(String pedidoId) throws Exception{
		Pedido pedido = pedidoService.obtenerPedidoCaspioPorId(pedidoId);
		if(pedido.getCancelacionDiferenciaPrecioMonto()!= null && pedido.getCancelacionDiferenciaPrecioMonto()>0){
			List<LiquidacionSAF> liquidaciones = liquidacionDAO.obtenerLiquidacionPorPedidoSAF(pedido.getNroPedido());
			if(liquidaciones.size()>0){
				return false;
			}
		}else{
			return false;			
		}
		return true;
	}
	
	public DetalleDiferenciaPrecio obtenerMontoDiferenciaPrecio(Integer pedidoId) throws Exception {		
		Double sumMontoCertificado=0.00;
		//1.- Suma de los certificados
		//Consultar listado de PedidoContrato por pedidoId y obtener los contratos, capturar cada contratoId
		//Sumar el monto disponible del certificado de cada contrato MontoDisponible
		List<Contrato> listPedidoContrato = pedidoService.obtenerContratosxPedidoCaspio(String.valueOf(pedidoId));
		if(null!=listPedidoContrato){
			for(Contrato pc:listPedidoContrato){
				Contrato contrato= contratoService.obtenerContratoCaspioPorId(String.valueOf(pc.getPedidoContrato_ContratoId()));
				sumMontoCertificado += contrato.getMontoCertificado();
			}
		}
		
		Double sumImporteTotalInversion=0.00;
		//2.- Suma de el importe total de las inversiones
		//Consultar el listado de PedidoInversion por pedidoId y sumar los montos de todas las inversiones
		List<Inversion> listPedidoInversionCaspio = inversionService.listarPedidoInversionPorPedidoId(String.valueOf(pedidoId));
		if(null!=listPedidoInversionCaspio){
			for(Inversion pedidoInversionCaspio:listPedidoInversionCaspio){
				sumImporteTotalInversion += pedidoInversionCaspio.getImporteInversion()==null?0.00:pedidoInversionCaspio.getImporteInversion();
			}
		}
		
		//3.- Restar (1) - (2) = diferenciaPrecio
		Double diferenciaPrecio = sumMontoCertificado - sumImporteTotalInversion;
		System.out.println("DIFERENCIA: "+diferenciaPrecio+"= "+sumMontoCertificado+" - "+sumImporteTotalInversion);
		
		//4.- Con la lista de contratos de la tabla PedidoContrato consultar la diferencia de precio en el SAF y sumarlas = importeFinanciado
		List<Contrato> listaContratos= pedidoService.obtenerContratosxPedidoCaspio(String.valueOf(pedidoId));
		Double sumImporteDiferenciaPrecio = getSumaDiferenciaPrecioxPedido(listaContratos);
		System.out.println("sumImporteDiferenciaPrecio:: "+sumImporteDiferenciaPrecio);
		
		//5.- Restar (3) - (4) = saldoDiferencia
		Double saldoDiferencia = (diferenciaPrecio<0?diferenciaPrecio*-1:diferenciaPrecio) - sumImporteDiferenciaPrecio;
		System.out.println("saldoDiferencia:: "+saldoDiferencia);
		
		DetalleDiferenciaPrecio ddp=new DetalleDiferenciaPrecio();
		ddp.setPedidoId(pedidoId);
		ddp.setDiferenciaPrecio(String.valueOf(diferenciaPrecio));
		ddp.setImporteFinanciado(String.valueOf(sumImporteDiferenciaPrecio));
		ddp.setSaldoDiferencia(String.valueOf(saldoDiferencia));
		
		return ddp;
	}
	
	private double getSumaDiferenciaPrecioxPedido(List<Contrato> listaContratos){
		double sumaDiferenciaPrecio=0.00;
		for(Contrato contrato : listaContratos){
			Double dblDifPrecioSaf=0.00;
			try {
				dblDifPrecioSaf = contratoDao.obtenerDiferenciaPrecioPorContrato(contrato.getNroContrato());
			} catch (Exception e) {
				LOGGER.error("###Error al obtener la diferencia de precio en al suma:",e);
			}
			sumaDiferenciaPrecio = sumaDiferenciaPrecio + dblDifPrecioSaf;		
		}
		return sumaDiferenciaPrecio;
	}

}
