package luna.tmm.rosettastone.application.resources;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import luna.tmm.rosettastone.utils.Constants;
import luna.tmm.rosettastone.utils.RSID;
import luna.tmm.rosettastone.utils.RetObject;
import luna.tmm.rosettastone.utils.TMMUtils;
import luna.tmm.rosettastone.utils.WebAPI;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.server.mvc.Viewable;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

@Path("auth")
public class Authorization {
	
	protected final static Logger logger = LogManager.getLogger(Authorization.class);
	protected static ObjectMapper objectMapper = new ObjectMapper();
	
	private final WebAPI webApi = new WebAPI();
	
	/**
	 * From Launchpad to TMM
	 * @param tokenId
	 * @return
	 * @throws  
	 */
	@GET
    @Produces(MediaType.TEXT_HTML)
    public Response auth(@PathParam("token") String tokenParam,
    		@CookieParam("RSID") String rsidcookie)  {
		RSID rsid = TMMUtils.getUserToken(rsidcookie);
		
		try{
		  URI errPage = URI.create(Constants.ERROR_URL); 
		  
		  //Get token and userid from cookies
	        logger.info("------Login RSID-------" + rsid.toString());
	        if (StringUtils.isEmpty(rsid.getAccess_token())){
	            logger.error("access_token is null...");
	            //return new Viewable(targetPage, getRetMap(RetObject.ERROR));
	          return Response.seeOther(errPage)
	                    .build();   
	        }
	        
	        try {
	            String authHeader = TMMUtils.setTokenBearer(rsid.getAccess_token() );
	            //Fetch userid
	            if (StringUtils.isEmpty(rsid.getUserId() )){
	                logger.log(Level.DEBUG, "----Get Token----" + Constants.AUTH_URL + rsid.getAccess_token() );
	                RetObject token = webApi.call(Constants.AUTH_URL + rsid.getAccess_token());
	                logger.debug("---token---"+token.toString());
	                
	                Map tokenMap = objectMapper.readValue(token.getData(), Map.class);
	                rsid.setUserId((String) tokenMap.get("userId") );
	            }
	            
	            if ( !StringUtils.isEmpty(rsid.getUserId() ) ){
	                //Get user info
	                logger.log(Level.DEBUG, "----Get User Info----" + Constants.SCHOLAR_URL + rsid.getUserId());
	                RetObject scholarInfo = webApi.callMethod("GET", Constants.SCHOLAR_URL + rsid.getUserId(),
	                        authHeader );
	                logger.debug("---user info---"+scholarInfo.toString());
	                Map userMap = objectMapper.readValue(scholarInfo.getData(), Map.class);
	                
	                //Get org info
	                logger.debug("----Get Organization Info----" + Constants.ORG_URL + userMap.get("organization"));
	                RetObject orgInfo = webApi.callMethod("GET", Constants.ORG_URL + userMap.get("organization"),authHeader);
	                logger.debug("---organization info---"+orgInfo.toString());
	                Map orgMap = objectMapper.readValue(orgInfo.getData(), Map.class);
	                
	                String username = (String)userMap.get("userField6");//User name
	                Object rsaServers = orgMap.get("rsaServers");//RSAServers
	                
//	                return new Viewable(targetPage, getRetMap(RetObject.OK, "user", scholarInfo.getData(),
//	                      "org", orgInfo.getData(), 
//	                      "tmmurl", TMMUtils.getTMMTargetServer(rsaServers) 
//	                              + TMMUtils.getTMMParamQuery(username)) );
	                return Response.seeOther(new URI(TMMUtils.getTMMTargetServer(rsaServers) 
	                      + TMMUtils.getTMMParamQuery(username)))
	                        .build(); 
	            } else {
	                logger.error("---User Id is NULL---");
	                //return new Viewable(targetPage, getRetMap(RetObject.ERROR));
	                return Response.seeOther(errPage)
	                        .build();     
	            }
	        } catch (IOException|URISyntaxException e) {
	            logger.error(e.getMessage(), e);
	            //return new Viewable(targetPage, getRetMap(RetObject.ERROR));
	            return Response.seeOther(errPage)
                        .build();   
	        }
		}catch (Exception e){
		    return Response.status(500).entity("Could not find address, please retry!")
		                .build();
		}
		
    }
	
	/**
	 * For cookies test when not in same domains
	 * @param rsid
	 * @return
	 * @throws URISyntaxException
	 */
	@POST
	@Path("cookie")
	@Consumes("application/x-www-form-urlencoded")
	@Produces(MediaType.TEXT_HTML)
	public Response setCookies(@FormParam("rsid") String rsid) throws URISyntaxException{
		logger.debug(rsid);
		return Response.seeOther(new URI("auth/"))
				.cookie(new NewCookie("RSID", rsid)).build();
	}
	
	/**
	 * For test when error happen
	 * @param msg
	 * @return
	 */
	@GET
	@Path("/error")
	@Produces(MediaType.TEXT_HTML)
	public Viewable errorPage(@QueryParam("msg") String msg){
		return new Viewable("/pages/error", TMMUtils.getRetMap("1", "msg", msg));
	}
	
	@PUT
	@Path("/test")
	@Consumes("application/x-www-form-urlencoded")
//	@Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    public String test(@FormParam("data") String data) {
        System.out.println("------test-------" + data);
        return data;
    }
	
	@GET
	@Path("/test2")
	@Produces(MediaType.TEXT_HTML)
	public Viewable test2(){
		return new Viewable("/mock/mock");
//		return new Viewable("/pages/error");
	}
	
	
}
