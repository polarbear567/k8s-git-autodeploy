package leo.deploy.controller;

import leo.deploy.condition.EnableWebHook;
import leo.deploy.config.DeployQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Leo Li
 */
@RestController
@Conditional(EnableWebHook.class)
public class WebHookController {

	@Autowired
	private DeployQueue deployQueue;

	@RequestMapping("/receive")
	public void update(@RequestBody String body) {
		deployQueue.put(body);
	}
}
