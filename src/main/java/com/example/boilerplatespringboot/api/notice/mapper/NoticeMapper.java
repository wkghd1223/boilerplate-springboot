package com.example.boilerplatespringboot.api.notice.mapper;

import com.example.boilerplatespringboot.api.notice.dto.NoticeDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NoticeMapper {
  List<NoticeDto.ListItem> getNoticeList(NoticeDto.ListRequest query);
  Integer getNoticeListCount(NoticeDto.ListRequest query);

  NoticeDto.Item getNotice(@Param("noticeId") Long noticeId);
}
