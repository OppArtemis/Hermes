/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Endpoints Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloEndpoints
*/

package com.artemis.hermes.backend;
import java.util.Random;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;

import javax.inject.Named;

/**
 * An endpoint class we are exposing
 */
@Api(
        name = "myApi",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "backend.hermes.artemis.com",
                ownerName = "backend.hermes.artemis.com",
                packagePath = ""
        )
)
public class MyEndpoint {

    /**
     * A simple endpoint method that takes a name and says Hi back
     */
    @ApiMethod(name = "sayHi")
    public MyBean sayHi(@Named("name") String name) {
        MyBean response = new MyBean();

        // Generate a random number
        Random rand = new Random();
        int dice = rand.nextInt(6) + 1;

        String greetingMessage;

        if (dice == 6) {
            greetingMessage = "wanna go to McDonald's?";
        } else if (dice == 5) {
            greetingMessage = "cooking at home saves money.";
        } else if (dice == 4) {
            greetingMessage = "you should go on a diet.";
        } else {
            greetingMessage = "what would you like to eat?";
        }

        response.setData("Hi, " + name + " " + greetingMessage);

        return response;
    }

}
