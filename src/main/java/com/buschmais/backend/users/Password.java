package com.buschmais.backend.users;

import lombok.*;
import org.springframework.data.annotation.Transient;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Data
public class Password {

	@NonNull
	private String passwordHash;

	@Setter(AccessLevel.NONE)
	@Getter(AccessLevel.NONE)
	@Transient
	private static PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	public Password() {
		changePassword("default"); //password noch ändern über StringTafel
	}

	public Password(@NonNull final String password) {
		changePassword(password);
	}

	public void changePassword(@NonNull final String newPassword) {
		//System.out.println("Encode: " + password + " " + passwordEncoder.encode(password));
		this.passwordHash = passwordEncoder.encode(newPassword);
	}

	public boolean checkPassword(@NonNull final String password) {
		//System.out.println("Check: " + password + "\n" + passwordEncoder.encode(password) + "\n" + passwordHash);
		return passwordEncoder.matches(password, passwordHash);
	}
}
