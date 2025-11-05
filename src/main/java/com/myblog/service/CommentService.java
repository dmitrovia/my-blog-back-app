package com.myblog.service;

import com.myblog.dao.CommentDao;
import com.myblog.model.Comment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CommentService {

    private final CommentDao commentDao;

    public CommentService(CommentDao commentDao) {
        this.commentDao = commentDao;
    }

    public List<Comment> findByPostId(Long postId) {
        return commentDao.findByPostId(postId);
    }

    public Comment findById(Long postId, Long commentId) {
        return commentDao.findById(postId, commentId);
    }

    public Comment create(Comment comment) {
        return commentDao.save(comment);
    }

    public Comment update(Comment comment) {
        commentDao.update(comment);
        return comment;
    }

    public void deleteById(Long postId, Long commentId) {
        commentDao.deleteById(postId, commentId);
    }
}