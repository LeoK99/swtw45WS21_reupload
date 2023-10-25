package com.buschmais.backend.adrAccess;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class AccessRights {

	@Getter(AccessLevel.PRIVATE)
	@Setter(AccessLevel.PRIVATE)
	private int flags;

	public static final int READABLE    = 0x01;
	public static final int WRITABLE    = 0x02;
	public static final int VOTABLE     = 0x04;

	public AccessRights(){
		this.flags = 0;
	}

	public AccessRights(final int flags){
		this.flags = flags;
	}

	/**
	 * Creates a new AccessRights element with the given rights
	 * @param read right to read an ADR (true/false)
	 * @param write right to change attributes of an ADR (true/false)
	 */
	public AccessRights(final boolean read, final boolean write) {
		this.flags = ((read?1:0) * READABLE + (write?1:0) * WRITABLE);
	}

	/**
	 * Creates a new AccessRights element with the given rights
	 * @param read right to read an ADR (true/false)
	 * @param write right to change attributes of an ADR (true/false)
	 * @param vote if true, all users in the corresponding AccessGroup become automatically assigned to list of voters
	 */
	public AccessRights(final boolean read, final boolean write, final boolean vote) {
		this.flags = ((read?1:0) * READABLE + (write?1:0) * WRITABLE);
	}


	public void setReadable(final boolean val){
		if(val)
			this.flags |= READABLE;
		else
			this.flags &= ~READABLE;
	}

	public void setWritable(final boolean val){
		if(val)
			this.flags |= WRITABLE;
		else
			this.flags &= ~WRITABLE;
	}

	public void setVotable(final boolean val){
		if(val)
			this.flags |= VOTABLE;
		else
			this.flags &= ~VOTABLE;
	}

	public boolean hasAtLeast(AccessRights accessRights) {
		return (this.flags & accessRights.flags) == accessRights.flags;
	}

	public boolean isReadable(){
		return (flags & READABLE) != 0;
	}

	public boolean isWritable(){
		return (flags & WRITABLE) != 0;
	}

	public boolean isVotable(){
		return (flags & VOTABLE) != 0;
	}
}
