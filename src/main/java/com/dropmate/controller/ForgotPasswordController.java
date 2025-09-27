package com.dropmate.controller;

import java.io.UnsupportedEncodingException;
import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.dropmate.aspects.TrackExecutionTime;
import com.dropmate.entity.User;
import com.dropmate.exceptions.UserNotFountException;
import com.dropmate.service.UserService;
import com.dropmate.utils.Utility;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;


@CrossOrigin("*")
@Controller
@Slf4j
public class ForgotPasswordController {

    @Autowired
    private UserService userService;

    @Autowired
    private JavaMailSender javaMailSender;

    @TrackExecutionTime
    @GetMapping("/forgot-password")
    public String getForgotPassword(Model model) {
    	log.info("******* ForgotPasswordController ---> getForgotPassword *********");

    	return "auth/forgotpwd";
    }

    @TrackExecutionTime
    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email, HttpServletRequest request,final RedirectAttributes redirectAttributess) throws UserNotFountException {
    	log.info("******* ForgotPasswordController ---> processForgotPassword *********");
    	 String token =  UUID.randomUUID().toString();
        
        try {
        	User user = userService.findByEmail(email).orElse(null);
        	if(Objects.isNull(user)) {
        		log.info("Given emailId not exist : {}",email);
        		redirectAttributess.addFlashAttribute("errorMessage","Please enter valid registered email!.");
        		
        	}else {

                String resetPasswordLink = Utility.getSiteUrl(request) + "/reset_password?token=" + token;
                log.info("Reset Password Link : {} ",resetPasswordLink);

                sendEmail(email, resetPasswordLink);
                log.info("Reset password link send to registered email : {}",email);
                redirectAttributess.addFlashAttribute("success","Send Reset password link to your registered email!.");
        	}
    		
        } catch (UnsupportedEncodingException | MessagingException e) {
            e.printStackTrace();
        }
        log.info("******* ForgotPasswordController ---> processForgotPassword END *********");
        return "redirect:/reset_success";
    }

    // Sending email with reset password token
    private void sendEmail(String email, String resetPasswordLink) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("info@dropmate.com", "DropMate Support");
        helper.setTo(email);

        String subject = "Here's the link to reset your password";

        String content = "<p>Hello,</p>"
                + "<p>You have requested to reset your password.</p>"
                + "<p>Click the link below to change your password:</p>"
                + "<p><a href=\"" + resetPasswordLink + "\">Change my password</a></p>"
                + "<br>"
                + "<p>Ignore this email if you do remember your password, "
                + "or you have not made the request.</p>";

        helper.setSubject(subject);

        helper.setText(content, true);

        javaMailSender.send(message);
    }

//    @TrackExecutionTime
//    @GetMapping("/reset_password")
//    public String showResetPasswordForm(@RequestParam("token") String token,Model model) {
//    	log.info("******* ForgotPasswordController ---> showResetPasswordForm --> Start *********");
//        User user = userService.getResetPasswordToken(token);
//        if (user == null) {
//        	model.addAttribute("errorMessage","Invalid Token");
//        }
//        model.addAttribute("token", token);
//        return "reset_password";
//    }
//
//    @TrackExecutionTime
//    @PostMapping("/reset_password")
//    public String processResetPassword(@ModelAttribute ResetPassword resetPassword, HttpServletRequest request,final RedirectAttributes redirectAttributess) {
//        String token = resetPassword.getToken();
//        String password = resetPassword.getPassword();
//        User user = userService.getResetPasswordToken(token);
//        if (user == null) {
//            redirectAttributess.addFlashAttribute("errorMessage","Opps! - Invalid Token,Please enter valid token!.");
//        } else {
//            userService.updatePassword(user, password);
//            redirectAttributess.addFlashAttribute("success","Password updated successfully. Please login with updated password!.");
//        }
//        return "redirect:/reset_success";
//    }
    
    @TrackExecutionTime
	@GetMapping(value = "/reset_success")
	public ModelAndView registrationSuccess(Model model) {
		log.info(" ForgotPasswordController --> registrationSuccess --> ");
		ModelAndView mView = new ModelAndView("reset_success");
		String success = (String)model.asMap().get("success");
    	String errorMessage = (String)model.asMap().get("errorMessage");
    	model.addAttribute("success",success);
    	model.addAttribute("errorMessage",errorMessage);
		return mView;
	}
}
