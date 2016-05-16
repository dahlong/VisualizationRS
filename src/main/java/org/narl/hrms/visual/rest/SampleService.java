package org.narl.hrms.visual.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.narl.hrms.visual.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;

@Path("rest")
public class SampleService {

//	private static final Queue<String> ITEMS = new ConcurrentLinkedQueue<String>();
	
//    private static final SseBroadcaster BROADCASTER = new SseBroadcaster();
    
	@Autowired
	TestService test ;

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("sayHello")
	public String HelloWorld2(){
		return "Hello World !!" ;
	}
	
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("/sayHelloByName/{name}")
	public String HelloWorld(@PathParam("name") String name){
		return test.sayHi(name);
		//return "Hello World, "+name ;
	}
	
	@POST
	@Path("postTest")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//	@Consumes(MediaType.MULTIPART_FORM_DATA)
//	@Produces(MediaType.TEXT_HTML) //有中文 return不行
	@Produces(MediaType.TEXT_XML)
	public String postMethod(@FormParam("yearmonth") String yearmonth, @FormParam("org") String org ) {
	  return "<h2>Hello, post (年-月)= " + yearmonth + ", 組織=" +org +"</h2>";
	}
	
	
	
//	@GET
//	@Path("sse")
//    @Produces(SseFeature.SERVER_SENT_EVENTS)
//    public EventOutput getServerSentEvents() {
//        final EventOutput eventOutput = new EventOutput();
//	        new Thread(new Runnable() {
//	            public void run() {
//	                try {
//	                    for (int i = 0; i < 10; i++) {
//	                        final OutboundEvent.Builder eventBuilder  = new OutboundEvent.Builder();
//	                        eventBuilder.name("message-to-client");
//	                        eventBuilder.data(String.class, "Hello world " + i + "!");
//	                        final OutboundEvent event = eventBuilder.build();
//	                        eventOutput.write(event);
//	                    }
//	                } catch (IOException e) {
//	                    throw new RuntimeException( "Error when writing the event.", e);
//	                    
//	                } finally {
//	                    try {
//	                        eventOutput.close();
//	                    } catch (IOException ioClose) {
//	                        throw new RuntimeException( "Error when closing the event output.", ioClose);
//	                    }
//	                }
//	            }
//	        }).start();
//        return eventOutput;
//    }
	

	
//	@GET
//    @Produces(MediaType.TEXT_PLAIN)
//    public String listItems() {
//        return ITEMS.toString();
//    }
//
//    @GET
//    @Path("events")
//    @Produces(SseFeature.SERVER_SENT_EVENTS)
//    public EventOutput itemEvents() {
//        final EventOutput eventOutput = new EventOutput();
//        BROADCASTER.add(eventOutput);
//        return eventOutput;
//    }
//
//    @POST
//    @Path("eventspost")
//    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//    public void addItem(@FormParam("name") String name) {
//        ITEMS.add(name);
//        BROADCASTER.broadcast(new OutboundEvent.Builder().data(String.class, name).build());
//        BROADCASTER.broadcast(new OutboundEvent.Builder().name("size").data(Integer.class, ITEMS.size()).build());
//    }
}


