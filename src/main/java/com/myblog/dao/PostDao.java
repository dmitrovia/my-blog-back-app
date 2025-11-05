package com.myblog.dao;

import com.myblog.model.Post;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Repository
public class PostDao {

    private final JdbcTemplate jdbcTemplate;

    public PostDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Post> findAll(String search, int pageNumber, int pageSize) {
        String[] parts = search.split("\\s+");
        StringBuilder sql = new StringBuilder("SELECT p.id, p.title, p.text, p.image, COUNT(l.id) as likes_count, COUNT(c.id) as comments_count FROM posts p ");
        sql.append("LEFT JOIN likes l ON p.id = l.post_id ");
        sql.append("LEFT JOIN comments c ON p.id = c.post_id ");
        sql.append("LEFT JOIN tags t ON p.id = t.post_id ");
        sql.append("WHERE 1=1 ");

        StringBuilder tagClause = new StringBuilder();
        StringBuilder substringBuilder = new StringBuilder();

        for (String part : parts) {
            if (part.startsWith("#")) {
                tagClause.append(" AND t.tag = ? ");
            } else if (!part.trim().isEmpty()) {
                substringBuilder.append(part).append(" ");
            }
        }

        String substring = substringBuilder.toString().trim();
        String tagClauseStr = tagClause.toString();

        if (!substring.isEmpty()) {
            sql.append(" AND LOWER(p.title) LIKE LOWER(?) ");
        }

        if (!tagClauseStr.isEmpty()) {
            sql.append(tagClauseStr);
        }

        sql.append("GROUP BY p.id, p.title, p.text, p.image ");
        sql.append("ORDER BY p.created_at DESC ");
        sql.append("LIMIT ? OFFSET ?");

        List<Object> argsList = new ArrayList<>();

        for (String part : parts) {
            if (part.startsWith("#")) {
                argsList.add(part.substring(1));
            }
        }

        if (!substring.isEmpty()) {
            argsList.add("%" + substring + "%");
        }

        argsList.add(pageSize);
        argsList.add((pageNumber - 1) * pageSize);

        return jdbcTemplate.query(sql.toString(), postRowMapper(), argsList.toArray());
    }

    public Post findById(Long id) {
        String sql = "SELECT p.id, p.title, p.text, p.image, COUNT(l.id) as likes_count, COUNT(c.id) as comments_count FROM posts p " +
                "LEFT JOIN likes l ON p.id = l.post_id " +
                "LEFT JOIN comments c ON p.id = c.post_id " +
                "WHERE p.id = ? " +
                "GROUP BY p.id, p.title, p.text, p.image";

        return jdbcTemplate.queryForObject(sql, postRowMapper(), id);
    }

    public Post save(Post post) {
        String sql = "INSERT INTO posts (title, text, image) VALUES (?, ?, ?) RETURNING id";
        Long id = jdbcTemplate.queryForObject(sql, Long.class, post.getTitle(), post.getText(), post.getImage());
        post.setId(id);

        if (post.getTags() != null) {
            for (String tag : post.getTags()) {
                jdbcTemplate.update("INSERT INTO tags (post_id, tag) VALUES (?, ?)", id, tag);
            }
        }

        return post;
    }

    public void update(Post post) {
        String sql = "UPDATE posts SET title = ?, text = ?, image = ? WHERE id = ?";
        jdbcTemplate.update(sql, post.getTitle(), post.getText(), post.getImage(), post.getId());

        jdbcTemplate.update("DELETE FROM tags WHERE post_id = ?", post.getId());

        if (post.getTags() != null) {
            for (String tag : post.getTags()) {
                jdbcTemplate.update("INSERT INTO tags (post_id, tag) VALUES (?, ?)", post.getId(), tag);
            }
        }
    }

    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM posts WHERE id = ?", id);
    }

    public void incrementLikes(Long postId) {
        String sql = "INSERT INTO likes (post_id) VALUES (?)";
        jdbcTemplate.update(sql, postId);
    }

    public int getLikesCount(Long postId) {
        String sql = "SELECT COUNT(*) FROM likes WHERE post_id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, postId);
    }

    public int getCommentsCount(Long postId) {
        String sql = "SELECT COUNT(*) FROM comments WHERE post_id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, postId);
    }

    public List<String> getTagsByPostId(Long postId) {
        String sql = "SELECT tag FROM tags WHERE post_id = ?";
        return jdbcTemplate.queryForList(sql, String.class, postId);
    }

    private RowMapper<Post> postRowMapper() {
        return (ResultSet rs, int rowNum) -> {
            Post post = new Post();
            post.setId(rs.getLong("id"));
            post.setTitle(rs.getString("title"));
            post.setText(rs.getString("text"));
            post.setImage(rs.getBytes("image"));
            post.setLikesCount(rs.getInt("likes_count"));
            post.setCommentsCount(rs.getInt("comments_count"));
            return post;
        };
    }
}