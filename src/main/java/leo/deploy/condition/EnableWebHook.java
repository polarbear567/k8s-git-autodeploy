package leo.deploy.condition;

import org.apache.commons.lang3.StringUtils;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @author Leo Li
 */
public class EnableWebHook implements Condition {
	private static final String AUTODEPLOY_TYPE = System.getenv("AUTODEPLOY_TYPE");
	@Override
	public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
		return StringUtils.equalsIgnoreCase("webhook", AUTODEPLOY_TYPE);
	}
}
