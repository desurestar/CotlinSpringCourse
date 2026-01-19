package kaf.pin.lab1corp.controller.api;

import kaf.pin.lab1corp.DTO.response.PostResponse;
import kaf.pin.lab1corp.entity.Post;
import kaf.pin.lab1corp.exception.BadRequestException;
import kaf.pin.lab1corp.exception.ResourceNotFoundException;
import kaf.pin.lab1corp.service.PostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "*")
public class PostRestController {

    private final PostService postService;
    private final Logger logger = LoggerFactory.getLogger(PostRestController.class);

    @Autowired
    public PostRestController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public ResponseEntity<List<PostResponse>> getAllPosts() {
        try {
            List<Post> posts = postService.getAllPosts();
            List<PostResponse> response = posts.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching posts", e);
            throw new RuntimeException("Failed to fetch posts");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable Long id) {
        try {
            Optional<Post> post = postService.getPostById(id);
            if (post.isEmpty()) {
                throw new ResourceNotFoundException("Post not found with id: " + id);
            }
            
            return ResponseEntity.ok(convertToResponse(post.get()));
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error fetching post", e);
            throw new RuntimeException("Failed to fetch post");
        }
    }

    @PostMapping
    public ResponseEntity<PostResponse> createPost(@RequestBody Map<String, String> request) {
        try {
            String postName = request.get("postName");
            if (postName == null || postName.trim().isEmpty()) {
                throw new BadRequestException("Post name is required");
            }
            
            Post post = new Post();
            post.setPost_name(postName);
            
            Post savedPost = postService.savePost(post);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(convertToResponse(savedPost));
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error creating post", e);
            throw new BadRequestException("Failed to create post: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        try {
            Optional<Post> postOpt = postService.getPostById(id);
            if (postOpt.isEmpty()) {
                throw new ResourceNotFoundException("Post not found with id: " + id);
            }
            
            String postName = request.get("postName");
            if (postName == null || postName.trim().isEmpty()) {
                throw new BadRequestException("Post name is required");
            }
            
            Post post = postOpt.get();
            post.setPost_name(postName);
            
            Post updatedPost = postService.savePost(post);
            
            return ResponseEntity.ok(convertToResponse(updatedPost));
        } catch (ResourceNotFoundException | BadRequestException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error updating post", e);
            throw new BadRequestException("Failed to update post: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        try {
            Optional<Post> postOpt = postService.getPostById(id);
            if (postOpt.isEmpty()) {
                throw new ResourceNotFoundException("Post not found with id: " + id);
            }
            
            Post post = postOpt.get();
            if (post.getEmployesList() != null && !post.getEmployesList().isEmpty()) {
                throw new BadRequestException("Cannot delete post with employees assigned");
            }
            
            postService.deletePost(id);
            
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException | BadRequestException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error deleting post", e);
            throw new BadRequestException("Failed to delete post: " + e.getMessage());
        }
    }

    private PostResponse convertToResponse(Post post) {
        return new PostResponse(post.getId(), post.getPost_name());
    }
}
