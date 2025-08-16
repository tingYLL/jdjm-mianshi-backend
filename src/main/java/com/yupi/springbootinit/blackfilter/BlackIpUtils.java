package com.yupi.springbootinit.blackfilter;

import cn.hutool.bloomfilter.BitMapBloomFilter;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.base.Charsets;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * 黑名单过滤工具类
 */
@Slf4j
public class BlackIpUtils {

    // 改用Guava BloomFilter（默认初始容量100，误判率1%）
    private static BloomFilter<String> bloomFilter = BloomFilter.create(
            Funnels.stringFunnel(Charsets.UTF_8),
            100,
            0.01
    );

    public static boolean isBlackIp(String ip) {
        return bloomFilter.mightContain(ip);
    }

    public static void rebuildBlackIp(String configInfo) {
        if (StrUtil.isBlank(configInfo)) {
            configInfo = "{}";
        }
        Yaml yaml = new Yaml();
        Map map = yaml.loadAs(configInfo, Map.class);
        List<String> blackIpList = (List<String>) map.get("blackIpList");

        synchronized (BlackIpUtils.class) {
            if (CollUtil.isNotEmpty(blackIpList)) {
                // 核心修改点：使用Guava初始化（958506改为实际数量）
                BloomFilter<String> newFilter = BloomFilter.create(
                        Funnels.stringFunnel(Charsets.UTF_8),
                        blackIpList.size(),  // 动态数量
                        0.001                // 误判率0.1%
                );
                for (String blackIp : blackIpList) {
                    newFilter.put(blackIp);
                }
                bloomFilter = newFilter;
            } else {
                bloomFilter = BloomFilter.create(
                        Funnels.stringFunnel(Charsets.UTF_8),
                        100,
                        0.01
                );
            }
        }
    }
}
