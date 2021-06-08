package com.langyun.auto.factories;

import com.google.auto.service.AutoService;
import com.langyun.auto.annotation.AutoIgnore;
import com.langyun.auto.common.AbstractMicaProcessor;
import com.langyun.auto.common.BootAutoType;
import com.langyun.auto.common.MultiSetMap;


import javax.annotation.processing.*;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * spring boot 自动配置处理器
 *
* @author langyun
 * @date 2021年06月09日 0:31
 */
@AutoService(Processor.class)
@SupportedAnnotationTypes("*")
@SupportedOptions("debug")
public class AutoFactoriesProcessor extends AbstractMicaProcessor {
	/**
	 * 处理的注解 @FeignClient
	 */
	private static final String FEIGN_CLIENT_ANNOTATION = "org.springframework.cloud.openfeign.FeignClient";
	/**
	 * Feign 自动配置
	 */
	private static final String FEIGN_AUTO_CONFIGURE_KEY = "net.dreamlu.mica.feign.MicaFeignAutoConfiguration";
	/**
	 * The location to look for factories.
	 * <p>Can be present in multiple JAR files.
	 */
	private static final String FACTORIES_RESOURCE_LOCATION = "META-INF/spring.factories";
	/**
	 * devtools，有 Configuration 注解的 jar 一般需要 devtools 配置文件
	 */
	private static final String DEVTOOLS_RESOURCE_LOCATION = "META-INF/spring-devtools.properties";
	/**
	 * 数据承载
	 */
	private final MultiSetMap<String, String> factories = new MultiSetMap<>();
	/**
	 * 元素辅助类
	 */
	private Elements elementUtils;

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		elementUtils = processingEnv.getElementUtils();
	}

	@Override
	protected boolean processImpl(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		if (roundEnv.processingOver()) {
			generateFactoriesFiles();
		} else {
			processAnnotations(annotations, roundEnv);
		}
		return false;
	}

	private void processAnnotations(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		// 日志 打印信息 gradle build --debug
		log(annotations.toString());
		Set<? extends Element> elementSet = roundEnv.getRootElements();
		log("All Element set: " + elementSet.toString());

		// 过滤 TypeElement
		Set<TypeElement> typeElementSet = elementSet.stream()
			.filter(this::isClassOrInterface)
			.filter(e -> e instanceof TypeElement)
			.map(e -> (TypeElement) e)
			.collect(Collectors.toSet());
		// 如果为空直接跳出
		if (typeElementSet.isEmpty()) {
			log("Annotations elementSet is isEmpty");
			return;
		}

		for (TypeElement typeElement : typeElementSet) {
			// ignore @AutoIgnore Element
			if (isAnnotation(elementUtils, typeElement, AutoIgnore.class.getName())) {
				log("Found @AutoIgnore annotation，ignore Element: " + typeElement.toString());
			} else if (isAnnotation(elementUtils, typeElement, FEIGN_CLIENT_ANNOTATION)) {
				log("Found @FeignClient Element: " + typeElement.toString());

				ElementKind elementKind = typeElement.getKind();
				// Feign Client 只处理 接口
				if (ElementKind.INTERFACE != elementKind) {
					fatalError("@FeignClient Element " + typeElement + " 不是接口。");
					continue;
				}

				String factoryName = typeElement.getQualifiedName().toString();
				if (factories.containsVal(factoryName)) {
					continue;
				}

				log("读取到新配置 spring.factories factoryName：" + factoryName);
				factories.put(FEIGN_AUTO_CONFIGURE_KEY, factoryName);
			} else {
				for (BootAutoType autoType : BootAutoType.values()) {
					String annotation = autoType.getAnnotation();
					if (isAnnotation(elementUtils, typeElement, annotation)) {
						log("Found @" + annotation + " Element: " + typeElement.toString());

						String factoryName = typeElement.getQualifiedName().toString();
						if (factories.containsVal(factoryName)) {
							continue;
						}

						log("读取到新配置 spring.factories factoryName：" + factoryName);
						factories.put(autoType.getConfigureKey(), factoryName);
					}
				}
			}
		}
	}

	private void generateFactoriesFiles() {
		if (factories.isEmpty()) {
			return;
		}
		Filer filer = processingEnv.getFiler();
		try {
			// 1. spring.factories
			FileObject factoriesFile = filer.createResource(StandardLocation.CLASS_OUTPUT, "", FACTORIES_RESOURCE_LOCATION);
			FactoriesFiles.writeFactoriesFile(factories, factoriesFile.openOutputStream());
			String classesPath = factoriesFile.toUri().toString().split("classes")[0];
			Path projectPath = Paths.get(new URI(classesPath)).getParent();
			// 2. devtools 配置，因为有 @Configuration 注解的需要 devtools
			String projectName = projectPath.getFileName().toString();
			FileObject devToolsFile = filer.createResource(StandardLocation.CLASS_OUTPUT, "", DEVTOOLS_RESOURCE_LOCATION);
			FactoriesFiles.writeDevToolsFile(projectName, devToolsFile.openOutputStream());
		} catch (IOException | URISyntaxException e) {
			fatalError(e);
		}
	}
}
