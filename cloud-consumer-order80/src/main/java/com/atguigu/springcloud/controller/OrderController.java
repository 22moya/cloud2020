package com.atguigu.springcloud.controller;

import com.atguigu.springcloud.entities.CommonResult;
import com.atguigu.springcloud.entities.Payment;
import com.atguigu.springcloud.lb.LoadBalancer;

import com.netflix.discovery.converters.Auto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import java.net.URI;
import java.util.List;

@Slf4j
@RestController
public class OrderController {
    //public static final String PAYMENT_URL="http://localhost:8001";
    public static final String PAYMENT_URL="http://CLOUD-PAYMENT-SERVICE";
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private LoadBalancer loadBalancer;

    @Autowired
    private DiscoveryClient discoveryClient;

    @GetMapping("/consumer/payment/create")//客户端都是发get请求所以都是getMapping
    public CommonResult<Payment>creat(Payment payment){
        return restTemplate.postForObject(PAYMENT_URL+"/payment/creat",payment,CommonResult.class);
    }
    @GetMapping("/consumer/payment/get/{id}")
    public CommonResult<Payment>getPaymentById(@PathVariable Long id){
        return restTemplate.getForObject(PAYMENT_URL+"/payment/get/"+id,CommonResult.class);
    }
    @GetMapping("/consumer/payment/getForEntity/{id}")
    public CommonResult<Payment>getPaymentById2(@PathVariable Long id){
        ResponseEntity<CommonResult> entity =restTemplate.getForEntity(PAYMENT_URL+"/payment/get/"+id, CommonResult.class);
        if (entity.getStatusCode().is2xxSuccessful()) {
            return entity.getBody();
        }else {
            return new CommonResult<>(444,"操作失败");
        }
        }
    @GetMapping("/consumer/payment/create2")//客户端都是发get请求所以都是getMapping
    public CommonResult<Payment>creat2(Payment payment){
        return restTemplate.postForEntity(PAYMENT_URL+"/payment/creat",payment,CommonResult.class).getBody();
    }

    @GetMapping( "/consumer/payment/lb")
    public String getPaymentLB(){
        List<ServiceInstance> instanceList=discoveryClient.getInstances("CLOUD-PAYMENT-SERVICE");
        if(instanceList==null||instanceList.size()==0){
            return null;
        }
        ServiceInstance serviceInstance =loadBalancer.instances(instanceList);
        URI uri = serviceInstance.getUri();
        return restTemplate.getForObject(uri+"payment/lb", String.class);

    }


    }

