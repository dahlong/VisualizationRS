package org.narl.hrms.visual.rest;

import static org.junit.Assert.*;
import javax.ws.rs.core.Application;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

public class SampleRESTJerseyTest extends JerseyTest {

	@Override
    protected Application configure() {
        return new ResourceConfig(SampleService.class);
    }
	
	@Test
	public void test1() {
		final String hello = target("/rest/sayHello").request().get(String.class);
		assertEquals("Hello World !!", hello);
	}
	
	@Test
	public void test2() {
		final String hello = target("/rest/sayHelloByName/James").request().get(String.class);
		assertEquals("Hello World, James", hello);
	}
}
