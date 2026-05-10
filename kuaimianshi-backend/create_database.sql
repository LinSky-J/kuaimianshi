create database if not exists mianshiya default character set utf8mb4 collate utf8mb4_unicode_ci;

use mianshiya;

create table if not exists user
(
    id                 bigint auto_increment primary key comment 'id',
    userAccount        varchar(256)                             not null comment 'account',
    userPassword       varchar(512)                             null comment 'password',
    unionId            varchar(256)                             null comment 'wechat unionId',
    mpOpenId           varchar(256)                             null comment 'wechat openId',
    userName           varchar(256)                             null comment 'user name',
    userAvatar         varchar(1024)                            null comment 'user avatar',
    userProfile        varchar(512)                             null comment 'user profile',
    userRole           varchar(256) default 'user'              not null comment 'user role',
    phoneNumber        varchar(20)                              null comment 'phone number',
    email              varchar(256)                             null comment 'email',
    grade              varchar(50)                              null comment 'grade',
    workExperience     varchar(512)                             null comment 'work experience',
    expertiseDirection varchar(512)                             null comment 'expertise direction',
    editTime           datetime     default CURRENT_TIMESTAMP   not null comment 'edit time',
    createTime         datetime     default CURRENT_TIMESTAMP   not null comment 'create time',
    updateTime         datetime     default CURRENT_TIMESTAMP   not null on update CURRENT_TIMESTAMP comment 'update time',
    isDelete           tinyint      default 0                   not null comment 'is delete',
    unique key uk_userAccount (userAccount),
    index idx_unionId (unionId),
    index idx_mpOpenId (mpOpenId)
) comment 'user' collate = utf8mb4_unicode_ci;

create table if not exists question_bank
(
    id          bigint auto_increment primary key comment 'id',
    title       varchar(256)                       null comment 'title',
    description text                               null comment 'description',
    picture     varchar(2048)                      null comment 'picture',
    userId      bigint                             not null comment 'creator user id',
    editTime    datetime default CURRENT_TIMESTAMP not null comment 'edit time',
    createTime  datetime default CURRENT_TIMESTAMP not null comment 'create time',
    updateTime  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment 'update time',
    isDelete    tinyint  default 0                 not null comment 'is delete',
    index idx_title (title)
) comment 'question bank' collate = utf8mb4_unicode_ci;

create table if not exists question
(
    id         bigint auto_increment primary key comment 'id',
    title      varchar(256)                       null comment 'title',
    content    text                               null comment 'content',
    tags       varchar(1024)                      null comment 'tags json array',
    answer     text                               null comment 'answer',
    userId     bigint                             not null comment 'creator user id',
    editTime   datetime default CURRENT_TIMESTAMP not null comment 'edit time',
    createTime datetime default CURRENT_TIMESTAMP not null comment 'create time',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment 'update time',
    isDelete   tinyint  default 0                 not null comment 'is delete',
    index idx_title (title),
    index idx_userId (userId)
) comment 'question' collate = utf8mb4_unicode_ci;

create table if not exists question_bank_question
(
    id             bigint auto_increment primary key comment 'id',
    questionBankId bigint                             not null comment 'question bank id',
    questionId     bigint                             not null comment 'question id',
    userId         bigint                             not null comment 'creator user id',
    createTime     datetime default CURRENT_TIMESTAMP not null comment 'create time',
    updateTime     datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment 'update time',
    unique key uk_bank_question (questionBankId, questionId)
) comment 'question bank relation' collate = utf8mb4_unicode_ci;

create table if not exists mock_interview
(
    id             bigint auto_increment primary key comment 'id',
    workExperience varchar(256)                       not null comment 'work experience',
    jobPosition    varchar(256)                       not null comment 'job position',
    difficulty     varchar(50)                        not null comment 'difficulty',
    messages       mediumtext                         null comment 'messages',
    status         int      default 0                 not null comment 'status',
    userId         bigint                             not null comment 'creator user id',
    createTime     datetime default CURRENT_TIMESTAMP not null comment 'create time',
    updateTime     datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment 'update time',
    isDelete       tinyint  default 0                 not null comment 'is delete',
    index idx_userId (userId)
) comment 'mock interview' collate = utf8mb4_unicode_ci;
