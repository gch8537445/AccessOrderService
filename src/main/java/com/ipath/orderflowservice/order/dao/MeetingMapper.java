package com.ipath.orderflowservice.order.dao;

import com.ipath.orderflowservice.order.bean.vo.EventVo;
import com.ipath.orderflowservice.order.bean.vo.MeetingVo;
import com.ipath.orderflowservice.order.dao.bean.Meeting;
import com.ipath.orderflowservice.order.dao.vo.SyncMeetingVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MeetingMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Meeting record);

    int insertSelective(Meeting record);

    Meeting selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Meeting record);

    int updateByPrimaryKey(Meeting record);

    /**
     * 获取会议列表
     *
     * @param companyId 公司id
     * @param param 筛选参数
     * @return 会议列表
     */
    List<MeetingVo> meetingList(@Param("companyId") Long companyId, @Param("param") String param);

    /**
     * 获取需要同步的会议信息
     *
     * @return 同步会议vo
     */
    List<SyncMeetingVo> getSyncMeeting(Long syncMeetingCompanyId);

    /**
     * 获取常用会议
     * @param userid
     * @param param
     * @return
     */
    List<MeetingVo> getCommonlyUsedMeeting(@Param("userid") long userid,@Param("param") String param);


    List<EventVo> getByCompanyIdAndIoTitle(@Param("companyId") Long companyId, @Param("ioTitle") String ioTitle, @Param("ioStatus") String ioStatus);
}