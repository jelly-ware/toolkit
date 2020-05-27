package org.jellyware.toolkit;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.ProcessBean;

import org.jellyware.toolkit.annot.Eager;

public class CDI {
	public static class Proxyable<T> {
		T value;

		public static <T> Proxyable<T> of(T value) {
			Proxyable<T> proxyable = new Proxyable<>();
			proxyable.value = value;
			return proxyable;
		}

		public T get() {
			return value;
		}

		public Optional<T> toOptional() {
			return Optional.ofNullable(value);
		}
	}

	public static class EagerExtension implements javax.enterprise.inject.spi.Extension {
		private final Set<Bean<?>> beans = new LinkedHashSet<Bean<?>>();

		<X> void processBean(@Observes ProcessBean<X> event) {
			if (event.getAnnotated().isAnnotationPresent(Eager.class)
					&& event.getAnnotated().isAnnotationPresent(ApplicationScoped.class)) {
				beans.add(event.getBean());
			}
		}

		void afterDeploymentValidation(@Observes AfterDeploymentValidation event, BeanManager beanManager) {
			for (Bean<?> bean : beans) {
				// the call to toString() is a cheat to force the bean to be
				// initialized
				beanManager.getReference(bean, bean.getBeanClass(), beanManager.createCreationalContext(bean))
						.toString();
			}
		}
	}

	public static Class<?> original(Class<?> cls) {
		return cls.getSuperclass();
	}

	public static Class<?> original(Object obj) {
		return isProxy(obj) ? original(obj.getClass()) : obj.getClass();
	}

	public static boolean isProxy(Object obj) {
		try {
			return Class.forName("org.jboss.weld.bean.proxy.ProxyObject").isInstance(obj);
		} catch (Exception e) {
		}
		return false;
	}
}
