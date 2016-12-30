package com.pandero.ws.service.impl;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailParseException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.pandero.ws.bean.EmailBean;
import com.pandero.ws.service.MailService;

@Service
public class MailServiceImpl implements MailService{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MailServiceImpl.class);
	
	@Value("${ruta.documentos.generados}")
	private String rutaDocumentosGenerados;
	
	@Autowired
	private JavaMailSender mailSender;

	
//	public void sendMail(String emailFrom, String emailTo, String asunto, String documento, String textoEmail,boolean isFormatHtml) {
	public void sendMail(EmailBean email) {
		LOGGER.info("##sendMail... email:"+email);
	   MimeMessage message = mailSender.createMimeMessage();

	   try{
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			
			helper.setFrom(email.getEmailFrom());
			helper.setReplyTo("afernandezl@pandero.com.pe");
//			helper.setTo(email.getEmailTo());
			helper.setTo("afernandezl@pandero.com.pe");
			helper.setSubject(email.getSubject());
			
//			if(email.isFormatHtml()){
//				LOGGER.info("##Se envia un HTML");
//				message.setContent(email.getTextoEmail(), "text/html");
//			}else{
//				LOGGER.info("##Se envia un texto");
				helper.setText(email.getTextoEmail(),email.isFormatHtml());
//			}
			
			LOGGER.info("##email.isEnviarArchivo():"+email.isEnviarArchivo());
			if(email.isEnviarArchivo()){
				String rutaDocumento = rutaDocumentosGenerados+"/"+email.getDocumento();
				LOGGER.info("###rutaDocumento:"+rutaDocumento);
				FileSystemResource file = new FileSystemResource(rutaDocumento);
				helper.addAttachment(file.getFilename(), file);
			}
		
	     }catch (MessagingException e) {
	    	 throw new MailParseException(e);
	     }
	   
	     mailSender.send(message);
    }
	
}
