package cn.iocoder.yudao.module.erp.service.agri;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.iocoder.yudao.module.erp.dal.dataobject.sale.ErpSaleOrderDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.sale.ErpSaleOutDO;
import cn.iocoder.yudao.module.erp.dal.mysql.sale.ErpSaleOrderMapper;
import cn.iocoder.yudao.module.erp.dal.mysql.sale.ErpSaleOutMapper;
import cn.iocoder.yudao.module.erp.framework.seetong.config.SeetongProperties;
import cn.iocoder.yudao.module.erp.framework.seetong.core.SeetongClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 视频存证服务实现
 *
 * 流程：交易发生 → 等待2分钟（NVR转码就绪）→ 取前后10分钟片段 → 下载到本地 → 更新账目URL
 */
@Service
@Slf4j
public class VideoStorageServiceImpl implements VideoStorageService {

    @Resource
    private SeetongProperties properties;

    @Resource
    private SeetongClient seetongClient;

    @Resource
    private ErpSaleOrderMapper saleOrderMapper;

    @Resource
    private ErpSaleOutMapper saleOutMapper;

    @Override
    @Async
    public void downloadAndStoreVideo(Long bizId, String bizType, String cameraId, LocalDateTime videoTime) {
        if (!properties.isEnabled()) {
            log.debug("[VideoStorage][Seetong 未启用，跳过视频存证 bizId={}]", bizId);
            return;
        }
        if (StrUtil.isEmpty(cameraId) || videoTime == null) {
            log.debug("[VideoStorage][cameraId 或 videoTime 为空，跳过 bizId={}]", bizId);
            return;
        }

        try {
            // 1. 等待 NVR 录像就绪（实际录像一般有约2分钟延迟才可回放）
            Thread.sleep(120_000L);

            // 2. 计算时间窗口：交易前 preMinutes 分钟 ~ 交易后 postMinutes 分钟
            LocalDateTime startTime = videoTime.minusMinutes(properties.getPreMinutes());
            LocalDateTime endTime   = videoTime.plusMinutes(properties.getPostMinutes());

            // 3. 获取回放地址（HLS m3u8）
            String playbackUrl = seetongClient.getPlaybackUrl(cameraId, startTime, endTime);
            if (StrUtil.isEmpty(playbackUrl)) {
                log.warn("[VideoStorage][获取回放地址失败 bizId={} cameraId={}]", bizId, cameraId);
                return;
            }

            // 4. 构建本地存储路径： /data/video/2024-03-15/sale_order_1001.mp4
            String day      = videoTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String fileName = String.format("%s_%d.mp4", bizType, bizId);
            String dir      = properties.getLocalPath() + File.separator + day;
            String fullPath = dir + File.separator + fileName;

            FileUtil.mkdir(dir); // 确保目录存在
            log.info("[VideoStorage][开始下载视频存证: {} -> {}]", playbackUrl, fullPath);
            HttpUtil.downloadFile(playbackUrl, new File(fullPath));

            // 5. 构建访问 URL 并写回数据库
            String videoUrl = properties.getUrlPrefix() + "/" + day + "/" + fileName;
            if ("sale_order".equals(bizType)) {
                saleOrderMapper.updateById(new ErpSaleOrderDO().setId(bizId).setVideoUrl(videoUrl));
            } else if ("sale_out".equals(bizType)) {
                saleOutMapper.updateById(new ErpSaleOutDO().setId(bizId).setVideoUrl(videoUrl));
            }
            log.info("[VideoStorage][视频存证成功: bizId={} url={}]", bizId, videoUrl);

        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("[VideoStorage][视频存证失败 bizId={}: {}]", bizId, e.getMessage(), e);
        }
    }
}
