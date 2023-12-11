-- com.zhouzhitong.test.mybatis.bean.Person
CREATE TABLE person (
    id  bigint auto_increment comment '自增主键'
        primary key,
    create_time      datetime    default CURRENT_TIMESTAMP not null comment '创建时间',
    created_by       varchar(64) default 'system'          null comment '创建人',
    deleted          tinyint(1)  default 0                 not null comment '是否被删除（0、否  1、是）',
    last_modified_by varchar(64) default 'system'          null comment '最后修改人',
    update_time      datetime    default CURRENT_TIMESTAMP not null comment '最后修改时间',
	name varchar(64),
	age integer,
	phone varchar(64),
	address json
);


