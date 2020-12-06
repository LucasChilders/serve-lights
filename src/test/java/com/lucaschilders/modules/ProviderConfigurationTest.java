package com.lucaschilders.modules;

import com.google.common.collect.Sets;
import com.lucaschilders.providers.Provider;
import com.lucaschilders.util.ProviderName;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Verify that the Guice injection has been setup correctly for each provider.
 *
 * TODO these tests are gross
 */
@RunWith(MockitoJUnitRunner.class)
public class ProviderConfigurationTest {

    private static final String GET_PROVIDERS = "getProviders";

    @Test
    public void test_providerName_none_are_null() {
        for (final ProviderName provider : ProviderName.values()) {
            if (!provider.equals(ProviderName.GLOBAL)) {
                if (provider.getClazz() == null) {
                    fail(String.format("Provider [%s] was missing a class to provide.", provider.getName()));
                }
            }
        }
    }

    @Test
    public void test_getProviders_exists() {
        try {
            getGetProvidersMethod();
        } catch (final Exception e) {
            fail(String.format("Could not find the [%s] method.", GET_PROVIDERS));
        }
    }

    @Test
    public void test_getProviders_correctArgumentCount() {
        Method method = null;
        try {
            method = getGetProvidersMethod();
        } catch (final Exception e) {
            fail(String.format("Could not find the [%s] method.", GET_PROVIDERS));
        }

        assertEquals(getConfiguredProviders().size(), method.getParameterCount());
    }

    @Test
    public void test_getProviders_correct_return_count() throws InvocationTargetException, IllegalAccessException {
        final Set<Class<?>> configuredProviders = getConfiguredProviders();

        Method method = null;
        try {
            method = getGetProvidersMethod();
        } catch (final Exception e) {
            fail(String.format("Could not find the [%s] method.", GET_PROVIDERS));
        }

        Object[] params = new Object[method.getParameterCount()];
        for (int i = 0; i < method.getParameterCount(); i++) {
            params[i] = null;
        }
        final HashMap<ProviderName, Provider<?, ?>> resp = (HashMap<ProviderName, Provider<?, ?>>) method.invoke(new LightModule(), params);
        assertEquals(configuredProviders.size(), resp.values().size());
    }

    @Test
    public void test_allProviders_have_configuration_guice_providers() {
        final Set<Class<?>> configuredProviders = getConfiguredProviders();
        int count = 0;
        for (final Class<?> clazz : configuredProviders) {
            boolean found = false;
            for (final Method method : ConfigModule.class.getDeclaredMethods()) {
                if (method.getName().equalsIgnoreCase(String.format("get%sConfig", clazz.getSimpleName()))) {
                    count++;
                    found = true;
                    break;
                }
            }
            if (!found) {
                fail(String.format("Couldn't find an @provides method called get%sConfig in com.lucaschilders.modules.ConfigModule!", clazz));
            }
        }
        assertEquals(count, configuredProviders.size());
    }

    private Set<Class<?>> getConfiguredProviders() {
        final Set<Class<?>> configuredProviders = Sets.newHashSet();
        for (final ProviderName provider : ProviderName.values()) {
            if (!provider.equals(ProviderName.GLOBAL)) {
                if (provider.getClazz() != null) {
                    configuredProviders.add(provider.getClazz());
                }
            }
        }
        return configuredProviders;
    }

    private Method getGetProvidersMethod() throws Exception {
        final Class<?> base = Class.forName("com.lucaschilders.modules.LightModule");
        for (final Method method : base.getDeclaredMethods()) {
            if (method.getName().equalsIgnoreCase(GET_PROVIDERS)) {
                return method;
            }
        }
        throw new Exception(String.format("Could not find the [%s] method!", GET_PROVIDERS));
    }
}
