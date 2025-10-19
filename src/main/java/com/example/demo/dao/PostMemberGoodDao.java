package com.example.demo.dao;

import org.apache.ibatis.annotations.*;

@Mapper
public interface PostMemberGoodDao {
  @Select("select count(*) from posts_members_good where pno=#{pno} and username=#{loginId}")
  boolean existsByPnoAndUsername(long pno, String loginId);

  @Insert("insert into posts_members_good values(#{pno}, #{loginId})")
  long save(long pno, String loginId);
}
