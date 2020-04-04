package com.mantledillusion.data.saman;

import java.util.Arrays;
import java.util.Map;
import java.util.WeakHashMap;

public class ProcessingContext {

	private final Map<String, Object> context = new WeakHashMap<>();

	private ProcessingContext() {}

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
		if (valueType == null) {
			throw new IllegalArgumentException("No context value available for a null value type");
		}
		return has(valueType.getName());
	}

	public <T> boolean has(String key) {
		return this.context.containsKey(key);
	}

	public <T> T get(Class<T> valueType) {
		if (valueType == null) {
			throw new IllegalArgumentException("No context value available for a null value type");
		}
		return get(valueType.getName());
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String key) {
		if (!this.context.containsKey(key)) {
			throw new IllegalStateException("No context value available for key " + key);
		}
		return (T) this.context.get(key);
	}

	public <T> T get(Class<T> valueType, T defaultValue) {
		if (valueType == null) {
			throw new IllegalArgumentException("No context value available for a null value type");
		}
		return get(valueType.getName(), defaultValue);
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String key, T defaultValue) {
		return this.context.containsKey(key) ? (T) this.context.get(key) : defaultValue;
	}

	public <T> ProcessingContext set(T value) {
		if (value != null) {
			return set(value.getClass().getName(), value, false);
		}
		return this;
	}

	public <T> ProcessingContext set(String key, T value) {
		return set(key, value, false);
	}

	public <T> ProcessingContext set(T value, boolean strict) {
		if (value == null) {
			if (strict) {
				throw new IllegalArgumentException("Cannot set a null context value");
			}
		} else {
			return set(value.getClass().getName(), value, strict);
		}
		return this;
	}

	public <T> ProcessingContext set(String key, T value, boolean strict) {
		if (key == null || value == null) {
			if (strict) {
				throw new IllegalArgumentException("Cannot set a null context value");
			}
		} else {
			this.context.put(key, value);
		}
		return this;
	}

	public <T> void remove(Class<T> valueType) {
		if (valueType == null) {
			throw new IllegalArgumentException("Cannot remove a context value by a null value type");
		}
		remove(valueType.getName());
	}

	public <T> void remove(String key) {
		this.context.remove(key);
	}

	public void clear() {
		this.context.clear();
	}
}
