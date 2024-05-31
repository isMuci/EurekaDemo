package com.example.service.impl;

import com.example.entity.Order;
import com.example.entity.Product;
import com.example.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @Override
    public Order get(Integer id) {
        return new Order(1,"aa",getByAnnotation());
    }

    private List<Product> getByDiscovery() {
        StringBuffer sb=null;
        List<String> serviceIds=discoveryClient.getServices();
        if(CollectionUtils.isEmpty(serviceIds)){
            return null;
        }
        List<ServiceInstance> instances = discoveryClient.getInstances("eureka-provider");
        if(CollectionUtils.isEmpty(instances)){
            return null;
        }

        ServiceInstance serviceInstance = instances.get(0);
        sb = new StringBuffer();
        sb.append("http://"+serviceInstance.getHost()+":"+serviceInstance.getPort()+"/product/list");
        ResponseEntity<List<Product>> res = restTemplate.exchange(sb.toString(), HttpMethod.GET, null, new ParameterizedTypeReference<List<Product>>() {
        });
        return res.getBody();
    }

    private List<Product> getByLoadBalancer() {
        StringBuffer sb=null;

        ServiceInstance si = loadBalancerClient.choose("eureka-provider");
        if(si==null){
            return null;
        }

        sb = new StringBuffer();
        sb.append("http://"+si.getHost()+":"+si.getPort()+"/product/list");
        ResponseEntity<List<Product>> res = restTemplate.exchange(sb.toString(), HttpMethod.GET, null, new ParameterizedTypeReference<List<Product>>() {
        });
        return res.getBody();
    }

    private List<Product> getByAnnotation() {

        ResponseEntity<List<Product>> res = restTemplate.exchange("http://eureka-provider/product/list", HttpMethod.GET, null, new ParameterizedTypeReference<List<Product>>() {
        });
        return res.getBody();
    }
}
