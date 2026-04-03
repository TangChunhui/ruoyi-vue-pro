package cn.iocoder.yudao.module.erp.framework.seetong.core;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.module.erp.framework.seetong.config.SeetongProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Seetong Cloud API 客户端
 */
@Component
@Slf4j
public class SeetongClient {

    @Resource
    private SeetongProperties properties;

    private String accessToken;
    private LocalDateTime expireTime;

    /**
     * 获得访问令牌
     */
    public synchronized String getAccessToken() {
        if (StrUtil.isNotEmpty(accessToken) && LocalDateTime.now().isBefore(expireTime)) {
            return accessToken;
        }
        // 刷新 Token
        Map<String, Object> params = new HashMap<>();
        params.put("appKey", properties.getAppKey());
        params.put("appSecret", properties.getAppSecret());
        params.put("username", properties.getUsername());
        params.put("password", properties.getPassword());

        String result = HttpUtil.post(properties.getBaseUrl() + "/v1/token", params);
        JSONObject json = JSONUtil.parseObj(result);
        if (json.getInt("code") == 0) {
            accessToken = json.getStr("accessToken");
            // 假设过期时间是 24 小时，根据 API 实际值调整
            expireTime = LocalDateTime.now().plusHours(20);
            return accessToken;
        } else {
            log.error("[SeetongClient][getAccessToken 失败，原因({})]", json.getStr("msg"));
            return null;
        }
    }

    /**
     * 获得回放地址 (HLS/FLV)
     *
     * @param deviceId 设备 ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 播放地址
     */
    public String getPlaybackUrl(String deviceId, LocalDateTime startTime, LocalDateTime endTime) {
        String token = getAccessToken();
        if (StrUtil.isEmpty(token)) {
            return null;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        Map<String, Object> params = new HashMap<>();
        params.put("accessToken", token);
        params.put("deviceId", deviceId);
        params.put("startTime", startTime.format(formatter));
        params.put("endTime", endTime.format(formatter));
        params.put("protocol", "hls"); // 默认 HLS

        String result = HttpUtil.get(properties.getBaseUrl() + "/v1/playback/url", params);
        JSONObject json = JSONUtil.parseObj(result);
        if (json.getInt("code") == 0) {
            return json.getStr("url");
        } else {
            log.error("[SeetongClient][getPlaybackUrl 失败，设备({})，原因({})]", deviceId, json.getStr("msg"));
            return null;
        }
    }
}
