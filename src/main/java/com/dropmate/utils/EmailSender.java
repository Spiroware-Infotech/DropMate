package com.dropmate.utils;

import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmailSender {

	private final JavaMailSender mailSender;
	private final TemplateEngine emailTemplateEngine;

	@Value("${contact.email}")
	private String fromMail;

	public static String host;
	
	public EmailSender(JavaMailSender mailSender,
			@Qualifier("emailTemplateEngine") TemplateEngine emailTemplateEngine) {
		this.mailSender = mailSender;
		this.emailTemplateEngine = emailTemplateEngine;
	}

	public void sendHtmlEmail(final String to, final String subject,final String templateName, final Map<String, Object> variables)
            throws MessagingException {
        Context context = new Context();
        context.setVariables(variables);
        String htmlContent = emailTemplateEngine.process(templateName, context); 

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        helper.setFrom(fromMail);

        mailSender.send(message);
    }
	
	/** Send Mail 
	 * @param user 
	 * @param siteURL **/
//	public void sendMail(final Email email,final String templateName, final User user, String siteURL) {
//		MimeMessagePreparator preparator = new MimeMessagePreparator() {
//			public void prepare(MimeMessage mimeMessage) throws Exception {
//				Map<String, Object> model = new HashMap<String, Object>();
//				MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true);
//				String verifyURL = siteURL + "/verify?code=" + user.getVerificationCode();
//				model.put("home", host);
//				model.put("user", user);
//				model.put("link", verifyURL);
//				message.setFrom(new InternetAddress(fromMail));
//				message.setTo(email.getTo().toArray(new String[0]));
//				message.setSubject(email.getSubject());
//				
//				Context context = new Context();
//				context.setVariables(model);
//				String body = emailTemplateEngine.process(templateName, context);
//				message.setText(body, true);
//				// Add inline image
//				ClassPathResource image = new ClassPathResource("static/assets/uploads/images/sq.png");
//				message.addInline("logo", image); // 'logo' matches the cid in the template
//			}
//		};
//		JavaMailSenderImpl mailSenderImpl=(JavaMailSenderImpl) mailSender;
//		mailSender.send(preparator);
//		log.info("Register successfully");
//		
//	}
	
	/** ContactUs sending EMAILS **/
//	public void sendContactUsEmail(final Email email, final ContactUs contactUs,
//			final SmtpDetails smtp, final VelocityEngine velocityEngine, final HttpServletRequest request,final EmailTemplateContent emailTemplatesContent) {
//		MimeMessagePreparator preparator = new MimeMessagePreparator() {
//			public void prepare(MimeMessage mimeMessage) throws Exception {
//				Map<String, Object> model = new HashMap<String, Object>();
//				MimeMessageHelper message = new MimeMessageHelper(mimeMessage,true);
//				
//				String tableContent1 = emailTemplatesContent.getTemplateBody().replace("${firstName}", contactUs.getParentName());
//				String tableContent  = tableContent1.replace("${message}", contactUs.getMessage());
//				model.put("home", host);
//				model.put("tableBody", tableContent);
//				message.setTo(new InternetAddress(contactus));
//				message.setFrom(email.getFrom());
//				message.setSubject("contact Us");
//				String body = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine,"contactUsBody.vm", "UTF-8", model);
//				message.setText(body, true);
//				FileSystemResource res = new FileSystemResource(new File(request.getServletContext().getRealPath("/resources/users/images/site-logo.png")));
//				message.addInline("contactImg", res);
//			}
//		};
//		JavaMailSenderImpl mailSender2=(JavaMailSenderImpl) mailSender;
//		mailSender2.setHost(smtp.getHostname());
//		mailSender2.setPort(smtp.getPortno());
//		mailSender2.setUsername(smtp.getUsername());
//		mailSender2.setPassword(smtp.getPassword());
//		mailSender.send(preparator);
//		System.out.println("Contastus sent successfully");
//	}
}