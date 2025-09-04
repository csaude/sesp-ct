package org.openmrs.module.sespct.web.controller;

import org.openmrs.module.sespct.registration.ClientRegistrationService;
import org.openmrs.module.sespct.webhooks.WebhookSubscriptionService;
import org.openmrs.module.sespct.oauth.OAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/sespct/admin/ct")
public class CtAdminController {
	
	private static final Logger log = LoggerFactory.getLogger(CtAdminController.class);
	
	@Autowired
	private ClientRegistrationService clientRegistration;
	
	@Autowired
	private WebhookSubscriptionService webhookSubscription;
	
	@Autowired
	private OAuthService oauth;
	
	/** POST /openmrs/sespct/admin/ct/subscribe → creates webhook subscription in CT */
	@RequestMapping(value = "/subscribe", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> subscribe() {
		try {
			String id = webhookSubscription.createSubscription();
			return new ResponseEntity<Map<String, Object>>(Collections.<String, Object> singletonMap("subscriptionId", id),
			        HttpStatus.OK);
		}
		catch (Exception e) {
			log.error("Failed to create CT webhook subscription", e);
			Map<String, Object> err = new HashMap<String, Object>();
			err.put("error", "subscribe_failed");
			err.put("message", e.getMessage());
			return new ResponseEntity<Map<String, Object>>(err, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/** POST /openmrs/sespct/admin/ct/unsubscribe?id=SUB_ID → deletes webhook subscription in CT */
	@RequestMapping(value = "/unsubscribe", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> unsubscribe(@RequestParam("id") String id) {
		try {
			webhookSubscription.deleteSubscription(id);
			return new ResponseEntity<Map<String, Object>>(Collections.<String, Object> singletonMap("status", "ok"),
			        HttpStatus.OK);
		}
		catch (Exception e) {
			log.error("Failed to delete CT webhook subscription {}", id, e);
			Map<String, Object> err = new HashMap<String, Object>();
			err.put("error", "unsubscribe_failed");
			err.put("message", e.getMessage());
			return new ResponseEntity<Map<String, Object>>(err, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
