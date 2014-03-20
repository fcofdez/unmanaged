package extensions; /**
 * Created by fran on 20/03/14.
 */
import java.nio.charset.Charset;

import javax.ws.rs.*;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.neo4j.graphdb.*;


//START SNIPPET: extensions.HelloWorldResource
@Path( "/helloworld" )
public class HelloWorldResource
{
    private static enum RelTypes implements RelationshipType
    {
        KNOWS
    }

    private final GraphDatabaseService database;

    public HelloWorldResource( @Context GraphDatabaseService database )
    {
        this.database = database;
    }

    @GET
    @Produces( MediaType.TEXT_PLAIN )
    @Path( "/{nodeId}" )
    public Response hello( @PathParam( "nodeId" ) long nodeId )
    {

        return Response.status( Status.OK ).entity(
                ("Hello World, nodeId=" + nodeId).getBytes( Charset.forName("UTF-8") ) ).build();
    }

    @Path("/{nodeId}")
    @POST
    public Response createUser( @PathParam("nodeId") long nodeId,
                                @DefaultValue("a") @QueryParam("nodea") String nodea,
                                @DefaultValue("b") @QueryParam("nodeb") String nodeb)
    {
        try ( Transaction tx = this.database.beginTx() )
        {

            Node firstNode, secondNode;
            Relationship relationship;
            Label msg = DynamicLabel.label("MESSAGE");


            firstNode = this.database.createNode();
            firstNode.setProperty( "message", nodea );
            firstNode.addLabel(msg);
            secondNode = this.database.createNode();
            secondNode.addLabel(msg);
            secondNode.setProperty( "message", nodeb );

            relationship = firstNode.createRelationshipTo( secondNode, RelTypes.KNOWS );
            relationship.setProperty( "message", "brave Neo4j " );

            tx.success();
        }
        return Response.status( Status.OK ).build();
    }

}
