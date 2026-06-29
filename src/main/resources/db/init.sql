-- 1. dfme_message 表
CREATE TABLE dfme_message (
                              id BIGINT PRIMARY KEY AUTO_INCREMENT,
                              sndr VARCHAR(20),
                              rcvr VARCHAR(20),
                              seqn VARCHAR(20),
                              ddtm VARCHAR(20),
                              type VARCHAR(10) DEFAULT 'DFME',
                              styp VARCHAR(20) NOT NULL COMMENT '子类型：STLS/GTLS/BLLS...',
                              flid VARCHAR(20),
                              ffid VARCHAR(50),
                              fide VARCHAR(50),
                              awcd VARCHAR(10),
                              flno VARCHAR(20),
                              mfid VARCHAR(20),
                              mffi VARCHAR(50),
                              raw_data JSON COMMENT '完整XML转JSON',
                              create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                              INDEX idx_styp (styp),
                              INDEX idx_flid (flid),
                              INDEX idx_fide (fide)
);

-- 2. base_message 表
CREATE TABLE base_message (
                              id BIGINT PRIMARY KEY AUTO_INCREMENT,
                              sndr VARCHAR(20),
                              rcvr VARCHAR(20),
                              seqn VARCHAR(20),
                              ddtm VARCHAR(20),
                              type VARCHAR(10) DEFAULT 'BASE',
                              styp VARCHAR(20) NOT NULL COMMENT 'APUE/CFIE/CFUE',
                              code VARCHAR(20) COMMENT '机场三字码',
                              frcd VARCHAR(20) COMMENT '机场四字码',
                              apat VARCHAR(10) COMMENT '机场属性',
                              cnnm VARCHAR(100) COMMENT '中文名称',
                              ennm VARCHAR(100) COMMENT '英文名称',
                              aiso VARCHAR(5) COMMENT '是否开启',
                              apsn VARCHAR(20) COMMENT '航站简称',
                              cf_code VARCHAR(20) COMMENT '飞机编号',
                              cf_tp VARCHAR(20) COMMENT '飞机机型',
                              awcd VARCHAR(10) COMMENT '航空公司',
                              stnm VARCHAR(10) COMMENT '最大座位数',
                              rstn VARCHAR(10) COMMENT '可供座位数',
                              raw_data JSON,
                              create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                              INDEX idx_styp (styp),
                              INDEX idx_code (code)
);

-- 3. dfoe_message 表
CREATE TABLE dfoe_message (
                              id BIGINT PRIMARY KEY AUTO_INCREMENT,
                              sndr VARCHAR(20),
                              rcvr VARCHAR(20),
                              seqn VARCHAR(20),
                              ddtm VARCHAR(20),
                              type VARCHAR(10) DEFAULT 'DFOE',
                              styp VARCHAR(20) NOT NULL COMMENT 'DFIE/DFDL/DFDE',
                              flid VARCHAR(20),
                              ffid VARCHAR(50),
                              fide VARCHAR(50),
                              dltp VARCHAR(10) COMMENT '下载类型',
                              recd VARCHAR(10) COMMENT '记录总数',
                              raw_data JSON,
                              create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                              INDEX idx_styp (styp),
                              INDEX idx_flid (flid)
);