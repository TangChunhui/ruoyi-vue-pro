-- ============================================================
-- 农资 ERP 模块 核心数据脚本 (整合幂等版 V4 - 20260407)
-- ============================================================

-- ------------------------------------------------------------
-- 定义一个用于安全增加列的存储过程 (防止 Duplicate Column Name 错误)
-- ------------------------------------------------------------
DROP PROCEDURE IF EXISTS `AddColSafe`;
DELIMITER //
CREATE PROCEDURE `AddColSafe`(
    IN tbl VARCHAR(64),
    IN col VARCHAR(64),
    IN def VARCHAR(500)
)
BEGIN
    DECLARE col_exists INT DEFAULT 0;
    SELECT COUNT(*) INTO col_exists FROM information_schema.COLUMNS 
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = tbl AND COLUMN_NAME = col;
    
    IF col_exists = 0 THEN
        SET @sql_stmt = CONCAT('ALTER TABLE `', tbl, '` ADD COLUMN `', col, '` ', def);
        PREPARE stmt FROM @sql_stmt;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END //
DELIMITER ;

-- ----------------------------
-- 1. 基础信息表 (DDL)
-- ----------------------------
CALL AddColSafe('erp_supplier', 'business_license_no', 'VARCHAR(100) NULL COMMENT \'经营许可证号\' AFTER `bank_address`');
CALL AddColSafe('erp_supplier', 'license_expiry_date', 'DATETIME NULL COMMENT \'资质有效期\' AFTER `business_license_no`');
CALL AddColSafe('erp_supplier', 'license_file_url', 'VARCHAR(500) NULL COMMENT \'资质附件 URL\' AFTER `license_expiry_date`');

CALL AddColSafe('erp_customer', 'address', 'VARCHAR(500) NULL COMMENT \'地址\' AFTER `bank_address`');
CALL AddColSafe('erp_customer', 'land_area', 'DECIMAL(10, 2) NULL COMMENT \'种植面积（亩）\' AFTER `address`');
CALL AddColSafe('erp_customer', 'main_crops', 'VARCHAR(200) NULL COMMENT \'主要种植作物\' AFTER `land_area`');
CALL AddColSafe('erp_customer', 'credit_limit', 'DECIMAL(24, 2) NULL DEFAULT 0 COMMENT \'信用额度\' AFTER `main_crops`');
CALL AddColSafe('erp_customer', 'current_debt', 'DECIMAL(24, 2) NULL DEFAULT 0 COMMENT \'当前欠款\' AFTER `credit_limit`');

CALL AddColSafe('erp_product', 'registration_no', 'VARCHAR(100) NULL COMMENT \'农药登记证号\' AFTER `min_price`');
CALL AddColSafe('erp_product', 'registration_expiry_date', 'DATETIME NULL COMMENT \'农药登记证有效期\' AFTER `registration_no`');
CALL AddColSafe('erp_product', 'is_restricted', 'TINYINT(1) NOT NULL DEFAULT 0 COMMENT \'是否高毒限用\' AFTER `registration_expiry_date`');
CALL AddColSafe('erp_product', 'standard_dosage_per_mu', 'DECIMAL(10, 2) NULL COMMENT \'标准亩用量\' AFTER `is_restricted`');
CALL AddColSafe('erp_product', 'agri_type', 'TINYINT NULL DEFAULT NULL COMMENT \'农资类型(1农药2化肥3种子)\' AFTER `standard_dosage_per_mu`');

-- ----------------------------
-- 2. 销售主表 (DDL)
-- ----------------------------
CALL AddColSafe('erp_sale_order', 'usage_intent', 'VARCHAR(500) NULL COMMENT \'用途/防治对象\' AFTER `remark`');
CALL AddColSafe('erp_sale_order', 'usage_method', 'VARCHAR(500) NULL COMMENT \'施用方法\' AFTER `usage_intent`');
CALL AddColSafe('erp_sale_order', 'dosage_advice', 'VARCHAR(200) NULL COMMENT \'建议用量\' AFTER `usage_method`');
CALL AddColSafe('erp_sale_order', 'buyer_id_card', 'VARCHAR(50) NULL COMMENT \'购买人身份证\' AFTER `dosage_advice`');
CALL AddColSafe('erp_sale_order', 'camera_id', 'VARCHAR(100) NULL COMMENT \'监控摄像 ID\' AFTER `buyer_id_card`');
CALL AddColSafe('erp_sale_order', 'video_time', 'DATETIME NULL COMMENT \'视频对应时间\' AFTER `camera_id`');
CALL AddColSafe('erp_sale_order', 'video_url', 'VARCHAR(500) NULL COMMENT \'本地存证视频 URL\' AFTER `video_time`');
CALL AddColSafe('erp_sale_order', 'cashier_id', 'BIGINT NULL COMMENT \'收银员编号\' AFTER \`remark\`');
CALL AddColSafe('erp_sale_order', 'finance_status', 'TINYINT NULL DEFAULT 0 COMMENT \'财务审核状态\' AFTER \`cashier_id\`');
ALTER TABLE `erp_sale_order` MODIFY COLUMN `customer_id` BIGINT NULL COMMENT '客户编号(可为空)';

-- 预设匿名散客 (ID 为 1，配合收银台快速结账)
INSERT IGNORE INTO `erp_customer` (`id`, `name`, `mobile`, `status`, `create_time`, `update_time`, `creator`, `updater`, `deleted`) 
VALUES (1, '匿名散客', '13888888888', 0, NOW(), NOW(), '1', '1', 0);

CALL AddColSafe('erp_sale_out', 'camera_id', 'VARCHAR(100) NULL COMMENT \'监控摄像 ID\' AFTER `remark`');
CALL AddColSafe('erp_sale_out', 'video_time', 'DATETIME NULL COMMENT \'视频对应时间\' AFTER `camera_id`');
CALL AddColSafe('erp_sale_out', 'video_url', 'VARCHAR(500) NULL COMMENT \'本地存证视频 URL\' AFTER `video_time`');

-- ----------------------------
-- 3. 库存相关 (DDL)
-- ----------------------------
CALL AddColSafe('erp_stock', 'batch_no', 'VARCHAR(100) NULL COMMENT \'生产批次号\' AFTER `id`');
CALL AddColSafe('erp_stock', 'production_date', 'DATETIME NULL COMMENT \'生产日期\' AFTER `batch_no`');
CALL AddColSafe('erp_stock', 'expiry_date', 'DATETIME NULL COMMENT \'有效期至\' AFTER `production_date`');

CALL AddColSafe('erp_stock_record', 'batch_no', 'VARCHAR(100) NULL COMMENT \'生产批次号\' AFTER `biz_no`');
CALL AddColSafe('erp_stock_record', 'production_date', 'DATETIME NULL COMMENT \'生产日期\' AFTER `batch_no`');
CALL AddColSafe('erp_stock_record', 'expiry_date', 'DATETIME NULL COMMENT \'有效期至\' AFTER `production_date`');

-- ----------------------------
-- 4. 业务明细表 (DDL) - 复数修正
-- ----------------------------
CALL AddColSafe('erp_sale_order_items', 'batch_no', 'VARCHAR(100) NULL COMMENT \'生产批次号\' AFTER `tax_price`');
CALL AddColSafe('erp_sale_order_items', 'production_date', 'DATETIME NULL COMMENT \'生产日期\' AFTER `batch_no`');
CALL AddColSafe('erp_sale_order_items', 'expiry_date', 'DATETIME NULL COMMENT \'有效期至\' AFTER `production_date`');

CALL AddColSafe('erp_purchase_order_items', 'batch_no', 'VARCHAR(100) NULL COMMENT \'生产批次号\' AFTER `tax_price`');
CALL AddColSafe('erp_purchase_order_items', 'production_date', 'DATETIME NULL COMMENT \'生产日期\' AFTER `batch_no`');
CALL AddColSafe('erp_purchase_order_items', 'expiry_date', 'DATETIME NULL COMMENT \'有效期至\' AFTER `production_date`');

CALL AddColSafe('erp_sale_out_items', 'batch_no', 'VARCHAR(100) NULL COMMENT \'生产批次号\' AFTER `tax_price`');
CALL AddColSafe('erp_sale_out_items', 'production_date', 'DATETIME NULL COMMENT \'生产日期\' AFTER `batch_no`');
CALL AddColSafe('erp_sale_out_items', 'expiry_date', 'DATETIME NULL COMMENT \'有效期至\' AFTER `production_date`');

CALL AddColSafe('erp_purchase_in_items', 'batch_no', 'VARCHAR(100) NULL COMMENT \'生产批次号\' AFTER `tax_price`');
CALL AddColSafe('erp_purchase_in_items', 'production_date', 'DATETIME NULL COMMENT \'生产日期\' AFTER `batch_no`');
CALL AddColSafe('erp_purchase_in_items', 'expiry_date', 'DATETIME NULL COMMENT \'有效期至\' AFTER `production_date`');

CALL AddColSafe('erp_purchase_return_items', 'batch_no', 'VARCHAR(100) NULL COMMENT \'生产批次号\' AFTER `tax_price`');
CALL AddColSafe('erp_purchase_return_items', 'production_date', 'DATETIME NULL COMMENT \'生产日期\' AFTER `batch_no`');
CALL AddColSafe('erp_purchase_return_items', 'expiry_date', 'DATETIME NULL COMMENT \'有效期至\' AFTER `production_date`');

CALL AddColSafe('erp_sale_return_items', 'batch_no', 'VARCHAR(100) NULL COMMENT \'生产批次号\' AFTER `tax_price`');
CALL AddColSafe('erp_sale_return_items', 'production_date', 'DATETIME NULL COMMENT \'生产日期\' AFTER `batch_no`');
CALL AddColSafe('erp_sale_return_items', 'expiry_date', 'DATETIME NULL COMMENT \'有效期至\' AFTER `production_date`');

-- 财务现场视频
CALL AddColSafe('erp_finance_receipt', 'record_video_url', 'VARCHAR(1000) NULL COMMENT \'记账现场视频存证 URL\' AFTER `remark`');
CALL AddColSafe('erp_finance_payment', 'record_video_url', 'VARCHAR(1000) NULL COMMENT \'记账现场视频存证 URL\' AFTER `remark`');

-- ----------------------------
-- 5. 菜单修改 (DML)
-- ----------------------------
-- 1. 获取菜单挂载点
SET @ERP_ID = (SELECT id FROM system_menu WHERE (name = 'ERP 系统' OR path = '/erp') AND deleted = 0 LIMIT 1);
SET @ERP_ID = IFNULL(@ERP_ID, 2563);

-- 2. 隐藏原“ERP 首页” (不再需要中间层)
UPDATE `system_menu` SET `visible` = 0 WHERE `component_name` = 'ErpHome';

-- 3. 物理重组：重建农资顶级入口 (标准化 Layout 挂载)
DELETE FROM `system_menu` WHERE `name` IN ('农资管理系统') OR `path` LIKE '/agri%';

INSERT INTO `system_menu`(
    `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `icon`, `create_time`, `update_time`, `creator`, `updater`, `deleted`
) VALUES 
('农资管理系统', 'erp:agri-report:query', 1, 0, 0, '/agri', 'Layout', NULL, 0, 1, 1, 1, 'ep:management', NOW(), NOW(), '1', '1', 0);
SET @AGRI_PARENT_ID = LAST_INSERT_ID();

-- 4. 强力挂载子功能 (使用绝对路径避免前端解析歧义)
DELETE FROM `system_menu` WHERE `name` IN ('经营合规驾驶舱', '农资收银台', '农户管理', '农资采购入库', '农资销售出库', '农资库存查询', '农资综合台账', '限用农药台账', '当日销售明细', '农资财务汇总', '供应商管理', '农资商品管理') AND `parent_id` > 0;

INSERT INTO `system_menu` (
    `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `icon`, `create_time`, `update_time`, `creator`, `updater`, `deleted`
) VALUES 
('经营合规驾驶舱', 'erp:agri-report:query', 2, 0, @AGRI_PARENT_ID, 'dashboard', 'erp/agri/report/Dashboard', 'AgriDashboard', 0, 1, 1, 0, 'ep:odometer', NOW(), NOW(), '1', '1', 0),
('农资收银台', 'erp:sale-order:create', 2, 1, @AGRI_PARENT_ID, 'cashier', 'erp/agri/cashier/index', 'AgriCashier', 0, 1, 1, 0, 'ep:shopping-cart', NOW(), NOW(), '1', '1', 0),
('农户管理', 'erp:sale-customer:query', 2, 2, @AGRI_PARENT_ID, 'farmer', 'erp/sale/customer/index', 'ErpCustomer', 0, 1, 1, 0, 'ep:user', NOW(), NOW(), '1', '1', 0),
('农资采购入库', 'erp:purchase-in:query', 2, 3, @AGRI_PARENT_ID, 'purchase-in', 'erp/purchase/in/index', 'ErpPurchaseIn', 0, 1, 1, 0, 'ep:download', NOW(), NOW(), '1', '1', 0),
('农资销售出库', 'erp:sale-out:query', 2, 4, @AGRI_PARENT_ID, 'sale-out', 'erp/sale/out/index', 'ErpSaleOut', 0, 1, 1, 0, 'ep:upload', NOW(), NOW(), '1', '1', 0),
('农资库存查询', 'erp:stock:query', 2, 5, @AGRI_PARENT_ID, 'stock-stock', 'erp/stock/stock/index', 'ErpStock', 0, 1, 1, 0, 'ep:box', NOW(), NOW(), '1', '1', 0),
('农资综合台账', 'erp:agri-report:query', 2, 6, @AGRI_PARENT_ID, 'ledger', 'erp/agri/ledger/index', 'AgriLedger', 0, 1, 1, 0, 'ep:notebook', NOW(), NOW(), '1', '1', 0),
('限用农药台账', 'erp:agri-report:query', 2, 7, @AGRI_PARENT_ID, 'restricted-sale-ledger', 'erp/agri/report/RestrictedSale', 'RestrictedSale', 0, 1, 1, 0, 'ep:list', NOW(), NOW(), '1', '1', 0),
('当日销售明细', 'erp:agri-report:query', 2, 8, @AGRI_PARENT_ID, 'sales-detail', 'erp/agri/report/SalesDetail', 'SalesDetail', 0, 1, 1, 0, 'ep:list', NOW(), NOW(), '1', '1', 0),
('农资财务汇总', 'erp:agri-report:query', 2, 9, @AGRI_PARENT_ID, 'agri-finance', 'erp/agri/report/AgriFinance', 'AgriFinance', 0, 1, 1, 0, 'ep:money', NOW(), NOW(), '1', '1', 0),
('供应商管理', 'erp:supplier:query', 2, 10, @AGRI_PARENT_ID, 'supplier', 'erp/purchase/supplier/index', 'ErpSupplier', 0, 1, 1, 0, 'ep:office-building', NOW(), NOW(), '1', '1', 0),
('农资商品管理', 'erp:product:query', 2, 11, @AGRI_PARENT_ID, 'product', 'erp/product/product/index', 'ErpProduct', 0, 1, 1, 0, 'ep:goods', NOW(), NOW(), '1', '1', 0);

-- ----------------------------
-- 5. 菜单精简模式 (夫妻店专享)
-- 隐藏所有非核心农资模块，确保界面清爽
-- ----------------------------
-- a. 鸠占鹊巢：将原“首页”物理重定向到“经营合规驾驶舱”
UPDATE `system_menu` 
SET `name` = '经营合规驾驶舱', 
    `path` = 'index', 
    `component` = 'erp/agri/report/Dashboard', 
    `component_name` = 'AgriDashboard',
    `icon` = 'ep:odometer',
    `visible` = 1
WHERE `name` = '首页' OR `path` = 'index';

-- b. 隐藏其它所有无关的一级菜单
UPDATE `system_menu` SET `visible` = 0 WHERE `parent_id` = 0 AND `name` != '经营合规驾驶舱';

-- c. 重新插入农资管理系统入口 (作为二级归拢)
INSERT INTO `system_menu`(
    `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `icon`, `create_time`, `update_time`, `creator`, `updater`, `deleted`
) VALUES
('农资管理系统', 'erp:agri-report:query', 1, 0, 0, '/agri', 'Layout', NULL, 0, 1, 1, 1, 'ep:management', NOW(), NOW(), '1', '1', 0);
SET @PARENT_ID = LAST_INSERT_ID();

-- 将子菜单重新绑定到新父节点 (避免因 DELETE+INSERT 导致 parent_id 孤立)
UPDATE `system_menu` SET `parent_id` = @PARENT_ID WHERE `parent_id` = @AGRI_PARENT_ID AND `deleted` = 0;

-- d. 完成清理
COMMIT;

-- ----------------------------
-- 6. 字典数据 (DML) - 农资类型
-- ----------------------------
INSERT IGNORE INTO `system_dict_type` (`name`, `type`, `status`, `remark`, `creator`, `updater`, `deleted`, `create_time`, `update_time`)
VALUES ('农资类型', 'erp_agri_type', 0, '农药/化肥/种子', '1', '1', 0, NOW(), NOW());

INSERT IGNORE INTO `system_dict_data` (`sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `updater`, `deleted`, `create_time`, `update_time`)
VALUES
(1, '农药', '1', 'erp_agri_type', 0, 'danger', '', '高毒/一般农药', '1', '1', 0, NOW(), NOW()),
(2, '化肥', '2', 'erp_agri_type', 0, 'success', '', '各类化学肥料', '1', '1', 0, NOW(), NOW()),
(3, '种子', '3', 'erp_agri_type', 0, 'warning', '', '各类作物种子', '1', '1', 0, NOW(), NOW());

-- ----------------------------
-- 清理存储过程
-- ----------------------------
DROP PROCEDURE IF EXISTS `AddColSafe`;
