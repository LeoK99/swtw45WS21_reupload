package com.buschmais.backend.voting.users;

public interface User {
	String getUsername();
	Password getPassword();
	UserPrivileges getPrivileges();
}
