package com.buschmais.backend.users;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;

@Data
public class UserRights implements GrantedAuthority {

	public final static int CAN_MANAGE_USERS  = 0x01;
	public final static int CAN_MANAGE_VOTING = 0x02;
	public final static int CAN_SEE_ALL_ADRS  = 0x04;
	public final static int CAN_MANAGE_ADR_ACCESS  = 0x08;
	private final static int COMBINED_CAN_MANAGE_ADRS = CAN_MANAGE_VOTING | CAN_SEE_ALL_ADRS | CAN_MANAGE_ADR_ACCESS;
	private final static int COMBINED_ALL_FLAGS = CAN_MANAGE_USERS | CAN_MANAGE_VOTING | CAN_SEE_ALL_ADRS;

	private int flags;

	/**
	 * Erstellt ein neues UserRights Element mit den gegebenen Rechten
	 * @param manageUsers Recht auf Nutzerermanagment (true/false)
	 * @param manageAdrs Recht auf ADRManagment (true/false)
	 */
	public UserRights(final boolean manageUsers, final  boolean manageAdrs) {
		this.flags =	(manageUsers	? CAN_MANAGE_USERS 			: 0) |
						(manageAdrs		? COMBINED_CAN_MANAGE_ADRS 	: 0);
	}

	/**
	 * Erstellt ein neues UserRights Element mit den gegebenen Rechten
	 * @param manageUsers Recht auf Nutzerermanagment (true/false)
	 * @param manageAdrVotings Recht auf ADRVotingManagment (true/false)
	 * @param seeAllAdrs Recht auf das Einsehen aller ADRs (true/false)
	 * @param manageAdrAccess Recht auf das Einsehen aller ADRs (true/false)
	 */
	public UserRights(final boolean manageUsers, final boolean manageAdrVotings, final boolean seeAllAdrs, final boolean manageAdrAccess) {
		this.flags =	(manageUsers 		? CAN_MANAGE_USERS 		: 0) |
						(manageAdrVotings 	? CAN_MANAGE_VOTING 	: 0) |
						(seeAllAdrs 		? CAN_SEE_ALL_ADRS 		: 0) |
						(manageAdrAccess 	? CAN_MANAGE_ADR_ACCESS : 0);
	}

	public UserRights(){
		flags = 0;
	}

	public UserRights(final int flags) {
		this.flags = flags & COMBINED_ALL_FLAGS;
	}

	/**
	 * Gibt zurück, ob das Element mindestens alle gegebenen Rechte besitzt.
	 * Es wird also auch true zurückgegeben, wenn das Element mehr als die angeforderten Rechte besitzt.
	 * @param rights UserRights Element für zu überprüfende Rechte (entsprechendes Element kann über new UserRights(boolean, boolean, boolean) erstellt werden)
	 * @return boolean, ob das Element mindestens die entsprechenden Rechte besitzt
	 */
	public boolean hasAtLeast(@NonNull final UserRights rights){
		return (rights.flags & this.flags) == rights.flags;
	}

	public void setCanManageUsers(final boolean val){
		if(val)
			flags |= CAN_MANAGE_USERS;
		else
			flags &=~ CAN_MANAGE_USERS;
	}

	public void setCanManageAdrs(final boolean val){
		if(val)
			flags |= COMBINED_CAN_MANAGE_ADRS;
		else
			flags &=~ COMBINED_CAN_MANAGE_ADRS;
	}

	public void setCanManageVoting(final boolean val){
		if(val)
			flags |= CAN_MANAGE_VOTING;
		else
			flags &=~ CAN_MANAGE_VOTING;
	}

	public void setCanSeeAllAdrs(final boolean val){
		if(val)
			flags |= CAN_SEE_ALL_ADRS;
		else
			flags &=~ CAN_SEE_ALL_ADRS;
	}

	public void setCanManageAdrAccess(final boolean val){
		if(val)
			flags |= CAN_MANAGE_ADR_ACCESS;
		else
			flags &=~ CAN_MANAGE_ADR_ACCESS;
	}


	@Override
	public String getAuthority() {
		return	(this.hasRight(CAN_MANAGE_USERS) 		? "U":"_") +
				(this.hasRight(CAN_MANAGE_VOTING) 		? "V":"_") +
				(this.hasRight(CAN_SEE_ALL_ADRS) 		? "S":"_") +
				(this.hasRight(CAN_MANAGE_ADR_ACCESS) 	? "A":"_");
	}


	private boolean hasRight(int right) {
		return (right & this.flags) != 0;
	}
}