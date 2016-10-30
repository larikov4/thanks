package com.komandda.authentication;

import com.komandda.entity.User;
import com.komandda.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

	@Autowired
	private UserService userService;
	
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		  String username = authentication.getName();
	      String password = (String) authentication.getCredentials();
		  User user = userService.findOne(username, password);
	 
		  if (user == null || user.isDeleted()) {
	          throw new BadCredentialsException("User was not found.");
	      }

	      Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
	 
	      return new UsernamePasswordAuthenticationToken(user, password, authorities);
	}

	public boolean supports(Class<?> arg0) {
		return true;
	}

}
