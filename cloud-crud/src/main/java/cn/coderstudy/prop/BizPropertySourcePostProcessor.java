package cn.coderstudy.prop;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author liujun
 * @version 1.0
 * @description: BizPropretySourcePostProcessor
 * @date 2023/2/13 9:16 下午
 */
@Slf4j
public class BizPropertySourcePostProcessor implements BeanFactoryPostProcessor, InitializingBean, Ordered {
    private final ResourceLoader resourceLoader;

    private final List<PropertySourceLoader> propertySourceLoaders;

    public BizPropertySourcePostProcessor() {
        this.resourceLoader = new DefaultResourceLoader();
        this.propertySourceLoaders = SpringFactoriesLoader.loadFactories(PropertySourceLoader.class, getClass().getClassLoader());
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("BladePropertySourcePostProcessor init.");
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        log.info("BizProcessSourceProcessor process");
        Map<String, Object> beanEntryMap = beanFactory.getBeansWithAnnotation(BizPropertySource.class);

        Set<Map.Entry<String, Object>> entries = beanEntryMap.entrySet();
        if (entries.isEmpty()) {
            log.warn("Not found @BladePropertySource on spring bean class.");
            return;
        }

        List<PropertyFile> propertyFiles = new ArrayList<>();

        for (Map.Entry<String, Object> entry : entries) {
            Class<?> userClass = ClassUtils.getUserClass(entry.getValue());
            BizPropertySource bizPropertySource = AnnotationUtils.getAnnotation(userClass, BizPropertySource.class);
            if (bizPropertySource == null) {
                continue;
            }

            String value = bizPropertySource.value();
            boolean isLoadActive = bizPropertySource.loadActiveProfile();
            int order = bizPropertySource.order();
            propertyFiles.add(new PropertyFile(order, value, isLoadActive));
        }

        List<PropertyFile> soredPropertyFiles = propertyFiles.stream().distinct().sorted().collect(Collectors.toList());

        ConfigurableEnvironment environment = beanFactory.getBean(ConfigurableEnvironment.class);
        MutablePropertySources propertySources = environment.getPropertySources();
        String[] activeProfiles = environment.getActiveProfiles();

        HashMap<String, PropertySourceLoader> loaderMap = new HashMap<String, PropertySourceLoader>();
        for (PropertySourceLoader propertySourceLoader : propertySourceLoaders) {
            String[] fileExtensions = propertySourceLoader.getFileExtensions();
            for (String fileExtension : fileExtensions) {
                loaderMap.put(fileExtension, propertySourceLoader);
            }
        }

        List<PropertySource> sourceArrayList = new ArrayList<>();
        //加载activeProfile文件
        for (PropertyFile soredPropertyFile : soredPropertyFiles) {
            String extension = soredPropertyFile.getExtension();

            PropertySourceLoader loader = loaderMap.get(extension);
            if (loader == null) {
                throw new IllegalArgumentException("Can't find PropertySourceLoader for PropertySource extension:" + extension);
            }

            String location = soredPropertyFile.getLocation();
            Resource baseResource = resourceLoader.getResource(location);
            loadPropertySource(sourceArrayList, loader, location, baseResource);

            for (String activeProfile : activeProfiles) {
                if (soredPropertyFile.loadActiveProfile) {
                    String fileName = StringUtils.stripFilenameExtension(location);
                    String filePath = fileName + "-" + activeProfile + "." + extension;
                    Resource resource = resourceLoader.getResource(filePath);
                    loadPropertySource(sourceArrayList, loader, location, resource);
                }
            }
        }

        for (PropertySource propertySource : sourceArrayList) {
            propertySources.addLast(propertySource);
        }
    }


    private void loadPropertySource(List<PropertySource> sourceArrayList, PropertySourceLoader loader, String location, Resource resource) {
        if (resource.exists()) {
            String name = "bizPropertySource: [" + location + "]";
            try {
                sourceArrayList.addAll(loader.load(name, resource));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }


    @Getter
    @ToString
    @EqualsAndHashCode
    private static class PropertyFile implements Comparable<PropertyFile> {
        private final int order;
        private final String location;
        private final String extension;
        private final boolean loadActiveProfile;

        PropertyFile(int order, String location, boolean loadActiveProfile) {
            this.order = order;
            this.location = location;
            this.loadActiveProfile = loadActiveProfile;
            this.extension = Objects.requireNonNull(StringUtils.getFilenameExtension(location));
        }

        @Override
        public int compareTo(PropertyFile other) {
            return Integer.compare(this.order, other.order);
        }
    }
}
