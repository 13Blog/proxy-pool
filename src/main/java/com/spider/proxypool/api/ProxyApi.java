package com.spider.proxypool.api;

import com.spider.proxypool.common.Result;
import com.spider.proxypool.common.ResultGenerator;
import com.spider.proxypool.db.repositor.ProxyRepository;
import com.spider.proxypool.entity.ProxyEntity;
import com.spider.proxypool.utils.ProxyUtil;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Created by 13 on 2017/10/10.
 */
@RestController
@EnableAutoConfiguration
@RequestMapping("/api")
public class ProxyApi {

    @GetMapping("/")
    public Result index() {
        return ResultGenerator.genSuccessResult("SUCCESS");
    }

    @GetMapping("/get")
    public Result getProxy() {
        ProxyEntity proxy = ProxyRepository.getInstance().getRandomly();
        if (proxy != null) {
            return ResultGenerator.genSuccessResult(proxy);
        }
        return ResultGenerator.genFailResult("FAIL");
    }


    @GetMapping("/list")
    public Result getProxys(@RequestParam(value = "num", defaultValue = "1") int num) {
        List<ProxyEntity> list = ProxyRepository.getInstance().getList(num);
        if (list.size() > 0) {
            return ResultGenerator.genSuccessResult(list);
        }
        return ResultGenerator.genFailResult("fail");
    }

    @GetMapping("/count")
    public Result getCount() {
        int count = ProxyRepository.getInstance().getCount();
        return ResultGenerator.genSuccessResult(count);
    }


    @GetMapping("verify")
    public Result verifyProxy(@RequestParam String proxy) {
        if (proxy == null || !proxy.matches("\\d+\\.\\d+\\.\\d+\\.\\d+:\\d+")) {
            return ResultGenerator.genFailResult("参数非法！");
        }
        boolean useful = ProxyUtil.verifyProxy(proxy);
        if (!useful) ProxyRepository.getInstance().deleteByKey(proxy);
        Map<String, Object> map = new HashedMap();
        map.put("proxy", proxy);
        map.put("useful", useful);
        return ResultGenerator.genSuccessResult(map);
    }

    @GetMapping("/delete")
    public Result deleteProxy(@RequestParam String proxy) {
        if (proxy == null || !proxy.matches("\\d+\\.\\d+\\.\\d+\\.\\d+:\\d+")) {
            return ResultGenerator.genFailResult("参数非法！");
        }
        ProxyRepository.getInstance().deleteByKey(proxy);
        return ResultGenerator.genSuccessResult("SUCCESS");
    }
}
