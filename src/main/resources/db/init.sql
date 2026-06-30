-- ============================================================
-- 1. 公共表：TB_META（消息头）
-- ============================================================
CREATE TABLE TB_META (
                         id BIGINT PRIMARY KEY AUTO_INCREMENT,
                         sndr VARCHAR(20) COMMENT '消息发送者',
                         rcvr VARCHAR(20) COMMENT '消息接收者',
                         seqn VARCHAR(20) COMMENT '消息序号',
                         ddtm VARCHAR(20) COMMENT '发送时间',
                         type VARCHAR(10) COMMENT '消息大类型：BASE/DFME/DFOE',
                         styp VARCHAR(20) COMMENT '消息子类型：APUE/STLS/DEPE...',
                         create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                         INDEX idx_type (type),
                         INDEX idx_styp (styp)
);

-- ============================================================
-- 2. 公共表：TB_FATT（航班属性字典）
-- ============================================================
CREATE TABLE TB_FATT (
                         id BIGINT PRIMARY KEY AUTO_INCREMENT,
                         code VARCHAR(10) NOT NULL COMMENT '属性代码：2401/2402/2403/2404',
                         name VARCHAR(50) COMMENT '属性名称：国际/地区/国内/混合',
                         create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                         UNIQUE KEY uk_code (code)
);

-- 初始化数据
INSERT INTO TB_FATT (code, name) VALUES
                                     ('2401', '国际'),
                                     ('2402', '地区'),
                                     ('2403', '国内'),
                                     ('2404', '混合');

-- ============================================================
-- 3. 公共表：TB_STATE（航班状态字典）
-- ============================================================
CREATE TABLE TB_STATE (
                          id BIGINT PRIMARY KEY AUTO_INCREMENT,
                          code VARCHAR(20) NOT NULL COMMENT '状态编码：DEP/ARR/DLY/CAN...',
                          name VARCHAR(50) COMMENT '状态名称：起飞/到达/延误/取消...',
                          category VARCHAR(10) COMMENT '类别：正常/异常',
                          create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                          UNIQUE KEY uk_code (code)
);

-- 初始化数据
INSERT INTO TB_STATE (code, name, category) VALUES
                                                ('DEP', '起飞', '正常'),
                                                ('ARR', '到达', '正常'),
                                                ('ONR', '前站起飞', '正常'),
                                                ('CKI', '开始值机', '正常'),
                                                ('CKO', '截止值机', '正常'),
                                                ('BOR', '开始登机', '正常'),
                                                ('TBR', '过站登机', '正常'),
                                                ('LBD', '催促登机', '正常'),
                                                ('POK', '结束登机', '正常'),
                                                ('DLY', '延误', '异常'),
                                                ('CAN', '取消', '异常'),
                                                ('RTN', '返航', '异常'),
                                                ('BAK', '滑回', '异常'),
                                                ('ALT', '备降', '异常');

-- ============================================================
-- 4. BASE-APUE：机场信息
-- ============================================================
CREATE TABLE APUE_APOT (
                           id BIGINT PRIMARY KEY AUTO_INCREMENT,
                           meta_id BIGINT COMMENT '关联TB_META.id',
                           code VARCHAR(10) COMMENT '机场三字码',
                           frcd VARCHAR(10) COMMENT '机场四字码',
                           apat VARCHAR(10) COMMENT '机场属性',
                           cnnm VARCHAR(100) COMMENT '中文名称',
                           ennm VARCHAR(100) COMMENT '英文名称',
                           aiso VARCHAR(5) COMMENT '是否开启',
                           apsn VARCHAR(20) COMMENT '航站简称',
                           create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                           INDEX idx_meta_id (meta_id),
                           INDEX idx_code (code)
);

-- ============================================================
-- 5. BASE-CFIE：飞机增加
-- ============================================================
CREATE TABLE CFIE_CRFT (
                           id BIGINT PRIMARY KEY AUTO_INCREMENT,
                           meta_id BIGINT COMMENT '关联TB_META.id',
                           code VARCHAR(20) COMMENT '飞机编号',
                           cftp VARCHAR(20) COMMENT '机型',
                           awcd VARCHAR(10) COMMENT '所属航空公司',
                           stnm INT COMMENT '最大座位数',
                           rstn INT COMMENT '可供座位数',
                           create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                           INDEX idx_meta_id (meta_id),
                           INDEX idx_code (code)
);

-- ============================================================
-- 6. BASE-CFUE：飞机变更
-- ============================================================
CREATE TABLE CFUE_CRFT (
                           id BIGINT PRIMARY KEY AUTO_INCREMENT,
                           meta_id BIGINT COMMENT '关联TB_META.id',
                           code VARCHAR(20) COMMENT '飞机编号',
                           cftp VARCHAR(20) COMMENT '机型',
                           awcd VARCHAR(10) COMMENT '所属航空公司',
                           stnm INT COMMENT '最大座位数',
                           rstn INT COMMENT '可供座位数',
                           create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                           INDEX idx_meta_id (meta_id),
                           INDEX idx_code (code)
);

-- ============================================================
-- 7. DFOE-DFIE：航班增加
-- ============================================================
CREATE TABLE TB_DFIEBODY (
                             id BIGINT PRIMARY KEY AUTO_INCREMENT,
                             meta_id BIGINT COMMENT '关联TB_META.id',
                             flid VARCHAR(20) COMMENT '航班唯一编号',
                             ffid VARCHAR(50) COMMENT '航班标识(旧)',
                             fide VARCHAR(50) COMMENT '航班标识(新)',
                             awcd VARCHAR(10) COMMENT '航空公司',
                             flno VARCHAR(20) COMMENT '航班号',
                             fexd VARCHAR(20) COMMENT '执行日期',
                             flio VARCHAR(5) COMMENT '进出标志',
                             fltk VARCHAR(10) COMMENT '航班任务',
                             fatt VARCHAR(10) COMMENT '航班属性',
                             cftp VARCHAR(20) COMMENT '机型',
                             cfno VARCHAR(20) COMMENT '机号',
                             create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                             INDEX idx_meta_id (meta_id),
                             INDEX idx_flid (flid),
                             INDEX idx_fide (fide)
);

-- ============================================================
-- 8. DFOE-DFDE：航班删除
-- ============================================================
CREATE TABLE TB_DFDEBODY (
                             id BIGINT PRIMARY KEY AUTO_INCREMENT,
                             meta_id BIGINT COMMENT '关联TB_META.id',
                             flid VARCHAR(20) COMMENT '航班唯一编号',
                             ffid VARCHAR(50) COMMENT '航班标识(旧)',
                             fide VARCHAR(50) COMMENT '航班标识(新)',
                             create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                             INDEX idx_meta_id (meta_id),
                             INDEX idx_flid (flid),
                             INDEX idx_fide (fide)
);

-- ============================================================
-- 9. DFOE-DFDL：整表同步
-- ============================================================
CREATE TABLE TB_DFDLBODY (
                             id BIGINT PRIMARY KEY AUTO_INCREMENT,
                             meta_id BIGINT COMMENT '关联TB_META.id',
                             dltp VARCHAR(20) COMMENT '下载类型：FLID/DFTM/空',
                             recd INT COMMENT '记录总数',
                             create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                             INDEX idx_meta_id (meta_id)
);

-- ============================================================
-- 10. DFME-AFID：航班衔接变更
-- ============================================================
CREATE TABLE AFID_DFLT (
                           id BIGINT PRIMARY KEY AUTO_INCREMENT,
                           meta_id BIGINT COMMENT '关联TB_META.id',
                           flid VARCHAR(20) COMMENT '航班唯一编号',
                           ffid VARCHAR(50) COMMENT '航班标识(旧)',
                           fide VARCHAR(50) COMMENT '航班标识(新)',
                           afid VARCHAR(20) COMMENT '衔接航班ID（空表示取消衔接）',
                           create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                           INDEX idx_meta_id (meta_id),
                           INDEX idx_flid (flid),
                           INDEX idx_fide (fide)
);

-- ============================================================
-- 11. DFME-AIRL：航线变更（主表）
-- ============================================================
CREATE TABLE AIRL_DFLT (
                           id BIGINT PRIMARY KEY AUTO_INCREMENT,
                           meta_id BIGINT COMMENT '关联TB_META.id',
                           flid VARCHAR(20) COMMENT '航班唯一编号',
                           ffid VARCHAR(50) COMMENT '航班标识(旧)',
                           fide VARCHAR(50) COMMENT '航班标识(新)',
                           awcd VARCHAR(10) COMMENT '航空公司',
                           flno VARCHAR(20) COMMENT '航班号',
                           flio VARCHAR(5) COMMENT '进出标志',
                           create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                           INDEX idx_meta_id (meta_id),
                           INDEX idx_flid (flid),
                           INDEX idx_fide (fide)
);

-- ============================================================
-- 12. DFME-AIRL：航线变更（明细表-航站列表）
-- ============================================================
CREATE TABLE AIRL_DFLTDETAIL (
                                 id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                 master_id BIGINT COMMENT '关联AIRL_DFLT.id',
                                 apno INT COMMENT '航站序号',
                                 apcd VARCHAR(10) COMMENT '航站三字码',
                                 fptt VARCHAR(20) COMMENT '计划起飞时间',
                                 fplt VARCHAR(20) COMMENT '计划降落时间',
                                 apat VARCHAR(10) COMMENT '航站属性',
                                 create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                                 INDEX idx_master_id (master_id)
);

-- ============================================================
-- 13. DFME-ARRE：到达本站
-- ============================================================
CREATE TABLE ARRE_DFLT (
                           id BIGINT PRIMARY KEY AUTO_INCREMENT,
                           meta_id BIGINT COMMENT '关联TB_META.id',
                           flid VARCHAR(20) COMMENT '航班唯一编号',
                           ffid VARCHAR(50) COMMENT '航班标识(旧)',
                           fide VARCHAR(50) COMMENT '航班标识(新)',
                           awcd VARCHAR(10) COMMENT '航空公司',
                           flno VARCHAR(20) COMMENT '航班号',
                           flio VARCHAR(5) COMMENT '进出标志',
                           frlt VARCHAR(20) COMMENT '实际降落时间',
                           ista VARCHAR(10) COMMENT '航班状态',
                           create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                           INDEX idx_meta_id (meta_id),
                           INDEX idx_flid (flid),
                           INDEX idx_fide (fide)
);

-- ============================================================
-- 14. DFME-BORE：开始登机
-- ============================================================
CREATE TABLE BORE_DFLT (
                           id BIGINT PRIMARY KEY AUTO_INCREMENT,
                           meta_id BIGINT COMMENT '关联TB_META.id',
                           flid VARCHAR(20) COMMENT '航班唯一编号',
                           ffid VARCHAR(50) COMMENT '航班标识(旧)',
                           fide VARCHAR(50) COMMENT '航班标识(新)',
                           awcd VARCHAR(10) COMMENT '航空公司',
                           flno VARCHAR(20) COMMENT '航班号',
                           bort VARCHAR(20) COMMENT '开始登机时间',
                           mbor VARCHAR(20) COMMENT '国际部分开始登机时间',
                           ista VARCHAR(10) COMMENT '航班状态',
                           create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                           INDEX idx_meta_id (meta_id),
                           INDEX idx_flid (flid),
                           INDEX idx_fide (fide)
);

-- ============================================================
-- 15. DFME-CANE：取消
-- ============================================================
CREATE TABLE CANE_DFLT (
                           id BIGINT PRIMARY KEY AUTO_INCREMENT,
                           meta_id BIGINT COMMENT '关联TB_META.id',
                           flid VARCHAR(20) COMMENT '航班唯一编号',
                           ffid VARCHAR(50) COMMENT '航班标识(旧)',
                           fide VARCHAR(50) COMMENT '航班标识(新)',
                           awcd VARCHAR(10) COMMENT '航空公司',
                           flno VARCHAR(20) COMMENT '航班号',
                           abst VARCHAR(10) COMMENT '不正常状态',
                           iast VARCHAR(10) COMMENT '内部不正常状态',
                           abrs VARCHAR(20) COMMENT '不正常原因',
                           create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                           INDEX idx_meta_id (meta_id),
                           INDEX idx_flid (flid),
                           INDEX idx_fide (fide)
);

-- ============================================================
-- 16. DFME-CFCE：更换飞机
-- ============================================================
CREATE TABLE CFCE_DFLT (
                           id BIGINT PRIMARY KEY AUTO_INCREMENT,
                           meta_id BIGINT COMMENT '关联TB_META.id',
                           flid VARCHAR(20) COMMENT '航班唯一编号',
                           ffid VARCHAR(50) COMMENT '航班标识(旧)',
                           fide VARCHAR(50) COMMENT '航班标识(新)',
                           cftp VARCHAR(20) COMMENT '新机型',
                           cfno VARCHAR(20) COMMENT '新机号',
                           create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                           INDEX idx_meta_id (meta_id),
                           INDEX idx_flid (flid),
                           INDEX idx_fide (fide)
);

-- ============================================================
-- 17. DFME-CKIE：开始值机
-- ============================================================
CREATE TABLE CKIE_DELT (
                           id BIGINT PRIMARY KEY AUTO_INCREMENT,
                           meta_id BIGINT COMMENT '关联TB_META.id',
                           flid VARCHAR(20) COMMENT '航班唯一编号',
                           ffid VARCHAR(50) COMMENT '航班标识(旧)',
                           fide VARCHAR(50) COMMENT '航班标识(新)',
                           awcd VARCHAR(10) COMMENT '航空公司',
                           flno VARCHAR(20) COMMENT '航班号',
                           fcrs VARCHAR(20) COMMENT '值机实际开始时间',
                           ista VARCHAR(10) COMMENT '航班状态',
                           create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                           INDEX idx_meta_id (meta_id),
                           INDEX idx_flid (flid),
                           INDEX idx_fide (fide)
);

-- ============================================================
-- 18. DFME-CKOE：截止值机
-- ============================================================
CREATE TABLE CKOE_DELT (
                           id BIGINT PRIMARY KEY AUTO_INCREMENT,
                           meta_id BIGINT COMMENT '关联TB_META.id',
                           flid VARCHAR(20) COMMENT '航班唯一编号',
                           ffid VARCHAR(50) COMMENT '航班标识(旧)',
                           fide VARCHAR(50) COMMENT '航班标识(新)',
                           awcd VARCHAR(10) COMMENT '航空公司',
                           flno VARCHAR(20) COMMENT '航班号',
                           fcre VARCHAR(20) COMMENT '值机实际结束时间',
                           ista VARCHAR(10) COMMENT '航班状态',
                           create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                           INDEX idx_meta_id (meta_id),
                           INDEX idx_flid (flid),
                           INDEX idx_fide (fide)
);

-- ============================================================
-- 19. DFME-DEPE：本站起飞
-- ============================================================
CREATE TABLE DEPE_DELT (
                           id BIGINT PRIMARY KEY AUTO_INCREMENT,
                           meta_id BIGINT COMMENT '关联TB_META.id',
                           flid VARCHAR(20) COMMENT '航班唯一编号',
                           ffid VARCHAR(50) COMMENT '航班标识(旧)',
                           fide VARCHAR(50) COMMENT '航班标识(新)',
                           awcd VARCHAR(10) COMMENT '航空公司',
                           flno VARCHAR(20) COMMENT '航班号',
                           frtt VARCHAR(20) COMMENT '实际起飞时间',
                           ista VARCHAR(10) COMMENT '航班状态',
                           create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                           INDEX idx_meta_id (meta_id),
                           INDEX idx_flid (flid),
                           INDEX idx_fide (fide)
);

-- ============================================================
-- 20. DFME-DLYE：延误
-- ============================================================
CREATE TABLE DLYE_DELT (
                           id BIGINT PRIMARY KEY AUTO_INCREMENT,
                           meta_id BIGINT COMMENT '关联TB_META.id',
                           flid VARCHAR(20) COMMENT '航班唯一编号',
                           ffid VARCHAR(50) COMMENT '航班标识(旧)',
                           fide VARCHAR(50) COMMENT '航班标识(新)',
                           awcd VARCHAR(10) COMMENT '航空公司',
                           flno VARCHAR(20) COMMENT '航班号',
                           abst VARCHAR(10) COMMENT '不正常状态',
                           iast VARCHAR(10) COMMENT '内部不正常状态',
                           abrs VARCHAR(20) COMMENT '不正常原因',
                           fett VARCHAR(20) COMMENT '预计时间',
                           create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                           INDEX idx_meta_id (meta_id),
                           INDEX idx_flid (flid),
                           INDEX idx_fide (fide)
);

-- ============================================================
-- 21. DFME-FETT：预计时间变更
-- ============================================================
CREATE TABLE FETT_DELT (
                           id BIGINT PRIMARY KEY AUTO_INCREMENT,
                           meta_id BIGINT COMMENT '关联TB_META.id',
                           flid VARCHAR(20) COMMENT '航班唯一编号',
                           ffid VARCHAR(50) COMMENT '航班标识(旧)',
                           fide VARCHAR(50) COMMENT '航班标识(新)',
                           awcd VARCHAR(10) COMMENT '航空公司',
                           flno VARCHAR(20) COMMENT '航班号',
                           fett VARCHAR(20) COMMENT '新预计起飞时间',
                           felt VARCHAR(20) COMMENT '新预计到达时间',
                           create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                           INDEX idx_meta_id (meta_id),
                           INDEX idx_flid (flid),
                           INDEX idx_fide (fide)
);

-- ============================================================
-- 22. DFME-FPTT：计划时间变更
-- ============================================================
CREATE TABLE FPTT_DELT (
                           id BIGINT PRIMARY KEY AUTO_INCREMENT,
                           meta_id BIGINT COMMENT '关联TB_META.id',
                           flid VARCHAR(20) COMMENT '航班唯一编号',
                           ffid VARCHAR(50) COMMENT '航班标识(旧)',
                           fide VARCHAR(50) COMMENT '航班标识(新)',
                           awcd VARCHAR(10) COMMENT '航空公司',
                           flno VARCHAR(20) COMMENT '航班号',
                           fptt VARCHAR(20) COMMENT '新计划起飞时间',
                           fplt VARCHAR(20) COMMENT '新计划降落时间',
                           create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                           INDEX idx_meta_id (meta_id),
                           INDEX idx_flid (flid),
                           INDEX idx_fide (fide)
);

-- ============================================================
-- 23. DFME-FRTT：实际时间变更
-- ============================================================
CREATE TABLE FRTT_DELT (
                           id BIGINT PRIMARY KEY AUTO_INCREMENT,
                           meta_id BIGINT COMMENT '关联TB_META.id',
                           flid VARCHAR(20) COMMENT '航班唯一编号',
                           ffid VARCHAR(50) COMMENT '航班标识(旧)',
                           fide VARCHAR(50) COMMENT '航班标识(新)',
                           awcd VARCHAR(10) COMMENT '航空公司',
                           flno VARCHAR(20) COMMENT '航班号',
                           frtt VARCHAR(20) COMMENT '新实际起飞时间',
                           frlt VARCHAR(20) COMMENT '新实际到达时间',
                           create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                           INDEX idx_meta_id (meta_id),
                           INDEX idx_flid (flid),
                           INDEX idx_fide (fide)
);

-- ============================================================
-- 24. DFME-DFUE：航班更新（主表）
-- ============================================================
CREATE TABLE DFUE_DFLT (
                           id BIGINT PRIMARY KEY AUTO_INCREMENT,
                           meta_id BIGINT COMMENT '关联TB_META.id',
                           flid VARCHAR(20) COMMENT '航班唯一编号',
                           ffid VARCHAR(50) COMMENT '航班标识(旧)',
                           fide VARCHAR(50) COMMENT '航班标识(新)',
                           awcd VARCHAR(10) COMMENT '航空公司',
                           flno VARCHAR(20) COMMENT '航班号',
                           create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                           INDEX idx_meta_id (meta_id),
                           INDEX idx_flid (flid),
                           INDEX idx_fide (fide)
);

-- ============================================================
-- 25. DFME-DFUE：航线更新（明细）
-- ============================================================
CREATE TABLE DFUE_DFLTAIRL (
                               id BIGINT PRIMARY KEY AUTO_INCREMENT,
                               master_id BIGINT COMMENT '关联DFUE_DFLT.id',
                               apno INT COMMENT '航站序号',
                               apcd VARCHAR(10) COMMENT '航站三字码',
                               fptt VARCHAR(20) COMMENT '计划起飞时间',
                               fplt VARCHAR(20) COMMENT '计划降落时间',
                               create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                               INDEX idx_master_id (master_id)
);

-- ============================================================
-- 26. DFME-DFUE：值机更新（明细）
-- ============================================================
CREATE TABLE DFUE_DFLTCKLS (
                               id BIGINT PRIMARY KEY AUTO_INCREMENT,
                               master_id BIGINT COMMENT '关联DFUE_DFLT.id',
                               fces VARCHAR(20) COMMENT '预计开始时间1',
                               fcee VARCHAR(20) COMMENT '预计结束时间1',
                               fcrs VARCHAR(20) COMMENT '实际开始时间1',
                               fcre VARCHAR(20) COMMENT '实际结束时间1',
                               mces VARCHAR(20) COMMENT '预计开始时间2',
                               mcee VARCHAR(20) COMMENT '预计结束时间2',
                               mcrs VARCHAR(20) COMMENT '实际开始时间2',
                               mcre VARCHAR(20) COMMENT '实际结束时间2',
                               create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                               INDEX idx_master_id (master_id)
);

-- ============================================================
-- 27. DFME-DFUE：航站楼更新（明细）
-- ============================================================
CREATE TABLE DFUE_DFLTTMCD (
                               id BIGINT PRIMARY KEY AUTO_INCREMENT,
                               master_id BIGINT COMMENT '关联DFUE_DFLT.id',
                               nmcd VARCHAR(20) COMMENT '国内航站楼编号',
                               jmcd VARCHAR(20) COMMENT '国际航站楼编号',
                               create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                               INDEX idx_master_id (master_id)
);

-- ============================================================
-- 28. DFME-HBTT：航班号变更
-- ============================================================
CREATE TABLE TB_HBTTBODY (
                             id BIGINT PRIMARY KEY AUTO_INCREMENT,
                             meta_id BIGINT COMMENT '关联TB_META.id',
                             flid VARCHAR(20) COMMENT '航班唯一编号',
                             ffid VARCHAR(50) COMMENT '航班标识(旧)',
                             fide VARCHAR(50) COMMENT '航班标识(新)',
                             hbid VARCHAR(50) COMMENT '变更后航班标识(旧)',
                             hbie VARCHAR(50) COMMENT '变更后航班标识(新)',
                             nfln VARCHAR(20) COMMENT '变更后航班号',
                             nawc VARCHAR(10) COMMENT '变更后航空公司',
                             create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                             INDEX idx_meta_id (meta_id),
                             INDEX idx_flid (flid),
                             INDEX idx_fide (fide)
);

-- ============================================================
-- 29. DFME-LBDE：催促登机
-- ============================================================
CREATE TABLE TB_LBDEBODY (
                             id BIGINT PRIMARY KEY AUTO_INCREMENT,
                             meta_id BIGINT COMMENT '关联TB_META.id',
                             flid VARCHAR(20) COMMENT '航班唯一编号',
                             ffid VARCHAR(50) COMMENT '航班标识(旧)',
                             fide VARCHAR(50) COMMENT '航班标识(新)',
                             awcd VARCHAR(10) COMMENT '航空公司',
                             flno VARCHAR(20) COMMENT '航班号',
                             lbdt VARCHAR(20) COMMENT '催促登机时间',
                             mlbd VARCHAR(20) COMMENT '国际部分催促登机时间',
                             ista VARCHAR(10) COMMENT '航班状态',
                             create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                             INDEX idx_meta_id (meta_id),
                             INDEX idx_flid (flid),
                             INDEX idx_fide (fide)
);

-- ============================================================
-- 30. DFME-ONRE：前站起飞
-- ============================================================
CREATE TABLE TB_ONREBODY (
                             id BIGINT PRIMARY KEY AUTO_INCREMENT,
                             meta_id BIGINT COMMENT '关联TB_META.id',
                             flid VARCHAR(20) COMMENT '航班唯一编号',
                             ffid VARCHAR(50) COMMENT '航班标识(旧)',
                             fide VARCHAR(50) COMMENT '航班标识(新)',
                             awcd VARCHAR(10) COMMENT '航空公司',
                             flno VARCHAR(20) COMMENT '航班号',
                             past VARCHAR(20) COMMENT '前站实际起飞时间',
                             eldt VARCHAR(20) COMMENT '本站预计降落时间',
                             ista VARCHAR(10) COMMENT '航班状态',
                             create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                             INDEX idx_meta_id (meta_id),
                             INDEX idx_flid (flid),
                             INDEX idx_fide (fide)
);

-- ============================================================
-- 31. DFME-POKE：结束登机
-- ============================================================
CREATE TABLE TB_POKEBODY (
                             id BIGINT PRIMARY KEY AUTO_INCREMENT,
                             meta_id BIGINT COMMENT '关联TB_META.id',
                             flid VARCHAR(20) COMMENT '航班唯一编号',
                             ffid VARCHAR(50) COMMENT '航班标识(旧)',
                             fide VARCHAR(50) COMMENT '航班标识(新)',
                             awcd VARCHAR(10) COMMENT '航空公司',
                             flno VARCHAR(20) COMMENT '航班号',
                             pokt VARCHAR(20) COMMENT '结束登机时间',
                             mpok VARCHAR(20) COMMENT '国际部分结束登机时间',
                             ista VARCHAR(10) COMMENT '航班状态',
                             create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                             INDEX idx_meta_id (meta_id),
                             INDEX idx_flid (flid),
                             INDEX idx_fide (fide)
);

-- ============================================================
-- 32. DFME-RTNE：返航
-- ============================================================
CREATE TABLE TB_RTNEBODY (
                             id BIGINT PRIMARY KEY AUTO_INCREMENT,
                             meta_id BIGINT COMMENT '关联TB_META.id',
                             flid VARCHAR(20) COMMENT '航班唯一编号',
                             ffid VARCHAR(50) COMMENT '航班标识(旧)',
                             fide VARCHAR(50) COMMENT '航班标识(新)',
                             awcd VARCHAR(10) COMMENT '航空公司',
                             flno VARCHAR(20) COMMENT '航班号',
                             abst VARCHAR(10) COMMENT '不正常状态',
                             iast VARCHAR(10) COMMENT '内部不正常状态',
                             abrs VARCHAR(20) COMMENT '不正常原因',
                             create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                             INDEX idx_meta_id (meta_id),
                             INDEX idx_flid (flid),
                             INDEX idx_fide (fide)
);

-- ============================================================
-- 33. DFME-SFLG：共享航班变更（主表）
-- ============================================================
CREATE TABLE TB_SFLGBODY (
                             id BIGINT PRIMARY KEY AUTO_INCREMENT,
                             meta_id BIGINT COMMENT '关联TB_META.id',
                             flid VARCHAR(20) COMMENT '航班唯一编号',
                             ffid VARCHAR(50) COMMENT '航班标识(旧)',
                             fide VARCHAR(50) COMMENT '航班标识(新)',
                             awcd VARCHAR(10) COMMENT '航空公司',
                             flno VARCHAR(20) COMMENT '航班号',
                             create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                             INDEX idx_meta_id (meta_id),
                             INDEX idx_flid (flid),
                             INDEX idx_fide (fide)
);

-- ============================================================
-- 34. DFME-SFLG：共享航班变更（明细）
-- ============================================================
CREATE TABLE TB_SFLGINFO (
                             id BIGINT PRIMARY KEY AUTO_INCREMENT,
                             master_id BIGINT COMMENT '关联TB_SFLGBODY.id',
                             sfaw VARCHAR(10) COMMENT '共享航空公司二字码',
                             sfno VARCHAR(20) COMMENT '共享航班号',
                             create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                             INDEX idx_master_id (master_id)
);

-- ============================================================
-- 35-36. DFME-BLLS：行李转盘（主表 + 明细）
-- ============================================================
CREATE TABLE BLLS_DFLT (
                           id BIGINT PRIMARY KEY AUTO_INCREMENT,
                           meta_id BIGINT COMMENT '关联TB_META.id',
                           flid VARCHAR(20) COMMENT '航班唯一编号',
                           ffid VARCHAR(50) COMMENT '航班标识(旧)',
                           fide VARCHAR(50) COMMENT '航班标识(新)',
                           awcd VARCHAR(10) COMMENT '航空公司',
                           flno VARCHAR(20) COMMENT '航班号',
                           create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                           INDEX idx_meta_id (meta_id),
                           INDEX idx_flid (flid),
                           INDEX idx_fide (fide)
);

CREATE TABLE BLLS_DFLTDETAIL (
                                 id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                 master_id BIGINT COMMENT '关联BLLS_DFLT.id',
                                 btno INT COMMENT '转盘序号',
                                 code VARCHAR(20) COMMENT '转盘编号',
                                 btat VARCHAR(10) COMMENT '转盘属性',
                                 estr VARCHAR(20) COMMENT '预计开始使用时间',
                                 eend VARCHAR(20) COMMENT '预计结束使用时间',
                                 rstr VARCHAR(20) COMMENT '实际开始使用时间',
                                 rend VARCHAR(20) COMMENT '实际结束使用时间',
                                 btsc VARCHAR(20) COMMENT '所属航站楼',
                                 exno VARCHAR(10) COMMENT '到达出口号',
                                 create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                                 INDEX idx_master_id (master_id)
);

-- ============================================================
-- 37-39. DFME-CKLS：值机柜台（主表 + 明细）
-- ============================================================
CREATE TABLE CKLS_DELT (
                           id BIGINT PRIMARY KEY AUTO_INCREMENT,
                           meta_id BIGINT COMMENT '关联TB_META.id',
                           flid VARCHAR(20) COMMENT '航班唯一编号',
                           ffid VARCHAR(50) COMMENT '航班标识(旧)',
                           fide VARCHAR(50) COMMENT '航班标识(新)',
                           awcd VARCHAR(10) COMMENT '航空公司',
                           flno VARCHAR(20) COMMENT '航班号',
                           fces VARCHAR(20) COMMENT '预计开始时间1',
                           fcee VARCHAR(20) COMMENT '预计结束时间1',
                           fcrs VARCHAR(20) COMMENT '实际开始时间1',
                           fcre VARCHAR(20) COMMENT '实际结束时间1',
                           mces VARCHAR(20) COMMENT '预计开始时间2',
                           mcee VARCHAR(20) COMMENT '预计结束时间2',
                           mcrs VARCHAR(20) COMMENT '实际开始时间2',
                           mcre VARCHAR(20) COMMENT '实际结束时间2',
                           fcdp VARCHAR(100) COMMENT '值机柜台显示1',
                           mcdp VARCHAR(100) COMMENT '值机柜台显示2',
                           create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                           INDEX idx_meta_id (meta_id),
                           INDEX idx_flid (flid),
                           INDEX idx_fide (fide)
);

CREATE TABLE CKLS_CNTR (
                           id BIGINT PRIMARY KEY AUTO_INCREMENT,
                           master_id BIGINT COMMENT '关联CKLS_DELT.id',
                           ckno INT COMMENT '柜台序号',
                           code VARCHAR(20) COMMENT '柜台编号',
                           ckat VARCHAR(10) COMMENT '柜台属性',
                           type VARCHAR(10) COMMENT '柜台类型',
                           ccar VARCHAR(10) COMMENT '所属值机岛',
                           estr VARCHAR(20) COMMENT '预计开始使用时间',
                           eend VARCHAR(20) COMMENT '预计结束使用时间',
                           rstr VARCHAR(20) COMMENT '实际开始使用时间',
                           rend VARCHAR(20) COMMENT '实际结束使用时间',
                           btsc VARCHAR(20) COMMENT '所属航站楼',
                           create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                           INDEX idx_master_id (master_id)
);

CREATE TABLE CKLS_DELTCK (
                             id BIGINT PRIMARY KEY AUTO_INCREMENT,
                             master_id BIGINT COMMENT '关联CKLS_DELT.id',
                             ckno INT COMMENT '柜台序号',
                             code VARCHAR(20) COMMENT '柜台编号',
                             ckat VARCHAR(10) COMMENT '柜台属性',
                             type VARCHAR(10) COMMENT '柜台类型',
                             ccar VARCHAR(10) COMMENT '所属值机岛',
                             estr VARCHAR(20) COMMENT '预计开始使用时间',
                             eend VARCHAR(20) COMMENT '预计结束使用时间',
                             rstr VARCHAR(20) COMMENT '实际开始使用时间',
                             rend VARCHAR(20) COMMENT '实际结束使用时间',
                             btsc VARCHAR(20) COMMENT '所属航站楼',
                             create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                             INDEX idx_master_id (master_id)
);

-- ============================================================
-- 40-41. DFME-GTLS：登机门（主表 + 明细）
-- ============================================================
CREATE TABLE GTLS_DELT (
                           id BIGINT PRIMARY KEY AUTO_INCREMENT,
                           meta_id BIGINT COMMENT '关联TB_META.id',
                           flid VARCHAR(20) COMMENT '航班唯一编号',
                           ffid VARCHAR(50) COMMENT '航班标识(旧)',
                           fide VARCHAR(50) COMMENT '航班标识(新)',
                           awcd VARCHAR(10) COMMENT '航空公司',
                           flno VARCHAR(20) COMMENT '航班号',
                           create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                           INDEX idx_meta_id (meta_id),
                           INDEX idx_flid (flid),
                           INDEX idx_fide (fide)
);

CREATE TABLE DELT_GATE (
                           id BIGINT PRIMARY KEY AUTO_INCREMENT,
                           master_id BIGINT COMMENT '关联GTLS_DELT.id',
                           gtno INT COMMENT '登机门序号',
                           code VARCHAR(20) COMMENT '登机门编号',
                           gtat VARCHAR(10) COMMENT '登机门属性',
                           estr VARCHAR(20) COMMENT '预计开始使用时间',
                           eend VARCHAR(20) COMMENT '预计结束使用时间',
                           rstr VARCHAR(20) COMMENT '实际开始使用时间',
                           rend VARCHAR(20) COMMENT '实际结束使用时间',
                           btsc VARCHAR(20) COMMENT '所属航站楼',
                           create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                           INDEX idx_master_id (master_id)
);

-- ============================================================
-- 42-43. DFME-STLS：机位（主表 + 明细）
-- ============================================================
CREATE TABLE TB_STLSBODY (
                             id BIGINT PRIMARY KEY AUTO_INCREMENT,
                             meta_id BIGINT COMMENT '关联TB_META.id',
                             flid VARCHAR(20) COMMENT '航班唯一编号',
                             ffid VARCHAR(50) COMMENT '航班标识(旧)',
                             fide VARCHAR(50) COMMENT '航班标识(新)',
                             awcd VARCHAR(10) COMMENT '航空公司',
                             flno VARCHAR(20) COMMENT '航班号',
                             create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                             INDEX idx_meta_id (meta_id),
                             INDEX idx_flid (flid),
                             INDEX idx_fide (fide)
);

CREATE TABLE TB_STLSINFO (
                             id BIGINT PRIMARY KEY AUTO_INCREMENT,
                             master_id BIGINT COMMENT '关联TB_STLSBODY.id',
                             stno INT COMMENT '机位序号',
                             code VARCHAR(20) COMMENT '机位编号',
                             estr VARCHAR(20) COMMENT '预计开始使用时间',
                             eend VARCHAR(20) COMMENT '预计结束使用时间',
                             rstr VARCHAR(20) COMMENT '实际开始使用时间',
                             rend VARCHAR(20) COMMENT '实际结束使用时间',
                             btsc VARCHAR(20) COMMENT '所属航站楼',
                             cssi VARCHAR(5) COMMENT '当前停放机位标示',
                             create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                             INDEX idx_master_id (master_id)
);

-- ============================================================
-- 44. TB_APRTINFO：航站信息
-- ============================================================
CREATE TABLE TB_APRTINFO (
                             id BIGINT PRIMARY KEY AUTO_INCREMENT,
                             meta_id BIGINT COMMENT '关联TB_META.id',
                             flid VARCHAR(20) COMMENT '航班唯一编号',
                             ffid VARCHAR(50) COMMENT '航班标识(旧)',
                             fide VARCHAR(50) COMMENT '航班标识(新)',
                             apno INT COMMENT '航站序号',
                             apcd VARCHAR(10) COMMENT '航站三字码',
                             fptt VARCHAR(20) COMMENT '计划起飞时间',
                             fplt VARCHAR(20) COMMENT '计划降落时间',
                             apat VARCHAR(10) COMMENT '航站属性',
                             create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                             INDEX idx_meta_id (meta_id),
                             INDEX idx_flid (flid)
);


-- 1. 用户表
CREATE TABLE sys_user (
                          id BIGINT PRIMARY KEY AUTO_INCREMENT,
                          username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
                          password VARCHAR(100) NOT NULL COMMENT '密码（BCrypt加密）',
                          email VARCHAR(100) COMMENT '邮箱',
                          phone VARCHAR(20) COMMENT '手机号',
                          real_name VARCHAR(50) COMMENT '真实姓名',
                          status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
                          create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                          update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 2. 角色表
CREATE TABLE sys_role (
                          id BIGINT PRIMARY KEY AUTO_INCREMENT,
                          role_code VARCHAR(50) NOT NULL UNIQUE COMMENT '角色编码：ROLE_USER/ROLE_MANAGER/ROLE_AUDITOR',
                          role_name VARCHAR(50) NOT NULL COMMENT '角色名称：普通用户/航班管理员/审核人员',
                          description VARCHAR(200) COMMENT '角色描述',
                          create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 3. 用户-角色关联表
CREATE TABLE sys_user_role (
                               id BIGINT PRIMARY KEY AUTO_INCREMENT,
                               user_id BIGINT NOT NULL COMMENT '用户ID',
                               role_id BIGINT NOT NULL COMMENT '角色ID',
                               UNIQUE KEY uk_user_role (user_id, role_id)
);

INSERT INTO sys_role (role_code, role_name, description) VALUES
                                                             ('ROLE_USER', '普通用户', '仅可查询航班信息'),
                                                             ('ROLE_MANAGER', '航班管理员', '查询、统计、航班变更申请、查看变更信息'),
                                                             ('ROLE_AUDITOR', '审核人员', '查询、统计、航班变更申请审核、查看变更信息');