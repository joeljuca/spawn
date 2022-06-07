package io.eigr.spawn;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertTrue;

import akka.NotUsed;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.Behaviors;
import akka.grpc.GrpcClientSettings;
import akka.stream.javadsl.AsPublisher;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import io.eigr.functions.protocol.ActorServiceClient;
import io.eigr.functions.protocol.Protocol;
import io.eigr.functions.protocol.actors.ActorOuterClass;
import okhttp3.*;
import org.junit.Test;
import reactor.core.publisher.EmitterProcessor;
import scala.concurrent.duration.Duration;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * Unit test for simple App.
 */
public class AppTest {

    private OkHttpClient client = new OkHttpClient();
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() throws IOException {

        HashMap<String, ActorOuterClass.Actor> actors = new HashMap<String, ActorOuterClass.Actor>();

        for (int i = 0; i < 2; i++) {
            String actorName = String.format("actor-test-0%s", i);
            actors.put(actorName, makeActor(actorName, i));
        }

        ActorOuterClass.Registry registry = ActorOuterClass.Registry.newBuilder()
                .putAllActors(actors)
                .build();

        ActorOuterClass.ActorSystem actorSystem = ActorOuterClass.ActorSystem.newBuilder()
                .setName("test-system")
                .setRegistry(registry)
                .build();

        Protocol.ServiceInfo si = Protocol.ServiceInfo.newBuilder()
                .setServiceName("jvm-sdk")
                .setServiceVersion("0.1.1")
                .setServiceRuntime(System.getProperty("java.version"))
                .setProtocolMajorVersion(1)
                .setProtocolMinorVersion(1)
                .build();

        Protocol.RegistrationRequest registration = Protocol.RegistrationRequest.newBuilder()
                .setServiceInfo(si)
                .setActorSystem(actorSystem)
                .build();

        RequestBody body = RequestBody.create(
                registration.toByteArray(), MediaType.parse("application/octet-stream"));

        Request request = new Request.Builder()
                .url("http://localhost:9001/api/v1/system")
                .post(body)
                .build();

        System.out.println("Send registration request...");

        Call call = client.newCall(request);
        Response response = call.execute();

        assertThat(response.code(), equalTo(200));
        Protocol.RegistrationResponse registrationResponse = Protocol.RegistrationResponse.parseFrom(response.body().bytes());
        System.out.println("Registration response: " + registrationResponse);

        /*
        byte[] byteState = BigInteger.valueOf(1).toByteArray();

        Any stateValue = Any.newBuilder()
                .setTypeUrl("type.googleapis.com/integer")
                .setValue(ByteString.copyFrom(byteState))
                .build();

        Protocol.InvocationRequest invocation = Protocol.InvocationRequest.newBuilder()
                .setAsync(false)
                .setSystem(actorSystem)
                .setActor(makeActor("actor-test-01", 1))
                .setCommandName("someFunction")
                .setValue(stateValue)
                .build();

        Protocol.ActorSystemRequest invocationRequest = Protocol.ActorSystemRequest.newBuilder()
                .setInvocationRequest(invocation)
                .build();
        */
        assertTrue(true);
    }

    private ActorOuterClass.Actor makeActor(String name, Integer state) {

        byte[] byteState = BigInteger.valueOf(state).toByteArray();

        Any stateValue = Any.newBuilder()
                .setTypeUrl("type.googleapis.com/integer")
                .setValue(ByteString.copyFrom(byteState))
                //.setValue( ByteString.copyFrom(String.format("test-%s", name).getBytes(StandardCharsets.UTF_8)))
                .build();

        ActorOuterClass.ActorState initialState = ActorOuterClass.ActorState.newBuilder()
                .setState(stateValue)
                .build();

        ActorOuterClass.ActorSnapshotStrategy snapshotStrategy = ActorOuterClass.ActorSnapshotStrategy.newBuilder()
                .setTimeout(ActorOuterClass.TimeoutStrategy.newBuilder().setTimeout(10000).build())
                .build();

        ActorOuterClass.ActorDeactivateStrategy deactivateStrategy = ActorOuterClass.ActorDeactivateStrategy.newBuilder()
                .setTimeout(ActorOuterClass.TimeoutStrategy.newBuilder().setTimeout(60000).build())
                .build();

        return ActorOuterClass.Actor.newBuilder()
                .setName(name)
                .setState(initialState)
                .setSnapshotStrategy(snapshotStrategy)
                .setDeactivateStrategy(deactivateStrategy)
                .build();
    }
}
