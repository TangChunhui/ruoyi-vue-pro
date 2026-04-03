package cn.iocoder.yudao.module.erp.service.agri;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.iocoder.yudao.module.erp.dal.mysql.sale.ErpSaleOrderMapper;
import cn.iocoder.yudao.module.erp.dal.mysql.sale.ErpSaleOutMapper;
import cn.iocoder.yudao.module.erp.framework.seetong.config.SeetongProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class VideoStorageServiceImpl implements VideoStorageService {

    @Resource
    private SeetongProperties properties;

    @Resource
    private ErpAgriReportService agriReportService;

    @Resource
    private ErpSaleOrderMapper saleOrderMapper;

    @Resource
    private ErpSaleOutMapper saleOutMapper;

    @Override
    @Async
    public void downloadAndStoreVideo(Long bizId, String bizType, String cameraId, LocalDateTime videoTime) {
        if (StrUtil.isEmpty(cameraId) || videoTime == null) {
            return;
        }

        try {
            // 1. 等待一段时间，确保监控录像已在 NVR/云端 就绪 (例如 2 分钟)
            // 现实场景中，回放视频生成可能有一点延迟
            Thread.sleep(120 * 1000L);

            // 2. 获取回放地址 (默认配置时长)
            // 注意：这里需要确保 SeetongClient 返回的是可直接下载的格式 (mp4)，如果是 HLS 可能需要 FFmpeg
            String playbackUrl = agriReportService.getPlaybackUrl(bizId, bizType, null, null);
            if (StrUtil.isEmpty(playbackUrl)) {
                log.warn("[VideoStorage][bizId({}) bizType({}) 获取播放地址失败]", bizId, bizType);
                return;
            }

            // 3. 构建本地存储路径
            String day = videoTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String fileName = String.format("%s_%s.mp4", bizType, bizId);
            String fullPath = properties.getLocalPath() + File.separator + day + File.separator + fileName;
            
            // 4. 下载文件
            // 如果 playbackUrl 是 m3u8，此处下载会变成下载 playlist，实际应用中可能需要 ffmpeg 命令行
            log.info("[VideoStorage][开始抓取视频: {} -> {}]", playbackUrl, fullPath);
            FileUtil.mkdir(properties.getLocalPath() + File.separator + day);
            HttpUtil.downloadFile(playbackUrl, fullPath);

            // 5. 更新数据库
            String videoUrl = properties.getUrlPrefix() + "/" + day + "/" + fileName;
            if ("sale_order".equals(bizType)) {
                saleOrderMapper.updateById(new cn.iocoder.yudao.module.erp.dal.dataobject.sale.ErpSaleOrderDO()
                        .setId(bizId).setVideoUrl(videoUrl));
            } else if ("sale_out".equals(bizType)) {
                saleOutMapper.updateById(new cn.iocoder.yudao.module.erp.dal.dataobject.sale.ErpSaleOutDO()
                        .setId(bizId).setVideoUrl(videoUrl));
            }
            log.info("[VideoStorage][视频抓取并关联成功: {}]", videoUrl);

        } catch (Exception e) {
            log.error("[VideoStorage][抓取视频失败: {}]", e.getMessage());
        }
    }
}
