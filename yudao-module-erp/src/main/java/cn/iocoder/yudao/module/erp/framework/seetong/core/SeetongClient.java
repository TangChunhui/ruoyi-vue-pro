package cn.iocoder.yudao.module.erp.framework.seetong.core;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.module.erp.framework.seetong.config.SeetongProperties;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    /** 摄像头设备信息 */
    @Data
    public static class DeviceVO {
        private String deviceId;
        private String deviceName;
        private String channelId;
        private String channelName;
        private Integer status; // 1=在线 0=离线
    }

    /**
     * 获得访问令牌（带本地缓存）
     */
    public synchronized String getAccessToken() {
        if (StrUtil.isNotEmpty(accessToken) && LocalDateTime.now().isBefore(expireTime)) {
            return accessToken;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("appKey", properties.getAppKey());
        params.put("appSecret", properties.getAppSecret());
        params.put("username", properties.getUsername());
        params.put("password", properties.getPassword());

        try {
            String result = HttpUtil.post(properties.getBaseUrl() + "/v1/token", params);
            JSONObject json = JSONUtil.parseObj(result);
            if (json.getInt("code") == 0) {
                accessToken = json.getStr("accessToken");
                expireTime = LocalDateTime.now().plusHours(20);
                return accessToken;
            }
            log.error("[SeetongClient][getAccessToken 失败，原因({})]", json.getStr("msg"));
        } catch (Exception e) {
            log.error("[SeetongClient][getAccessToken 异常: {}]", e.getMessage());
        }
        return null;
    }

    /**
     * 获得设备（摄像头）列表
     */
    public List<DeviceVO> getDeviceList() {
        String token = getAccessToken();
        if (StrUtil.isEmpty(token)) {
            return new ArrayList<>();
        }
        Map<String, Object> params = new HashMap<>();
        params.put("accessToken", token);
        params.put("pageSize", 100);
        params.put("pageNo", 1);

        try {
            String result = HttpUtil.get(properties.getBaseUrl() + "/v1/devices", params);
            JSONObject json = JSONUtil.parseObj(result);
            if (json.getInt("code") == 0) {
                JSONArray list = json.getJSONArray("list");
                List<DeviceVO> devices = new ArrayList<>();
                if (list != null) {
                    list.forEach(item -> {
                        JSONObject d = (JSONObject) item;
                        DeviceVO vo = new DeviceVO();
                        vo.setDeviceId(d.getStr("deviceId"));
                        vo.setDeviceName(d.getStr("deviceName"));
                        vo.setChannelId(d.getStr("channelId", d.getStr("deviceId")));
                        vo.setChannelName(d.getStr("channelName", d.getStr("deviceName")));
                        vo.setStatus(d.getInt("status", 0));
                        devices.add(vo);
                    });
                }
                return devices;
            }
            log.warn("[SeetongClient][getDeviceList 失败: {}]", json.getStr("msg"));
        } catch (Exception e) {
            log.error("[SeetongClient][getDeviceList 异常: {}]", e.getMessage());
        }
        return new ArrayList<>();
    }

    /**
     * 获得录像回放地址（HLS）
     *
     * @param deviceId  设备/通道 ID
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return HLS 播放地址，失败返回 null
     */
    public String getPlaybackUrl(String deviceId, LocalDateTime startTime, LocalDateTime endTime) {
        String token = getAccessToken();
        if (StrUtil.isEmpty(token)) {
            return null;
        }
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        Map<String, Object> params = new HashMap<>();
        params.put("accessToken", token);
        params.put("deviceId", deviceId);
        params.put("startTime", startTime.format(fmt));
        params.put("endTime", endTime.format(fmt));
        params.put("protocol", "hls");

        try {
            String result = HttpUtil.get(properties.getBaseUrl() + "/v1/playback/url", params);
            JSONObject json = JSONUtil.parseObj(result);
            if (json.getInt("code") == 0) {
                return json.getStr("url");
            }
            log.error("[SeetongClient][getPlaybackUrl 失败，设备({})，原因({})]", deviceId, json.getStr("msg"));
        } catch (Exception e) {
            log.error("[SeetongClient][getPlaybackUrl 异常: {}]", e.getMessage());
        }
        return null;
    }
}
