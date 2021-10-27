/*
 * Copyright 2020-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.graphql.boot.test;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.OverrideAutoConfiguration;
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;
import org.springframework.boot.test.autoconfigure.filter.TypeExcludeFilters;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.annotation.AliasFor;
import org.springframework.core.env.Environment;
import org.springframework.graphql.boot.test.tester.AutoConfigureGraphQlTester;
import org.springframework.graphql.boot.test.tester.AutoConfigureWebGraphQlTester;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Annotation that can be used for a Spring GraphQL test that focuses
 * <strong>only</strong> on Spring GraphQL components, without involving web frameworks.
 * <p>
 * Using this annotation will disable full auto-configuration and instead apply only
 * configuration relevant to GraphQL tests (i.e. {@code @Controller},
 * {@code @JsonComponent}, {@code Converter}/{@code GenericConverter}
 * and {@code RuntimeWiringConfigurer} beans but not
 * {@code WebInterceptor}, {@code @Component}, {@code @Service} or {@code @Repository} beans).
 * <p>
 * By default, tests annotated with {@code @GraphQlTest} will also auto-configure a
 * {@link org.springframework.graphql.test.tester.GraphQlTester}. For more fine-grained control of GraphQlTester the
 * {@link AutoConfigureGraphQlTester @AutoConfigureGraphQlTester} annotation can be used.
 * <p>
 * Typically {@code @GraphQlTest} is used in combination with
 * {@link org.springframework.boot.test.mock.mockito.MockBean @MockBean} or
 * {@link org.springframework.context.annotation.Import @Import} to create any collaborators required by your
 * {@code @Controller} beans.
 * <p>
 * If you are looking to load your full application configuration and use {@code WebGraphQlTester},
 * you should consider {@link org.springframework.boot.test.context.SpringBootTest @SpringBootTest} combined with
 * {@link AutoConfigureWebGraphQlTester @AutoConfigureWebGraphQlTester} rather than this
 * annotation.
 *
 * @author Brian Clozel
 * @since 1.0.0
 * @see AutoConfigureGraphQlTester
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@BootstrapWith(GraphQlTestContextBootstrapper.class)
@ExtendWith(SpringExtension.class)
@OverrideAutoConfiguration(enabled = false)
@TypeExcludeFilters(GraphQlTypeExcludeFilter.class)
@AutoConfigureCache
@AutoConfigureJson
@AutoConfigureGraphQl
@AutoConfigureGraphQlTester
@ImportAutoConfiguration
public @interface GraphQlTest {

	/**
	 * Properties in form {@literal key=value} that should be added to the Spring
	 * {@link Environment} before the test runs.
	 * @return the properties to add
	 */
	String[] properties() default {};

	/**
	 * Specifies the controllers to test. This is an alias of {@link #controllers()} which
	 * can be used for brevity if no other attributes are defined. See
	 * {@link #controllers()} for details.
	 * @see #controllers()
	 * @return the controllers to test
	 */
	@AliasFor("controllers")
	Class<?>[] value() default {};

	/**
	 * Specifies the controllers to test. May be left blank if all {@code @Controller}
	 * beans should be added to the application context.
	 * @see #value()
	 * @return the controllers to test
	 */
	@AliasFor("value")
	Class<?>[] controllers() default {};

	/**
	 * Determines if default filtering should be used with
	 * {@link SpringBootApplication @SpringBootApplication}. By default, only
	 * {@code @Controller} (when no explicit {@link #controllers() controllers} are
	 * defined), {@code @ControllerAdvice}, {@code WebInterceptor}
	 * and {@code RuntimeWiringConfigurer} beans are
	 * included.
	 * @see #includeFilters()
	 * @see #excludeFilters()
	 * @return if default filters should be used
	 */
	boolean useDefaultFilters() default true;

	/**
	 * A set of include filters which can be used to add otherwise filtered beans to the
	 * application context.
	 * @return include filters to apply
	 */
	ComponentScan.Filter[] includeFilters() default {};

	/**
	 * A set of exclude filters which can be used to filter beans that would otherwise be
	 * added to the application context.
	 * @return exclude filters to apply
	 */
	ComponentScan.Filter[] excludeFilters() default {};

	/**
	 * Auto-configuration exclusions that should be applied for this test.
	 * @return auto-configuration exclusions to apply
	 */
	@AliasFor(annotation = ImportAutoConfiguration.class, attribute = "exclude")
	Class<?>[] excludeAutoConfiguration() default {};
}