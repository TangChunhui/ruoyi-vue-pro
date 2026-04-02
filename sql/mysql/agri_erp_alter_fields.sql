-- ============================================================
-- 农资 ERP 扩展字段 DDL 脚本
-- 说明：在现有 ERP 表上追加农资合规所需字段
-- ============================================================

-- 1. 供应商表：追加经营许可证信息
ALTER TABLE `erp_supplier`
    ADD COLUMN `business_license_no` VARCHAR(100) NULL COMMENT '经营许可证号' AFTER `bank_address`,
    ADD COLUMN `license_expiry_date` DATETIME     NULL COMMENT '资质有效期'   AFTER `business_license_no`,
    ADD COLUMN `license_file_url`    VARCHAR(500) NULL COMMENT '资质附件 URL' AFTER `license_expiry_date`;

-- 2. 客户表：追加种植信息与信用额度
ALTER TABLE `erp_customer`
    ADD COLUMN `land_area`    DECIMAL(10, 2) NULL COMMENT '种植面积（亩）'       AFTER `bank_address`,
    ADD COLUMN `main_crops`   VARCHAR(200)   NULL COMMENT '主要种植作物'          AFTER `land_area`,
    ADD COLUMN `credit_limit` DECIMAL(24, 2) NULL DEFAULT 0 COMMENT '信用额度'    AFTER `main_crops`,
    ADD COLUMN `current_debt` DECIMAL(24, 2) NULL DEFAULT 0 COMMENT '当前欠款'    AFTER `credit_limit`;

-- 3. 产品表：追加登记证与高毒限用标识
ALTER TABLE `erp_product`
    ADD COLUMN `registration_no`          VARCHAR(100)   NULL COMMENT '农药登记证号'     AFTER `min_price`,
    ADD COLUMN `registration_expiry_date` DATETIME       NULL COMMENT '农药登记证有效期' AFTER `registration_no`,
    ADD COLUMN `is_restricted`            TINYINT(1)     NOT NULL DEFAULT 0 COMMENT '是否高毒限用' AFTER `registration_expiry_date`,
    ADD COLUMN `standard_dosage_per_mu`   DECIMAL(10, 2) NULL COMMENT '标准亩用量'        AFTER `is_restricted`;

-- 4. 销售订单表：追加农资处方合规字段
ALTER TABLE `erp_sale_order`
    ADD COLUMN `usage_intent`  VARCHAR(500) NULL COMMENT '用途/防治对象' AFTER `remark`,
    ADD COLUMN `usage_method`  VARCHAR(500) NULL COMMENT '施用方法'     AFTER `usage_intent`,
    ADD COLUMN `dosage_advice` VARCHAR(200) NULL COMMENT '建议用量'     AFTER `usage_method`,
    ADD COLUMN `buyer_id_card` VARCHAR(50)  NULL COMMENT '购买人身份证' AFTER `dosage_advice`;

-- 5. 收/付款单表：追加记账现场视频存证 URL
ALTER TABLE `erp_finance_receipt`
    ADD COLUMN `record_video_url` VARCHAR(1000) NULL COMMENT '记账现场视频存证 URL' AFTER `remark`;

ALTER TABLE `erp_finance_payment`
    ADD COLUMN `record_video_url` VARCHAR(1000) NULL COMMENT '记账现场视频存证 URL' AFTER `remark`;

-- 6. 溯源核心：业务项批次与效期追踪 (农资进阶版)
-- 销售与采购订单
ALTER TABLE `erp_sale_order_items`
    ADD COLUMN `batch_no`        VARCHAR(100) NULL COMMENT '生产批次号' AFTER `tax_price`,
    ADD COLUMN `production_date` DATETIME     NULL COMMENT '生产日期'   AFTER `batch_no`,
    ADD COLUMN `expiry_date`     DATETIME     NULL COMMENT '有效期至'   AFTER `production_date`;

ALTER TABLE `erp_purchase_order_items`
    ADD COLUMN `batch_no`        VARCHAR(100) NULL COMMENT '生产批次号' AFTER `tax_price`,
    ADD COLUMN `production_date` DATETIME     NULL COMMENT '生产日期'   AFTER `batch_no`,
    ADD COLUMN `expiry_date`     DATETIME     NULL COMMENT '有效期至'   AFTER `production_date`;

-- 出入库业务单据项
ALTER TABLE `erp_sale_out_items`
    ADD COLUMN `batch_no`        VARCHAR(100) NULL COMMENT '生产批次号' AFTER `tax_price`,
    ADD COLUMN `production_date` DATETIME     NULL COMMENT '生产日期'   AFTER `batch_no`,
    ADD COLUMN `expiry_date`     DATETIME     NULL COMMENT '有效期至'   AFTER `production_date`;

ALTER TABLE `erp_purchase_in_items`
    ADD COLUMN `batch_no`        VARCHAR(100) NULL COMMENT '生产批次号' AFTER `tax_price`,
    ADD COLUMN `production_date` DATETIME     NULL COMMENT '生产日期'   AFTER `batch_no`,
    ADD COLUMN `expiry_date`     DATETIME     NULL COMMENT '有效期至'   AFTER `production_date`;

-- 库管业务单据项 (其它入库、其它出库、调拨、盘点)
ALTER TABLE `erp_stock_in_item`
    ADD COLUMN `batch_no`        VARCHAR(100) NULL COMMENT '生产批次号' AFTER `total_price`,
    ADD COLUMN `production_date` DATETIME     NULL COMMENT '生产日期'   AFTER `batch_no`,
    ADD COLUMN `expiry_date`     DATETIME     NULL COMMENT '有效期至'   AFTER `production_date`;

ALTER TABLE `erp_stock_out_item`
    ADD COLUMN `batch_no`        VARCHAR(100) NULL COMMENT '生产批次号' AFTER `total_price`,
    ADD COLUMN `production_date` DATETIME     NULL COMMENT '生产日期'   AFTER `batch_no`,
    ADD COLUMN `expiry_date`     DATETIME     NULL COMMENT '有效期至'   AFTER `production_date`;

ALTER TABLE `erp_stock_move_item`
    ADD COLUMN `batch_no`        VARCHAR(100) NULL COMMENT '生产批次号' AFTER `total_price`,
    ADD COLUMN `production_date` DATETIME     NULL COMMENT '生产日期'   AFTER `batch_no`,
    ADD COLUMN `expiry_date`     DATETIME     NULL COMMENT '有效期至'   AFTER `production_date`;

ALTER TABLE `erp_stock_check_item`
    ADD COLUMN `batch_no`        VARCHAR(100) NULL COMMENT '生产批次号' AFTER `total_price`,
    ADD COLUMN `production_date` DATETIME     NULL COMMENT '生产日期'   AFTER `batch_no`,
    ADD COLUMN `expiry_date`     DATETIME     NULL COMMENT '有效期至'   AFTER `production_date`;

-- 7. 库存明细流水表：全量沉积溯源信息
ALTER TABLE `erp_stock_record`
    ADD COLUMN `batch_no`        VARCHAR(100) NULL COMMENT '生产批次号' AFTER `biz_no`,
    ADD COLUMN `production_date` DATETIME     NULL COMMENT '生产日期'   AFTER `batch_no`,
    ADD COLUMN `expiry_date`     DATETIME     NULL COMMENT '有效期至'   AFTER `production_date`;
