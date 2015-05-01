package de.pfink.spring.data.jpa.el;

import java.beans.FeatureDescriptor;
import java.util.Iterator;
import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ELResolver;

/**
 * Base class for ELResolvers that just resolve read-only base expressions like #{mybean}
 * @author Patrick Fink
 */
public abstract class BaseExpressionReadOnlyElResolver extends ELResolver {
        
        public abstract boolean isResponsible(ELContext elContext, String expression);
        public abstract Object resolve(ELContext elContext, String expression);

	@Override
	public Object getValue(ELContext elContext, Object base, Object property) throws ELException {
            return resolveIfResponsible(elContext, base, property);
	}

	@Override
	public Class<?> getType(ELContext elContext, Object base, Object property) throws ELException {
            Object o = resolveIfResponsible(elContext, base, property);
            if(o != null)
                return o.getClass();
            return null;
	}
        
        @Override
        public void setValue(ELContext context, Object base, Object property, Object value) {}


	@Override
	public boolean isReadOnly(ELContext elContext, Object base, Object property) throws ELException {
            return true;
	}

	@Override
	public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext elContext, Object base) {
            return null;
	}

	@Override
	public Class<?> getCommonPropertyType(ELContext elContext, Object base) {
            return Object.class;
	}
        
        private Object resolveIfResponsible(ELContext elContext, Object base, Object property) {
            return resolveIfResponsible(elContext, base, property.toString());
        }
        
        private Object resolveIfResponsible(ELContext elContext, Object base, String expression) {
            if (base == null) {
                if(isResponsible(elContext, expression)) {
                    return resolveAndThrowErrorIfNull(elContext, expression);
                }
            }
            return null;
        } 
        
        private Object resolveAndThrowErrorIfNull(ELContext elContext, String expression) {
            elContext.setPropertyResolved(true);
            Object object = resolve(elContext, expression);
            if(object == null)
                throw new ELException("Resolving of expression '"+expression+"' failed!");
            return object;
        }
}
