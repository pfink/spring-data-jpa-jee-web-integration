package de.pfink.spring.data.jpa.el;

import biz.paluch.jee.commons.BeanLookup;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.el.ELContext;
import javax.el.ELException;
import javax.enterprise.inject.spi.Bean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.util.StringUtils;

/**
 * Unfortunately, there is no support for the @Named annotation when using
 * Spring Data JPA with CDI. This ELResolver adds default EL names for each
 * repository. Each repository does have it's simple class name as it's EL name.
 * So the EL Name UserRepository woud be 'userRepository' ('UserRepository' is also valid)
 * 
 * Please consider the following constraints:
 * - All repository class names must end with "Repository".
 * - All EL expressions ending with "Repository" are reserved and should not
 * be used for other purposes.
 * - Multiple repositories with the same class name in different packages
 * are not supported
 * 
 * @author Patrick Fink
 */
@Slf4j
public class SpringDataJpaRepositoryElResolver extends BaseExpressionReadOnlyElResolver {
    private final Map<String, Class<? extends JpaRepository>> repositoryTypeCache = new HashMap();

    @Override
    public boolean isResponsible(ELContext elContext, String expression) {
        return expression.endsWith("Repository");
    }

    @Override
    public Object resolve(ELContext elContext, String expression) {
        String simpleClassName = resolveClassName(expression);
        Class<? extends JpaRepository> springDataRepositoryClass = resolveRepositoryClass(simpleClassName);        
        return BeanLookup.lookupBean(springDataRepositoryClass);
    }        

    private String resolveClassName(String expression) {        
        return StringUtils.capitalize(expression);
    }
    
    private Class<? extends JpaRepository> resolveRepositoryClass(String simpleClassName) {
        if(!repositoryTypeCache.containsKey(simpleClassName)) {
            refreshRepositoryTypeCache();
        }
        Class<? extends JpaRepository> repositoryClass = repositoryTypeCache.get(simpleClassName);
        if(repositoryClass == null)
            throw new ELException("Cannot resolve FQDN for repository class '"+simpleClassName+"'");
        return repositoryClass;
    }
    
    private void refreshRepositoryTypeCache() {
        repositoryTypeCache.clear();
        Set<Bean<?>> repositoryBeans = BeanLookup.beanManager().getBeans(JpaRepository.class);        
        for(Bean bean : repositoryBeans) {
            repositoryTypeCache.put(bean.getBeanClass().getSimpleName(), bean.getBeanClass());
        }
        log.debug("Found repositories: {}"+repositoryTypeCache);
        if(repositoryTypeCache.isEmpty())
            throw new ELException("Did not find any SpringDataJpa repositories!");
    }
}
