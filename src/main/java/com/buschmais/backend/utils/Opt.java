package com.buschmais.backend.utils;

import org.springframework.data.annotation.PersistenceConstructor;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class Opt <T> {
	private T value;

	@PersistenceConstructor
	private Opt(){}

	static public<T> Opt<T> Empty(){
		return new Opt<>();
	}

	static public <T> Opt<T> of(T value){
		Opt<T> res = new Opt<>();
		res.value = value;
		return res;
	}

	static public <T> Opt<T> ofNullable(T value){
		return of(value);
	}

	public void ifPresent(Consumer<T> action) {
		if (value != null) {
			action.accept(value);
		}
	}

	public <Ret> Ret ifPresent(Ret defaultValue, Function<T, Ret> action) {
		if (value != null) return action.apply(value);
		return defaultValue;
	}

	public void ifElse(Consumer<T> action, Runnable elseAction) {
		if (value != null) {
			action.accept(value);
		}else{
			elseAction.run();
		}
	}

	public boolean isPresent(){
		return value != null;
	}

	public T get(){
		return value;
	}

	public Optional<T> stdOpt(){
		return Optional.ofNullable(value);
	}
}