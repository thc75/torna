package cn.torna.swaggerplugin.scaner;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.ClassUtils;

/**
 * class扫描
 * @author tanghc
 */
public class ClassScanner {
	
	private static final String RESOURCE_PATTERN = "/**/*.class";

	private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

	private List<String> packagesList = new LinkedList<String>();

	private List<TypeFilter> typeIncludes = new LinkedList<TypeFilter>();

	private Set<Class<?>> classSet = new HashSet<Class<?>>();

	public ClassScanner(String[] entityPackage) {
		this(entityPackage, null);
	}

	@SuppressWarnings("unchecked")
    public ClassScanner(String[] entityPackage, Class<?> scanClass) {
		for (String packagePath : entityPackage) {
			this.packagesList.add(packagePath);
		}
		if (scanClass != null) {
			// scanClass是注解类型
			if(scanClass.isAnnotation()) {
				// AnnotationTypeFilter 有scanClass注解的类将被过滤出来,不能过滤接口
				typeIncludes.add(new AnnotationTypeFilter((Class<? extends Annotation>) scanClass, false));
			}else {
				// AssignableTypeFilter 继承或实现superClass的类将被过滤出来
				// superClass可以是接口
				typeIncludes.add(new AssignableTypeFilter(scanClass));
			}
		}
	}
	
	
	
	/**
	 * 将符合条件的Bean以Class集合的形式返回
	 * 
	 * @return 返回Mapper的class集合
	 * @throws IOException IO异常
	 * @throws ClassNotFoundException 文件找不到异常
	 */
	public Set<Class<?>> getClassSet() throws IOException, ClassNotFoundException {
		this.classSet.clear();
		if (!this.packagesList.isEmpty()) {
			for (String pkg : this.packagesList) {
				// classpath*:com/xx/dao/**/*.class
				String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
						+ ClassUtils.convertClassNameToResourcePath(pkg) + RESOURCE_PATTERN;
				Resource[] resources = this.resourcePatternResolver.getResources(pattern);
				MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(this.resourcePatternResolver);
				for (Resource resource : resources) {
					if (resource.isReadable()) {
						MetadataReader reader = readerFactory.getMetadataReader(resource);
						if (matchesEntityTypeFilter(reader, readerFactory)) {
							String className = reader.getClassMetadata().getClassName();
							this.classSet.add(Class.forName(className));
						}
					}
				}
			}
		}
		return this.classSet;
	}

	/**
	 * 检查当前扫描到的Bean含有任何一个指定的注解标记
	 * 
	 * @param reader
	 * @param readerFactory
	 * @return 返回true表示它是一个Mapper
	 * @throws IOException
	 */
	private boolean matchesEntityTypeFilter(MetadataReader reader, MetadataReaderFactory readerFactory)
			throws IOException {
		if (!this.typeIncludes.isEmpty()) {
			for (TypeFilter filter : this.typeIncludes) {
				if (filter.match(reader, readerFactory)) {
					return true;
				}
			}
		}
		return false;
	}
}
