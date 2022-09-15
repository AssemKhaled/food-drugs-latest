CREATE TABLE IF NOT EXISTS `tc_users_tokens` ( 
  `id` int(11) NOT NULL auto_increment,  
  `userid` int(11) NOT NULL,
  `tokenid` LONGTEXT NOT NULL,
  PRIMARY KEY  (`id`)
);


CREATE TABLE IF NOT EXISTS `tc_warehouses` (

  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL,
  `activity` varchar(50) DEFAULT NULL,
  `city` varchar(128) DEFAULT NULL,
  `address` varchar(4000) DEFAULT NULL,
  `latitude` double DEFAULT NULL,
  `longitude` double DEFAULT NULL,
  `licenseNumber` varchar(50) DEFAULT NULL,
  `licenseIssueDate` varchar(128) DEFAULT NULL,
  `licenseExpiryDate` varchar(128) DEFAULT NULL,
  `phone` varchar(50) DEFAULT NULL,
  `email` varchar(128) DEFAULT NULL,
  `landAreaInSquareMeter` varchar(50) DEFAULT NULL,
  `userId` int(11) DEFAULT NULL,
  `landCoordinates` varchar(10000) DEFAULT NULL,
  `managerMobile` varchar(40) DEFAULT NULL,
  `referenceKey` varchar(400) DEFAULT NULL,
  `reject_reason` varchar(4000) DEFAULT NULL,
  `delete_date` varchar(255)  NULL,
  `create_date` varchar(255)  NULL,
  `photo` LONGTEXT  NULL,
  PRIMARY KEY (`id`),
  KEY `userId` (`userId`),
  CONSTRAINT `tc_warehouses_ibfk_1` FOREIGN KEY (`userId`) REFERENCES `tc_users` (`id`)

)ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS `tc_inventories` (

  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(512) NOT NULL,
  `inventoryNumber` varchar(255) DEFAULT NULL,
  `activity` varchar(255) DEFAULT NULL,
  `storingCategory` varchar(255) DEFAULT NULL,
  `referenceKey` varchar(1080) DEFAULT NULL,
  `reject_reason` varchar(4000) DEFAULT NULL,
  `protocolType` varchar(128) DEFAULT NULL,
  `delete_date` varchar(255)  NULL,
  `create_date` varchar(255)  NULL,
  `trackerIMEI` varchar(255) DEFAULT NULL,
  `lastDataId` varchar(255) DEFAULT NULL,
  `lastUpdate` varchar(255) DEFAULT NULL,
  `GUID` varchar(255) DEFAULT NULL,
  `emailEasyCloud` varchar(255) DEFAULT NULL,
  `passwordEasyCloud` varchar(255) DEFAULT NULL,
  `APIToken` varchar(255) DEFAULT NULL,
  `userId` int(11) DEFAULT NULL,
  `warehouseId` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `userId` (`userId`),
  CONSTRAINT `tc_inventories_ibfk_1` FOREIGN KEY (`userId`) REFERENCES `tc_users` (`id`),
  KEY `warehouseId` (`warehouseId`),
  CONSTRAINT `tc_inventories_ibfk_2` FOREIGN KEY (`warehouseId`) REFERENCES `tc_warehouses` (`id`)

)ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE utf8_unicode_ci;


CREATE TABLE IF NOT EXISTS `tc_sensors_inventories` (

  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(512) NOT NULL,
  `type` varchar(512) NOT NULL,
  `inventoryId` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `inventoryId` (`inventoryId`),
  CONSTRAINT `tc_sensors_inventories_ibfk_1` FOREIGN KEY (`inventoryId`) REFERENCES `tc_inventories` (`id`)

)ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE utf8_unicode_ci;


CREATE TABLE IF NOT EXISTS `tc_user_client_device` (
  `id` int(11) NOT NULL auto_increment, 
  `userid` int(11) NOT NULL,
  `deviceid` int(11) NOT NULL,
   PRIMARY KEY  (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE utf8_unicode_ci;
CREATE TABLE IF NOT EXISTS `tc_user_client_driver` (
  `id` int(11) NOT NULL auto_increment, 
  `userid` int(11) NOT NULL,
  `driverid` int(11) NOT NULL,
   PRIMARY KEY  (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE utf8_unicode_ci;
CREATE TABLE IF NOT EXISTS `tc_user_client_group` (
  `id` int(11) NOT NULL auto_increment, 
  `userid` int(11) NOT NULL,
  `groupid` int(11) NOT NULL,
   PRIMARY KEY  (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE utf8_unicode_ci;
CREATE TABLE IF NOT EXISTS `tc_user_client_geofence` (
  `id` int(11) NOT NULL auto_increment, 
  `userid` int(11) NOT NULL,
  `geofenceid` int(11) NOT NULL,
   PRIMARY KEY  (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE utf8_unicode_ci;
CREATE TABLE IF NOT EXISTS `tc_user_client_computed` (
  `id` int(11) NOT NULL auto_increment, 
  `userid` int(11) NOT NULL,
  `computedid` int(11) NOT NULL,
   PRIMARY KEY  (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE utf8_unicode_ci;
CREATE TABLE IF NOT EXISTS `tc_user_client_point` (
  `id` int(11) NOT NULL auto_increment, 
  `userid` int(11) NOT NULL,
  `pointid` int(11) NOT NULL,
   PRIMARY KEY  (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE utf8_unicode_ci;
CREATE TABLE IF NOT EXISTS `tc_user_client_warehouse` (
  `id` int(11) NOT NULL auto_increment, 
  `userid` int(11) NOT NULL,
  `warehouseid` int(11) NOT NULL,
   PRIMARY KEY  (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE utf8_unicode_ci;
CREATE TABLE IF NOT EXISTS `tc_user_client_inventory` (
  `id` int(11) NOT NULL auto_increment, 
  `userid` int(11) NOT NULL,
  `inventoryid` int(11) NOT NULL,
   PRIMARY KEY  (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE utf8_unicode_ci;
CREATE TABLE IF NOT EXISTS `tc_points` (

  `id` int(11) NOT NULL auto_increment, 
  `name` varchar(512)  NULL,
  `latitude` double  NULL,
  `longitude` double  NULL,
  `userId` int(11),
  `photo` LONGTEXT  NULL,
  `delete_date` varchar(255)  NULL,
   PRIMARY KEY  (`id`)

)ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE utf8_unicode_ci;
CREATE TABLE IF NOT EXISTS `tc_schedule` (

  `id` int(11) NOT NULL auto_increment, 
  `expression` varchar(4000)  NULL,
  `task` LONGTEXT  NULL,
  `userId` int(11), 
  `date` varchar(255)  NULL,
  `date_type` varchar(255)  NULL,
  `email` varchar(255)  NULL,
  `delete_date` varchar(255)  NULL,
   PRIMARY KEY  (`id`)

)ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS `tc_user_roles` (

  `roleId` int(11) NOT NULL auto_increment,   
  `delete_date` varchar(255)  NULL,
  `name` varchar(255)  NULL,
  `permissions` LONGTEXT  NULL,
  `userId` int(11), 
   PRIMARY KEY  (`roleId`)

)ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE utf8_unicode_ci;
CREATE TABLE IF NOT EXISTS `tc_permissions` (

  `id` int(11) NOT NULL auto_increment,   
  `delete_date` varchar(255)  NULL,
  `functionality` LONGTEXT  NULL,
  `name` varchar(255)  NULL,
   PRIMARY KEY  (`id`)

)ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS `tc_group_device` (
  `groupid` int(11) NOT NULL,
  `deviceid` int(11) NOT NULL,
  KEY `fk_group_device_groupid` (`groupid`),
  KEY `fk_group_device_deviceid` (`deviceid`),
  CONSTRAINT `fk_group_device_deviceid` FOREIGN KEY (`deviceid`) REFERENCES `tc_devices` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_group_device_groupid` FOREIGN KEY (`groupid`) REFERENCES `tc_groups` (`id`) ON DELETE CASCADE

)ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE utf8_unicode_ci;

----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_users'
AND column_name='accountType'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_users'
, ' ADD COLUMN  `accountType` INT(11) NULL DEFAULT 0 '
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_devices'
AND column_name='simcardNumber'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_devices'
, ' ADD COLUMN `simcardNumber` text NULL DEFAULT NULL'
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_users'
AND column_name='exp_date'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_users'
, ' ADD COLUMN `exp_date` timestamp NULL DEFAULT NULL'
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_users'
AND column_name='parents'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_users'
, ' ADD COLUMN `parents` VARCHAR(255) NULL DEFAULT NULL'
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_users'
AND column_name='roleId'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_users'
, ' ADD COLUMN `roleId` INT(11) NULL DEFAULT NULL '
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_devices'
AND column_name='fuel'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_devices'
, ' ADD COLUMN `fuel` varchar(1080) NULL DEFAULT NULL'
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_devices'
AND column_name='representative'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_devices'
, ' ADD COLUMN `representative` LONGTEXT NULL DEFAULT NULL'
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_devices'
AND column_name='delete_from_elm'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_devices'
, ' ADD COLUMN `delete_from_elm` LONGTEXT NULL DEFAULT NULL'
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_devices'
AND column_name='icon'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_devices'
, ' ADD COLUMN `icon` varchar(1080) NULL DEFAULT NULL'
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_devices'
AND column_name='create_date'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_devices'
, ' ADD COLUMN `create_date` timestamp NULL DEFAULT NULL'
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_users'
AND column_name='create_date'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_users'
, ' ADD COLUMN `create_date` timestamp NULL DEFAULT NULL'
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_devices'
AND column_name='expired'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_devices'
, ' ADD COLUMN  `expired` TINYINT(1) DEFAULT 0 NOT NULL '
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_devices'
AND column_name='port'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_devices'
, ' ADD COLUMN `port` text NULL DEFAULT NULL'
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_devices'
AND column_name='protocol'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_devices'
, ' ADD COLUMN `protocol` text NULL DEFAULT NULL'
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_devices'
AND column_name='device_type'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_devices'
, ' ADD COLUMN `device_type` text NULL DEFAULT NULL'
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_devices'
AND column_name='sensorSettings'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_devices'
, ' ADD COLUMN `sensorSettings` varchar(1080) NULL DEFAULT NULL'
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_groups'
AND column_name='type'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_groups'
, ' ADD COLUMN `type` varchar(255) NULL DEFAULT NULL'
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_attributes'
AND column_name='delete_date'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_attributes'
, ' ADD COLUMN `delete_date` varchar(255) NULL DEFAULT NULL '
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_notifications'
AND column_name='delete_date'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_notifications'
, ' ADD COLUMN `delete_date` varchar(255) NULL DEFAULT NULL '
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_users'
AND column_name='dateType'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_users'
, ' ADD COLUMN  `dateType` INT(11) NULL DEFAULT 0 '
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_warehouses'
AND column_name='licenseIssueDateType'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_warehouses'
, ' ADD COLUMN  `licenseIssueDateType` INT(11) NULL DEFAULT 0 '
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_warehouses'
AND column_name='licenseExpiryDateType'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_warehouses'
, ' ADD COLUMN  `licenseExpiryDateType` INT(11) NULL DEFAULT 0 '
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_users'
AND column_name='dateOfBirth'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_users'
, ' ADD COLUMN `dateOfBirth` date NULL DEFAULT NULL'
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_devices'
AND column_name='user_id'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_devices'
, ' ADD COLUMN `user_id` int(11) NULL DEFAULT NULL'
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_devices'
AND column_name='regestration_to_elm_date'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_devices'
, ' ADD COLUMN `regestration_to_elm_date` date NULL DEFAULT NULL'
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_devices'
AND column_name='delete_from_elm_date'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_devices'
, ' ADD COLUMN `delete_from_elm_date` date NULL DEFAULT NULL'
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_devices'
AND column_name='update_date_in_elm'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_devices'
, ' ADD COLUMN `update_date_in_elm` date NULL DEFAULT NULL'
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_schedule'
AND column_name='email'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_schedule'
, ' ADD COLUMN `email` varchar(255) NULL DEFAULT NULL '
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_devices'
AND column_name='position_id'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_devices'
, ' ADD COLUMN `position_id` text NULL DEFAULT NULL'
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_inventories'
AND column_name='GUID'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_inventories'
, ' ADD COLUMN `GUID` varchar(255) NULL DEFAULT NULL '
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_inventories'
AND column_name='emailEasyCloud'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_inventories'
, ' ADD COLUMN `emailEasyCloud` varchar(255) NULL DEFAULT NULL '
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_inventories'
AND column_name='passwordEasyCloud'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_inventories'
, ' ADD COLUMN `passwordEasyCloud` varchar(255) NULL DEFAULT NULL '
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_inventories'
AND column_name='APIToken'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_inventories'
, ' ADD COLUMN `APIToken` varchar(255) NULL DEFAULT NULL '
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_inventories'
AND column_name='lastUpdate'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_inventories'
, ' ADD COLUMN `lastUpdate` varchar(255) NULL DEFAULT NULL '
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_inventories'
AND column_name='lastDataId'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_inventories'
, ' ADD COLUMN `lastDataId` varchar(255) NULL DEFAULT NULL '
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_devices'
AND column_name='DTYPE'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_devices'
, ' ADD COLUMN `DTYPE` varchar(20) NULL DEFAULT NULL '
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_drivers'
AND column_name='DTYPE'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_drivers'
, ' ADD COLUMN `DTYPE` varchar(20) NULL DEFAULT NULL '
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_users'
AND column_name='DTYPE'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_users'
, ' ADD COLUMN `DTYPE` varchar(20) NULL DEFAULT NULL '
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_points'
AND column_name='attributes'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_points'
, ' ADD COLUMN `attributes` LONGTEXT NULL DEFAULT NULL '
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_attributes'
AND column_name='attributes'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_attributes'
, ' ADD COLUMN `attributes` LONGTEXT NULL DEFAULT NULL '
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_schedule'
AND column_name='attributes'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_schedule'
, ' ADD COLUMN `attributes` LONGTEXT NULL DEFAULT NULL '
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_permissions'
AND column_name='attributes'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_permissions'
, ' ADD COLUMN `attributes` LONGTEXT NULL DEFAULT NULL '
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_user_roles'
AND column_name='attributes'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_user_roles'
, ' ADD COLUMN `attributes` LONGTEXT NULL DEFAULT NULL '
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_drivers'
AND column_name='regestration_to_elm_date'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_drivers'
, ' ADD COLUMN `regestration_to_elm_date` date NULL DEFAULT NULL'
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_drivers'
AND column_name='delete_from_elm_date'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_drivers'
, ' ADD COLUMN `delete_from_elm_date` date NULL DEFAULT NULL'
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_drivers'
AND column_name='update_date_in_elm'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_drivers'
, ' ADD COLUMN `update_date_in_elm` date NULL DEFAULT NULL'
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_users'
AND column_name='regestration_to_elm_date'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_users'
, ' ADD COLUMN `regestration_to_elm_date` date NULL DEFAULT NULL'
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_users'
AND column_name='delete_from_elm_date'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_users'
, ' ADD COLUMN `delete_from_elm_date` date NULL DEFAULT NULL'
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_users'
AND column_name='update_date_in_elm'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_users'
, ' ADD COLUMN `update_date_in_elm` date NULL DEFAULT NULL'
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;


----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_inventories'
AND column_name='regestration_to_elm_date'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_inventories'
, ' ADD COLUMN `regestration_to_elm_date` date NULL DEFAULT NULL'
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_inventories'
AND column_name='delete_from_elm_date'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_inventories'
, ' ADD COLUMN `delete_from_elm_date` date NULL DEFAULT NULL'
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_inventories'
AND column_name='update_date_in_elm'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_inventories'
, ' ADD COLUMN `update_date_in_elm` date NULL DEFAULT NULL'
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;


----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_warehouses'
AND column_name='regestration_to_elm_date'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_warehouses'
, ' ADD COLUMN `regestration_to_elm_date` date NULL DEFAULT NULL'
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_warehouses'
AND column_name='delete_from_elm_date'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_warehouses'
, ' ADD COLUMN `delete_from_elm_date` date NULL DEFAULT NULL'
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_warehouses'
AND column_name='update_date_in_elm'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_warehouses'
, ' ADD COLUMN `update_date_in_elm` date NULL DEFAULT NULL'
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_devices'
AND column_name='lastTemp'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_devices'
, ' ADD COLUMN `lastTemp` double DEFAULT 0'
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

----------------------------------------------------------------
set @col_exists = 0;
SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME='tc_devices'
AND column_name='lastHum'
and table_schema = database()
into @col_exists;

set @stmt = case @col_exists
when 0 then CONCAT(
'alter table tc_devices'
, ' ADD COLUMN `lastHum` double DEFAULT 0'
,';')
else 'select ''column already exists, no op'''
end;

PREPARE stmt FROM @stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
----------------------------------------------------------------
--ALTER TABLE `tc_devices` MODIFY `calibrationData` VARCHAR(1080);
--ALTER TABLE `tc_devices` DROP `positionid`;
--ALTER TABLE `tc_devices` ADD COLUMN `positionid` text NULL DEFAULT NULL;

--ALTER TABLE `tc_permissions` MODIFY `functionality` VARCHAR(1080);

----------------------------------------------------------------
--ALTER TABLE `tc_users`
--ADD COLUMN  `accountType` INT(11) NULL DEFAULT 0 ,
--ADD COLUMN  `dateType` INT(11) NULL DEFAULT 0 ,
--ADD COLUMN `dateOfBirth` VARCHAR(255) NULL DEFAULT NULL ,
--ADD COLUMN `parents` VARCHAR(255) NULL DEFAULT NULL ,
--ADD COLUMN `roleId` INT(11) NULL DEFAULT NULL ;

--ALTER TABLE `tc_devices` ADD `sensorSettings` varchar(1080) NULL DEFAULT NULL;
--ALTER TABLE `tc_devices` ADD `fuel` varchar(1080) NULL DEFAULT NULL;
--ALTER TABLE `tc_groups` ADD `type` varchar(255) NULL DEFAULT NULL;

--ALTER TABLE `tc_attributes` ADD `delete_date` varchar(255) NULL DEFAULT NULL;
--ALTER TABLE `tc_notifications` ADD `delete_date` varchar(255) NULL DEFAULT NULL;
