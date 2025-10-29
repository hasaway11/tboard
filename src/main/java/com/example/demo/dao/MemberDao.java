package com.example.demo.dao;

import com.example.demo.entity.*;
import org.apache.ibatis.annotations.*;

import java.util.*;

@Mapper
public interface MemberDao {
  @Select("select count(*) from member where username=#{username} and rownum=1")
  boolean existsByUsername(String username);

  long insert(Member member);

  @Select("select username from member where email=#{email}")
  Optional<String> findUsernameByEmail(String email);

  @Update("update member set profile=#{profile} where username=#{username}")
  long updateProfileByUsername(String profile, String username);

  @Update("update member set password=#{newPassword} where username=#{username}")
  long updatePasswordByUsername(String username, String newPassword);

  @Select("select * from member where username=#{username}")
  Optional<Member> findByUsername(String username);

  @Update("update member set failed_attempts=failed_attempts+1 where username=#{username}")
  long increaseFailedAttemptsByUsername(String username);

  @Update("update member set is_lock=1, failed_attempts=5 where username=#{username}")
  long lockAccountByUsername(String username);

  @Update("update member set failed_attempts=0 where username=#{username}")
  long resetFailedAttemptsByUsername(String username);

  @Delete("delete from member where username=#{username}")
  long deleteByUsername(String username);

  @Select("select password from member where username=#{username}")
  String findPasswordByUsername(String username);

  @Select("select profile from member where username=#{username}")
  String findProfileByUsername(String username);
}








