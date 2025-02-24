package com.example.boilerplatespringboot.api.notice.service;

import com.example.boilerplatespringboot.api.notice.dto.NoticeDto;
import com.example.boilerplatespringboot.api.notice.entity.NoticeEntity;
import com.example.boilerplatespringboot.api.notice.mapper.NoticeMapper;
import com.example.boilerplatespringboot.api.notice.repository.NoticeRepository;
import com.example.boilerplatespringboot.common.enums.ApiStatus;
import com.example.boilerplatespringboot.common.exception.ApiException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class NoticeService {

  private final NoticeMapper noticeMapper;
  private final NoticeRepository noticeRepository;

  @Transactional(readOnly = true)
  public NoticeDto.ListResponse getList(NoticeDto.ListRequest query) {
    NoticeDto.ListResponse res = new NoticeDto.ListResponse();
    res.setItems(noticeMapper.getNoticeList(query));
    res.setTotalCount(noticeMapper.getNoticeListCount(query));
    return res;
  }

  @Transactional(readOnly = true)
  public NoticeDto.Item getOne(Long noticeId) {
    NoticeDto.Item item = noticeMapper.getNotice(noticeId);
    if (item == null) {
      throw new ApiException(ApiStatus.RESOURCE_NOT_FOUND);
    }
    return item;
  }

  @Transactional
  public void save(NoticeDto.Create dto) {
    noticeRepository.save(NoticeEntity.from(dto));
  }

  @Transactional
  public void update(NoticeDto.Update dto) {
    Long noticeId = dto.getNoticeId();
    NoticeEntity entity = noticeRepository.findById(noticeId)
        .orElseThrow(() -> new ApiException(ApiStatus.RESOURCE_NOT_FOUND));

    entity.setTitl(dto.getTitle());
    entity.setCntnt(dto.getContent());

    noticeRepository.save(entity);
  }

  @Transactional
  public void delete(Long noticeId) {
    noticeRepository.deleteById(noticeId);
  }
}
