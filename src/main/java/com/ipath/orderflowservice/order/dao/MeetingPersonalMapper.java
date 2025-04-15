package com.ipath.orderflowservice.order.dao;

import com.ipath.orderflowservice.order.bean.param.PersonalMeetingParam;
import com.ipath.orderflowservice.order.bean.vo.MeetingPersonalVo;
import com.ipath.orderflowservice.order.bean.vo.MeetingVo;
import com.ipath.orderflowservice.order.dao.bean.MeetingPersonal;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MeetingPersonalMapper {
    int deleteByPrimaryKey(Long id);

    int insert(MeetingPersonal record);

    int insertSelective(MeetingPersonal record);

    MeetingPersonal selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MeetingPersonal record);

    int updateByPrimaryKey(MeetingPersonal record);

    /**
     * 获取 个人会议
     *
     * @param userid 用户id
     * @return 个人会议列表
     */
    List<MeetingPersonalVo> getPersonalMeeting(@Param("userid") long userid);

    /**
     * 获取个人会议信息
     *
     * @param userid    用户id
     * @param meetingId 会议id
     * @return 个人会议信息
     */
    MeetingPersonalVo getPersonalMeetingByUseridAndMeeting(@Param("userid") long userid, @Param("meetingId") Long meetingId);

    /**
     * 一键删除 个人会议信息(不包含owner会议)
     * @param userid 用户id
     * @return
     */
    int oneClickDeletion(@Param("userid") long userid);

    /**
     * 自动同步 批量新增 个人会议
     * @param insertList
     * @return
     */
    int batchInsert(@Param("list") List<MeetingPersonal> insertList);

    /**
     * 自动同步 批量修改 个人会议
     * @param updateList
     * @return
     */
    int batchUpdate(@Param("list") List<MeetingPersonal> updateList);

    /**
     * 自动同步 批量删除 个人会议
     * @return
     */
    int syncRemoveMeetingPersonal();

    /**
     * 获取 个人会议列表
     * @param personalMeetingParam
     * @return
     */
    List<MeetingVo> getPersonalMeetingByParam(PersonalMeetingParam personalMeetingParam);



}