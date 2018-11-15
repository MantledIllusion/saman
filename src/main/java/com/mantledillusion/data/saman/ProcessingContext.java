package com.mantledillusion.data.saman;

import java.util.Arrays;
import java.util.Map;
import java.util.WeakHashMap;

public class ProcessingContext {

	private final Map<Class<?>, Object> context = new WeakHashMap<>();

	private ProcessingContext() {
	}

	protected ProcessingContext(ProcessingContext base) {
		if (base != null) {
			this.context.putAll(base.context);
		}
	}

	/**
	 * Factory method, creates a new {@link ProcessingContext} with the given
	 * 
	 * @param contextValues
	 *            Optional context values that should be included in the context by
	 *            their {@link Class} type; might be null, might not contain nulls.
	 * @return A new {@link ProcessingContext} instance, never null
	 */
	public static ProcessingContext of(Object... contextValues) {
		ProcessingContext context = new ProcessingContext();
		if (contextValues != null) {
			Arrays.stream(contextValues).forEach(value -> context.set(value));
		}
		return context;
	}

	public <T> boolean has(Class<T> valueType) {
		return this.context.containsKey(valueType);
	}

	@SuppressWarnings("unchecked")
	public <T> T get(Class<T> valueType) {
		if (valueType == null) {
			throw new IllegalArgumentException("No context value available for a null value type");
		} else if (!this.context.containsKey(valueType)) {
			throw new IllegalStateException("No context value available for type " + valueType.getName());
		}
		return (T) this.context.get(valueType);
	}

	public <T> ProcessingContext set(T value) {
		if (value == null) {
			throw new IllegalArgumentException("Cannot set a null context value");
		}
		this.context.put(value.getClass(), value);
		return this;
	}

	public <T> void remove(Class<T> valueType) {
		if (valueType == null) {
			throw new IllegalArgumentException("Cannot remove a context value by a null value type");
		}
		this.context.remove(valueType);
	}

	public void clear() {
		this.context.clear();
	}
}
