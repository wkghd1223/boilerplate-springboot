-- PostgreSQL

CREATE USER bearable PASSWORD 'bearable1@' SUPERUSER;
CREATE DATABASE bearable OWNER bearable;

CREATE SCHEMA service;
-- Sequence for file
CREATE sequence service.file_tbl_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- Sequence for Notice
CREATE sequence service.ntc_tbl_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


CREATE TABLE "service"."ntc_tbl" (
                                     "ntc_id"	bigint	DEFAULT nextval('service.ntc_tbl_id_seq'::regclass)	NOT NULL,
                                     "prt_univ_id"	bigint		NULL,
                                     "titl"	varchar(255)		NOT NULL,
                                     "cntnt"	text		NOT NULL,
                                     "crt_at"	timestamp	default CURRENT_TIMESTAMP	NOT NULL,
                                     "del_at"	timestamp		NULL
);

COMMENT ON COLUMN "service"."ntc_tbl"."ntc_id" IS '공지사항 아이디';
COMMENT ON COLUMN "service"."ntc_tbl"."prt_univ_id" IS '제휴대학 아이디';
COMMENT ON COLUMN "service"."ntc_tbl"."titl" IS '제목';
COMMENT ON COLUMN "service"."ntc_tbl"."cntnt" IS '내용';
COMMENT ON COLUMN "service"."ntc_tbl"."crt_at" IS '생성일시';
COMMENT ON COLUMN "service"."ntc_tbl"."del_at" IS '삭제일시';

CREATE TABLE "service"."file_tbl" (
                                      "file_id"	char(20)	DEFAULT concat('F', lpad(nextval('service.file_tbl_id_seq'::regclass)::text, 19, '0'::text))	NOT NULL,
                                      "crt_at"	timestamp	default CURRENT_TIMESTAMP	NOT NULL,
                                      "del_at"	timestamp		NULL
);
COMMENT ON COLUMN "service"."file_tbl"."file_id" IS '파일 아이디';
COMMENT ON COLUMN "service"."file_tbl"."crt_at" IS '생성일시';
COMMENT ON COLUMN "service"."file_tbl"."del_at" IS '삭제일시';

CREATE TABLE "service"."file_dtl_tbl" (
                                          "file_num"	int	DEFAULT 1	NOT NULL,
                                          "file_id"	char(20)		NOT NULL,
                                          "pth"	varchar(255)		NULL,
                                          "file_nm"	varchar(255)		NULL,
                                          "orig_file_nm"	varchar(255)		NULL,
                                          "ext"	varchar(50)		NULL,
                                          "sz"	bigint		NULL
);
COMMENT ON COLUMN "service"."file_dtl_tbl"."file_num" IS '파일 번호';
COMMENT ON COLUMN "service"."file_dtl_tbl"."file_id" IS '파일 아이디';
COMMENT ON COLUMN "service"."file_dtl_tbl"."pth" IS '경로';
COMMENT ON COLUMN "service"."file_dtl_tbl"."file_nm" IS '파일명';
COMMENT ON COLUMN "service"."file_dtl_tbl"."orig_file_nm" IS '원본파일명';
COMMENT ON COLUMN "service"."file_dtl_tbl"."ext" IS '확장자';
COMMENT ON COLUMN "service"."file_dtl_tbl"."sz" IS '크기';

ALTER TABLE "service"."ntc_tbl" ADD CONSTRAINT "PK_NTC_TBL" PRIMARY KEY ( "ntc_id" );
ALTER TABLE "service"."file_tbl" ADD CONSTRAINT "PK_FILE" PRIMARY KEY ( "file_id" );
ALTER TABLE "service"."file_dtl_tbl" ADD CONSTRAINT "PK_FILE_DTL" PRIMARY KEY ( "file_num", "file_id" );


ALTER TABLE "service"."file_dtl_tbl" ADD CONSTRAINT "FK_file_TO_file_dtl_1" FOREIGN KEY ( "file_id" )
    REFERENCES "service"."file_tbl" ( "file_id" );