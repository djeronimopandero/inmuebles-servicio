package com.pandero.ws.service.impl;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailParseException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.pandero.ws.service.MailService;

@Service
public class MailServiceImpl implements MailService{
	
	@Value("${ruta.documentos.generados}")
	private String rutaDocumentosGenerados;
	
	@Autowired
	private JavaMailSender mailSender;

	public void sendMail(String emailFrom, String emailTo, String asunto, String documento) {

	   MimeMessage message = mailSender.createMimeMessage();

	   try{
		MimeMessageHelper helper = new MimeMessageHelper(message, true);

		helper.setFrom(emailFrom);
		helper.setTo(emailTo);
		helper.setSubject(asunto);
		helper.setText("Se adjunta la orden irrevocable correspondiente");
		String rutaDocumento = rutaDocumentosGenerados+"/"+documento;
		System.out.println("RUTA DOCUMENTO:: "+rutaDocumento);
		FileSystemResource file = new FileSystemResource(rutaDocumento);
		helper.addAttachment(file.getFilename(), file);

	     }catch (MessagingException e) {
		throw new MailParseException(e);
	     }
	     mailSender.send(message);
    }
	
}
