package ru.job4j.site.service;

import org.springframework.cloud.client.discovery.DiscoveryClient;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class EurekaUriProvider {

    private final DiscoveryClient discoveryClient;
    private final Map<String, String> uriCache = new ConcurrentHashMap<>();

    public String getUri(String serviceId) {
        String cached = uriCache.get(serviceId);
        if (cached != null) {
            return cached;
        }
        List<ServiceInstance> list = discoveryClient.getInstances(serviceId);
        if (list.isEmpty()) {
            return "http://" + serviceId + ":0";
        }
        ServiceInstance instance = list.get(0);
        String uri = instance.getUri().toString();
        uriCache.put(serviceId, uri);
        return uri;
    }
}
