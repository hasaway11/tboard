package com.example.demo.dao;

import org.apache.ibatis.annotations.*;

@Mapper
public interface PostMemberGoodDao {
  @Select("select count(*) from post_member_good where pno=#{pno} and username=#{loginId}")
  boolean existsByPnoAndUsername(long pno, String loginId);

  @Insert("insert into post_member_good values(#{pno}, #{loginId})")
  long save(long pno, String loginId);
}
