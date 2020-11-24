package ethan.gateway.router;

import java.util.List;
import java.util.Random;

public class MyRandomHttpRouter implements HttpEndpointRouter {

    @Override
    public String route(List<String> endpoints) {
        if (null == endpoints || endpoints.isEmpty()) {
            throw new RuntimeException("endpoints is empty");
        }

        return endpoints.get(getRandomIndex(endpoints.size()));


    }

    public int getRandomIndex(int size) {
        Random random = new Random();
        return  random.nextInt(size);
    }

}
