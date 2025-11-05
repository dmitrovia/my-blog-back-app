package com.myblog.service;

import com.myblog.dao.PostDao;
import com.myblog.model.Post;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PostService {

    private final PostDao postDao;

    public PostService(PostDao postDao) {
        this.postDao = postDao;
    }

    public List<Post> findAll(String search, int pageNumber, int pageSize) {
        return postDao.findAll(search, pageNumber, pageSize);
    }

    public Post findById(Long id) {
        Post post = postDao.findById(id);
        post.setTags(postDao.getTagsByPostId(id));
        post.setLikesCount(postDao.getLikesCount(id));
        post.setCommentsCount(postDao.getCommentsCount(id));
        return post;
    }

    public Post create(Post post) {
        Post saved = postDao.save(post);
        saved.setTags(post.getTags());
        saved.setLikesCount(0);
        saved.setCommentsCount(0);
        return saved;
    }

    public Post update(Post post) {
        postDao.update(post);
        post.setTags(postDao.getTagsByPostId(post.getId()));
        post.setLikesCount(postDao.getLikesCount(post.getId()));
        post.setCommentsCount(postDao.getCommentsCount(post.getId()));
        return post;
    }

    public void deleteById(Long id) {
        postDao.deleteById(id);
    }

    public int incrementLikes(Long postId) {
        postDao.incrementLikes(postId);
        return postDao.getLikesCount(postId);
    }

    public void updateImage(Long id, byte[] image) {
        Post post = postDao.findById(id);
        post.setImage(image);
        postDao.update(post);
    }

    public byte[] getImage(Long id) {
        Post post = postDao.findById(id);
        return post.getImage();
    }
}