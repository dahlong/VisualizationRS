package org.narl.hrms.visual.rest;

import static org.junit.Assert.*;

import javax.ws.rs.core.Application;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;
import org.narl.hrms.visual.rest.SampleService;

public class SampleRESTJerseyTest extends JerseyTest {

	@Override
    protected Application configure() {
        return new ResourceConfig(SampleService.class);
    }
	
	@Test
	public void test() {
		final String hello = target("/rest/hello/sayHello").request().get(String.class);
		assertEquals("Hello World !!", hello);
	}
	
}
