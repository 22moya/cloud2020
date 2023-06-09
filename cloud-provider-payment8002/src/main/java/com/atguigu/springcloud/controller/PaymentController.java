package com.atguigu.springcloud.controller;

import com.atguigu.springcloud.service.PaymentService;
import com.atguigu.springcloud.entities.CommonResult;
import com.atguigu.springcloud.entities.Payment;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
public class PaymentController {
    @Autowired
    private PaymentService paymentService;
    @Value("${server.port}")
    private String serverPort;
    @Autowired
    private DiscoveryClient discoveryClient;
    @PostMapping(value = "/payment/creat")
    public CommonResult create(@RequestBody Payment payment){
        int result = paymentService.create(payment);
        log.info("----插入结果：{}",result);
        if(result>0){
            return new CommonResult<>(200,"插入数据成功,serverPort:"+serverPort,result);
        }else {
            return new CommonResult(444,"插入数据失败");
        }

    }
    @GetMapping(value = "/payment/get/{id}")
    public CommonResult getPaymentById(@PathVariable Long id){
        Payment payment = paymentService.getPaymentById(id);
        log.info("----查询结果： {}",payment);
        if(payment!=null){

            return new CommonResult<>(200,"查询数据成功",payment);

        }else {
            return new CommonResult(444,"没有对应的记录，查询ID："+id,null);
        }

    }
    /**
     * 服务发现
     * @return
     */
    @GetMapping("/payment/discovery")
    public Object discovery(){
        List<String> services = discoveryClient.getServices();
        for(String element:services){
            log.info("element:"+element);
        }
        discoveryClient.getInstances("CLOUD-PAYMENT-SERVICE").stream()
                .forEach(item-> log.info(item.getServiceId()+"\t"+item.getHost()+"\t"+item.getPort()+"\t"+item.getUri()));
        return this.discoveryClient;
    }
    @GetMapping(value = "/payment/lb")
    public String getPaymentLB(){
        return serverPort;
    }
}
