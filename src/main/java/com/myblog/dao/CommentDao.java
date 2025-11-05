package com.myblog.dao;

import com.myblog.model.Comment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.List;

@Repository
public class CommentDao {

    private final JdbcTemplate jdbcTemplate;

    public CommentDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Comment> findByPostId(Long postId) {
        String sql = "SELECT id, post_id, text FROM comments WHERE post_id = ? ORDER BY created_at";
        return jdbcTemplate.query(sql, commentRowMapper(), postId);
    }

    public Comment findById(Long postId, Long commentId) {
        String sql = "SELECT id, post_id, text FROM comments WHERE post_id = ? AND id = ?";
        return jdbcTemplate.queryForObject(sql, commentRowMapper(), postId, commentId);
    }

    public Comment save(Comment comment) {
        String sql = "INSERT INTO comments (post_id, text) VALUES (?, ?) RETURNING id";
        Long id = jdbcTemplate.queryForObject(sql, Long.class, comment.getPostId(), comment.getText());
        comment.setId(id);
        return comment;
    }

    public void update(Comment comment) {
        String sql = "UPDATE comments SET text = ? WHERE post_id = ? AND id = ?";
        jdbcTemplate.update(sql, comment.getText(), comment.getPostId(), comment.getId());
    }

    public void deleteById(Long postId, Long commentId) {
        String sql = "DELETE FROM comments WHERE post_id = ? AND id = ?";
        jdbcTemplate.update(sql, postId, commentId);
    }

    private RowMapper<Comment> commentRowMapper() {
        return (ResultSet rs, int rowNum) -> {
            Comment comment = new Comment();
            comment.setId(rs.getLong("id"));
            comment.setPostId(rs.getLong("post_id"));
            comment.setText(rs.getString("text"));
            return comment;
        };
    }
}