-- 农资 ERP 电子台账菜单初始化
SET @parent_id = (SELECT id FROM system_menu WHERE alias = 'ErpStatistics' LIMIT 1);

-- 插入限用农药电子台账菜单
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES ('限用农药台账', 'erp:agri-report:query', 2, 89, @parent_id, 'restricted-sale-ledger', 'list', 'erp/agri/report/RestrictedSale', 0, 1, 1, 1, 'admin', NOW(), 'admin', NOW(), 0);
