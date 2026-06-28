-- 1. 机场基础信息表（对应 BASE-APUE）
CREATE TABLE airport_base (
                              id INT PRIMARY KEY AUTO_INCREMENT,
                              code VARCHAR(10) COMMENT '机场三字码',
                              frcd VARCHAR(10) COMMENT '机场四字码',
                              apat VARCHAR(10) COMMENT '机场属性',
                              cnnm VARCHAR(50) COMMENT '中文名称',
                              ennm VARCHAR(50) COMMENT '英文名称',
                              aiso VARCHAR(5) COMMENT '是否开启',
                              apsn VARCHAR(20) COMMENT '航站简称',
                              meta_sndr VARCHAR(20) COMMENT '消息发送者',
                              meta_seqn VARCHAR(20) COMMENT '消息序号',
                              meta_ddtm VARCHAR(20) COMMENT '消息时间',
                              create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 2. 航班衔接变更表（对应 DFME-AFID）
CREATE TABLE flight_afid (
                             id INT PRIMARY KEY AUTO_INCREMENT,
                             flid VARCHAR(20) COMMENT '航班唯一编号',
                             ffid VARCHAR(50) COMMENT '航班标识(旧)',
                             fide VARCHAR(50) COMMENT '航班标识(新)',
                             afid VARCHAR(20) COMMENT '衔接航班ID(空表示取消)',
                             meta_sndr VARCHAR(20),
                             meta_seqn VARCHAR(20),
                             meta_ddtm VARCHAR(20),
                             create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 3. 航班删除记录表（对应 DFOE-DFDE）
CREATE TABLE flight_delete (
                               id INT PRIMARY KEY AUTO_INCREMENT,
                               flid VARCHAR(20) COMMENT '航班唯一编号',
                               ffid VARCHAR(50) COMMENT '航班标识(旧)',
                               fide VARCHAR(50) COMMENT '航班标识(新)',
                               meta_sndr VARCHAR(20),
                               meta_seqn VARCHAR(20),
                               meta_ddtm VARCHAR(20),
                               create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);