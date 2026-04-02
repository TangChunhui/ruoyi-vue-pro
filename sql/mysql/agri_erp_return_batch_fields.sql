-- Agri ERP: Add batch traceability fields to return items
ALTER TABLE `erp_purchase_return_items`
ADD COLUMN `batch_no` varchar(50) DEFAULT NULL COMMENT '生产批次',
ADD COLUMN `production_date` datetime DEFAULT NULL COMMENT '生产日期',
ADD COLUMN `expiry_date` datetime DEFAULT NULL COMMENT '有效期至';

ALTER TABLE `erp_sale_return_items`
ADD COLUMN `batch_no` varchar(50) DEFAULT NULL COMMENT '生产批次',
ADD COLUMN `production_date` datetime DEFAULT NULL COMMENT '生产日期',
ADD COLUMN `expiry_date` datetime DEFAULT NULL COMMENT '有效期至';
