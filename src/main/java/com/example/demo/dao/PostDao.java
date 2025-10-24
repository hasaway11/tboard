package com.example.demo.dao;

import com.example.demo.dto.*;
import com.example.demo.entity.*;
import org.apache.ibatis.annotations.*;

import java.util.*;

@Mapper
public interface PostDao {
  long insert(Post post);

  List<Post> findAll(long pageno, long pagesize);

  @Select("select count(*) from post")
  long count();

  long increaseReadCnt(long pno);

  @Select("select * from post where pno=#{pno}")
  Optional<Post> findByPno(long pno);

  Optional<PostDto.PostResponse> findByPnoWithComments(long pno);

  @Update("update post set title=#{title}, content=#{content} where pno=#{pno}")
  long updateByPno(PostDto.UpdateRequest dto);

  @Delete("delete from post where pno=#{pno}")
  long deleteByPno(long pno);

  @Select("select good_cnt from post where pno=#{pno}")
  Optional<Integer> findGoodCntByPno(long pno);

  @Update("update post set good_cnt=good_cnt+1 where pno=#{pno}")
  long increaseGoodCntByPno(long pno);
}








