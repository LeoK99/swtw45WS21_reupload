package com.buschmais.backend.adr;

public class AccessFlags {
	private int flags;

	public static final int WRITABLE = 0x01;
	public static final int READABLE = 0x02;

	public AccessFlags(){
		this.flags = 0;
	}
	public AccessFlags(final int flags){
		this.flags = flags;
	}

	public void setWritable(final boolean val){
		if(val)
			this.flags |= WRITABLE;
		else
			this.flags &= ~WRITABLE;
	}

	public void setReadable(final boolean val){
		if(val)
			this.flags |= READABLE;
		else
			this.flags &= ~READABLE;
	}

	public boolean isWritable(){
		return (flags & WRITABLE) != 0;
	}

	public boolean isReadable(){
		return (flags & READABLE) != 0;
	}
}
