package com.buschmais.backend.voting.users;

public class UserPrivileges {

	public final static int canCreateUsers = 0x01;
	public final static int canDeleteUsers = 0x02;
	public final static int canModifyUsers = 0x04;

	private int flags;

	public UserPrivileges(){
		flags = 0;
	}

	public UserPrivileges(final int flags) {
		this.flags = flags;
	}

	public boolean canCreateUsers(){
		return (flags & canCreateUsers) != 0;
	}

	public boolean canDeleteUsers(){
		return (flags & canDeleteUsers) != 0;
	}

	public boolean canModifyUsers(){
		return (flags & canModifyUsers) != 0;
	}

	public void setCreateUsers(final boolean val){
		if(val)
			flags |= canCreateUsers;
		else
			flags &=~ canCreateUsers;
	}

	public void setDeleteUsers(final boolean val){
		if(val)
			flags |= canDeleteUsers;
		else
			flags &=~ canDeleteUsers;
	}

	public void setModifyUsers(final boolean val){
		if(val)
			flags |= canModifyUsers;
		else
			flags &=~ canModifyUsers;
	}

}
