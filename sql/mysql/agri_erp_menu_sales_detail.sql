-- ----------------------------
-- 农资 ERP - “当日销售明细”菜单
-- ----------------------------
-- 父菜单 ID: 这里假设是 2190 (ERP -> 报表统计)
SET @PARENT_ID = 2190;

-- 1. 插入菜单
INSERT INTO `system_menu` (
    `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `component`, `status`, `visible`, `keep_alive`, `always_show`, `icon`, `create_time`, `update_time`, `creator`, `updater`, `deleted`
) VALUES (
    '当日销售明细', 'erp:agri-report:query', 2, 2, @PARENT_ID, 'sales-detail', 'erp/agri/report/SalesDetail', 0, 1, 1, 0, 'ep:list', NOW(), NOW(), '1', '1', 0
);
