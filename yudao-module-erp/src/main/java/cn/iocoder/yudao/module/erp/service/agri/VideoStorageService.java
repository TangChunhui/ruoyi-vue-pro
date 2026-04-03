package cn.iocoder.yudao.module.erp.service.agri;

import java.time.LocalDateTime;

/**
 * 视频存储服务接口
 * 用于将监控视频从云端/摄像头抓取并存储到本地
 */
public interface VideoStorageService {

    /**
     * 异步抓取并存储视频
     *
     * @param bizId 业务编号 (订单 ID / 出库 ID)
     * @param bizType 业务类型 (sale_order / sale_out)
     * @param cameraId 摄像头 ID
     * @param videoTime 视频关联时间
     */
    void downloadAndStoreVideo(Long bizId, String bizType, String cameraId, LocalDateTime videoTime);
}
