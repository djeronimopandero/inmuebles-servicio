package com.pandero.ws.dao.impl;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.pandero.ws.dao.InversionDao;

@Repository
public class InversionDaoImpl implements InversionDao{

	private static final Logger LOG = LoggerFactory.getLogger(GeneralDaoImpl.class);
	private JdbcTemplate jdbcTemplate;
		
	@Autowired
	@Qualifier("dataSource")
	public void setDataSource(DataSource dataSource)	{
	    this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public List<Map<String, Object>> getListaEventoInversionPorNumeroInversion(String nroInversion,String[] arrayContratos) throws Exception {
		String sql = "SELECT *  "+
				"FROM   (SELECT TOP 1 1  "+
				"                             AS  "+
				"                             Indice,  "+
				"                     'EVALUACIÓN CREDITICIA'  "+
				"                             AS Instancia,  "+
				"                     CASE  "+
				"                       WHEN Isnull(cre_ev.evaluacioncomiteid, 0) = 2 THEN  "+
				"                       'APROBADO'  "+
				"                       WHEN Isnull(cre_ev.evaluacioncomiteid, 0) = 3 THEN  "+
				"                       'DESAPROBADO'  "+
				"                       ELSE 'EN EVALUACIÓN'  "+
				"                     END  "+
				"                             AS EstadoSolicitud,  "+
				"                     CONVERT(VARCHAR(10),  "+
				"                     CONVERT(DATE, cre.creditofechacreacion, 106),  "+
				"                             103) AS  "+
				"                     FechaModificacion,  "+
				"                     usu.usuarionombre  "+
				"                             AS UsuarioNombre,  "+
				"                     ''  "+
				"                             AS Referencia  "+
				"        FROM  cre_credito cre  "+
				"                LEFT JOIN cre_creditoevento cre_ev  "+
				"                 ON cre_ev.CreditoID = cre.CreditoID "+
				"                LEFT JOIN gen_evento ev  "+
				"                 ON cre_ev.eventoid = ev.eventoid  "+
				"                LEFT JOIN seg_usuario usu  "+
				"                 ON usu.usuarioid = cre_ev.usuarioidevaluacion  "+
				"                JOIN CRE_CreditoContrato creCon  "+
				"                 ON creCon.CreditoID = cre.CreditoID  "+
				"                JOIN foc_contrato foc  "+
				"                 ON foc.contratoid = creCon.contratoid  "+
				"        WHERE  foc.contratoNumero IN ("+StringUtils.join(arrayContratos,",")+")  "+
				"        ORDER  BY cre_ev.creditoeventoid DESC  "+
				"        UNION  "+
				"        SELECT 2                                     AS Indice,  "+
				"               'REGISTRO DE INVERSIÓN INMOBILIARIA' AS Instancia,  "+
				"               'CONFIRMADO'                          AS EstadoSolicitud,  "+
				"               CONVERT(VARCHAR(10),  "+
				"               CONVERT(DATE, pi.pedidoinversionfechaconfirmacion,  "+
				"               106),  "+
				"               103)                                  AS FechaModificacion,  "+
				"               usu.usuarionombre                     AS UsuarioNombre,  "+
				"               ''                                    AS Referencia  "+
				"        FROM   log_pedidoinversion pi  "+
				"               JOIN seg_usuario usu  "+
				"                 ON pi.usuarioidcreacion = usu.usuarioid  "+
				"        WHERE  pedidoinversionnumero = '"+nroInversion+"'  "+
				"        UNION  "+
				"        SELECT TOP 1 2                                     AS Indice,  "+
				"                     'REGISTRO DE INVERSIÓN INMOBILIARIA' AS Instancia,  "+
				"                     'NO CONFIRMADO'                       AS EstadoSolicitud,  "+
				"                     ''                                    AS FechaModificacion,  "+
				"                     ''                                    AS UsuarioNombre,  "+
				"                     ''                                    AS Referencia  "+
				"        FROM   foc_contrato  "+
				"        WHERE  NOT EXISTS (SELECT 1  "+
				"                           FROM   log_pedidoinversion  "+
				"                           WHERE  pedidoinversionnumero = '"+nroInversion+"')  "+
				"        UNION  "+
				"        SELECT 5                       AS Indice,  "+
				"               'ENTREGA DE INVERSIÓN' AS Instancia,  "+
				"               CASE  "+
				"                 WHEN pedidoinversionestado = 2 THEN 'CONFIRMADO'  "+
				"                 ELSE 'PENDIENTE'  "+
				"               END                     AS EstadoSolicitud,  "+
				"               CASE  "+
				"                 WHEN pedidoinversionestado = 2 THEN CONVERT(VARCHAR(10),  "+
				"                 CONVERT(DATE, pi.pedidoinversionfechamodificacion, 106),  "+
				"                 103)  "+
				"                 ELSE ''  "+
				"               END                     AS FechaModificacion,  "+
				"               CASE  "+
				"                 WHEN pedidoinversionestado = 2 THEN u.usuarionombre  "+
				"                 ELSE ''  "+
				"               END                     AS UsuarioNombre,  "+
				"               ''                      AS Referencia  "+
				"        FROM   log_pedidoinversion pi  "+
				"               JOIN seg_usuario u  "+
				"                 ON pi.usuarioidmodificacion = u.usuarioid  "+
				"        WHERE  pedidoinversionnumero = '"+nroInversion+"') t  ";				
		LOG.info("Ejecutando consulta "+ sql);
		return jdbcTemplate.queryForList(sql);
	}
	
}
