-- ----------------------------
-- 农资 ERP - “经营合规驾驶舱”菜单
-- ----------------------------
-- 父菜单 ID: 这里假设是 2190 (ERP -> 报表统计)
SET @PARENT_ID = 2190;

-- 1. 插入菜单 (排在第一位)
INSERT INTO `system_menu` (
    `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `component`, `status`, `visible`, `keep_alive`, `always_show`, `icon`, `create_time`, `update_time`, `creator`, `updater`, `deleted`
) VALUES (
    '经营合规驾驶舱', 'erp:agri-report:query', 2, 0, @PARENT_ID, 'agri-dashboard', 'erp/agri/report/Dashboard', 0, 1, 1, 0, 'ep:odometer', NOW(), NOW(), '1', '1', 0
);
