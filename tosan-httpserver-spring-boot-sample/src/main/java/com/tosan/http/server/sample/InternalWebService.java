package com.tosan.http.server.sample;

import com.tosan.http.server.starter.annotation.Timer;
import org.springframework.stereotype.Service;

/**
 * @author AmirHossein ZamanZade
 * @since 10/26/2022
 */
@Service
public class InternalWebService {

    @Timer(serviceType = "InternalWebService", serviceName = "internalService")
    public void internalService() {
        System.out.println("start internalService");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("end internalService");
    }
}
